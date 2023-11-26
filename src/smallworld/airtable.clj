(ns smallworld.airtable
  "A minimal Clojure client for Airtable.com's HTTP API.
   Supports retrieval of whole tables as well as individual records.
   Dependencies: [org.clojure/data.json \"0.2.6\"] [clj-http \"2.0.0\"]"
  (:require [clj-http.client :as client]
            [clojure.data.json :as json]
            [clojure.set :as set]
            [clojure.string :as string]))

(def api-base "https://api.airtable.com/v0")

(defn build-request-uri [base-id resource-path]
  (string/join "/" (concat [api-base (name base-id)] (map name resource-path))))

(defn get-in-base*
  [{:keys [api-key base-id] :as base} resource-path]
  (let [req-uri (build-request-uri base-id resource-path)]
    (client/get req-uri {:headers {"Authorization" (str "Bearer " api-key)}})))

(defn ^:private kwdize [m]
  (set/rename-keys m {"id" :id
                      "fields" :fields
                      "createdTime" :created-time}))

(defn validate-base [{:keys [api-key base-id] :as base}]
  (assert api-key ":api-key must present in passed credentials")
  (assert base-id ":base-id must present in passed credentials"))

(defn validate-resource-path [resource-path]
  (assert (sequential? resource-path)  "resource-path must be a sequence")
  (assert (<= (count resource-path) 2) "resource-path can't have more than two items"))

(defn get-in-base
  "Retrieve tables and records from a table.
   `base` needs to be a map containing `:api-key` and `:base-id`.
   `resource-path` must be a sequence containing the table name 
   and an optional record id.
   `:base-id` and elements in `resource-path` can be strings or keywords."
  [base resource-path]
  (validate-base base)
  (validate-resource-path resource-path)
  (let [data (-> (get-in-base* base resource-path) :body json/read-str)]
    (if (= (count resource-path) 1)
      (map kwdize (get data "records"))
      (kwdize data))))