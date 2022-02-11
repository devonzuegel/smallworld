(ns smallworld.web
  (:gen-class)
  #_{:clj-kondo/ignore [:deprecated-var]}
  (:require [compojure.core :refer [defroutes GET ANY]]
            [compojure.handler]
            [compojure.route :as route]
            [clojure.java.io :as io]
            [ring.adapter.jetty :as jetty]
            [ring.util.response :as response]
            [ring.middleware.session.cookie :as cookie]
            [ring.middleware.session :as msession] ;; documentation: https://www.baeldung.com/clojure-ring
            [oauth.twitter :as oauth]
            [clojure.pprint :as pp]
            [smallworld.memoize :as m]
            [smallworld.db :as db]
            [smallworld.coordinates :as coord-utils]
            [smallworld.util :as util]
            [smallworld.current-user :as cu]
            [clojure.data.json :as json]
            [clojure.string :as str]
            [cheshire.core :refer [generate-string]]
            [environ.core :refer [env]]))

(def debug? false)

;; if you need to make the locations more precise in the future, use the bbox
;; (bounding box) rather than just the coordinates
(defn extract-coordinates [raw-result]
  (let [result      (json/read-str raw-result)
        status-code (get result "statusCode")
        coordinates (get-in result ["resourceSets" 0 "resources" 0 "geocodePoints" 0 "coordinates"])
        coordinates {:lat (first coordinates) :lng (second coordinates)}]
    (if (= 200 status-code) coordinates
        (print "houston, we have a problem..."))))

(defn get-coordinates-from-city [city-str]
  (if (or (empty? city-str) (nil? city-str))
    (do (when debug? (println "city-str:" city-str))
        nil ; return nil coordinates if no city string is given TODO: return :no-result
        )
    (try
      (let [city    (java.net.URLEncoder/encode (or city-str "") "UTF-8") ;; the (if empty?) shouldn't caught the nil string thing... not sure why it didn't
            api-key (java.net.URLEncoder/encode (util/get-env-var "BING_MAPS_API_KEY") "UTF-8")]
        (when debug?
          (println "city:" city)
          (println "api-key:" api-key)
          (println ""))
        (-> (str "https://dev.virtualearth.net/REST/v1/Locations/" city "?key=" api-key)
            slurp
            extract-coordinates))
      (catch Throwable e
        (println "\nBing Maps API - returning nil, because API call failed: ")
        (println e)
        nil))))


;; (def -coordinates-cache (atom {})) ; consider creating a second memoization with an even faster atom
(def -memoized-coordinates (m/my-memoize get-coordinates-from-city db/coordinates-table))
(def coordinates-cache (atom {}))
(def memoized-coordinates (m/my-memoize
                           (fn [city-str] (-memoized-coordinates city-str))
                           coordinates-cache))

(defn coordinates-not-defined? [coords]
  (or (nil? coords)
      (some nil? (vals coords))))

(defn distance-btwn-coordinates [coords1 coords2]
  (when debug?
    (println "coords1:" coords1)
    (println "coords2:" coords2)
    (println ""))
  (if (or (coordinates-not-defined? coords1)
          (coordinates-not-defined? coords2))
    nil
    (coord-utils/haversine coords1 coords2)))

(defn normal-img-to-full-size [friend]
  (let [original-url (:profile-image-url-https friend)]
    (if (nil? original-url)
      nil
      (str/replace original-url "_normal" ""))))

;; TODO: instead of doing this messy split thing, get a list of city/country names & see if they're in this string
(defn location-from-name [name]
  (let [name (or name "")
        name (clojure.string/replace name #"they/them" "")
        name (clojure.string/replace name #"she/her" "")
        name (clojure.string/replace name #"he/him" "")
        split-name (str/split name #" in ")]
    (if (= 1 (count (or split-name "")))
      nil
      (last split-name))))

;; "main" refers to the location set in the Twitter :location field
;; "name-location" refers to the location described in their Twitter :name (which may be nil)
(defn get-relevant-user-data [friend current-user]
  (let [current-user? (or (= current-user :current-user)
                          (= (:screen-name current-user) (:screen-name friend)))
        ; locations as strings
        friend-main-location  (:location friend)
        friend-name-location  (location-from-name (:name friend))
        current-main-location (when (not current-user?) (:main-location current-user))
        current-name-location (when (not current-user?) (:location current-user))
        ; locations as coordinates
        friend-main-coords  (memoized-coordinates (or friend-main-location ""))
        friend-name-coords  (memoized-coordinates (or friend-name-location ""))
        current-main-coords (when (not current-user?) (:main-coords current-user))
        current-name-coords (when (not current-user?) (:name-coords current-user))]

    ;; (pp/pprint {:friend-main-coords  (memoized-coordinates (or friend-main-location ""))
    ;;             :friend-name-coords  (memoized-coordinates (or friend-name-location ""))
    ;;             :current-main-coords (when (not current-user?) (:main-coords current-user))
    ;;             :current-name-coords (when (not current-user?) (:name-coords current-user))})

    ;; (when debug?
    ;;   (println "---------------------------------------------------")
    ;;   (println " friend-main-location:" friend-main-location)
    ;;   (println " friend-name-location:" friend-name-location)
    ;;   ;; (println "current-main-location:" current-main-location)
    ;;   ;; ;; (println "current-name-location:" current-name-location)
    ;;   ;; (println "")
    ;;   ;; ;; (println "current-user?:         " current-user?)
    ;;   ;; ;; (println "current-user:          " current-user)
    ;;   ;; (println "")
    ;;   ;; ;; (println "current-main-location: " current-main-location)
    ;;   ;; ;; (println "current-name-location: " current-name-location)
    ;;   ;; (println "friend :name           " (:name friend))
    ;;   ;; ;; (println "current-name-coords:   " current-name-coords)
    ;;   ;; ;; (println "current-main-coords:   " current-main-coords)
    ;;   (println "friend-main-coords:    " friend-main-coords)
    ;;   (println "friend-main-coords:    " friend-name-coords))

    {:name                    (:name friend)
     :screen-name             (:screen-name friend)
     :user-id                 :TODO ;; TODO: generate this in the db
     :profile_image_url_large (normal-img-to-full-size friend)
     :distance (when (not current-user?)
                 {:name-main (distance-btwn-coordinates current-name-coords friend-main-coords)
                  :name-name (distance-btwn-coordinates current-name-coords friend-name-coords)
                  :main-main (distance-btwn-coordinates current-main-coords friend-main-coords)
                  :main-name (distance-btwn-coordinates current-main-coords friend-name-coords)})
     :main-location friend-main-location
     :name-location friend-name-location
     :main-coords   friend-main-coords
     :name-coords   friend-name-coords}))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; twitter oauth ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;; TODO: make it so this --with-access-token works with atom memoization too, to speed it up
(defn fetch-current-user--with-access-token [access-token]
  (let [client (oauth/oauth-client (util/get-env-var "TWITTER_CONSUMER_KEY")
                                   (util/get-env-var "TWITTER_CONSUMER_SECRET")
                                   (:oauth-token access-token)
                                   (:oauth-token-secret access-token))]
    (client {:method :get
             :url (str "https://api.twitter.com/1.1/account/verify_credentials.json")
             :body "user.fields=created_at,description,entities,id,location,name,profile_image_url,protected,public_metrics,url,username"})))

(defn --fetch-friends [screen-name] ;; use the memoized version of this function!
  (when debug?
    (println "============================================================== start")
    (println "\n\n\nfetching friends for " screen-name "\n\n\n"))
  (try
    (let [; the sql-result should never be an empty list; if it is, that means the
          ; access token was deleted. it shouldn't be possible to get to this state,
          ; but if it does happen at some point, then we may need to add a way for
          ; the user to re-authenticate.
          sql-result   (db/select-by-request-key db/access_tokens-table screen-name)
          access-token (get-in (first sql-result) [:data :access_token]) ; TODO: memoize this with an atom for faster, non-db access, a la: (get @access-tokens screen-name)
          client       (oauth/oauth-client (util/get-env-var "TWITTER_CONSUMER_KEY")
                                           (util/get-env-var "TWITTER_CONSUMER_SECRET")
                                           (:oauth-token access-token)
                                           (:oauth-token-secret access-token))]
      ;; (println "access-token: ---------------------------------------------")
      ;; (println access-token)
      ;; (println "client: ---------------------------------------------------")
      ;; (println client)
      (loop [cursor -1 ;; -1 is the first page, while future pages are gotten from previous_cursor & next_cursor
             result-so-far []]

        (println "cursor: -------------------------------------------------")
        (println cursor)
        ;; (println "result-so-far: ------------------------------------------")
        ;; (println result-so-far)
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

          (println "api-response:         " (keys api-response))
          (println "(first screen-names): " (first screen-names))
          (println "(count screen-names): " (count screen-names))
          (println "next-cursor:          " next-cursor)
          (println "friends count so far: " (count result-so-far))
          (println "----------------------------------------")
          (println "============================================================ end")

          new-result ;; TODO: undo me once save-to-db is working
          #_(if (= next-cursor 0)
              new-result ;; return final result if Twitter returns a cursor of 0
              (recur next-cursor new-result) ;; else, recur by appending the page to the result so far
              ))))
    (catch Throwable e
      (println "🔴 caught exception when getting friends for screen-name:" screen-name)
      (println (pr-str e))
      :failed)))
;; (def -friends-cache (clojure.java.io/file "memoized-friends.edn")) ; TODO: this is just for development so that we don't hit Twitter's API rate limit
;; (def -memoized-friends (m/my-memoize --fetch-friends -friends-cache))
(def memoized-friends (m/my-memoize
                       (fn [screen-name] {:friends (--fetch-friends screen-name)})
                       db/users-table))

(def friends-cache-relevant-data (atom {}))
(defn --fetch-friends-relevant-data [screen-name current-user]
  (map #(get-relevant-user-data % current-user)
       (take 80 (:friends (memoized-friends screen-name))))) ; TODO: can add (take X) for debugging
(def memoized-friends-relevant-data
  (m/my-memoize --fetch-friends-relevant-data friends-cache-relevant-data))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; server ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn set-session [response-so-far new-session]
  (assoc response-so-far
         :session new-session))

(defn get-current-user [req]
  (get-in req [:session :current-user]
          cu/empty-session))

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
             current-user   (get-relevant-user-data (fetch-current-user--with-access-token access-token)
                                                    :current-user)
             screen-name    (:screen-name current-user)]
         (db/insert-or-update! db/access_tokens-table screen-name {:access_token access-token}) ; TODO: consider memoizing for speed
         (println (str "@" screen-name ") has successfully authorized Small World to access their Twitter account"))
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

(defn get-users-friends [req]
  (let [-current-user (get-current-user req)
        logged-in?    (not= cu/empty-session -current-user)]
    (generate-string (if logged-in?
                       (memoized-friends-relevant-data (:screen-name -current-user)
                                                       (get-current-user req))
                       []))))

;; app is function that takes a request, and returns a response
(defroutes devons-app ; order matters in this function!
  ;; oauth & session endpoints
  (GET "/login"      _   (start-oauth-flow))
  (GET "/authorized" req (store-fetched-access-token-then-redirect-home req))
  (GET "/session"    req (generate-string (get-current-user req)))
  (GET "/logout"     req (logout req))

  ;; app data endpoints
  (GET "/friends" req (get-users-friends req))

  ;; general resources
  (GET "/"                      [] (io/resource "public/index.html"))
  (GET "/about"                 [] (io/resource "public/index.html")) ; TODO: make more elegant
  (GET "/css/mapbox-gl.inc.css" [] (io/resource "cljsjs/mapbox/production/mapbox-gl.inc.css"))
  (route/resources "/")
  (ANY "*" [] (route/not-found "<h1 class='not-found'>404 not found</h1>")))

(def app-handler (-> devons-app
                     (compojure.handler/site {:session
                                              {:cookie-name "small-world-session"
                                               :store (cookie/cookie-store
                                                       {:key (util/get-env-var "COOKIE_STORE_SECRET_KEY")})}})
                     #_util/server-logger))

(defonce server* (atom nil))

(defn start! [port]
  (some-> @server* (.stop))

  ; create the tables if they don't already exists
  (db/create-table db/users-table)
  (db/create-table db/coordinates-table)
  (db/create-table db/access_tokens-table)

  (let [port (Integer. (or port (util/get-env-var "PORT") 5000))
        server (jetty/run-jetty #'app-handler {:port port :join? false})]

    (reset! server* server)))

(defn stop! []
  (if @server*
    (.stop @server*)
    (println "@server* is nil – no server to stop")))

(defn -main [& args]
  (let [default-port 8080
        port (System/getenv "PORT")
        port (if (nil? port)
               (do (println "PORT not defined. Defaulting to" default-port)
                   default-port)
               (Integer/parseInt port))]
    (println "\nSmall World is running on" (str "http://localhost:" port) "\n")
    (start! port)))