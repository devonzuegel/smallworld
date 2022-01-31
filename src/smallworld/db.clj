(ns smallworld.db (:require [clojure.java.jdbc :as sql]
                            [clojure.data.json :as json]
                            [clojure.pprint :as pp]
                            [clj-postgresql.types]))

(def url "postgresql://localhost:5432/devonzuegel") ; TODO: this will have to accept an enviroment variable
(def table-name :users)
(def users-schema [[:id          :integer "primary key" "generated always as identity"]
                   [:screen_name :text    "not null" "unique"]
                   [:data        :json]
                   [:created_at  :timestamp "default current_timestamp"]
                   [:updated_at  :timestamp "default current_timestamp"]
                     ; TODO: get "on update current_timestamp" working for :updated_at
                     ; I thought the following would work, but the db throws an error:
                     ;; [:updated_at  :timestamp "default current_timestamp" "on update current_timestamp"]
                   ])

(defn recreate-table []
  (sql/db-do-commands url (str " drop table if exists " (symbol table-name)))
  (println "done dropping table named" table-name "(if it existed)")

  (sql/db-do-commands url (sql/create-table-ddl table-name users-schema))
  (println "done creating table named" table-name))

(defn show-all []
  (pp/pprint (sql/query url ["select * from users"]))
  (println "done selecting all from" table-name))

(defn insert! [screen-name value]
  (let [data {:screen_name screen-name
              :data {:value (str value)}}]
    (sql/insert! url table-name data)
    (println "inserted" (str data) "into" table-name)))

(defn sample []
  (recreate-table)

  (let [data {:screen_name "devonzuegel"
              :data {:a "1"}}]
    (sql/insert! url table-name data)
    (println "done inserting devonzuegel into" table-name))

  (show-all)

  (let [new-data {:data {:b "2"}}]
    (pp/pprint (sql/update! url table-name new-data ["screen_name = ?" "devonzuegel"]))
    (println "done updating devonzuegel"))

  (show-all))

;; (try
;;   (sample)
;;   (catch Throwable t (pp/pprint t)))

;; (pp/pprint (sql/query url ["select users from tables"]))
;; (pp/pprint (sql/query url ["show tables"]))

;; (sql/db-do-commands url (str "show tables" (symbol table-name)))
;; (sql/db-do-commands url (str "show user_tables"))