(ns smallworld.db
  (:require [clojure.java.io :as io]
            [clojure.java.jdbc :as sql] ; https://clojure.github.io/java.jdbc
            [clojure.pprint :as pp]
            [clojure.string :as str]
            [clojure.walk :as walk]
            [jdbc.pool.c3p0 :as pool]
            [schema.core :as s]
            [smallworld.clj-postgresql.types] ; this enables the :json type
            [smallworld.util :as util]))

;; Note: don't fully trust these, they are useful guidance, not type checked

(def CoordinatesRow
  {:id s/Int
   :request_key s/Str
   :data s/Any
   :created_at java.util.Date
   :updated_at java.util.Date})

(def Coordinate
  {:lat s/Num ,
   :lng s/Num})

(def Location
  {:name s/Str ,
   :coords Coordinate,
   :distances (s/maybe s/Any)
   :special-status (s/enum "twitter-location" "from-display-name")
   :name-initial-value s/Str})

(def ProfileRow
  {:id s/Int
   :request_key s/Str
   :data s/Any
   :created_at java.util.Date
   :updated_at java.util.Date})

(def Friend
  {:name (s/maybe s/Str)
   :location (s/maybe s/Str)
   :screen-name s/Str
   :id s/Int
   :profile-image-url (s/maybe s/Str)
   :profile-image-url-https (s/maybe s/Str)
   :email (s/maybe s/Str)
   s/Any s/Any})

(def AbridgedFriend
  (-> Friend
      (select-keys [:name :screen-name :email])
      (assoc :profile_image_url_large (s/maybe s/Str)
             :locations [(s/maybe Location)])))

(def FriendsRow
  {:id s/Int
   :request_key s/Str
   :data {:friends [Friend]}
   :created_at java.util.Date
   :updated_at java.util.Date})

(def Settings
  {:id s/Int
   :screen_name s/Str
   :name s/Str
   :twitter_avatar s/Str
   :welcome_flow_complete s/Bool
   :locations [Location]
   :friends_refresh (s/maybe s/Any) ;; TODO, known to be nilable
   :email_address s/Str
   :email_notifications s/Str
   :created_at java.util.Date
   :updated_at java.util.Date})

(def Impersonation
  {:screen_name (s/maybe s/Str)
   :created_at java.util.Date
   :updated_at java.util.Date})

(def debug? false)
(def db-uri (java.net.URI. (util/get-env-var "DATABASE_URL")))
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

; table names
(def twitter-profiles-table :twitter_profiles) ; store all data from Twitter sign up
(def settings-table         :settings)         ; store Small World-specific settings
(def friends-table          :friends)          ; memoized storage: friends of the user (request_key)
(def coordinates-table      :coordinates)      ; memoized storage: map of city/country names to coordinates
(def access_tokens-table    :access_tokens)    ; memoized storage: Twitter access tokens
(def impersonation-table    :impersonation)    ; stores screen_name of the user who the admin is impersonating (for debug only)

(def twitter-profiles-schema (slurp (io/resource "sql/schema-twitter-profiles.sql")))
(def settings-schema         (slurp (io/resource "sql/schema-settings.sql")))
(def friends-schema          (slurp (io/resource "sql/schema-friends.sql")))
(def coordinates-schema      (slurp (io/resource "sql/schema-coordinates.sql")))
(def access-tokens-schema    (slurp (io/resource "sql/schema-access-tokens.sql")))
(def impersonation-schema    (slurp (io/resource "sql/schema-impersonation.sql")))

(defn escape-str [str] ; TODO: replace this this the ? syntax, which escapes for you
  (str/replace str "'" "''"))

(defn where [column-name value]
  (str " where " (name column-name) " = '" (escape-str value) "'"))

(defn table-exists? [table-name]
  (->> table-name
       name
       escape-str
       (#(sql/query @pool (str "SELECT table_name FROM information_schema.tables where table_name = '" % "'")))
       count
       (not= 0)))

(defn create-table [table-name schema]
  (if (table-exists? table-name)
    (println "table" table-name "already exists")
    (do
      (println "creating table"  table-name)
      (if (string? schema)
        (sql/db-do-commands @pool (clojure.string/split schema #"--- split here ---"))
        (sql/db-do-commands @pool (sql/create-table-ddl (name table-name) schema))))))

(defn recreate-table [table-name schema] ; leave this commented out by default, since it's destructive
  (sql/db-do-commands @pool (str " drop table if exists " (name table-name)))
  (create-table table-name schema)
  (when debug?
    (println "done dropping table named " table-name " (if it existed)")
    (println "done creating table named " table-name)))

(defn select-all [table]
  (sql/query @pool (str "select * from " (name table))))

(defn select-first [table]
  (first (sql/query @pool (str "select * from " (name table) " limit 1"))))

(defn show-all [table-name]
  (println)
  (let [results (if (= table-name friends-table)
                  (sql/query @pool (str "select request_key from " (name friends-table)))
                  (sql/query @pool (str "select * from " (name table-name))))]
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
     (sql/query @pool (str "select * from " (name table-name)
                           (where col-name col-value))))))

(defn insert! [table-name data]
  (when debug?
    (println "inserting the following data into" table-name)
    (pp/pprint data))
  (sql/insert! @pool table-name data))

; TODO: this was meant to simplify the code, but it's best to just replace it
; everywhere with sql/update! probably
(defn update! [table-name col-name col-value new-json]
  (sql/update! @pool table-name new-json [(str (name col-name) " = ?")
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
        new-data    (dissoc (merge (first sql-results) data)
                            :id :updated_at)
        new-data    (if (not= table-name settings-table)
                      new-data
                      (assoc new-data ; TODO: this only applies to settings table, not any others! yuck
                             :locations (or (:locations data)
                                            (vec (:locations (first sql-results))))))]
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

;; Settings

(s/defn get-settings :- (s/maybe Settings)
  [screen-name :- s/Str]
  (first (select-by-col settings-table :screen_name screen-name)))

(s/defn upsert-settings!
  [settings :- Settings]
  (insert-or-update! settings-table :screen_name settings))

;; Impersonation

(s/defn get-current-impersonation :- (:screen_name Impersonation)
  []
  (:screen_name (select-first impersonation-table)))

;; Friends

(s/defn get-friends :- (s/maybe [Friend])
  [screen-name :- s/Str]
  (some-> (select-by-col friends-table :request_key screen-name)
          first
          (get-in [:data :friends])))

(s/defn upsert-friends!
  [screen-name :- s/Str friends :- [Friend]]
  (insert-or-update! friends-table :request_key
                     {:request_key screen-name
                      :data        {:friends friends}}))

(comment
  (do
    (println "--------------------------------")
    (println)
    (pp/pprint (first (select-by-col settings-table :screen_name "devon_dos")))
    (println)
    (pp/pprint (:email_address (first (select-by-col settings-table :screen_name "devon_dos"))))
    (println)
    (println "--------------------------------"))

  (recreate-table settings-table settings-schema)
  (insert! settings-table {:screen_name "aaa" :main_location_corrected "bbb"})
  (update! settings-table :screen_name "aaa" {:welcome_flow_complete true})
  (update! settings-table :screen_name "aaa" {:screen_name "foo"})
  (update! settings-table :screen_name "foo" {:screen_name "aaa"})
  (insert-or-update! settings-table :screen_name
                     {:screen_name "devonzuegel" :main_location_corrected "bbb"})
  (insert-or-update! settings-table :screen_name
                     {:screen_name "devon_dos" :welcome_flow_complete false})
  (insert-or-update! settings-table :screen_name
                     {:screen_name "devon_dos" :email_address "1@gmail.com"})
  (select-by-col settings-table :screen_name "devonzuegel")
  (show-all settings-table)

  (show-all twitter-profiles-table)

  (recreate-table friends-table friends-schema)
  (select-by-col friends-table :request_key "devonzuegel")
  (get-in (vec (select-by-col friends-table :request_key "devon_dos")) [0 :data :friends])
  (select-by-col friends-table :request_key "meadowmaus")
  (show-all friends-table)

  (sql/delete! @pool friends-table       ["request_key = ?" "devonzuegel"])
  (sql/delete! @pool access_tokens-table ["request_key = ?" "devonzuegel"])
  (show-all access_tokens-table)

  (select-by-col access_tokens-table :request_key "devonzuegel")
  (select-by-col access_tokens-table :request_key "meadowmaus")

  (recreate-table :coordinates friends-schema)
  (show-all :coordinates)
  (pp/pprint (select-by-col :coordinates :request_key "Miami Beach"))
  (update! :coordinates :request_key "Miami Beach" {:data {:lat 25.792236328125 :lng -80.13484954833984}})
  (select-by-col :coordinates :request_key "spain"))
