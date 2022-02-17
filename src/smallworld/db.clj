(ns smallworld.db (:require [clojure.java.jdbc :as sql] ; https://clojure.github.io/java.jdbc
                            [clojure.pprint :as pp]
                            [smallworld.clj-postgresql.types] ; this enables the :json type
                            [smallworld.util :as util]
                            [clojure.walk :as walk]
                            [clojure.string :as str]))

(def debug? false)
(def url (util/get-env-var "DATABASE_URL"))
(def memoized-data-schema [[:id          :integer   "primary key" "generated always as identity"]
                           [:request_key :text      "not null" "unique"]
                           [:data        :json]
                           [:created_at  :timestamp "default current_timestamp"]
                           [:updated_at  :timestamp "default current_timestamp"]
                           ; TODO: get "on update current_timestamp" working for :updated_at
                           ; I thought the following would work, but the db throws an error:
                           ;; [:updated_at  :timestamp "default current_timestamp" "on update current_timestamp"]
                           ])
(def settings-schema [[:id                      :integer   "primary key" "generated always as identity"]
                      [:screen_name             :text      "not null"    "unique"]
                      [:main_location_corrected :text]
                      [:name_location_corrected :text]
                      [:welcome_flow_complete   :boolean   "not null"    "default false"]
                      [:created_at              :timestamp "default current_timestamp"]
                      [:updated_at              :timestamp "default current_timestamp"]
                      ; TODO: get "on update current_timestamp" working for :updated_at
                      ; I thought the following would work, but the db throws an error:
                      ;; [:updated_at  :timestamp "default current_timestamp" "on update current_timestamp"]
                      ])

; table names
(def friends-table       :friends)
(def settings-table      :settings)
(def coordinates-table   :coordinates)
(def access_tokens-table :access_tokens)

(defn escape-str [str] ; TODO: replace this this the ? syntax, which escapes for you
  (str/replace str "'" "''"))

(defn where [column-name value]
  (str " where " (name column-name) " = '" (escape-str value) "'"))

(defn table-exists? [table-name]
  (->> table-name
       name
       escape-str
       (#(sql/query url (str "SELECT table_name FROM information_schema.tables where table_name = '" % "'")))
       count
       (not= 0)))

(defn create-table [table-name schema]
  (if (table-exists? table-name)
    (println "table" table-name "already exists")
    (do
      (println "creating table"  table-name)
      (sql/db-do-commands url (sql/create-table-ddl (name table-name) schema)))))

(defn recreate-table [table-name schema] ; leave this commented out by default, since it's destructive
  (sql/db-do-commands url (str " drop table if exists " (name table-name)))
  (create-table table-name schema)
  (when debug?
    (println "done dropping table named " table-name " (if it existed)")
    (println "done creating table named " table-name)))

(defn show-all [table-name]
  (println)
  (let [results (if (= table-name friends-table)
                  (sql/query url (str "select request_key from users"))
                  (sql/query url (str "select * from " (name table-name))))]
    (pp/pprint results)
    (when (= table-name friends-table)
      (println "not printing {:data {:friends}} because it's too long"))
    (println "\ncount: " (count results)))
  (println))

(defn select-by-col [table-name col-name col-value]
  (when debug?
    (println "(select-by-col" table-name col-name col-value ")"))
  (if (nil? col-value)
    []
    (walk/keywordize-keys
     (sql/query url (str "select * from " (name table-name)
                         (where col-name col-value))))))

(defn insert! [table-name data]
  (when debug?
    (println "inserting the following data into" table-name)
    (pp/pprint data))
  (sql/insert! url table-name data))

; TODO: this was meant to simplify the code, but it's best to just replace it
; everywhere with sql/update! probably
(defn update! [table-name col-name col-value new-json]
  (sql/update! url table-name new-json [(str (name col-name) " = ?")
                                        col-value]))

; TODO: turn this into a single query to speed it up
(defn memoized-insert-or-update! [table-name request_key data]
  (let [sql-results (select-by-col table-name :request_key request_key)
        exists? (not= 0 (count sql-results))]
    (when debug?
      (println "result:" sql-results)
      (println "exists? " exists?)
      (pp/pprint (select-by-col table-name :request_key request_key)))
    (if-not exists?
      (insert! table-name {:request_key request_key :data data})
      (update! table-name :request_key request_key {:data data}))))

(defn insert-or-update! [table-name col-name data]
  (let [col-name    (keyword col-name)
        col-value   (get data col-name)
        sql-results (select-by-col table-name col-name col-value)
        exists?     (not= 0 (count sql-results))]
    (when debug?
      (println "result:" sql-results)
      (println "exists? " exists?)
      (pp/pprint (select-by-col table-name col-name col-value)))
    (if-not exists?
      (insert! table-name data)
      (update! table-name col-name col-value data))))

(comment
  (recreate-table settings-table settings-schema)
  (insert! settings-table {:screen_name "aaa" :main_location_corrected "bbb"})
  (update! settings-table :screen_name "aaa" {:welcome_flow_complete true})
  (update! settings-table :screen_name "aaa" {:screen_name "foo"})
  (update! settings-table :screen_name "foo" {:screen_name "aaa"})
  (insert-or-update! settings-table
                     :screen_name
                     {:screen_name "devonzuegel" :main_location_corrected "bbb"})
  (insert-or-update! settings-table
                     :screen_name
                     {:screen_name "devon_dos" :welcome_flow_complete false})
  (select-by-col settings-table :screen_name "devonzuegel")
  (show-all settings-table)

  (recreate-table friends-table memoized-data-schema)
  (select-by-col friends-table :request_key "devonzuegel")
  (select-by-col friends-table :request_key "devon_dos")
  (select-by-col friends-table :request_key "meadowmaus")
  (show-all friends-table)

  (sql/delete! url friends-table         ["request_key = ?" "devonzuegel"])
  (sql/delete! url access_tokens-table ["request_key = ?" "devonzuegel"])
  (show-all access_tokens-table)

  (select-by-col access_tokens-table :request_key "devonzuegel")
  (select-by-col access_tokens-table :request_key "meadowmaus")

  (recreate-table :coordinates memoized-data-schema)
  (show-all :coordinates)
  (pp/pprint (select-by-col :coordinates :request_key "Miami Beach"))
  (update! :coordinates :request_key "Miami Beach" {:data {:lat 25.792236328125 :lng -80.13484954833984}})
  (select-by-col :coordinates :request_key "spain"))
