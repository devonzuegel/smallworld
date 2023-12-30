(ns smallworld.matchmaking
  (:require [clojure.core.memoize :as memoize]
            [clojure.pprint       :as pp]
            [smallworld.airtable  :as airtable]
            [smallworld.util      :as util]
            [cheshire.core :refer [generate-string]]
            [clojure.data.json :as json]
            [meetcute.util :as mc.util]))

(def airtable-base {:api-key (util/get-env-var "AIRTABLE_BASE_API_KEY")
                    :base-id "appF2K8ThWvtrC6Hs"})

(defn seconds [n] (* n 1000))
(defn minutes [n] (* n 60 (seconds 1)))

(defn fetch-all-bios! []
  (println "fetching bios from Airtable")
  (airtable/get-in-base airtable-base ["bios-devons-test-2"]))

(def fetch-all-bios-memoized
  (memoize/ttl fetch-all-bios! {} :ttl/threshold (minutes 1 #_(* 60 24 7)))) ; TODO: set to 1 week just for testing

(defn get-all-bios []
  (let [all-bios-raw (fetch-all-bios-memoized)]
    (map (fn [bio] (merge (:fields bio) {:id (:id bio)})) all-bios-raw)))

(defn get-all-phones []
  (->> (get-all-bios)
       (map (fn [bio]
              (get-in bio ["Phone"])))
       (map mc.util/clean-phone)
       set))

(defn existing-phone-number? [phone]
  (let [phone (mc.util/clean-phone phone)
        all-phones (get-all-phones)]
    (contains? all-phones phone)))

(defn get-field [bio field]
  (get-in bio [(keyword field)]))

(defn my-profile [phone]
  (let [all-bios (get-all-bios)
        bio (first (filter (fn [bio]
                             (= phone (mc.util/clean-phone (get-in bio ["Phone"]))))
                           all-bios))]
    (generate-string {:fields (airtable/kwdize bio)})))

(defn update-profile [req]
  (pp/pprint "req:")
  (pp/pprint req)
  (let [parsed-body (:params req)
        all-bios (get-all-bios)
        bio-id (get-field parsed-body "id")
        phone (get-field parsed-body "Phone")]
    (println "")
    (println "      phone : " phone)
    (println "parsed-body : ")
    (pp/pprint parsed-body)
    (println "")

    (let [bio (first (filter (fn [this-bio]
                               (or (= bio-id
                                      (get-field this-bio "id"))
                                   (= (mc.util/clean-phone phone)
                                      (mc.util/clean-phone (get-in this-bio ["Phone"])))))
                             all-bios))
          fields-to-change  (util/exclude-keys parsed-body [:id])]
      (if (nil? bio)
        (generate-string {:error "We couldn't find a profile with that phone number. You probably need to sign up!"})
        (do
          ;; (println "")
          ;; (pp/pprint "bio:")
          ;; (pp/pprint bio)
          (println "")
          (println "bio id: ")
          (println "  " bio-id)
          (println "")
          (println "Anything else you'd like your potential matches to know?")
          (println "  " (get-field parsed-body "Anything else you'd like your potential matches to know?"))
          (pp/pprint "fields-to-change:")
          (pp/pprint fields-to-change)

          (let [data (-> (airtable/update-in-base airtable-base
                                                  ["bios-devons-test-2" (:id bio)]
                                                  {:fields fields-to-change})
                         :body
                         json/read-str)]
            (pp/pprint "data:")
            (pp/pprint data)
            (generate-string (airtable/kwdize data))))))))
