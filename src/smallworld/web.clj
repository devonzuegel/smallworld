(ns smallworld.web
  (:gen-class)
  (:require [compojure.core :refer [defroutes GET ANY]]
            [compojure.handler :refer [site]]
            [compojure.route :as route]
            [clojure.java.io :as io]
            [ring.adapter.jetty :as jetty]
            [twttr.api :as api]
            [clojure.pprint :as pp]
            [clojure.data.json :as json]
            [cheshire.core :refer [generate-string]]
            [twttr.auth :refer [env->UserCredentials]]
            [yesql.core :refer [defqueries]]
            [environ.core :refer [env]]))

; Define a database connection spec. (This is standard clojure.java.jdbc.)
(def db-spec {:classname "org.postgresql.Driver"
              :subprotocol "postgresql"
              :subname "//localhost:5432/smallworld-local"
              :user "devonzuegel"})

; Import the SQL query as a function.
(defqueries "queries/users.sql" {:connection db-spec})

;; TODO: ask Sebas why I get "Hello World" when I refresh the Heroku page but
;; "hello small world!" when I hard refresh. Note – when I refresh OR hard-refresh
;; in Incognito mode, I just get "hello small world!" as I expect. So maybe it's
;; a cache thing?  or a chrome extension thing?  but whyyy?


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Variables ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

; read credentials from environment variables, namely:
; CONSUMER_KEY, CONSUMER_SECRET, ACCESS_TOKEN, and ACCESS_TOKEN_SECRET
;; (def creds        (env->UserCredentials))
;; (def friends      (atom ()))
;; (def result_count 200) ;; 200 is the max allowed by the Twitter API
(def screen-name  "sebasbensu")
(def filename     "friends-sebasbensu.edn")

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Helpers ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn store-to-file [data]
  (let [result (with-out-str (pr data))]
    (spit filename result)))

(defn read-from-file []
  (read-string (slurp (clojure.java.io/resource filename))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;; (defn fetch-friends-from-twitter-api []
;;   (loop [cursor nil
;;          result-so-far []]
;;     (let [api-response    (api/friends-list creds :params {:screen_name screen-name
;;                                                            :count       result_count
;;                                                            :cursor      cursor})
;;           page-of-friends (:users api-response)
;;           new-result      (concat result-so-far page-of-friends)
;;           screen-names    (map :screen_name (:users api-response))
;;           next-cursor     (:next_cursor api-response)]

;;       (comment
;;         (pp/pprint "(first screen-names): " (first screen-names))
;;         (pp/pprint "(count screen-names): " (count screen-names))
;;         (pp/pprint "next-cursor:          " next-cursor)
;;         (pp/pprint "friends count so far: " (count result-so-far))
;;         (pp/pprint "----------------------------------------"))

;;       (if (= next-cursor 0)
;;         ;; return final result if Twitter returns a cursor of 0
;;         new-result
;;         ;; else, recur by appending the page to the result so far
;;         (recur next-cursor new-result)))))

;; ;; ;; Don't run this too often! You will hit the Twitter rate limit very quickly.
;; ;; (store-to-file (fetch-friends-from-twitter-api))

(def friends-from-storage (read-from-file)) ;; TODO: store this in their local storage
(def n-friends (count (map :screen_name friends-from-storage)))
(str "@" screen-name " is following " n-friends " accounts")

; Haversine formula
; a = sin²(Δφ/2) + cos φ1 ⋅ cos φ2 ⋅ sin²(Δλ/2)
; c = 2 ⋅ atan2( √a, √(1−a) )
; d = R ⋅ c
; where φ is latitude, λ is longitude, R is earth’s radius (mean radius = 6,371km);
; "Implementation of Haversine formula. Takes two sets of latitude/longitude pairs and returns the shortest great circle distance between them (in km)"
(defn haversine [{lon1 :lng lat1 :lat} {lon2 :lng lat2 :lat}]
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
;; (bounding box) ratehr than just the coordinates
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

(defn coordinates-not-defined [coords]
  (or (nil? coords)
      (some nil? (vals coords))))

(defn get-distance-between-coordinates [coords1 coords2]
  (if (or (coordinates-not-defined coords1)
          (coordinates-not-defined coords2))
    nil
    (haversine coords1 coords2)))

(defn get-relevant-friend-data [friend]
  (let [friend-coordinates (:coordinates friend)  ;; (get-coordinates-from-city (:location friend))
        my-coordinates     (:ba stored-coordinates)]
    {:name        (:name friend)
     :screen_name (:screen_name friend)
     :location    (:location friend)
     :coordinates friend-coordinates
     :distance    (get-distance-between-coordinates friend-coordinates
                                                    my-coordinates)}))

;; (def x friends-from-storage)
;; (def with-coords (map get-relevant-friend-data x))
;; (store-to-file with-coords)
;; (pp/pprint with-coords)

(defroutes app
  (GET "/friends" []
    (generate-string (map get-relevant-friend-data
                          friends-from-storage)))

  (GET "/" [] (slurp (io/resource "public/index.html")))

  (route/resources "/")

  (ANY "*" [] (route/not-found "<h1>404 Not found</h1>")))

(defonce server* (atom nil))

(defn start! [port]
  (some-> @server* (.stop))
  (let [port (Integer. (or port (env :port) 5000))
        server
        (jetty/run-jetty (site #'app)
                         {:port port :join? false})]
    (reset! server* server)))

(defn stop! []
  (.stop @server*))

(defn -main [& args]
  (start! (Integer/parseInt (System/getenv "PORT"))))