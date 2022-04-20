(ns smallworld.worker
  (:gen-class) ; forces Clojure to make a Java class that you can find through the jar
  (:require [smallworld.db :as db]
            [smallworld.web :as backend]))

(defn timestamp [] (str (new java.util.Date)))

(defn log [string]
  (println (timestamp) "--" string))

(defn try-to-refresh-friends [total-count]
  (fn [i user]
    ; TODO: figure out why none of the stuff inside of here is printing when
    ; it's run by the bash script, even though it is run when run from the repl
    (try
      (log (str "[user " i "/" total-count "] refresh friends for " (:screen_name user)))
      (backend/refresh-friends-from-twitter user)
      (catch Throwable e
        (println "\ncouldn't refresh friends for user" (:screen_name user))
        (println e)
        nil))))

(defn -main []
  (println)
  (println "===============================================")
  (log "starting worker.clj")
  (println)
  (let [all-users (db/select-all db/settings-table)
        curried-refresh-friends (try-to-refresh-friends (count all-users))]
    (log (str "preparing to refresh friends for " (count all-users) " users!"))
    (map-indexed curried-refresh-friends all-users))
  (println)
  (log "finished worker.clj")
  (println "===============================================")
  (println))
