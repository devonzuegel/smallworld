(ns smallworld.matchmaking (:require [clojure.core.memoize :as memoize]
                                     [clojure.pprint       :as pp]
                                     [smallworld.airtable  :as airtable]
                                     [smallworld.util      :as util]
                                     [cheshire.core :refer [generate-string]]
                                     [clojure.data.json :as json]
                                     [clojure.string :as str]
                                     #_[goog.object :as obj]))

(def airtable-base {:api-key (util/get-env-var "AIRTABLE_BASE_API_KEY")
                    :base-id "appF2K8ThWvtrC6Hs"})

(defn seconds [n] (* n 1000))
(defn minutes [n] (* n 60 (seconds 1)))

(defn fetch-all-bios! []
  (println "fetching bios from Airtable")
  (airtable/get-in-base airtable-base ["bios-devons-test"]))

(def fetch-all-bios-memoized
  (memoize/ttl fetch-all-bios! {} :ttl/threshold (minutes 30)))

(defn get-all-bios []
  (let [all-bios-raw (fetch-all-bios-memoized)]
    (map (fn [bio] (merge (:fields bio) {:id (:id bio)})) all-bios-raw)))

(defn clean-phone [phone] (if (nil? phone)
                            nil
                            (str/replace phone #"[^0-9]" "")))

(defn update-profile [req]
  (let [parsed-body (json/read-str (slurp (:body req)) :key-fn keyword)
        all-bios (get-all-bios)
        phone (get-in parsed-body [:phone])]
    (println "")
    (println "   phone: " phone)
    (println "all-bios: ")
    (pp/pprint (map (fn [bio] (clean-phone (get-in bio ["Phone"]))) all-bios))
    (println "")

    ;; (generate-string [1 2 3 4])

    ; map through all bios and create a new array with true if the phone number matches, false if it doesn't:
    (let [matching-bios (map (fn [bio] (= (clean-phone phone)
                                          (clean-phone (get-in bio ["Phone"]))))
                             all-bios)]
      (println "matching-bios: " matching-bios)
      (println "")

      (let [bio (first (filter (fn [this-bio] (= (clean-phone phone)
                                                 (clean-phone (get-in this-bio ["Phone"]))))
                               all-bios))]
        (println "selected bio: " bio)
        (generate-string bio)

        #_(let [new-data (get-in req [:body])]
            (let [merged-data (merge bio new-data)]
              (airtable/update-in-base airtable-base ["bios-devons-test" (:id bio)] merged-data))))

      #_(generate-string [1 2 3]))))


#_(defn update-profile [phone new-data]
    (let [phone (str/replace phone #"[^0-9]" "")]
    ; get the current profile without updating it:
      (let [current-profile (airtable/get-in-base airtable-base ["bios-devons-test" phone])]
        current-profile
        #_(let [merged-data (merge current-profile new-data)]
            (airtable/update-in-base airtable-base ["bios-devons-test" phone] merged-data)))))