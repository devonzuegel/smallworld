(ns smallworld.worker
  (:gen-class) ; forces Clojure to make a Java class that you can find through the jar
  (:require [smallworld.db :as db]
            [smallworld.web :as backend]))

(defn timestamp [] (str (new java.util.Date)))

(defn log [string]
  (println (timestamp) "--" string))

(defn -main []
  (println)
  (println "===============================================")
  (log "starting worker.clj")
  (println)
  (let [all-users (db/select-all db/settings-table)]
    (log (str "preparing to refresh friends for " (count all-users) " users"))
    (map backend/refresh-friends-from-twitter all-users))
  (println)
  (log "finished worker.clj")
  (println "===============================================")
  (println))