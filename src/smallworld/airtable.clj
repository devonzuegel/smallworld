(ns smallworld.airtable
  "A minimal Clojure client for Airtable.com's HTTP API.
   Supports retrieval of whole tables as well as individual records.
   Dependencies: [org.clojure/data.json \"0.2.6\"] [clj-http \"2.0.0\"]"
  (:require [clj-http.client :as client]
            [clojure.data.json :as json]
            [clojure.pprint :as pp]
            [clojure.set :as set]
            [clojure.string :as string]
            [clojure.string :as str]))

(def api-base "https://api.airtable.com/v0")

(defn build-request-uri-old [base-id resource-path]
  ;; (println)
  ;; (println)
  ;; (print "running `build-request-uri-old` with: ")
  ;; (println "    base-id: " base-id)
  ;; (println "    resource-path: " resource-path)
  ;; (println)
  ;; (println)
  (string/join "/" (concat [api-base (name base-id)] (map name resource-path))))

(defn build-request-uri [base-id resource-path offset]
  (let [base-id       (if (keyword? base-id) (str base-id) base-id)
        resource-path (map (fn [x] (if (keyword? x) (str x) x)) resource-path)
        resource-path (str/join "/" resource-path)
        offset        (if offset (str "?offset=" offset) "")]
    (str api-base "/" base-id "/" resource-path offset)))

(defn get-in-base*
  [{:keys [api-key base-id] :as _base} resource-path & {:keys [offset]}]
  (let [req-uri (build-request-uri base-id resource-path offset)]
    ;; (println "    req-uri:   " req-uri)
    (client/get req-uri {:headers {"Authorization" (str "Bearer " api-key)}})))

(defn kwdize [m]
  (set/rename-keys m {"id" :id
                      "fields" :fields
                      "createdTime" :created-time}))

(defn validate-base [{:keys [api-key base-id] :as _base}]
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
  [base resource-path & {:keys [offset]}]
  (validate-base base)
  (validate-resource-path resource-path)


  (let [data       (-> (get-in-base* base resource-path :offset offset) :body json/read-str)
        records    (map kwdize (get data "records"))
        new-offset (get data "offset")]
    ;; (println)
    ;; (println "running `get-in-base` with:")
    ;; (println "    offset for this page: " offset)
    ;; (println "    records in this page: " (count records))
    ;; (println "    offset for next page: " new-offset)
    (if new-offset
      (concat records (get-in-base base resource-path :offset new-offset))
      records)))

; only update the fields included in the request, do not overwrite any fields not provided
(defn update-in-base [base resource-path record-id-or-records]
  (validate-base base)
  (validate-resource-path resource-path)
  (let [req-uri (build-request-uri-old (:base-id base) resource-path)
        req-body (json/write-str (if (sequential? record-id-or-records)
                                   {:records record-id-or-records}
                                   record-id-or-records))]
    ;; (println "")
    ;; (pp/pprint "req-body:")
    ;; (pp/pprint req-body)
    ;; (println "")
    ;; (pp/pprint "record-id-or-records:")
    ;; (pp/pprint record-id-or-records)
    ;; (println "")
    (client/patch req-uri {:headers {"Authorization" (str "Bearer " (:api-key base))
                                     "Content-Type" "application/json"}
                           :body req-body})))

(defn create-in-base [base resource-path record-or-records]
  (validate-base base)
  (validate-resource-path resource-path)
  (let [req-uri (build-request-uri-old (:base-id base) resource-path)
        req-body (json/write-str (if (sequential? record-or-records)
                                   {:records record-or-records}
                                   record-or-records))]
    (client/post req-uri {:headers {"Authorization" (str "Bearer " (:api-key base))
                                    "Content-Type" "application/json"}
                          :body req-body})))
