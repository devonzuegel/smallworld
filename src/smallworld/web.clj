(ns smallworld.web
  (:gen-class)
  (:require [cheshire.core :refer [generate-string]]
            [clojure.data.json :as json]
            [clojure.java.io :as io]
            [clojure.pprint :as pp]
            [clojure.set :as set]
            [compojure.core :refer [ANY defroutes GET POST]]
            [compojure.handler]
            [compojure.route :as route]
            [oauth.twitter :as oauth]
            [ring.adapter.jetty :as jetty]
            [ring.middleware.session.cookie :as cookie]
            [ring.util.response :as response]
            [smallworld.admin :as admin]
            [smallworld.coordinates :as coordinates]
            [smallworld.db :as db]
            [smallworld.email :as email]
            [smallworld.memoize :as m]
            [smallworld.session :as session]
            [smallworld.user-data :as user-data]
            [smallworld.util :as util]
            [timely.core :as timely]))

(def debug? false)

(defn log-event [name data]
  (util/log (str name ": " data)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; server ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn set-session [response-so-far new-session]
  (assoc response-so-far
         :session new-session))

(defn get-session [req]
  (let [session-data (get-in req [:session] session/blank)
        ;; screen-name  (:screen-name session-data)
        result       session-data #_(if (= screen-name admin/screen-name)
                                      (let [new-screen-name (:screen_name (db/select-first db/impersonation-table))]
                                        (if (nil? new-screen-name)
                                          session-data
                                          {:screen-name new-screen-name :impersonation? true}))
                                      session-data)]
    (log-event "get-session" result)
    result))

;; TODO: make it so this --with-access-token works with atom memoization too, to speed it up
(defn fetch-current-user--with-access-token [access-token]
  (let [client (oauth/oauth-client (util/get-env-var "TWITTER_CONSUMER_KEY")
                                   (util/get-env-var "TWITTER_CONSUMER_SECRET")
                                   (:oauth-token access-token)
                                   (:oauth-token-secret access-token))]
    (client {:method :get
             :url (str "https://api.twitter.com/1.1/account/verify_credentials.json")
             :body "include_email=true"})))

;; step 1
;; in prod: this will redirect to https://small-world-friends.herokuapp.com/authorized,
;;          the 'Callback URL' set up at https://developer.twitter.com/en/apps/9258522
;; in dev:  this will redirect to http://localhost:3001/authorized,
;;          the 'Callback URL' set up at https://developer.twitter.com/en/apps/9258699
(defn start-oauth-flow []
  (let [request-token (oauth/oauth-request-token (util/get-env-var "TWITTER_CONSUMER_KEY")
                                                 (util/get-env-var "TWITTER_CONSUMER_SECRET"))
        redirect-url  (oauth/oauth-authorization-url (:oauth-token request-token))]
    (log-event "start-oauth-flow" {:message "someone has started the oauth flow (we don't yet have the screen name)"})
    (response/redirect redirect-url)))

;; step 2
(defn store-fetched-access-token-then-redirect-home [req]
  (try (let [oauth-token    (get-in req [:params :oauth_token])
             oauth-verifier (get-in req [:params :oauth_verifier])
             access-token   (oauth/oauth-access-token (util/get-env-var "TWITTER_CONSUMER_KEY") oauth-token oauth-verifier)
             api-response   (fetch-current-user--with-access-token access-token)
             current-user   (user-data/abridged api-response {:screen-name (:screen-name api-response)})
             screen-name    (:screen-name api-response)]
         (when debug?
           (pp/pprint "twitter verify_credentials.json:")
           (pp/pprint api-response)
           (println "screen-name:  " screen-name))
         (db/memoized-insert-or-update! db/access_tokens-table    screen-name {:access_token access-token}) ; TODO: consider memoizing with an atom for speed
         (db/memoized-insert-or-update! db/twitter-profiles-table screen-name {:request_key screen-name :data api-response}) ; TODO: consider memoizing with an atom for speed
         (db/insert-or-update! db/settings-table :screen_name {:screen_name    screen-name
                                                               :name           (:name api-response)
                                                               :locations      (:locations current-user)
                                                               :twitter_avatar (user-data/normal-img-to-full-size api-response)})
         (log-event "new-authorization" {:screen-name screen-name
                                         :message (str "@" screen-name ") has successfully authorized small world to access their Twitter account")})
         (set-session (response/redirect "/") {:access-token access-token
                                               :screen-name  (:screen-name api-response)}))
       (catch Throwable e
         (println "user failed to log in")
         (println e)
         (response/redirect "/"))))

(defn logout [req]
  (let [screen-name (:screen-name (get-session req))
        logout-msg (if (nil? screen-name)
                     "no-op: there was no active session"
                     (str "@" screen-name " has logged out"))]
    (log-event "logout" {:screen-name screen-name
                         :message logout-msg})
    (set-session (response/redirect "/") {})))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;; settings ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn get-settings [req screen-name]
  (let [settings (first (db/select-by-col db/settings-table :screen_name screen-name)) ; TODO: set in the session for faster access
        ip-address (or (get-in req [:headers "x-forwarded-for"]) (:remote-addr req))]
    (log-event "get-settings" (if (nil? screen-name) {} {:screen-name screen-name
                                                         :settings    settings
                                                         :ip-address ip-address}))
    settings))

(defn update-settings [req]
  (let [screen-name     (:screen-name (get-session req))
        parsed-body     (json/read-str (slurp (:body req)) :key-fn keyword)
        new-settings    (merge parsed-body {:screen_name screen-name})]
    (log-event "update-settings" {:screen-name screen-name
                                  :settings    new-settings})
    (when debug?
      (println "----------------------------------------------")
      (println "(:session req):")
      (pp/pprint (:session req))
      (println "----------------------------------------------")
      (println "new-settings:")
      (pp/pprint new-settings)
      (println "----------------------------------------------"))

    ; if user just completed the welcome flow, send welcome email
    (when (:welcome_flow_complete new-settings)
      (email/send-email {:to (:email_address new-settings)
                         :template (:welcome email/TEMPLATES)
                         :dynamic_template_data {:twitter_screen_name screen-name
                                                 :twitter_url (str "https://twitter.com/" screen-name)}}))

    ; TODO: add try-catch to handle failures
    ; TODO: simplify/consolidate where the settings stuff is stored
    (db/insert-or-update! db/settings-table :screen_name new-settings)
    (response/response (generate-string new-settings))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; twitter data fetching ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;; 

(def abridged-friends-cache (atom {}))

(defn --fetch-friends [screen-name] ;; use the memoized version of this function!
  (when debug?
    (println "================================================================================================= start")
    (println "fetching friends for " screen-name))

  (log-event "fetch-twitter-friends--begin" {:screen-name screen-name})
  (try
    (let [; the sql-result should never be an empty list; if it is, that means the
          ; access token was deleted. it shouldn't be possible to get to this state,
          ; but if it does happen at some point, then we may need to add a way for
          ; the user to re-authenticate.
          ; note – access tokens don't expire, though the user can revoke them:
          ; https://developer.twitter.com/en/docs/authentication/faq#:~:text=How%20long%20does%20an%20access%20token%20last
          sql-result   (db/select-by-col db/access_tokens-table :request_key screen-name)
          access-token (get-in (first sql-result) [:data :access_token]) ; TODO: memoize this with an atom for faster, non-db access, a la: (get @access-tokens screen-name)
          client       (oauth/oauth-client (util/get-env-var "TWITTER_CONSUMER_KEY")
                                           (util/get-env-var "TWITTER_CONSUMER_SECRET")
                                           (:oauth-token access-token)
                                           (:oauth-token-secret access-token))]
      (loop [cursor -1 ; -1 is the first page, while future pages are gotten from previous_cursor & next_cursor
             result-so-far []]

        (log-event "fetch-twitter-friends--page" {:screen-name screen-name
                                                  :cursor cursor})
        (let [api-response  (client {:method :get
                                     :url "https://api.twitter.com/1.1/friends/list.json"
                                     :body (str "count=200"
                                                "&cursor=" (str cursor)
                                                "&skip_status=false"
                                                "&include_user_entities=true")})
              page-of-friends (:users api-response)
              new-result      (concat result-so-far page-of-friends)
              next-cursor     (:next-cursor api-response)]

          (when debug?
            (println "\nnext-cursor:          " next-cursor)
            (println "new-result count:     " (count new-result))
            (println "updating @" screen-name "'s friends... (partially!)")
            (println "-----------------------------------------------------------------------------------------\n"))

          (db/insert-or-update! db/friends-table :request_key
                                {:request_key screen-name
                                 :data        {:friends (vec new-result)}})
          (if (= next-cursor 0)
            (do (log-event "fetch-twitter-friends--end" {:screen-name screen-name
                                                         :cursor cursor
                                                         :result-count (count new-result)})
                new-result) ; return final result if Twitter returns a cursor of 0
            (recur next-cursor new-result)))))
    (catch Throwable e
      (println "🔴 caught exception when getting friends for screen-name:" screen-name)
      (when (= 429 (get-in e [:data :status])) (println "you hit the Twitter rate limit!"))
      (println (pr-str e))
      :failed)))

(def memoized-friends (m/my-memoize
                       (fn [screen-name]
                         (let [friends-result (--fetch-friends screen-name)]
                           (if (= :failed friends-result)
                             :failed
                             {:friends friends-result})))
                       db/friends-table))

(defn --fetch-abridged-friends [screen-name current-user]
  (map #(user-data/abridged % current-user)
       (:friends (memoized-friends screen-name)))) ; can add (take X) for debugging

(defn --fetch-abridged-friends--not-memoized [screen-name current-user]
  (let [friends (get-in (first (db/select-by-col db/friends-table :request_key screen-name))
                        [:data :friends])
        result (map #(user-data/abridged % current-user) friends)]
    (swap! abridged-friends-cache #(assoc % screen-name result))
    result))

(def memoized-abridged-friends
  (m/my-memoize --fetch-abridged-friends abridged-friends-cache))

(defn get-users-friends [req & [screen-name]]
  (let [session-screen-name (:screen-name (get-session req))
        logged-out?   (nil? session-screen-name)
        screen-name   (or screen-name session-screen-name)
        result        (if logged-out?
                        []
                        (memoized-abridged-friends screen-name (get-settings req session-screen-name)))]
    (log-event "get-users-friends" {:count (count result)
                                    :screen-name screen-name})
    (generate-string result)))

; TODO: consolidate this with memoized-abridged-friends
(defn get-users-friends--not-memoized [req]
  (let [screen-name (:screen-name (get-session req))
        logged-out? (nil? screen-name)
        result      (if logged-out?
                      []
                      (--fetch-abridged-friends--not-memoized screen-name (get-settings req screen-name)))]
    (generate-string result)))

(defn select-location-fields [friend]
  (merge (select-keys friend [:location :name :screen-name])))

(defn refresh-friends-from-twitter [settings] ; optionally pass in settings in case it's already computed so that we don't have to recompute
  (let [screen-name      (:screen_name settings)
        old-friends (map ; fetch the old friends before friends gets updated from the twitter fetch
                     select-location-fields
                     (-> db/friends-table
                         (db/select-by-col :request_key screen-name)
                         vec
                         (get-in [0 :data :friends])))
        friends-result   (--fetch-friends screen-name)
        curr-user-info   {:screen-name screen-name
                          :locations (:locations settings)}
        friends-abridged (map #(user-data/abridged % curr-user-info)
                              friends-result)
        new-friends (map select-location-fields
                         (vec friends-result))
        diff (vec (vals (group-by :screen-name
                                  (concat (set/difference (set old-friends) (set new-friends))
                                          (set/difference (set new-friends) (set old-friends))))))
        diff-html (if (= 0 (count diff)) ; this branch shouldn't be called, but defining the behavior just in case
                    "none of your friends have updated their Twitter location or display name!"
                    (str "<ul>"
                         (clojure.string/join
                          (remove nil?
                                  (flatten
                                   (map (fn [friend]
                                          (let [before (first friend)
                                                after  (second friend)
                                                name-before     (if (clojure.string/blank? (:name before))     "·" (:name before))
                                                name-after      (if (clojure.string/blank? (:name after))      "·" (:name after))
                                                location-before (if (clojure.string/blank? (:location before)) "·" (:location before))
                                                location-after  (if (clojure.string/blank? (:location after))  "·" (:location after))
                                                highlight #(str "<span style=\"background: white; margin: 2px 4px; white-space: nowrap; padding: 0 8px;  display: inline-block; border-radius: 12px;  box-shadow: 0 4px 6px rgba(50, 50, 93, 0.05), 0 1px 6px rgba(0, 0, 0, 0.05);\">" % "</span>")]

                                            [(when (not= (:name before) (:name after))
                                               (str "<li><b>@" (:screen-name after) "</b> updated their name: "
                                                    (highlight name-before) " → " (highlight name-after)
                                                    "<br/></li>"))

                                             (when (not= (:location before) (:location after))
                                               (str "<li><b>@" (:screen-name after) "</b> updated their location: "
                                                    (highlight location-before) " → " (highlight location-after)
                                                    "<br/></li>"))]))
                                        diff))))
                         "</ul>"))]
    (if (= :failed friends-result)
      (let [failure-message (str "could not refresh friends for @" screen-name)]
        (log-event "refresh-twitter-friends--failed" {:screen-name screen-name
                                                      :failure-message failure-message})
        (generate-string (response/bad-request {:message failure-message})))
      (let [email-address (-> db/settings-table
                              (db/select-by-col :screen_name screen-name)
                              first
                              :email_address)]
        (log-event "refreshed-twitter-friends--success" {:screen-name screen-name
                                                         :diff-count  (count diff)
                                                         :diff-html   diff-html
                                                         :email_notifications (:email_notifications settings)
                                                         :send-email? (and (= "daily" (:email_notifications settings))
                                                                           (not-empty diff))
                                                              ;
                                                         })

        (println (str "\n\nhere are @" screen-name "'s friends that changed:"))
        (pp/pprint diff)
        (println (str "\n\nhere is the generated HTML:"))
        (pp/pprint diff-html)
        (println "\n\n")

        (when (and (= "daily" (:email_notifications settings))
                   (not-empty diff))
          (email/send-email {:to email-address
                             :template (:friends-on-the-move email/TEMPLATES)
                             :dynamic_template_data {:twitter_screen_name screen-name
                                                     :friends             diff-html}}))
        (db/update! db/friends-table :request_key screen-name {:data {:friends friends-result}})
        (swap! abridged-friends-cache
               assoc screen-name friends-abridged)
        (when debug? (println (str "done refreshing friends for @" screen-name
                                   " (friends count: " (count friends-abridged) ")")))
        (generate-string friends-abridged)))))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; worker ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;


(def failures (atom []))

(defn try-to-refresh-friends [total-count]
  (fn [i user]
    (try
      (util/log (str "[user " i "/" total-count "] refresh friends for " (:screen_name user)))
      (let [result (refresh-friends-from-twitter user)]
        ; this is a hack :) it will be fragile if the error message ever changes
        (when (clojure.string/starts-with? result "caught exception")
          (throw (Throwable. result))))
      (catch Throwable e
        (println "\ncouldn't refresh friends for user" (:screen_name user))
        (swap! failures (conj user))
        (println e)
        nil))))

(defn worker []
  (println)
  (println "===============================================")
  (util/log "starting worker.clj")
  (println)
  (let [all-users (db/select-all db/settings-table) ; (db/select-by-col db/settings-table :screen_name "antimatter15")
        n-users (count all-users)
        ;; n-failures (count @failures)
        curried-refresh-friends (try-to-refresh-friends n-users)]
    (log-event "worker-start" {:count   n-users
                               :message (str "preparing to refresh friends for " n-users " users\n")})
    (doall (map-indexed curried-refresh-friends all-users))
    (log-event "worker-done" {:count   n-users
                              :message (str "finished refreshing friends for " n-users " users")})

    ;; TODO: put this back when we actually catch failures (currently, we don't)
    (when (= "prod-heroku" (util/get-env-var "ENVIRONMENT"))
      (email/send-email {:to "avery.sara.james@gmail.com"
                         :subject (str "[" (util/get-env-var "ENVIRONMENT") "] worker.clj finished for " n-users " users") #_n-failures #_" failures out of "
                         :type "text/plain"
                         :body (str "finished refreshing friends for " n-users " users" ; ": " n-failures " failures"
                                    #_"\n\n"
                                    #_"users that failed:\n" #_(with-out-str (pp/pprint @failures)))})))

  (println)
  (println "===============================================")
  (println))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; app core ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

; TODO: prepend data endpoints with /api/v1/
;; app is function that takes a request, and returns a response
(defroutes smallworld-routes ; order matters in this function!
  ;; oauth & session endpoints
  (GET "/login"      _   (start-oauth-flow))
  (GET "/authorized" req (store-fetched-access-token-then-redirect-home req))
  (GET "/logout"     req (logout req))
  (GET "/api/v1/session" req (generate-string (select-keys (get-session req) [:screen-name :impersonation?])))

  ;; admin endpoints
  (GET "/api/v1/admin/summary" req (admin/summary-data get-session req))
  (GET "/api/v1/admin/friends/:screen_name" req (admin/friends-of-specific-user get-session get-users-friends req))
  (GET "/api/v1/admin/refresh_all_users_friends" req (admin/refresh-all-users-friends (get-session req) log-event worker))

  ;; app data endpoints
  (GET "/api/v1/settings" req (generate-string (get-settings req (:screen-name (get-session req)))))
  (POST "/api/v1/settings/update" req (update-settings req))
  (POST "/api/v1/coordinates" req (let [parsed-body (json/read-str (slurp (:body req)) :key-fn keyword)
                                        location-name (:location-name parsed-body)]
                                    (generate-string (coordinates/memoized location-name))))
  (GET "/api/v1/friends"      req (get-users-friends req))
  (GET "/api/v1/friends/refresh-atom" req (get-users-friends--not-memoized req))
  ; recompute distances from new locations, without fetching data from Twitter
  (GET "/api/v1/friends/recompute-locations" req (let [screen-name  (:screen-name (get-session req))
                                                       friends-full (:friends (memoized-friends screen-name))
                                                       settings     (get-settings req screen-name)
                                                       corrected-curr-user (merge (get-session req)
                                                                                  {:locations (:locations settings)})
                                                       friends-abridged (map #(user-data/abridged % corrected-curr-user) friends-full)]
                                                   (println (str "recomputed friends distances for @" screen-name " (count: " (count friends-abridged) ")"))
                                                   (swap! abridged-friends-cache
                                                          assoc screen-name friends-abridged)
                                                   (generate-string friends-abridged)))
  ; re-fetch data from Twitter – TODO: this should be a POST not a GET
  (GET "/api/v1/friends/refetch-twitter" req (let [screen-name (:screen-name (get-session req))
                                                   settings    (first (db/select-by-col db/settings-table :screen_name screen-name))]
                                               (refresh-friends-from-twitter settings))) ; TODO: keep refactoring
  ;; general resources
  (route/resources "/")
  (ANY "*" [] (io/resource "public/index.html")))

(defn- https-url [url-string & [port]]
  (let [url (java.net.URL. url-string)]
    (str (java.net.URL. "https" (.getHost url) (or port -1) (.getFile url)))))

(defn- get-request? [{method :request-method}]
  (or (= method :head)
      (= method :get)))

; given a HTTP request, return a redirect response to the equivalent HTTPS url
(defn ssl-redirect-response [request]
  (-> (response/redirect (https-url (ring.util.request/request-url request)))
      ; responding 301 to a POST changes it to a GET, because 301 is older & 307 is newer, so we need to respond to POST requests with a 307
      (response/status   (if (get-request? request) 301 307))))

; redirect any HTTP request to the equivalent HTTPS url
(defn ssl-redirect [handler]
  ; note: we also have a setting in Cloudflare that forces SSL, so if you remove
  ; this, you'll probably still get an SSL redirect
  (fn [request]
    (let [url     (ring.util.request/request-url request)
          host    (.getHost (java.net.URL. url))
          headers (:headers request)]

      (when debug?
        (println)
        (println "url:    " (.toString url))
        (println "host:   " host)
        (println "scheme: " (:scheme request)) ; still not sure why this doesn't match the x-forwarded-proto header when on HTTPS...
        (println "header: " (headers "x-forwarded-proto"))
        (println "headers:" headers)
        (println))

      ; normally we'd use `(:scheme request)` to check for HTTPS instead of `x-forwarded-proto`, but for some reason `(:scheme request)` always says HTTP even when it's HTTPS, which results in infinite redirects
      (if (or  (clojure.string/includes? host "localhost") ; don't redirect localhost (it doesn't support SSL)
               (= "https" (headers "x-forwarded-proto"))   ; don't redirect if already HTTPS
               (= :https  (:scheme request)))
        (handler request)
        (ssl-redirect-response request)))))

; redirect any `www.smallworld.kiwi` request to the equivalent raw domain `smallworld.kiwi` url
(defn www-redirect [handler & [port]]
  (fn [request]
    (let [url  (java.net.URL. (ring.util.request/request-url request))
          host (.getHost url)]

      (if (= host "www.smallworld.kiwi")
        (response/redirect (str (java.net.URL. "https" "smallworld.kiwi" (or port -1) (.getFile url))))
        (handler request)))))

(def app-handler
  (-> smallworld-routes       ; takes a request, returns response
      ssl-redirect            ; middleware: takes a handler, returns a handler
      www-redirect            ; middleware: takes a handler, returns a handler
      (compojure.handler/site ; middleware: takes a handler, returns a handler
       {:session {:cookie-name "small-world-session"
                  :cookie-attrs {:expires "Sat May 29 20:42:00 EDT 2222"}
                  :store (cookie/cookie-store
                          {:key (util/get-env-var "COOKIE_STORE_SECRET_KEY")})}})))

(def scheduled-time (timely/at (timely/hour 2) (timely/minute 47))) ; in UTC

(def schedule-id (atom nil))

(defn end-schedule []
  (println "ending schedule:" @schedule-id)
  (timely/end-schedule @schedule-id)
  (reset! schedule-id nil))

(defonce server* (atom nil))

(defn start! [port]
  (some-> @server* (.stop))

  ; create the tables if they don't already exists
  (db/create-table db/settings-table         db/settings-schema)
  (db/create-table db/twitter-profiles-table db/twitter-profiles-schema)
  (db/create-table db/friends-table          db/friends-schema)
  (db/create-table db/coordinates-table      db/coordinates-schema)
  (db/create-table db/access_tokens-table    db/access-tokens-schema)
  (db/create-table db/impersonation-table    db/impersonation-schema)

  (let [port (Integer. (or port (util/get-env-var "PORT") 5000))
        server (jetty/run-jetty #'app-handler {:port port :join? false})]

    (reset! server* server)))

(defn stop! []
  (if @schedule-id
    (end-schedule)
    (println "@schedule-id is nil – no schedule to end"))
  (if @server*
    (.stop @server*)
    (println "@server* is nil – no server to stop")))

(defn -main []
  (try (timely/start-scheduler)
       (catch Exception e
         (if (= (:cause (Throwable->map e)) "Scheduler already started")
           (println "scheduler already started") ; it's fine, this isn't a real error, so just continue
           (throw e))))

  (println "starting scheduler to run every day at"
           (str (first (:hour scheduled-time)) ":" (first (:minute scheduled-time))) "UTC")
  (let [id (timely/start-schedule
            #_(timely/scheduled-item (timely/every 1 :minutes) worker) ; this is just for debugging
            (timely/scheduled-item (timely/daily scheduled-time) worker))]
    (reset! schedule-id id)
    (println "started scheduler with id:" @schedule-id))



  (println "starting server...")
  (let [default-port 3001
        port (System/getenv "PORT")
        port (if (nil? port)
               (do (println "PORT not defined. Defaulting to" default-port)
                   default-port)
               (Integer/parseInt port))]
    (println "\nsmall world is running on" (str "http://localhost:" port) "\n")
    (start! port)))