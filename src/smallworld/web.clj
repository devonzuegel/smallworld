(ns smallworld.web
  (:gen-class)
  (:require [cheshire.core :refer [generate-stream generate-string]]
            [clojure.data.json :as json]
            [clojure.java.io :as io]
            [clojure.pprint :as pp]
            [clojure.set :as set]
            [clojure.string :as str]
            [compojure.core :as compo :refer [ANY defroutes GET POST]]
            [compojure.handler :as compojure-handler]
            [compojure.route :as route]
            [oauth.twitter :as oauth]
            [ring.adapter.jetty :as jetty]
            [ring.middleware.session.cookie :as cookie]
            [ring.util.io :as ring-io]
            [ring.util.request :as ring-request]
            [ring.util.response :as ring-response]
            [meetcute.routes :as mc.routes]
            [ketchup.routes]
            [smallworld.admin :as admin]
            [smallworld.coordinates :as coordinates]
            [smallworld.db :as db]
            [smallworld.email :as email]
            [smallworld.memoize :as m]
            [smallworld.mocks :as mocks]
            [smallworld.session :as session]
            [smallworld.user-data :as user-data]
            [smallworld.util :as util]
            [meetcute.logic :as logic]
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
        screen-name  (:screen-name session-data)
        result       (if (admin/is-admin {:screen-name screen-name})
                       (let [new-screen-name (:screen_name (db/select-first db/impersonation-table))]
                         (if (nil? new-screen-name)
                           session-data
                           {:screen-name new-screen-name :impersonation? true}))
                       session-data)]
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
    (ring-response/redirect redirect-url)))

(defn location-sort-order [location]
  (case (:special-status location)
    "twitter-location"  1
    "from-display-name" 2
    "added-manually"    3
    3))

;; step 2
(defn store-fetched-access-token-then-redirect-home [req]
  (try (let [oauth-token    (get-in req [:params :oauth_token])
             oauth-verifier (get-in req [:params :oauth_verifier])
             access-token   (oauth/oauth-access-token (util/get-env-var "TWITTER_CONSUMER_KEY") oauth-token oauth-verifier)
             api-response   (fetch-current-user--with-access-token access-token)
             current-user   (user-data/abridged api-response {:screen-name (:screen-name api-response)})
             screen-name    (:screen-name api-response)]
         (when debug?
           (pp/pprint "twitter verify_credentials.json =============================================")
           (pp/pprint api-response)
           (println "current-user ==================================================================")
           (pp/pprint current-user))
         (db/memoized-insert-or-update! db/access_tokens-table    screen-name {:access_token access-token}) ; TODO: consider memoizing with an atom for speed
         (db/memoized-insert-or-update! db/twitter-profiles-table screen-name {:request_key screen-name :data api-response}) ; TODO: consider memoizing with an atom for speed
         (let [sql-results (db/select-by-col db/settings-table :screen_name screen-name)
               exists?     (not= 0 (count sql-results))
               new-settings {:screen_name    screen-name
                             :email_address  (:email api-response)
                             :name           (:name api-response)
                             :locations      (:locations current-user)
                             :twitter_avatar (user-data/normal-img-to-full-size api-response)}]
           (if-not exists?
             ; if user doesn't exist, create a new row with the new settings
             (db/insert! db/settings-table new-settings)
             ; else, update their locations to include the new locations
             (let [old-locations (set (:locations (first sql-results)))
                   new-locations (set (:locations new-settings))
                   merged-locations (->> (set/union new-locations old-locations)
                                         (sort-by location-sort-order))]
               (db/update! db/settings-table :screen_name screen-name (assoc new-settings :locations merged-locations))
               (when debug?
                 (println "---- old-locations: ----------------------")
                 (pp/pprint old-locations)
                 (println "---- new-locations: ----------------------")
                 (pp/pprint new-locations)
                 (println "---- merged-locations: -------------------")
                 (pp/pprint merged-locations)
                 (println "------------------------------------------")))))
         (log-event "new-authorization" {:screen-name screen-name
                                         :message (str "@" screen-name ") has successfully authorized small world to access their Twitter account")})
         (set-session (ring-response/redirect "/") {:access-token access-token
                                                    :screen-name  (:screen-name api-response)}))
       (catch Throwable e
         (println "user failed to log in")
         (println e)
         (ring-response/redirect "/"))))

(defn logout [req]
  (let [screen-name (:screen-name (get-session req))
        logout-msg (if (nil? screen-name)
                     "no-op: there was no active session"
                     (str "@" screen-name " has logged out"))]
    (log-event "logout" {:screen-name screen-name
                         :message logout-msg})
    (set-session (ring-response/redirect "/signin") {})))

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
    (ring-response/response (generate-string new-settings))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; twitter data fetching ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def max-results (* 15 200))

(defn fetch-friends-from-twitter [screen-name] ;; use the memoized version of this function!
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
          (if (or (= next-cursor 0) (= max-results (count new-result)))
            (do (log-event "fetch-twitter-friends--end" {:screen-name screen-name
                                                         :cursor cursor
                                                         :result-count (count new-result)})
                (when (= max-results (count new-result))
                  (log-event "fetch-twitter-friends--max-results" {:screen-name screen-name
                                                                   :details "stopped requesting friends before we were done because we'll get rate otherwise"}))
                new-result) ; return final result if Twitter returns a cursor of 0
            (recur next-cursor new-result)))))
    (catch Throwable e
      (println "🔴 caught exception when getting friends for screen-name:" screen-name)
      (when (= 429 (get-in e [:data :status])) (println "🔴🔴🔴 you hit the Twitter rate limit!"))
      (println (pr-str e))
      nil #_failure)))

(def memoized-friends (m/my-memoize
                       (fn [screen-name]
                         (let [friends-result (fetch-friends-from-twitter screen-name)]
                           (if (nil? friends-result)
                             :failed
                             {:friends friends-result})))
                       db/friends-table))

; TODO: consolidate this with memoized-friends
(defn get-users-friends--not-memoized [req writer]
  (let [screen-name (:screen-name (get-session req))
        logged-out? (nil? screen-name)]
    (if logged-out?
      (generate-stream [] writer)
      (let [current-user (get-settings req screen-name)
            friends (get-in (first (db/select-by-col db/friends-table :request_key screen-name))
                            [:data :friends])
            result (map #(user-data/abridged % current-user) friends)]
        (generate-stream result writer)))))

(defn select-location-fields [friend]
  (select-keys friend [:location #_:name :screen-name]))

(defn select-user-fields [user]
  (select-keys user [:created_at
                     :name
                     :screen_name
                     :email_address
                     :phone
                     :email_notifications
                     :last_ping
                     :status
                     :email_address
                     :updated_at]))

(defn highlight [highlighted-str]
  (str "<span style=\"background: white; margin: 2px 4px; white-space: nowrap; "
       "padding: 0 8px;  display: inline-block; border-radius: 12px; "
       "box-shadow: 0 4px 6px rgba(50, 50, 93, 0.05), 0 1px 6px rgba(0, 0, 0, 0.05);\">"
       highlighted-str
       "</span>"))

(defn html-line-result [screen-name _type before after]
  (str "<li><b><a href='https://twitter.com/" screen-name "'>@" screen-name "</a>:</b> "
       (highlight before) " → " (highlight after)
       "<br/></li>")
  #_(str "<li><b>@" screen-name "</b> updated their " type ": "
         (highlight before) " → " (highlight after)
         "<br/></li>"))

(defn get-coordinate [location]
  (let [result (db/get-coordinate location)
        lat-lng (:data (first result))]
    (if (or (empty? result) (nil? (:lat lat-lng)) (nil? (:lng lat-lng)))
      nil
      lat-lng)))

(defn map-assoc-coordinates [list-of-friends]
  (map #(assoc % :coordinates (get-coordinate (:location %))) list-of-friends))

(def debug-refresh-friends-from-twitter? false)

(defn is-close [min-distance]
  (fn [location-pair]
    (let [loc1 (get-in location-pair [0 :location])
          loc2 (get-in location-pair [1 :location])
          lat1 (get-in location-pair [0 :coordinates :lat])
          lng1 (get-in location-pair [0 :coordinates :lng])
          lat2 (get-in location-pair [1 :coordinates :lat])
          lng2 (get-in location-pair [1 :coordinates :lng])]

      ;; (pp/pprint location-pair)
      ;; (println loc1 "→" loc2)
      ;; (pp/pprint [lat1 lng1 lat2 lng2])
      ;; (println "")
      ;; (println "    " (get-in location-pair [0 :location])) ;"     " lat1 " " lng1)
      ;; (println "    " (get-in location-pair [1 :location])) ;"     " lat2 " " lng2)
      (if (util/none-nil? lat1 lng1 lat2 lng2)
        (let [distance-in-miles  (user-data/coord-distance-miles [lat1 lng1] [lat2 lng2])]
          (println "    " distance-in-miles "miles apart")
          (< distance-in-miles min-distance))
        false))))

(def radius-in-miles 2000) ; TODO: probably want to make this radius smaller at some point

(defn not-near-any-of-my-locations [my-locations settings]
  (fn [[their-location1 their-location2]]
    (case (:email_notifications settings)
      "city-specific" (let [my-their-location-pairs (mapcat (fn [my-location]
                                                              (let [my-location {:location    (:name my-location) ; TODO: reconcile this schema with the one used for friends so that we don't need this translation
                                                                                 :coordinates (:coords my-location)}]
                                                                [[my-location their-location1]
                                                                 [my-location their-location2]]))
                                                            my-locations)]
                              ;; (println "\nmy-locations: =============================================")
                              ;; (pp/pprint my-locations)
                              ;; (println "\ntheir-location1: ==========================================")
                              ;; (pp/pprint their-location1)
                              ;; (println "\ntheir-location2: ==========================================")
                              ;; (pp/pprint their-location2)
                              ;; (println "\nmy-their-location-pairs: ==================================")
                              ;; (pp/pprint my-their-location-pairs)
                              ;; (println "")
                        (not-any? #((is-close radius-in-miles) %) my-their-location-pairs))
      false ; by default, show all locations; i.e. don't to filter out any of them
      )))

(defn refresh-friends-from-twitter [settings i total-count] ; optionally pass in settings in case it's already computed so that we don't have to recompute
  (let [settings (if debug-refresh-friends-from-twitter? mocks/settings settings)
        screen-name (:screen_name settings)
        old-friends (map-assoc-coordinates
                     (if debug-refresh-friends-from-twitter?
                       [{:screen-name "A" :location "SF"}
                        {:screen-name "B" :location "NYC"}
                        {:screen-name "C" :location "-"}
                        {:screen-name "D" :location "SF"}
                        {:screen-name "E" :location "London"}
                        {:screen-name "F" :location "New York"}
                        {:screen-name "G" :location "SF"}
                        {:screen-name "H" :location "San Antonio ish"}
                        {:screen-name "I" :location "Brooklyn, NY"}]
                       (map ; fetch the old friends before friends gets updated from the twitter fetch
                        select-location-fields
                        (-> db/friends-table
                            (db/select-by-col :request_key screen-name)
                            vec
                            (get-in [0 :data :friends])))))
        friends-result  (if debug-refresh-friends-from-twitter?
                          [(merge mocks/raw-twitter-friend {:screen-name "A" :location "San Francisco"})
                           (merge mocks/raw-twitter-friend {:screen-name "B" :location "-"})
                           (merge mocks/raw-twitter-friend {:screen-name "C" :location "--"})
                           (merge mocks/raw-twitter-friend {:screen-name "D" :location "Miami"})
                           (merge mocks/raw-twitter-friend {:screen-name "E" :location "Paris"})
                           (merge mocks/raw-twitter-friend {:screen-name "F" :location "New York"})
                           (merge mocks/raw-twitter-friend {:screen-name "G" :location "Oakland"})
                           (merge mocks/raw-twitter-friend {:screen-name "H" :location "San Antonio"})
                           (merge mocks/raw-twitter-friend {:screen-name "I" :location "Brooklyn"})]
                          (fetch-friends-from-twitter screen-name))]
    ;; (println "count old-friends for " screen-name ":" (count old-friends))
    ;; (pp/pprint old-friends)
    (if (nil? friends-result)
      (println "🔴 Error fetching friends from Twitter for screen-name:" screen-name)
      (let [curr-user-locations (map util/str-keys-to-keywords (:locations settings))
            curr-user-info {:screen-name screen-name
                            :locations curr-user-locations}
            friends-abridged (map #(user-data/abridged % curr-user-info) friends-result)
            new-friends (map-assoc-coordinates (map select-location-fields (vec friends-result)))
            diff-all (->> old-friends
                          set
                          (set/difference (set new-friends))
                          (concat (set/difference (set old-friends) (set new-friends)))
                          (group-by :screen-name)
                          vals
                          vec
                          (remove #(and (nil? (first %)) (nil? (second %)))) ; remove if both are nil
                          (remove #(and (= (get-in % [0 :coordinates]) nil)
                                        (= (get-in % [1 :coordinates]) nil))) ; remove entries where neither location has coordinates
                          (remove #(= (get-in % [0 :screen-name]) "levelsio")) ; this user has some sort of programmatic saying that updates that location every day, take them out so that people don't get spammed
                          (remove #(str/includes? (get-in % [0 :location]) "subscribe")) ; remove users who have "subscribe" in their location
                          (remove #(str/includes? (get-in % [1 :location]) "subscribe"))
                          (remove #(str/includes? (get-in % [0 :location]) ".com")) ; remove users who have ".com" in their location
                          (remove #(str/includes? (get-in % [1 :location]) ".com"))
                          (remove #(str/includes? (get-in % [0 :location]) "http")) ; remove users who have "http" in their location
                          (remove #(str/includes? (get-in % [1 :location]) "http"))
                          (remove #(str/includes? (get-in % [0 :location]) "www")) ; remove users who have "www" in their location
                          (remove #(str/includes? (get-in % [1 :location]) "www")))
            diff-filtered (->> diff-all
                               (remove #(coordinates/very-similar-location-names [(:location (first %)) (:location (second %))]))
                               (remove (is-close 2)) ; remove locations that are very close to each other, implying that it's not a major update
                               (remove (not-near-any-of-my-locations curr-user-locations settings)))
            diff-html (if (= 0 (count diff-filtered)) ; this branch shouldn't be called, but defining the behavior just in case
                        "none of your friends have updated their Twitter location or display name!"
                        (str "<ul>"
                             (->> diff-filtered
                                  (map (fn [friend]
                                         (let [before (first friend)
                                               after  (second friend)
                                          ;;  name-before     (if (str/blank? (:name before))     "[blank]" (:name before))
                                          ;;  name-after      (if (str/blank? (:name after))      "[blank]" (:name after))
                                               location-before (if (str/blank? (:location before)) "[blank]" (:location before))
                                               location-after  (if (str/blank? (:location after))  "[blank]" (:location after))
                                               screen-name (:screen-name after)]
                                           [; put the following back if we decide to notify on name updates again
                                            #_(when (not= (:name before) (:name after))
                                                (html-line-result screen-name "name" name-before name-after))
                                            (when (not= (:location before) (:location after))
                                              (html-line-result screen-name "location" location-before location-after))])))
                                  flatten
                                  (remove nil?)
                                  str/join)
                             "</ul>"))]

        (if (nil? friends-result)
          (let [failure-message (str "could not refresh friends for @" screen-name)]
            (log-event "refresh-twitter-friends--failed" {:screen-name screen-name
                                                          :failure-message failure-message})
            (generate-string (ring-response/bad-request {:message failure-message})))
          (let [email-address (:email_address settings)]
            (db/update-twitter-last-fetched! screen-name)
            (log-event "refreshed-twitter-friends--success" {:screen-name screen-name
                                                             :diff-count  (count diff-filtered)
                                                             :diff-html   diff-html
                                                             :email_notifications (:email_notifications settings)
                                                             :send-email? (and (= "daily" (:email_notifications settings))
                                                                               (not-empty diff-filtered))})
            ;; (pp/pprint "old-friends: ==============================================")
            ;; (pp/pprint old-friends)
            ;; (pp/pprint "new-friends: ==============================================")
            ;; (pp/pprint new-friends)
            (println "\n\n")
            (pp/pprint "\ncurr-user-info: ===========================================")
            (pp/pprint curr-user-info)
            (pp/pprint "\ndiff-all – diff-filtered: ============================================")
            (pp/pprint (set/difference (set diff-all) (set diff-filtered)))
            (pp/pprint "\ndiff-filtered: ============================================")
            (pp/pprint diff-filtered)
            (pp/pprint "\ndiff-all: =================================================")
            (pp/pprint diff-all)
            ;; (pp/pprint "settings: =====================================================")
            ;; (pp/pprint settings)
            ;; (pp/pprint "friends-abridged: =========================================")
            ;; (pp/pprint friends-abridged)
            ;; (pp/pprint "friends-result: ==========================================")
            ;; (pp/pprint friends-result)
            ;; (println (str "\n\nhere is the result from twitter"))
            ;; (pp/pprint friends-result)
            ;; (println (str "\n\nhere are @" screen-name "'s friends that changed: (diff-filtered)"))
            ;; (pp/pprint diff-filtered)
            ;; (println (str "\n\nhere is the generated HTML:"))
            ;; (pp/pprint diff-html)
            (println "\n\n")

            (email/send-email {:to "avery.sara.james@gmail.com"
                               :subject (str "@" screen-name "'s friends on the move [" i "/" total-count "]")
                              ;;  :type "text/plain"
                               :body (str "<pre>"
                                          "radius-in-miles = " radius-in-miles "\n"
                                          "# my-locations  = " (count (:locations curr-user-info)) "\n"
                                          "\n"
                                          "(:email_notifications settings) = " (:email_notifications settings) "\n"
                                          "diff-filtered      = " (count diff-filtered) "\n"
                                          "diff-all           = " (count diff-all) "\n"
                                          "\n"
                                          "my-locations: ============================\n\n"
                                          (str/join "\n " (map (fn [l] (str (:name l) " (" (:lat (:coords l)) ", " (:lng (:coords l)) ")"))
                                                               (:locations curr-user-info))) "\n"
                                          "\n"
                                          "diff-all – diff-filtered: ===========================\n"
                                          (with-out-str (pp/pprint (map (fn [pair] [(:location (first pair)) (:location (second pair))])
                                                                        (set/difference (set diff-all) (set diff-filtered)))))
                                          "\n"
                                          "diff-filtered: ===========================\n"
                                          (with-out-str (pp/pprint (map (fn [pair] [(:location (first pair))
                                                                                    (:location (second pair))])
                                                                        diff-filtered))) "\n"
                                          "\n"
                                          "diff-all: ================================\n"
                                          (with-out-str (pp/pprint (map (fn [pair] [(:location (first pair))
                                                                                    (:location (second pair))])
                                                                        diff-all))) "\n\n"
                                          "</pre>")})

            (when (and (or (= "daily"         (:email_notifications settings))
                           (= "city-specific" (:email_notifications settings)))
                       (not-empty diff-filtered))
              (println "sending email to" screen-name "now: =============")
              (if debug-refresh-friends-from-twitter?
                (println "SKIPPED EMAIL SEND – TESTING")
                (email/send-email {:to email-address
                                   :template (:friends-on-the-move email/TEMPLATES)
                                   :dynamic_template_data {:twitter_screen_name screen-name
                                                           :friends             diff-html}})))
            (db/update! db/friends-table :request_key screen-name {:data {:friends friends-result}})
            (when debug? (println (str "done refreshing friends for @" screen-name
                                       " (friends count: " (count friends-abridged) ")")))
            (generate-string friends-abridged)))))))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; worker ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;


(def failures  (atom []))
(def skipped   (atom []))
(def refetched (atom []))

(defn refreshed-in-last-day? [user]
  #_false ; for debugging
  (let [last-fetched (inst-ms (:twitter_last_fetched user)) #_(first (db/select-by-col db/settings-table :screen_name screen-name))
        now (inst-ms (java.time.Instant/now))
        difference (- now last-fetched)]
    (< difference 86400000))) ; 86400000 ms = 1 day

(defn try-to-refresh-friends [total-count]
  (fn [i user]
    (let [screen-name (:screen_name user)
          user-abridged (select-keys user [:screen_name :email_address :name])
          n-per-cycle 20]
      (if (refreshed-in-last-day? user) ;; user hasn't been refreshed in the last 24 hours
        (do
          (println "🟠 skipping because they've been refreshed in the last 24 hours: " screen-name)
          (swap! skipped conj user-abridged))
        (if (>= (count @refetched) n-per-cycle)
          (println "🟡 skipping because we've already refetched " n-per-cycle " users in this cycle: " screen-name)
          (try
            (println "🟢 refreshing because they haven't been refreshed in the last 24 hours: " screen-name)
            (util/log (str "[user " i "/" total-count "] refresh friends for " screen-name))
            (let [result (refresh-friends-from-twitter user i total-count)]
              ; this is a hack :) it will be fragile if the error message ever changes
              (if (str/starts-with? result "caught exception")
                (throw (Throwable. result))
                (do (db/update-twitter-last-fetched! screen-name)
                    (swap! refetched conj user-abridged))))
            (catch Throwable e
              (println "\ncouldn't refresh friends for user" screen-name)
              (swap! failures conj user-abridged)
              (println e)
              nil)))))))

(defn email-update-worker []
  (println "\n===============================================")
  (util/log "starting email-update worker")
  (println)
  (let [all-users   (shuffle (db/select-all db/settings-table))
                    ; (db/select-by-col :settings :screen_name "devonzuegel")
                    ; (take-last 205 (db/select-all db/settings-table))
        n-users     (count all-users)
        curried-refresh-friends (try-to-refresh-friends n-users)]
    (println "found" n-users "users... refreshing their friends now...")
    (log-event "email-update-worker-start" {:count   n-users
                                            :message (str "preparing to refresh friends for " n-users " users\n")})
    (doall (map-indexed curried-refresh-friends all-users))
    (System/gc)
    (log-event "garbage-collection" {:details "cleaning up after the email-update worker"})
    (log-event "email-update-worker-done" {:count   n-users
                                           :message (str "finished refreshing friends for " n-users " users")})
    (let [to-print (str "finished iterating through " n-users " users.\n\n"
                        (count @failures)  " failures\n\n"
                        (count @refetched) " refetched\n\n"
                        (count @skipped)   " skipped\n\n----------\n\n"
                        "users that failed:\n" (with-out-str (pp/pprint @failures)) "\n\n"
                        "users refetched:\n"   (with-out-str (pp/pprint @refetched)))]
      (println to-print)

      ;; Removing this because it's too noisy
      (when (= (:prod util/ENVIRONMENTS) (util/get-env-var "ENVIRONMENT"))
        (email/send-email {:to "avery.sara.james@gmail.com"
                           :subject (str "[" (util/get-env-var "ENVIRONMENT") "] worker.clj refetched " (count @refetched) " users")
                           :type "text/plain"
                           :body to-print})
        (reset! failures [])
        (reset! skipped [])
        (reset! refetched []))))

  (println "\n===============================================\n"))

(defn worker-endpoint [req]
  (if-not (admin/is-admin (get-session req))
    (generate-string (ring-response/bad-request {:message "you don't have access to this page"}))
    (email-update-worker)))

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
  (GET "/api/v1/admin/refresh_all_users_friends" req (admin/refresh-all-users-friends (get-session req) log-event email-update-worker))

  ;; app data endpoints
  (GET "/api/v1/settings" req (generate-string (get-settings req (:screen-name (get-session req)))))
  (POST "/api/v1/settings/update" req (update-settings req))
  (POST "/api/v1/coordinates" req (let [parsed-body (json/read-str (slurp (:body req)) :key-fn keyword)
                                        raw-location-name (:location-name parsed-body)
                                        normalized-location-name (user-data/normalize-location raw-location-name)]
                                    (generate-string (coordinates/memoized normalized-location-name))))
  (GET "/api/v1/friends" req (ring-response/response (ring-io/piped-input-stream
                                                      (fn [input-stream]
                                                        (let [writer (io/make-writer input-stream {})]
                                                          (get-users-friends--not-memoized req writer))))))
  ; recompute distances from new locations, without fetching data from Twitter
  (GET "/api/v1/friends/recompute-locations" req (let [screen-name  (:screen-name (get-session req))
                                                       friends-full (:friends (memoized-friends screen-name))
                                                       settings     (get-settings req screen-name)
                                                       corrected-curr-user (merge (get-session req)
                                                                                  {:locations (:locations settings)})
                                                       friends-abridged (map #(user-data/abridged % corrected-curr-user) friends-full)]
                                                   (println (str "recomputed friends distances for @" screen-name " (count: " (count friends-abridged) ")"))
                                                   (generate-string friends-abridged)))
  ; re-fetch data from Twitter – TODO: this should be a POST not a GET
  (GET "/api/v1/friends/refetch-twitter" req (let [screen-name (:screen-name (get-session req))
                                                   settings    (first (db/select-by-col db/settings-table :screen_name screen-name))]
                                               (refresh-friends-from-twitter settings nil nil))) ; TODO: keep refactoring
  (GET "/api/v1/worker" req (worker-endpoint req))

  (compo/context "/ketchup" []
    ketchup.routes/app)

  ;; meetcute routes:
  (compo/context "/meetcute" []
    mc.routes/app)

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
  (-> (ring-response/redirect (https-url (ring-request/request-url request)))
      ; responding 301 to a POST changes it to a GET, because 301 is older & 307 is newer, so we need to respond to POST requests with a 307
      (ring-response/status   (if (get-request? request) 301 307))))

; redirect any HTTP request to the equivalent HTTPS url
(defn ssl-redirect [handler]
  ; note: we also have a setting in Cloudflare that forces SSL, so if you remove
  ; this, you'll probably still get an SSL redirect
  (fn [request]
    (let [url     (ring-request/request-url request)
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
      (if (or  (str/includes? host "localhost") ; don't redirect localhost (it doesn't support SSL)
               (= "https" (headers "x-forwarded-proto"))   ; don't redirect if already HTTPS
               (= :https  (:scheme request)))
        (handler request)
        (ssl-redirect-response request)))))

; redirect any `www.smallworld.kiwi` request to the equivalent raw domain `smallworld.kiwi` url
(defn www-redirect [handler & [port]]
  (fn [request]
    (let [url  (java.net.URL. (ring-request/request-url request))
          host (.getHost url)]

      (if (= host "www.smallworld.kiwi")
        (ring-response/redirect (str (java.net.URL. "https" "smallworld.kiwi" (or port -1) (.getFile url))))
        (handler request)))))

(defn trim-trailing-slash [handler]
  (fn [request]
    (let [uri       (request :uri)
          clean-uri (if (and (not= "/" uri) (.endsWith uri "/"))
                      (subs uri 0 (- (count uri) 1))
                      uri)]
      (if (= uri clean-uri)
        (handler request)
        (ring-response/redirect clean-uri)))))


(def one-year-in-seconds (* 60 #_seconds 60 #_minutes 24 #_hours 365 #_days))

(def app-handler
  (-> smallworld-routes       ; takes a request, returns response
      ssl-redirect            ; middleware: takes a handler, returns a handler
      www-redirect            ; middleware: takes a handler, returns a handler
      trim-trailing-slash     ; middleware: takes a handler, returns a handler
      (compojure-handler/site ; middleware: takes a handler, returns a handler
       {:session {:cookie-name "small-world-session"
                  :cookie-attrs {:max-age one-year-in-seconds} ; Safari requires max-age, not expiry: https://www.reddit.com/r/webdev/comments/jfk6t8/setting_cookie_expiry_date_always_defaults_to/g9kqnh5
                  :store (cookie/cookie-store
                          {:key (util/get-env-var "COOKIE_STORE_SECRET_KEY")})}})))

(def meetcute-job-id        (atom nil))
(def email-update-worker-id (atom nil))
(def garbage-collection-id  (atom nil))

(defn end-schedule []
  (println "ending email update worker schedule:" @email-update-worker-id)
  (timely/end-schedule @email-update-worker-id)
  (reset! email-update-worker-id nil))

(defonce server* (atom nil))

(defn start! [port]
  (some-> @server* (.stop))
  ; create the tables if they don't already exist
  ; SMALL WORLD tables:
  (comment
    (db/create-table db/settings-table         db/settings-schema)

    (db/create-table db/twitter-profiles-table db/twitter-profiles-schema)

    (db/create-table db/friends-table          db/friends-schema)

    (db/create-table db/coordinates-table      db/coordinates-schema)

    (db/create-table db/access_tokens-table    db/access-tokens-schema)

    (db/create-table db/impersonation-table    db/impersonation-schema)

    ; KETCHUP CLUB tables:
    (db/create-table db/users-table            db/users-schema))

  (let [port (Integer. (or port (util/get-env-var "PORT") 5000))
        server (jetty/run-jetty #'app-handler {:port port :join? false})]

    (reset! server* server)))

(defn stop! []
  (if @email-update-worker-id
    (end-schedule)
    (println "@email-update-worker-id is nil – no schedule to end"))
  (if @garbage-collection-id
    (end-schedule)
    (println "@garbage-collection-id is nil – no schedule to end"))
  (if @server*
    (.stop @server*)
    (println "@server* is nil – no server to stop")))

; this is a hack – there was a memory leak somewhere, so we run a GC to clean up.
; I *think* the source of the memory leak was an atom that I removed a while back,
; but I'm just putting this in here to be sure.  if this project ever becomes
; "serious", I'll come back to it to find the memory leak and remove this worker.
(defn garbage-collection-worker []
  (System/gc)
  #_(log-event "garbage-collection" {}))

(defn start-scheduled-workers []

  (try (timely/start-scheduler)
       (catch Exception e
         (if (= (:cause (Throwable->map e)) "Scheduler already started")
           (println "scheduler already started") ; it's fine, this isn't a real error, so just continue
           (throw e))))


  ; TODO: put me back!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
  ; TODO: put me back!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
  ; TODO: put me back!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
  #_(let [mins 10
          id (timely/start-schedule
              (timely/scheduled-item (timely/every mins :minutes)
                                     #(do
                                        (util/log "🍊 Starting the MeetCute job: refresh-todays-cutie-route-all")
                                        (util/log (str "     note: this job runs every " mins " minutes"))
                                        (logic/refresh-todays-cutie-route-all nil))))]
      (reset! meetcute-job-id id)
      (println)
      (println "🍊 started MeetCute job with id:" @meetcute-job-id))


  ;; (println "starting scheduler to run every 10 minutes")
  (println "🥝 Not starting email-update-worker, there's no point since the Twitter API was effectively shut down"))

; start the email-update worker that refreshes users' twitter info/friends
(let [env (util/get-env-var "ENVIRONMENT")]
  (if (= env (:prod util/ENVIRONMENTS))
    (println "\nnot starting email update worker because Twitter API access is revoked so it wouldn't work anyways")
    #_(let [id (timely/start-schedule
                (timely/scheduled-item (timely/every 8 :minutes) email-update-worker))]
        (reset! email-update-worker-id id)
        (println "\nstarted email update worker with id:" @email-update-worker-id))
    (println "\nnot starting email update worker because ENVIRONMENT is" env "not" (:prod util/ENVIRONMENTS))))

  ; start garbage collection worker
(let [id (timely/start-schedule
          (timely/scheduled-item (timely/each-minute) garbage-collection-worker))]
  (reset! garbage-collection-id id)
  (println "\nstarted garbage collection worker with id:" @garbage-collection-id))

(defn -main []
  (start-scheduled-workers)

  (println "\nstarting server...")
  (let [default-port 3001
        port (System/getenv "PORT")
        port (if (nil? port)
               (do (println "PORT not defined. Defaulting to" default-port)
                   default-port)
               (Integer/parseInt port))]
    (println "\nsmall world is running on" (str "http://localhost:" port) "\n")
    (start! port)))
