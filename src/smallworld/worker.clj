(ns smallworld.worker
  (:gen-class) ; forces Clojure to make a Java class that you can find through the jar
  (:require [clojure.pprint :as pp]
            [smallworld.db   :as db]
            [smallworld.email :as email]
            [smallworld.util :as util]
            [smallworld.web  :as backend]))

(def failures (atom []))

(defn try-to-refresh-friends [total-count]
  (fn [i user]
    ; TODO: figure out why none of the stuff inside of here is printing when
    ; it's run by the bash script, even though it is run when run from the repl
    (try
      (util/log (str "[user " i "/" total-count "] refresh friends for " (:screen_name user)))
      (backend/refresh-friends-from-twitter user)
      (catch Throwable e
        (println "\ncouldn't refresh friends for user" (:screen_name user))
        (swap! failures (conj user))
        (println e)
        nil))))

(defn -main []
  (println)
  (println "===============================================")
  (util/log "starting worker.clj")
  (println)
  (let [all-users (db/select-all db/settings-table)
        n-users (count all-users)
        n-failures (count @failures)
        curried-refresh-friends (try-to-refresh-friends n-users)]
    (util/log (str "preparing to refresh friends for " n-users " users"))
    (map-indexed curried-refresh-friends all-users)
    (util/log (str "finished refreshing friends for " n-failures " users: " n-failures " failures"))
    (email/send {:to "avery.sara.james@gmail.com"
                 :subject (str "[" (util/get-env-var "ENVIRONMENT") "] worker.clj finished: " n-failures " failures out of " n-users " users")
                 :type "text/plain"
                 :body (str "finished refreshing friends for " n-failures " users: " n-failures " failures"
                            "\n\n"
                            "users that failed:\n" (with-out-str (pp/pprint @failures)))}))
  (println)
  (util/log "finished worker.clj")
  (println "===============================================")
  (println))