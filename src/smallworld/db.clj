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
(def settings-schema [[:id                    :integer       "primary key" "generated always as identity"]
                      [:screen_name           "varchar(255)" "not null"    "unique"] ; TODO: use this instead to enable faster lookup (avoid pointers)
                      [:welcome_flow_complete :boolean       "not null"    "default false"]
                      [:locations             :json]
                      [:friends_refresh       :json]
                      [:email_address         "varchar(255)"]
                      [:email_notifications   "varchar(255)"]
                      [:created_at            :timestamp  "default current_timestamp"]
                      [:updated_at            :timestamp  "default current_timestamp"]
                      ; TODO: get "on update current_timestamp" working for :updated_at
                      ; I thought the following would work, but the db throws an error:
                      ;; [:updated_at  :timestamp "default current_timestamp" "on update current_timestamp"]
                      ])

; table names
(def twitter-profiles-table :twitter_profiles) ; store all data from Twitter sign up
(def settings-table         :settings)         ; store Small World-specific settings
(def friends-table          :friends)          ; memoized storage: friends of the user (request_key)
(def coordinates-table      :coordinates)      ; memoized storage: map of city/country names to coordinates
(def access_tokens-table    :access_tokens)    ; memoized storage: Twitter access tokens

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

(defn select-all [table]
  (sql/query url (str "select * from " (name table))))

(defn show-all [table-name]
  (println)
  (let [results (if (= table-name friends-table)
                  (sql/query url (str "select request_key from " (name friends-table)))
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
        exists?     (not= 0 (count sql-results))
        new-data    (assoc (dissoc (merge (first sql-results) data)
                                   :id :updated_at)
                           :locations (vec (:locations (first sql-results))))]
    (when debug?
      (println "--- running fn: insert-or-update! ---------")
      (println "col-name:   " col-name)
      (println "col-value:  " col-value)
      (println "sql-results:" sql-results)
      (println "exists?     " exists?)
      (println "data (arg): " data)
      (println "new data (merged): ")
      (pp/pprint new-data)
      (println "-------------------------------------------"))
    (if-not exists?
      (insert! table-name data)
      (update! table-name col-name col-value new-data))))

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
  (insert-or-update! settings-table
                     :screen_name
                     {:screen_name "devon_dos" :email_address "1@gmail.com"})
  (select-by-col settings-table :screen_name "devonzuegel")
  (show-all settings-table)

  (do
    (println "--------------------------------")
    (println)
    (pp/pprint (:locations (first (select-by-col settings-table :screen_name "devon_dos"))))
    (println)
    (pp/pprint (:email_address (first (select-by-col settings-table :screen_name "devon_dos"))))
    (println)
    (println "--------------------------------"))

  (show-all twitter-profiles-table)

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
