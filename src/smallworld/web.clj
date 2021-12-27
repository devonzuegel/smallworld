(ns smallworld.web
  (:gen-class)
  (:require [compojure.core :refer [defroutes GET ANY]]
            [compojure.handler :refer [site]]
            [compojure.route :as route]
            [clojure.java.io :as io]
            [ring.adapter.jetty :as jetty]
            ;; [clojure.pprint :as pp]
            [smallworld.memoize :as m]
            [clojure.data.json :as json]
            [cheshire.core :refer [generate-string]]
            [environ.core :refer [env]]))

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
  (if (empty? city-str)
    nil ; return nil coordinates if no city string is given
    (try
      (let [city    (java.net.URLEncoder/encode city-str "UTF-8")
            api-key (java.net.URLEncoder/encode (System/getenv "BING_MAPS_API_KEY") "UTF-8")]
        (-> (str "https://dev.virtualearth.net/REST/v1/Locations/" city "?key=" api-key)
            slurp
            extract-coordinates))
      (catch Throwable e
        (println "\ncaught exception: " (.getMessage e))
        nil))))

;; (def coordinates-cache (atom {}))
(def coordinates-cache (atom (read-string (slurp "resources/memoized-coordinates.edn"))))
;; (def coordinates-cache (clojure.java.io/file "memoized-coordinates.edn"))
(def memoized-coordinates-from-city (m/my-memoize get-coordinates-from-city coordinates-cache))

(defn coordinates-not-defined? [coords]
  (or (nil? coords)
      (some nil? (vals coords))))

(defn get-distance-between-coordinates [coords1 coords2]
  (if (or (coordinates-not-defined? coords1)
          (coordinates-not-defined? coords2))
    nil
    (haversine coords1 coords2)))

(defn get-relevant-friend-data [friend]
  (let [friend-coords (memoized-coordinates-from-city (:location friend))
        my-coords     (:ba stored-coordinates)]
    {:name                    (:name friend)
     :screen_name             (:screen_name friend)
     :location                (:location friend)
     :profile_image_url_large (clojure.string/replace (:profile_image_url_large friend) "_normal" "")
     :coordinates             friend-coords
     :distance                (get-distance-between-coordinates friend-coords my-coords)}))

;; (def x friends-from-storage)
;; (def with-coords (map get-relevant-friend-data x))
;; (store-to-file with-coords)
;; (pp/pprint with-coords)

#_(store-to-file "friends-sebasbensu-new.edn"
                 (map get-relevant-friend-data friends-from-storage))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; server ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defroutes app ; order matters in this function!
  (GET "/friends" [] (generate-string
                      (map get-relevant-friend-data friends-from-storage)))
  (GET "/" []        (slurp (io/resource "public/index.html")))
  (route/resources "/")
  (ANY "*" []        (route/not-found "<h1>404 Not found</h1>")))

(defonce server* (atom nil))

(defn start! [port]
  (some-> @server* (.stop))
  (let [port (Integer. (or port (env :port) 5000))
        server (jetty/run-jetty (site #'app) {:port port :join? false})]
    (reset! server* server)))

(defn stop! []
  (.stop @server*))

(defn -main [& args]
  (start! (Integer/parseInt (System/getenv "PORT"))))