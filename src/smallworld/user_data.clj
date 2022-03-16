(ns smallworld.user-data
  (:require [smallworld.coordinates :as coordinates]
            [clojure.string :as str]))

(def debug? false)

(defn normal-img-to-full-size [friend]
  (let [original-url (:profile-image-url-https friend)]
    (if (nil? original-url)
      nil
      (str/replace original-url "_normal" ""))))

;; TODO: instead of doing this messy split thing, get a list of city/country names & see if they're in this string
(defn location-from-name [name]
  (let [name (or name "")
        name (str/replace name #"they/them" "")
        name (str/replace name #"she/her" "")
        name (str/replace name #"he/him" "")
        split-name (clojure.string/split name #" in |\|")] ; split on " in " or "|"
    (if (= 1 (count (or split-name "")))
      nil
      (last split-name))))

;; "main" refers to the location set in the Twitter :location field
;; "name-location" refers to the location described in their Twitter :name (which may be nil)
(defn abridged [friend current-user]
  (let [is-current-user? (= (:screen-name current-user) (:screen-name friend))
        ; locations as strings
        friend-main-location  (:location friend)
        friend-name-location  (location-from-name (:name friend))
        ; locations as coordinates
        friend-main-coords  (coordinates/memoized (or friend-main-location ""))
        friend-name-coords  (coordinates/memoized (or friend-name-location ""))
        current-main-coords (when (not is-current-user?) (:main-coords current-user))
        current-name-coords (when (not is-current-user?) (:name-coords current-user))]

    (when debug?
      (println "---------------------------------------------------")
      (println " friend email:        " (:email friend))
      (println)
      (println " friend-main-location:" friend-main-location)
      (println " friend-name-location:" friend-name-location)
      (println "current-main-location:" (when (not is-current-user?) (:main-location current-user)))
      (println "current-name-location:" (when (not is-current-user?) (:location current-user)))
      (println)
      (println "is-current-user?:         " is-current-user?)
      (println "current-user:          " current-user)
      (println "friend :name           " (:name friend))
      (println)
      (println "current-name-coords:   " current-name-coords)
      (println "current-main-coords:   " current-main-coords)
      (println "friend-main-coords:    " friend-main-coords)
      (println "friend-main-coords:    " friend-name-coords)
      (println "-----------------------------------------------"))

    {:name                    (:name friend)
     :screen-name             (:screen-name friend)
     :profile_image_url_large (normal-img-to-full-size friend)
     ; note – email will only be available if the user has given us permission
     ; (i.e. if they are also the current-user) AND if they have set their email
     ; on Twitter, which is not required, so sometimes it'll be the empty string
     :email (:email friend)
     :locations [(when (not (str/blank? friend-main-location)) {:special-status :twitter-location ; formerly called "main location"
                                                                :name friend-main-location
                                                                :coords friend-main-coords
                                                                :distances {:to-main (coordinates/distance-btwn current-name-coords friend-main-coords)
                                                                            :to-name (coordinates/distance-btwn current-main-coords friend-main-coords)}})
                 (when (not (str/blank? friend-name-location)) {:special-status :from-display-name ; formerly called "name location"
                                                                :name friend-name-location
                                                                :coords friend-name-coords
                                                                :distances {:to-main (coordinates/distance-btwn current-name-coords friend-name-coords)
                                                                            :to-name (coordinates/distance-btwn current-main-coords friend-name-coords)}})]
     ; TODO: rm everything below this line
     :distance (when (not is-current-user?)
                 {:name-main (coordinates/distance-btwn current-name-coords friend-main-coords)
                  :name-name (coordinates/distance-btwn current-name-coords friend-name-coords)
                  :main-main (coordinates/distance-btwn current-main-coords friend-main-coords)
                  :main-name (coordinates/distance-btwn current-main-coords friend-name-coords)})
     :main-location friend-main-location
     :name-location friend-name-location
     :main-coords   friend-main-coords
     :name-coords   friend-name-coords}))