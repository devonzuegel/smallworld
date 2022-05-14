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

(defn distances-map [is-current-user? current-user friend-coords]
  (when (not is-current-user?) ; distances aren't relevant if the friend whose data we're abridging is the current user
    (zipmap
     (map #(:name %) (:locations current-user))
     (map #(coordinates/distance-btwn (:coords %) friend-coords)
          (:locations current-user)))))

;; "main" refers to the location set in the Twitter :location field
;; "name-location" refers to the location described in their Twitter :name (which may be nil)
(defn abridged [friend current-user]
  (let [is-current-user? (= (:screen-name current-user) (:screen-name friend))
        ; locations as strings
        friend-main-location  (:location friend)
        friend-name-location  (location-from-name (:name friend))
        ; locations as coordinates
        friend-main-coords  (coordinates/memoized (or friend-main-location ""))
        friend-name-coords  (coordinates/memoized (or friend-name-location ""))]

    (when debug?
      (println "---------------------------------------------------")
      (println " friend-main-location:" friend-main-location)
      (println " friend-name-location:" friend-name-location)
      (println "   friend-main-coords:" friend-main-coords)
      (println "   friend-main-coords:" friend-name-coords)
      (println)
      (println "     is-current-user?:" is-current-user?)
      (println "         current-user:" current-user)
      (println "          friend name:" (:name friend))
      (println "         friend email:" (:email friend))
      (println "-----------------------------------------------"))

    {:name                    (:name friend)
     :screen-name             (:screen-name friend)
     :profile_image_url_large (normal-img-to-full-size friend)
     ; note – email will only be available if the user has given us permission
     ; (i.e. if they are also the current-user) AND if they have set their email
     ; on Twitter, which is not required, so sometimes it'll be the empty string
     :email (:email friend)
     :locations [(when (not (str/blank? friend-main-location))
                   {:special-status "twitter-location" ; formerly called "main location"
                    :name           friend-main-location
                    :coords         friend-main-coords
                    :distances      (distances-map is-current-user? current-user friend-main-coords)})

                 (when (not (str/blank? friend-name-location))
                   {:special-status "from-display-name" ; formerly called "name location"
                    :name           friend-name-location
                    :coords         friend-name-coords
                    :distances      (distances-map is-current-user? current-user friend-name-coords)})]}))
