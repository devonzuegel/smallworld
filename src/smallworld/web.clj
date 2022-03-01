(ns smallworld.web
  (:gen-class)
  (:require [compojure.core :refer [defroutes GET POST ANY]]
            [compojure.handler]
            [compojure.route :as route]
            [clojure.java.io :as io]
            [ring.adapter.jetty :as jetty]
            [ring.util.response :as response]
            [ring.middleware.session.cookie :as cookie]
            [oauth.twitter :as oauth]
            [clojure.pprint :as pp]
            [smallworld.memoize :as m]
            [smallworld.db :as db]
            [smallworld.util :as util]
            [smallworld.session :as session]
            [cheshire.core :refer [generate-string]]
            [smallworld.user-data :as user-data]
            [clojure.data.json :as json]
            [smallworld.coordinates :as coordinates]))

(def debug? false)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; server ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn set-session [response-so-far new-session]
  (assoc response-so-far
         :session new-session))

(defn get-current-user [req]
  (get-in req [:session :current-user]
          session/blank))

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
    (response/redirect redirect-url)))

;; step 2
(defn store-fetched-access-token-then-redirect-home [req]
  (try (let [oauth-token    (get-in req [:params :oauth_token])
             oauth-verifier (get-in req [:params :oauth_verifier])
             access-token   (oauth/oauth-access-token (util/get-env-var "TWITTER_CONSUMER_KEY") oauth-token oauth-verifier)
             api-response   (fetch-current-user--with-access-token access-token)
             current-user   (user-data/abridged api-response (get-current-user req))
             screen-name    (:screen-name api-response)]
         (when debug?
           (pp/pprint "twitter verify_credentials.json:")
           (pp/pprint api-response)
           (println "screen-name:  " screen-name))
         (db/memoized-insert-or-update! db/access_tokens-table
                                        screen-name
                                        {:access_token access-token}) ; TODO: consider memoizing with an atom for speed
         (db/memoized-insert-or-update! db/profiles-table
                                        screen-name
                                        {:request_key screen-name :data api-response}) ; TODO: consider memoizing with an atom for speed
         (db/insert-or-update! db/settings-table
                               :screen_name
                               {:screen_name screen-name})
         (println (str "@" screen-name ") has successfully authorized small world to access their Twitter account"))
         (set-session (response/redirect "/") {:current-user current-user
                                               :access-token access-token}))
       (catch Throwable e
         (println "user failed to log in")
         (println e)
         (response/redirect "/"))))

(defn logout [req]
  (let [screen-name (:screen-name (get-current-user req))
        logout-msg (if (nil? screen-name)
                     "no-op: there was no active session"
                     (str "@" screen-name " has logged out"))]
    (println logout-msg)
    (set-session (response/redirect "/") {})))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;; settings ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn get-settings [req]
  (let [-current-user (get-current-user req)
        screen-name   (:screen-name -current-user)]
    ; TODO: set in the session for faster access
    (first (db/select-by-col db/settings-table :screen_name screen-name))))

(defn update-settings [req]
  (let [-current-user (get-current-user req)
        screen-name   (:screen-name -current-user)
        parsed-body   (json/read-str (slurp (:body req)) :key-fn keyword)
        new-values    (merge parsed-body {:screen_name screen-name})]
    (when debug?
      (println "update-settings - new-values:")
      (pp/pprint new-values))
    ; TODO: add try-catch to handle failures
    (db/insert-or-update! db/settings-table :screen_name new-values)
    (generate-string new-values)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; twitter data fetching ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;; ; TODO: save all this to the db

(defn --fetch-friends [screen-name] ;; use the memoized version of this function!
  (println "================================================================================================= start")
  (println "fetching friends for " screen-name)

  (try
    (let [; the sql-result should never be an empty list; if it is, that means the
          ; access token was deleted. it shouldn't be possible to get to this state,
          ; but if it does happen at some point, then we may need to add a way for
          ; the user to re-authenticate.
          sql-result   (db/select-by-col db/access_tokens-table :request_key screen-name)
          access-token (get-in (first sql-result) [:data :access_token]) ; TODO: memoize this with an atom for faster, non-db access, a la: (get @access-tokens screen-name)
          client       (oauth/oauth-client (util/get-env-var "TWITTER_CONSUMER_KEY")
                                           (util/get-env-var "TWITTER_CONSUMER_SECRET")
                                           (:oauth-token access-token)
                                           (:oauth-token-secret access-token))]
      (loop [cursor -1 ;; -1 is the first page, while future pages are gotten from previous_cursor & next_cursor
             result-so-far []]

        (let [api-response  (client {:method :get
                                     :url "https://api.twitter.com/1.1/friends/list.json"
                                     :body (str "count=200"
                                                "&cursor=" (str cursor)
                                                "&skip_status=false"
                                                "&include_user_entities=true")})
              page-of-friends (:users api-response)
              new-result      (concat result-so-far page-of-friends)
              screen-names    (map :screen-name (:users api-response))
              next-cursor     (:next-cursor api-response)]

          (println "api-response keys:    " (keys api-response))
          (println "(first screen-names): " (first screen-names))
          (println "(count screen-names): " (count screen-names))
          (println "next-cursor:          " next-cursor)
          (println "new-result count:     " (count new-result))
          (println "-----------------------------------------------------------------------------------------")

          (if (= next-cursor 0)
            new-result ; return final result if Twitter returns a cursor of 0
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

(def abridged-friends-cache (atom {}))
(defn --fetch-abridged-friends [screen-name current-user]
  (map #(user-data/abridged % current-user)
       (:friends (memoized-friends screen-name)))) ; can add (take X) for debugging
(def memoized-abridged-friends
  (m/my-memoize --fetch-abridged-friends abridged-friends-cache))

(defn get-users-friends [req & [screen-name]]
  (let [main-location (or (:main_location_corrected (get-settings req)) "")
        name-location (or (:name_location_corrected (get-settings req)) "")
        corrected-curr-user (merge
                             (get-current-user req)
                             {:main-location main-location
                              :main-coords (coordinates/memoized main-location)
                              :name-coords (coordinates/memoized name-location)
                              :name-location name-location})
        logged-in?    (not= session/blank corrected-curr-user)
        screen-name   (or screen-name (:screen-name corrected-curr-user))
        result        (if logged-in?
                        (memoized-abridged-friends screen-name corrected-curr-user)
                        [])]
    (println (str "count (get-users-friends @" screen-name "): " (count result)))
    (generate-string result)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; admin ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn admin-data [req]
  (let [current-user (get-current-user req)
        screen-name (:screen-name current-user)
        result (if-not (= "devonzuegel" screen-name)
                 (response/bad-request {:message "you don't have access to this page"})
                 {:profiles    (db/select-all db/profiles-table)
                  :settings    (db/select-all db/settings-table)
                  :friends     (map #(-> %
                                         (assoc :friends-count (count (get-in % [:data "friends"])))
                                         (dissoc :data))
                                    (db/select-all db/friends-table))
                  :coordinates (db/select-all db/coordinates-table)})]
    (generate-string result)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; app core ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

; TODO: prepend data endpoints with /api/v1/
;; app is function that takes a request, and returns a response
(defroutes smallworld-routes ; order matters in this function!
  ;; oauth & session endpoints
  (GET "/login"      _   (start-oauth-flow))
  (GET "/authorized" req (store-fetched-access-token-then-redirect-home req))
  (GET "/session"    req (generate-string (get-current-user req)))
  (GET "/logout"     req (logout req))

  ;; admin endpoints
  (GET "/admin/summary" req (admin-data req))
  (GET "/admin/friends/:screen_name" req (fn [{params :params}]
                                           (let [curr-user-screen-name (:screen-name (get-current-user req))
                                                 target-screen-name    (:screen_name params)]
                                             (println "params:             " params)
                                             (println "target-screen-name: " target-screen-name)
                                             (if-not (= "devonzuegel" curr-user-screen-name)
                                               (response/bad-request {:message "you don't have access to this page"})
                                               (get-users-friends req target-screen-name)))))

  ;; app data endpoints
  (GET "/settings" req (generate-string (get-settings req)))
  (POST "/settings/update" req (update-settings req))
  (POST "/coordinates" req (let [parsed-body (json/read-str (slurp (:body req)) :key-fn keyword)
                                 location-name (:location-name parsed-body)]
                             (println "parsed-body:")
                             (pp/pprint parsed-body)
                             (println "location-name: " location-name)
                             (generate-string (coordinates/memoized location-name))))
  (GET "/friends" req (get-users-friends req))
  ; without fetching data from Twitter, recompute distances from new locations
  (GET "/friends/recompute" req (let [screen-name  (:screen-name (get-current-user req))
                                      friends-full (:friends (memoized-friends screen-name))
                                      settings     (get-settings req)
                                      corrected-curr-user (merge
                                                           (get-current-user req)
                                                           {:main-location (:main_location_corrected settings)
                                                            :main-coords (coordinates/memoized (or (:main_location_corrected settings) ""))
                                                            :name-coords (coordinates/memoized (or (:name_location_corrected settings) ""))
                                                            :name-location (:name_location_corrected settings)})
                                      friends-abridged (map #(user-data/abridged % corrected-curr-user) friends-full)]
                                  (println (str "recomputed friends distances for @" screen-name " (count: " (count friends-abridged) ")"))
                                  (swap! abridged-friends-cache
                                         assoc screen-name friends-abridged)
                                  (generate-string friends-abridged)))
  ; re-fetch data from Twitter
  (GET "/friends/refresh" req (let [screen-name      (:screen-name (get-current-user req))
                                    friends-result   (--fetch-friends screen-name)
                                    settings         (get-settings req)
                                    corrected-curr-user (merge
                                                         (get-current-user req)
                                                         {:main-location (:main_location_corrected settings)
                                                          :main-coords (coordinates/memoized (or (:main_location_corrected settings) ""))
                                                          :name-coords (coordinates/memoized (or (:name_location_corrected settings) ""))
                                                          :name-location (:name_location_corrected settings)})
                                    friends-abridged (map #(user-data/abridged % corrected-curr-user)
                                                          friends-result)]
                                (if (= :failed friends-result)
                                  (let [failure-message (str "could not refresh friends for @" screen-name)]
                                    (println failure-message)
                                    (generate-string (response/bad-request {:message failure-message})))
                                  (do
                                    (println (str "refreshing friends for @" screen-name
                                                  " (count: " (count friends-abridged) ")"))
                                    (db/update! db/friends-table :request_key screen-name {:data {:friends friends-result}})
                                    (swap! abridged-friends-cache
                                           assoc screen-name friends-abridged)
                                    (generate-string friends-abridged)))))
  ;; general resources
  (GET "/css/mapbox-gl.inc.css" [] (io/resource "cljsjs/mapbox/production/mapbox-gl.inc.css"))
  (route/resources "/")
  (ANY "*"                      [] (io/resource "public/index.html")))

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
  (-> smallworld-routes
      ssl-redirect
      www-redirect
      (compojure.handler/site {:session
                               {:cookie-name "small-world-session"
                                :store (cookie/cookie-store
                                        {:key (util/get-env-var "COOKIE_STORE_SECRET_KEY")})}})))

(defonce server* (atom nil))

(defn start! [port]
  (some-> @server* (.stop))

  ; create the tables if they don't already exists
  (db/create-table db/settings-table      db/settings-schema)
  (db/create-table db/profiles-table      db/memoized-data-schema)
  (db/create-table db/friends-table       db/memoized-data-schema)
  (db/create-table db/coordinates-table   db/memoized-data-schema)
  (db/create-table db/access_tokens-table db/memoized-data-schema)

  (let [port (Integer. (or port (util/get-env-var "PORT") 5000))
        server (jetty/run-jetty #'app-handler {:port port :join? false})]

    (reset! server* server)))

(defn stop! []
  (if @server*
    (.stop @server*)
    (println "@server* is nil – no server to stop")))

(defn -main []
  (let [default-port 8080
        port (System/getenv "PORT")
        port (if (nil? port)
               (do (println "PORT not defined. Defaulting to" default-port)
                   default-port)
               (Integer/parseInt port))]
    (println "\nsmall world is running on" (str "http://localhost:" port) "\n")
    (start! port)))