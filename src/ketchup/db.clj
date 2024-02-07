(ns ketchup.db
  (:require [clojure.java.jdbc :as sql]
            [clojure.pprint :as pp]
            [clojure.string :as str]
            [clojure.walk :as walk]
            [jdbc.pool.c3p0 :as pool]
            [ketchup.env :as env]))

;; ================================================================== 
;; DB connection

(def debug? false)

(def db-uri (java.net.URI. (env/get-env-var "DATABASE_URL")))

(def user-and-password
  (if (nil? (.getUserInfo db-uri))
    nil (clojure.string/split (.getUserInfo db-uri) #":")))

(def pool
  (delay
    (pool/make-datasource-spec
     {:classname "org.postgresql.Driver"
      :subprotocol "postgresql"
      :user (get user-and-password 0)
      :password (get user-and-password 1)
      :subname (if (= -1 (.getPort db-uri))
                 (format "//%s%s" (.getHost db-uri) (.getPath db-uri))
                 (format "//%s:%s%s" (.getHost db-uri) (.getPort db-uri) (.getPath db-uri)))})))

;; ==================================================================
;; Private utils

(defn escape-str [str] ; TODO: replace this this the ? syntax, which escapes for you
  (str/replace str "'" "''"))

(defn where [column-name value]
  (str " where " (name column-name) " = '" (escape-str value) "'"))

(defn insert! [table-name data]
  (when debug?
    (println "inserting the following data into" table-name)
    (pp/pprint data))
  (sql/insert! @pool table-name data))

(defn select-by-col [table-name col-name col-value]
  (when debug?
    (println "(select-by-col" table-name col-name col-value ")"))
  (if (nil? col-value)
    []
    (walk/keywordize-keys
     (sql/query @pool (str "select * from " (name table-name)
                           (where col-name col-value))))))

(defn find-or-insert! [table-name col-name data]
  (let [found-in-db (select-by-col table-name col-name (get-in data [col-name]))]
    (if (empty? found-in-db)
      (insert! table-name data)
      (first found-in-db))))

(defn select-all [table]
  (sql/query @pool (str "select * from " (name table))))

;; ==================================================================
;; API

(def users-table :users)   ;; stores screen_name of the user who the admin is impersonating (for debug only)

(defn find-or-insert-user! [data]
  (let [user-data (merge {:screen_name (:phone data)} ; if they didn't provide a screen_name, use their phone number
                         data)]
    (find-or-insert! users-table :phone user-data)))

(defn update-user-last-ping! [id status]
  (println "updating user last ping for id" id "to" status)
  (sql/db-do-commands @pool (str "update users set last_ping = now(), status = '" status "' "
                                 "where id = '" id "';")))