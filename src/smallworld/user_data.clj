(ns smallworld.user-data
  (:require [smallworld.coordinates :as coordinates]
            [clojure.string :as str]))

(def debug? false)

(defn normal-img-to-full-size [friend]
  (let [original-url (:profile-image-url-https friend)]
    (if (nil? original-url)
      nil
      (str/replace original-url "_normal" ""))))

(defn includes? [string substr]
  (str/includes? (str/lower-case string) (str/lower-case substr)))

(defn split-last [string splitter]
  (last (str/split string splitter)))

(defn remove-substr [string substr]
  (str/replace string substr ""))

(defn remove-emoji [string]
  (remove-substr string #"[\uD83C-\uDBFF\uDC00-\uDFFF]+"))

(defn normalize-location [name] ; case insensitive – used for coordinate lookup only, not for display
  (let [_s (or name "")
        _s (remove-substr _s #"(?i)they/them")
        _s (remove-substr _s #"(?i)she/her")
        _s (remove-substr _s #"(?i)he/him")
        _s (remove-substr _s #"(?i) soon")
        _s (remove-substr _s #"(?i) mostly")
        _s (remove-substr _s #"(?i) still")
        _s (remove-substr _s #"(?i)Planet Earth")
        _s (remove-substr _s #"(?i)Earth")
        _s (if (= _s "Canada") "whiteshell provincial park, canada" _s) ; approx. center of population in Canada
        _s (if (= _s "Québec") "Québec, Québec, Canada" _s) ; they probably meant Quebec city, not Québec province
        _s (if (includes? _s "subscribe")     "" _s)
        _s (if (includes? _s ".com")          "" _s)
        _s (if (includes? _s ".eth")          "" _s)
        _s (if (includes? _s "zoom")          "" _s)
        _s (if (includes? _s "san francisco") "San Francisco, CA" _s)
        _s (if (includes? _s "sf, ")          "San Francisco, CA" _s)
        _s (if (includes? _s "nyc")           "New York City" _s)
        _s (if (includes? _s "new york, ny")  "New York City" _s)
        _s (remove-substr _s #" \([^)]*\)") ; remove anything in parentheses (e.g. "sf (still!)" → "sf")
        _s (split-last _s #"→")
        _s (split-last _s #"·")
        _s (split-last _s #"•")
        _s (split-last _s #"✈️")
        _s (split-last _s #"🔜")
        _s (split-last _s #"➡️")
        _s (remove-emoji _s) ; this has to come after the splitting on specific emoji
        _s (split-last _s #"\/")
        _s (split-last _s #" and ")
        _s (split-last _s #"\|")
        _s (if (> (count (str/split _s #" ")) 3) "" _s) ; if there are more than 3 words, it's probably a sentence and not a place name
        _s (str/trim _s)
        _s (remove-substr _s #",$") ; remove trailing comma
        ]
    _s))

(defn location-from-name [name]
  (let [_s (str/replace name #" in " "|")
        _s (str/replace name #" | "  "|")
        _s (str/replace name #" visiting "  "|")
        _s (str/split _s #"\|")]
    (if (> 1 (count _s)) ; if there's only 1 element, assume they didn't put a location in their name
      (normalize-location (last _s))
      "")))

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
        friend-main-location (normalize-location (:location friend))
        friend-name-location (location-from-name (:name friend))
        ; locations as coordinates
        friend-main-coords  (coordinates/memoized (or friend-main-location ""))
        friend-name-coords  (coordinates/memoized (or friend-name-location ""))]

    (when debug?
      (println "---------------------------------------------------")
      (println)
      (println "             location:" (:location friend))
      (println " friend-main-location:" friend-main-location)
      (println "                 name:" (:name friend))
      (println " friend-name-location:" friend-name-location)
      (println)
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
                    :name           (:location friend)
                    :coords         friend-main-coords
                    :distances      (distances-map is-current-user? current-user friend-main-coords)})

                 (when (not (str/blank? friend-name-location))
                   {:special-status "from-display-name" ; formerly called "name location"
                    :name           (:name friend)
                    :coords         friend-name-coords
                    :distances      (distances-map is-current-user? current-user friend-name-coords)})]}))
