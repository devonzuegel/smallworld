(ns smallworld.coordinates)

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