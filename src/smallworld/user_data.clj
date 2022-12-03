(ns smallworld.user-data
  (:require [clojure.string :as str]
            [clojure.test :refer [deftest is]]
            [smallworld.coordinates :as coordinates])
  (:import (java.util.regex Pattern)))

(def debug? false)

(defn ^StringBuilder fast-replace [^StringBuilder sb ^Pattern pattern  ^String replacement]
  (let [matcher (.matcher pattern sb)]
    (loop [start 0]
      (when (.find matcher start)
        (.replace sb (.start matcher) (.end matcher) replacement)
        (recur (+ (.start matcher) (.length replacement))))))
  sb)

(defn normal-img-to-full-size [friend]
  (let [original-url (:profile-image-url-https friend)]
    (if (nil? original-url)
      nil
      (str (fast-replace (StringBuilder. original-url) #"_normal" "")))))

(defn includes? [string substr]
  (str/includes? (str/lower-case string) substr))

(defn split-last [string splitter]
  (or (last (str/split string splitter)) ""))

(defn remove-substr [^StringBuilder sb substr]
  (fast-replace sb substr ""))

(defn normalize-location [^String name] ; case insensitive – used for coordinate lookup only, not for display
  (let [_s (-> (str/lower-case (or name ""))
               (StringBuilder.)
               (remove-substr #"(?i)they/them")
               (remove-substr #"(?i)she/her")
               (remove-substr #"(?i)he/him")
               (remove-substr #"(?i) soon")
               (remove-substr #"(?i) mostly")
               (remove-substr #"(?i) still")
               (remove-substr #"(?i)Planet Earth")
               (remove-substr #"(?i)Earth")
               (str))]
    (cond
      (= _s "sf")                        "san francisco, california"
      (= _s "home")                      ""
      (includes? _s "at home")           ""
      (includes? _s "subscribe")         ""
      (includes? _s ".com")              ""
      (includes? _s ".net")              ""
      (includes? _s ".org")              ""
      (includes? _s ".eth")              ""
      (includes? _s "solana")            ""
      (includes? _s "blue/green sphere") ""
      (includes? _s "pale blue dot")     ""
      (includes? _s "zoom")              ""
      (includes? _s "san francisco")     "san francisco, california"
      (includes? _s "sf, ")              "san francisco, california"
      (includes? _s "nyc")               "new york city"
      (includes? _s "new york")          "new york city"
      :else (let [_s (StringBuilder. _s)
                  _s (remove-substr _s #" \([^)]*\)") ; remove anything in parentheses (e.g. "sf (still!)" → "sf")
                  _s (if (= "bay area")
                       _s
                       (str/replace _s #"(?i) area$" ""))
                  _s (-> (str _s)
                         (split-last #"\/")
                         (split-last #" and ")
                         (split-last #"\|")
                         (split-last #"→")
                         (split-last #"·")
                         (split-last #"•")
                         (split-last #"✈️")
                         (split-last #"🔜"))
                  ^String _s (split-last _s #"➡️")
                  _s (StringBuilder. _s)
                  _s (remove-substr _s #",$") ; remove trailing comma
                  _s (fast-replace _s #"[^a-zA-Z]" " ") ; remove any remaining non-letter/non-comma strings with spaces (e.g. emoji)
                  _s (str _s)]
              (cond
                (> (count (str/split _s #" ")) 3) "" ; if there are more than 3 words, it's probably a sentence and not a place name
                (= _s "new york")         "new york city"
                (= _s "california")       "san francisco"
                (= _s "bay area")         "san francisco bay"
                (= _s "british columbia") "vancouver, canada"          ; approx. center of population in British Columbia
                (= _s "canada")           "whiteshell provincial park" ; approx. center of population in Canada
                (= _s "québec")           "québec, québec, canada"     ; they probably meant Quebec city, not Québec province
                :else (str/trim _s))))))

(deftest test-normalize-location
  (is (= (normalize-location "sf, foobar")   "san francisco, california"))
  (is (= (normalize-location "New York")     "new york city"))
  (is (= (normalize-location "Zoom")         ""))
  (is (= (normalize-location "Seattle area") "seattle"))
  (is (= (normalize-location "CHQ → London") "london"))
  (is (= (normalize-location "Planet Earth") "")))

(defn title-case [s]
  (->> (str/split (str s) #"\b")
       (map str/capitalize)
       str/join))

(defn location-from-name [name]
  (let [_s (str/replace name #" in " "|")
        _s (str/replace _s #" \| "  "|")
        _s (str/replace _s #"(?i) soon!?$" "")
        _s (str/replace _s #" visiting "  "|")
        _s (str/replace _s #" at "  "|")
        _s (str/split _s #"\|")]
    (if (< 1 (count _s)) ; if there's only 1 element, assume they didn't put a location in their name
      (title-case (normalize-location (last _s)))
      "")))

(deftest test-location-from-name
  (is (= (location-from-name "Devon ☀️ in Buenos Aires")    "Buenos Aires"))
  (is (= (location-from-name "Devon visiting SF")           "San Francisco, California"))
  (is (= (location-from-name "Devon visiting London soon")  "London"))
  (is (= (location-from-name "Devon visiting London soon!") "London"))
  (is (= (location-from-name "Miami Beach Police")          ""))
  (is (= (location-from-name "Fairchild Garden")            ""))
  (is (= (location-from-name "Devon")                       "")))

(defn distances-map [is-current-user? current-user friend-coords]
  (when (not is-current-user?) ; distances aren't relevant if the friend whose data we're abridging is the current user
    (zipmap
     (map :name (:locations current-user))
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
                    :name           friend-name-location
                    :coords         friend-name-coords
                    :distances      (distances-map is-current-user? current-user friend-name-coords)})]}))
