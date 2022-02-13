(ns smallworld.user-data
  (:require [smallworld.coordinates :as coordinates]
            [clojure.string :as str]))

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
        split-name (str/split name #" in ")]
    (if (= 1 (count (or split-name "")))
      nil
      (last split-name))))

;; "main" refers to the location set in the Twitter :location field
;; "name-location" refers to the location described in their Twitter :name (which may be nil)
(defn abridged [friend current-user]
  (let [current-user? (or (= current-user :current-user)
                          (= (:screen-name current-user) (:screen-name friend)))
        ; locations as strings
        friend-main-location  (:location friend)
        friend-name-location  (location-from-name (:name friend))
        ; locations as coordinates
        friend-main-coords  (coordinates/memoized (or friend-main-location ""))
        friend-name-coords  (coordinates/memoized (or friend-name-location ""))
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
    ;;   ;; (println "current-main-location:" (when (not current-user?) (:main-location current-user)))
    ;;   ;; ;; (println "current-name-location:" (when (not current-user?) (:location current-user)))
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
     :profile_image_url_large (normal-img-to-full-size friend)
     :distance (when (not current-user?)
                 {:name-main (coordinates/distance-btwn current-name-coords friend-main-coords)
                  :name-name (coordinates/distance-btwn current-name-coords friend-name-coords)
                  :main-main (coordinates/distance-btwn current-main-coords friend-main-coords)
                  :main-name (coordinates/distance-btwn current-main-coords friend-name-coords)})
     :main-location friend-main-location
     :name-location friend-name-location
     :main-coords   friend-main-coords
     :name-coords   friend-name-coords}))
