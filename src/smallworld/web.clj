(ns smallworld.web
  (:gen-class)
  #_{:clj-kondo/ignore [:deprecated-var]}
  (:require [compojure.core :refer [defroutes GET ANY]]
            [compojure.handler :refer [site]]
            [compojure.route :as route]
            [clojure.java.io :as io]
            [ring.adapter.jetty :as jetty]
            ;; [oauth.client :as oauth]
            [oauth.twitter :as oauth]
            [clojure.pprint :as pp]
            [smallworld.memoize :as m]
            [clojure.data.json :as json]
            [clojure.string :as str]
            [cheshire.core :refer [generate-string]]
            [environ.core :refer [env]]))

(def current-user
  {:name "Devon Zuegel ☀️ in Buenos Aires"
   :screen-name "devonzuegel"
   :user-id "14058982"
   :location "Miami Beach"
   :profile-image-url-https "https://pbs.twimg.com/profile_images/1410680490949058566/lIlsTIH6.jpg"
   :coordinates {:lat 25.775083541870117
                 :lng -80.1947021484375}
   :distance 7102.906300799643})

(def filenames {:sebas-friends        "friends-sebasbensu.edn"
                :sebas-friends-full   "friends-sebasbensu--full-from-twitter.edn"
                :memoized-coordinates "memoized-coordinates.edn"})

(defn store-to-file [filename data]
  (let [result (with-out-str (pr data))]
    (spit filename result)))

(defn read-from-file [filename]
  (read-string (slurp (clojure.java.io/resource filename))))

(def friends-from-storage (read-from-file (:sebas-friends filenames))) ;; TODO: store this in their local storage

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

(def stored-coordinates {:sf         {:lat 37.78007888793945,   :lng -122.42015838623047}
                         :ba         {:lat -34.607566833496094, :lng -58.43708801269531}
                         :montevideo {:lat -34.90589141845703,  :lng -56.19131088256836}
                         :london     {:lat 51.500152587890625,  :lng -0.12623600661754608}
                         :miami      {:lat 25.775083541870117,  :lng -80.1947021484375}})

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
        #_(println "city-str:" city-str)
        nil ; return nil coordinates if no city string is given TODO: return :no-result
        )
    (try
      (let [city    (java.net.URLEncoder/encode city-str "UTF-8") ;; the (if empty?) shouldn't caught the nil string thing... not sure why it didn't
            api-key (java.net.URLEncoder/encode (System/getenv "BING_MAPS_API_KEY") "UTF-8")]
        #_(println "city:" city)
        #_(println "api-key:" api-key)
        #_(println "")
        (-> (str "https://dev.virtualearth.net/REST/v1/Locations/" city "?key=" api-key)
            slurp
            extract-coordinates))
      (catch Throwable e
        #_(println "\ncaught exception: " (.getMessage e))
        nil))))


(def -coordinates-cache (clojure.java.io/file "memoized-coordinates.edn"))
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

(defn location-from-name [name]
  (let [split-name (str/split name #" in ")]
    (if (= 1 (count (or split-name "")))
      nil
      (last split-name))))

;; "main" refers to the location set in the Twitter :location field
;; "name-location" refers to the location described in their Twitter :name (which may be nil)
(defn get-relevant-friend-data [friend]
  (let [; locations as strings
        friend-main-location  (:location friend)
        friend-name-location  (location-from-name (:name friend))
        current-main-location (:location current-user)
        current-name-location (location-from-name (:name current-user))
        ; locations as coordinates
        friend-main-coords  (memoized-coordinates (or friend-main-location ""))
        friend-name-coords  (memoized-coordinates (or friend-name-location ""))
        current-main-coords (memoized-coordinates (or current-main-location ""))
        current-name-coords (memoized-coordinates (or current-name-location ""))]

    ;; (println " friend-main-location:" friend-main-location)
    ;; (println " friend-name-location:" friend-name-location)
    ;; (println "current-main-location:" current-main-location)
    ;; (println "current-name-location:" current-name-location)

    {:name                    (:name friend)
     :screen-name             (:screen-name friend)
     :user-id                 :TODO ;; TODO: generate this in the db
     :profile_image_url_large (normal-img-to-full-size friend)
     :distance {:name-main (distance-btwn-coordinates current-name-coords friend-main-coords)
                :name-name (distance-btwn-coordinates current-name-coords friend-name-coords)
                :main-main (distance-btwn-coordinates current-main-coords friend-main-coords)
                :main-name (distance-btwn-coordinates current-main-coords friend-name-coords)}
     :main-location friend-main-location
     :name-location friend-name-location
     :main-coords   friend-main-coords
     :name-coords   friend-name-coords}))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; twitter oauth ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def consumer-key (System/getenv "CONSUMER_KEY"))
(def consumer-secret (System/getenv "CONSUMER_SECRET"))
(defonce access-tokens (atom {}))

(def friends-cache (clojure.java.io/file "memoized-friends.edn"))
(defn --fetch-friends [user-id] ;; use the memoized version of this function!
  (try
    (let [access-token (get @access-tokens user-id)
          client (oauth/oauth-client consumer-key consumer-secret (:oauth-token access-token) (:oauth-token-secret access-token))]
      (loop [cursor -1  ;; -1 is the first page, while future pages are gotten from previous_cursor & next_cursor
             result-so-far []]
        (let [api-response  (client {:method :get :url "https://api.twitter.com/1.1/friends/list.json"
                                     :body (str "count=200"
                                                "&cursor=" (str cursor)
                                                "&skip_status=false"
                                                "&include_user_entities=true")})
              page-of-friends (:users api-response)
              new-result      (concat result-so-far page-of-friends)
           ;; screen-names    (map :screen-name (:users api-response))
              next-cursor     (:next-cursor api-response)]

          ;; (println "api-response:         " (keys api-response))
          ;; (println "(first screen-names): " (first screen-names))
          ;; (println "(count screen-names): " (count screen-names))
          ;; (println "next-cursor:          " next-cursor)
          ;; (println "friends count so far: " (count result-so-far))
          ;; (println "----------------------------------------")

          (if (= next-cursor 0)
            new-result ;; return final result if Twitter returns a cursor of 0
            (recur next-cursor new-result) ;; else, recur by appending the page to the result so far
            ))))
    (catch Throwable e
      (println "🔴 caught exception when getting friends for user-id:" user-id)
      (println (pr-str e))
      :failed)))
(def memoized-friends (m/my-memoize --fetch-friends friends-cache))

(def friends-cache-relevant-data (atom {}))
(defn --fetch-friends-relevant-data [user-id]
  (let [friends (memoized-friends user-id)]
    (generate-string (map get-relevant-friend-data friends))))
(def memoized-friends-relevant-data (m/my-memoize --fetch-friends-relevant-data friends-cache-relevant-data))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; server ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn start-oauth-flow []
  (let [request-token (oauth/oauth-request-token consumer-key consumer-secret)]
    (oauth/oauth-authorize (:oauth-token request-token))
    nil))

(defn store-fetched-access-token [req]
  (let [oauth-token    (get-in req [:params :oauth_token])
        oauth-verifier (get-in req [:params :oauth_verifier])
        access-token   (oauth/oauth-access-token consumer-key oauth-token oauth-verifier)]
    (swap! access-tokens assoc (:user-id current-user) access-token)
    "you've successfully authorized Small World to access your Twitter account!"))

(defroutes app ; order matters in this function!
  (GET "/current-user" []        (generate-string (get-relevant-friend-data current-user)))
  (GET "/oauth"        []        (start-oauth-flow))
  (GET "/authorized"   [:as req] (store-fetched-access-token req))
  (GET "/friends"      []        (memoized-friends-relevant-data (:user-id current-user)))

  (GET "/" [] (slurp (io/resource "public/index.html")))
  (route/resources "/")
  (ANY "*" [] (route/not-found "<h1 class='not-found'>404 not found</h1>")))

(defonce server* (atom nil))

(defn start! [port]
  (some-> @server* (.stop))
  (let [port (Integer. (or port (env :port) 5000))
        server (jetty/run-jetty (site #'app) {:port port :join? false})]
    (reset! server* server)))

(defn stop! []
  (.stop @server*))

(defn -main [& args]
  (let [default-port 8080
        port (System/getenv "PORT")
        port (if (nil? port)
               (do (println "PORT not defined. Defaulting to" default-port)
                   default-port)
               (Integer/parseInt port))]
    (println "\nSmall World is running on" (str "http://localhost:" port) "\n")
    (start! port)))