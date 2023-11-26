(ns smallworld.matchmaking (:require [clojure.core.memoize :as memoize]
                                     [clojure.pprint       :as pp]
                                     [smallworld.airtable  :as airtable]
                                     [smallworld.util      :as util]
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