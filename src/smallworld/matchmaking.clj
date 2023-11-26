(ns smallworld.matchmaking (:require [clojure.core.memoize :as memoize]
                                     [smallworld.airtable  :as airtable]
                                     [smallworld.util :as util]))

(def airtable-base {:api-key (util/get-env "AIRTABLE_BASE_API_KEY")
                    :base-id "appF2K8ThWvtrC6Hs"})

(defn seconds [n] (* n 1000))
(defn minutes [n] (* n 60 (seconds 1)))

(defn fetch-all-bios []
  (println "fetching bios from Airtable")
  (airtable/get-in-base airtable-base ["bios-devons-test"]))

(defn fetch-all-bios-memoized []
  (println "fetch-all-bios-memoized")
  (memoize/ttl fetch-all-bios {} :ttl/threshold (minutes 30)))

