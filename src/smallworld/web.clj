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
            [smallworld.current-user :as cu]
            [clojure.data.json :as json]
            [clojure.string :as str]
            [cheshire.core :refer [generate-string]]
            [environ.core :refer [env]]))

;; TODO: fetch this from the Twitter client
(def current-user (atom cu/default-state))

(defn get-environment-var [key]
  (let [value (System/getenv key)]
    (when (nil? value) (throw (Throwable. (str "Environment variable not set: " key))))
    value))

(defn haversine [{lon1 :lng lat1 :lat} {lon2 :lng lat2 :lat}]
  ; Haversine formula
  ; a = sin²(Δφ/2) + cos φ1 ⋅ cos φ2 ⋅ sin²(Δλ/2)
  ; c = 2 ⋅ atan2( √a, √(1−a) )
  ; d = R ⋅ c
  ; where φ is latitude, λ is longitude, R is earth’s radius (mean radius = 6,371km);
  ; "Implementation of Haversine formula. Takes two sets of latitude/longitude pairs and returns the shortest great circle distance between them (in km)"
  (assert (every? some? [lon1 lat1 lon2 lat2]) "coordinates {:lat, :lng} must not be nil")
  (let [R 6378.137 ; radius of Earth in km
        dlat (Math/toRadians (- lat2 lat1))
        dlon (Math/toRadians (- lon2 lon1))
        lat1 (Math/toRadians lat1)
        lat2 (Math/toRadians lat2)
        a (+ (* (Math/sin (/ dlat 2)) (Math/sin (/ dlat 2)))
             (* (Math/sin (/ dlon 2)) (Math/sin (/ dlon 2)) (Math/cos lat1) (Math/cos lat2)))]
    (* R 2 (Math/asin (Math/sqrt a)))))

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
    (do #_(println "")
        (println "city-str:" city-str)
        nil ; return nil coordinates if no city string is given TODO: return :no-result
        )
    (try
      (let [city    (java.net.URLEncoder/encode (or city-str "") "UTF-8") ;; the (if empty?) shouldn't caught the nil string thing... not sure why it didn't
            api-key (java.net.URLEncoder/encode (get-environment-var "BING_MAPS_API_KEY") "UTF-8")]
        (println "city:" city)
        (println "api-key:" api-key)
        (println "")
        (-> (str "https://dev.virtualearth.net/REST/v1/Locations/" city "?key=" api-key)
            slurp
            extract-coordinates))
      (catch Throwable e
        (println "\ncaught exception (get-coordinates-from-city): ")
        (println e)
        nil))))


(def -coordinates-cache (clojure.java.io/file "memoized-coordinates.edn"))
;; (def -coordinates-cache (atom {}))
(def -memoized-coordinates (m/my-memoize get-coordinates-from-city -coordinates-cache))
(def coordinates-cache (atom {}))
(def memoized-coordinates (m/my-memoize
                           (fn [city-str] (-memoized-coordinates city-str))
                           coordinates-cache))

(defn coordinates-not-defined? [coords]
  (or (nil? coords)
      (some nil? (vals coords))))

(defn distance-btwn-coordinates [coords1 coords2]
  #_(println "coords1:" coords1)
  #_(println "coords2:" coords2)
  #_(println "")
  (if (or (coordinates-not-defined? coords1)
          (coordinates-not-defined? coords2))
    nil
    (haversine coords1 coords2)))

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

;; (defn f [a & {:keys [b] :or {b "default"}}]
;;   (println a)
;;   (println b))

;; "main" refers to the location set in the Twitter :location field
;; "name-location" refers to the location described in their Twitter :name (which may be nil)
(defn get-relevant-friend-data [friend & {:keys [current-user?] :or {current-user? false}}]
  (let [current-user-relevant-data current-user
        ; locations as strings
        friend-main-location  (:location friend)
        friend-name-location  (location-from-name (:name friend))
        current-main-location (when (not current-user?) (:location current-user-relevant-data))
        current-name-location (when (not current-user?) (location-from-name (:name current-user-relevant-data)))
        ; locations as coordinates
        friend-main-coords  (memoized-coordinates (or friend-main-location ""))
        friend-name-coords  (memoized-coordinates (or friend-name-location ""))
        current-main-coords (when (not current-user?) (:main-coords @current-user))
        current-name-coords (when (not current-user?) (:name-coords @current-user))]

    ;; (println " friend-main-location:" friend-main-location)
    ;; (println " friend-name-location:" friend-name-location)
    ;; (println "current-main-location:" current-main-location)
    ;; (println "current-name-location:" current-name-location)

    (println "---------------------------------------------------")
    (println "current-user?:             " current-user?)
    (println "@current-user:")
    (println @current-user)
    (println "current-user-relevant-data:" current-user-relevant-data)
    (println "current-main-location:     " current-main-location)
    (println "current-name-location:     " current-name-location)
    (println "(:name friend):            " (:name friend))
    (println "current-name-coords:       " current-name-coords)
    (println "current-main-coords:       " current-main-coords)
    (println "friend-main-coords:        " friend-main-coords)
    (println "friend-main-coords:        " friend-name-coords)

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

(defonce access-tokens (atom {}))

(def users-cache (atom {}))
;; TODO: make it so this --with-access-token works with memoization too
(defn fetch-current-user--with-access-token [access-token]
  (let [client (oauth/oauth-client (get-environment-var "TWITTER_CONSUMER_KEY")
                                   (get-environment-var "TWITTER_CONSUMER_SECRET")
                                   (:oauth-token access-token)
                                   (:oauth-token-secret access-token))]
    (client {:method :get
             :url (str "https://api.twitter.com/1.1/account/verify_credentials.json")
             :body "user.fields=created_at,description,entities,id,location,name,profile_image_url,protected,public_metrics,url,username"})))
(defn fetch-current-user [screen-name]
  (let [access-token (get @access-tokens screen-name)]
    (fetch-current-user--with-access-token access-token)))
(def memoized-user-data (m/my-memoize fetch-current-user users-cache))

(def friends-cache (clojure.java.io/file "memoized-friends.edn"))
;; (def friends-cache (atom {}))
(defn --fetch-friends [screen-name] ;; use the memoized version of this function!
  (try
    (let [access-token (get @access-tokens screen-name)
          client (oauth/oauth-client (get-environment-var "TWITTER_CONSUMER_KEY")
                                     (get-environment-var "TWITTER_CONSUMER_SECRET")
                                     (:oauth-token access-token)
                                     (:oauth-token-secret access-token))]
      (println "============================================================== start")
      (println "access-token: ---------------------------------------------")
      (println access-token)
      (println "client: ---------------------------------------------------")
      (println client)
      (loop [cursor -1 ;; -1 is the first page, while future pages are gotten from previous_cursor & next_cursor
             result-so-far []]

        (println "cursor: -------------------------------------------------")
        (println cursor)
        (println "result-so-far: ------------------------------------------")
        (println result-so-far)
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

          new-result ;; TODO: undo me once I've solved the Oauth issues
          #_(if (= next-cursor 0)
              new-result ;; return final result if Twitter returns a cursor of 0
              (recur next-cursor new-result) ;; else, recur by appending the page to the result so far
              ))))
    (catch Throwable e
      (println "🔴 caught exception when getting friends for screen-name:" screen-name)
      (println (pr-str e))
      :failed)))
(def memoized-friends (m/my-memoize --fetch-friends friends-cache))

(def friends-cache-relevant-data (atom {}))
(defn --fetch-friends-relevant-data [screen-name]
  (map get-relevant-friend-data (memoized-friends screen-name)))
(def memoized-friends-relevant-data
  (m/my-memoize --fetch-friends-relevant-data friends-cache-relevant-data))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; server ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;; step 1
;; in prod: this will redirect to https://small-world-friends.herokuapp.com/authorized,
;;          the 'Callback URL' set up at https://developer.twitter.com/en/apps/9258522
;; in dev:  this will redirect to http://localhost:3001/authorized,
;;          the 'Callback URL' set up at https://developer.twitter.com/en/apps/9258699
(defn start-oauth-flow []
  (let [request-token (oauth/oauth-request-token (get-environment-var "TWITTER_CONSUMER_KEY")
                                                 (get-environment-var "TWITTER_CONSUMER_SECRET"))
        redirect-url  (oauth/oauth-authorization-url (:oauth-token request-token))]
    (response/redirect redirect-url)))

;; step 2
(defn store-fetched-access-token-then-redirect-home [req]
  (let [oauth-token    (get-in req [:params :oauth_token])
        oauth-verifier (get-in req [:params :oauth_verifier])
        access-token   (oauth/oauth-access-token (get-environment-var "TWITTER_CONSUMER_KEY") oauth-token oauth-verifier)
        current-user   (get-relevant-friend-data (fetch-current-user--with-access-token access-token))
        screen-name    (:screen-name current-user)]
    (swap! access-tokens assoc screen-name access-token) ;; TODO: maybe get rid of this? or put it in db?
    (println (str "@" screen-name " (user-id: " "TODO" ") "
                  "has successfully authorized Small World to access their Twitter account"))
    (assoc (response/redirect "/") :session {:current-user current-user
                                             :access-token access-token})))

(defn logout [req]
  (assoc (response/redirect "/logged-out") :session {}))

;; app is function that takes a request, and returns a response
(defroutes devons-app ; order matters in this function!
  ;; oauth & session endpoints
  (GET "/login"      []  (start-oauth-flow))
  (GET "/authorized" req (store-fetched-access-token-then-redirect-home req))
  (GET "/session"    req (generate-string (get-in req [:session :current-user] {})))
  (GET "/logout"     req (logout req))

  ;; app data endpoints
  (GET "/friends"      [] (generate-string (if (= cu/default-state @current-user)
                                             []
                                             (memoized-friends-relevant-data (:screen-name @current-user)))))

  ;; general resources
  (GET "/" [] (slurp (io/resource "public/index.html")))
  (route/resources "/")
  (ANY "*" [] (route/not-found "<h1 class='not-found'>404 not found</h1>")))

(defn logger [handler]
  (fn [request]
    (println "\n=======================================================")
    (println "request:")
    (pp/pprint request)
    (println "")
    (let [response (handler request)]
      (println "response:")
      (pp/pprint response)
      (println "=======================================================\n")
      response)))

(def app-handler (-> devons-app
                     (compojure.handler/site {:session
                                              {:cookie-name "small-world-session"
                                               :store (cookie/cookie-store
                                                       {:key (get-environment-var "COOKIE_STORE_SECRET_KEY")})}})
                     logger))

(defonce server* (atom nil))

(defn start! [port]
  (some-> @server* (.stop))
  (let [port (Integer. (or port (get-environment-var "PORT") 5000))
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