(ns smallworld.coordinates (:require [clojure.data.json :as json]
                                     [smallworld.util :as util]
                                     [smallworld.memoize :as m]
                                     [smallworld.db :as db]))

(def debug? false)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; coordinate data fetching ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;; if you need to make the locations more precise in the future, use the bbox
;; (bounding box) rather than just the coordinates
(defn extract-coordinates [raw-result]
  (let [result      (json/read-str raw-result)
        status-code (get result "statusCode")
        coordinates (get-in result ["resourceSets" 0 "resources" 0 "geocodePoints" 0 "coordinates"])
        coordinates {:lat (first coordinates) :lng (second coordinates)}]
    (if (= 200 status-code) coordinates
        (print "houston, we have a problem..."))))

(defn get-from-city [city-str]
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
        (println "\nBing Maps API - returning nil, because API call failed to retrieve a valid result")
        nil))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; calculations ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

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

(defn coordinates-not-defined? [coords]
  (or (nil? coords)
      (some nil? (vals coords))))

(defn distance-btwn [coords1 coords2]
  (when debug?
    (println "coords1:" coords1)
    (println "coords2:" coords2)
    (println ""))
  (if (or (coordinates-not-defined? coords1)
          (coordinates-not-defined? coords2))
    nil
    (haversine coords1 coords2)))

(defn tokenize [s] (set (re-seq #"\w+" (clojure.string/lower-case s))))

(defn jaccard [s1 s2]
  (/ (count (clojure.set/intersection (tokenize s1) (tokenize s2)))
     (count (clojure.set/union (tokenize s1) (tokenize s2)))))

(defn very-similar-location-names [pairs]
  ; Don't consider them very similar if there is a slash or ampersand in either
  ; of the names, because that means there might be two locations in the string.
  (let [result (if (or (re-find #"/" (first pairs))
                       (re-find #"/" (second pairs))
                       (re-find #"&" (first pairs))
                       (re-find #"&" (second pairs)))
                 false
                 (> (apply jaccard pairs) 0.5))]
    (println "                      pairs:" pairs)
    (println "very-similar-location-names?" result)
    (println "                    jaccard:" (apply jaccard pairs))
    (println "")
    result))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;


(def table-memoized (m/my-memoize get-from-city db/coordinates-table))
(def atom-memoized  (m/my-memoize table-memoized (atom {})))
(def memoized atom-memoized) ; re-naming just for export
