(ns smallworld.worker
  (:gen-class) ; forces Clojure to make a Java class that you can find through the jar
  (:require [smallworld.db :as db]))

(defn -main []
  (println)
  (println "-----------------------------------")
  (println "starting worker.clj:" (str (new java.util.Date)))
  (println "-----------------------------------"))

(comment
  (db/show-all db/settings-table)
  (db/show-all db/friends-table)
  ;
  )