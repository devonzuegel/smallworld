(ns smallworld.web
  (:require [compojure.core :refer [defroutes GET ANY]]
            [compojure.handler :refer [site]]
            [compojure.route :as route]
            [clojure.java.io :as io]
            [ring.adapter.jetty :as jetty]
            [twttr.api :as api]
            [clojure.pprint :as pp]
            [cheshire.core :refer [generate-string]]
            [twttr.auth :refer [env->UserCredentials]]
            [yesql.core :refer [defqueries]]
            [environ.core :refer [env]]))

; Define a database connection spec. (This is standard clojure.java.jdbc.)
(def db-spec {:classname "org.postgresql.Driver"
              :subprotocol "postgresql"
              :subname "//localhost:5432/smallworld-local"
              :user "devonzuegel"})

; Import the SQL query as a function.
(defqueries "queries/users.sql" {:connection db-spec})

;; TODO: ask Sebas why I get "Hello World" when I refresh the Heroku page but
;; "hello small world!" when I hard refresh. Note – when I refresh OR hard-refresh
;; in Incognito mode, I just get "hello small world!" as I expect. So maybe it's
;; a cache thing?  or a chrome extension thing?  but whyyy?


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Variables ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

; read credentials from environment variables, namely:
; CONSUMER_KEY, CONSUMER_SECRET, ACCESS_TOKEN, and ACCESS_TOKEN_SECRET
(def creds        (env->UserCredentials))
(def friends      (atom ()))
(def result_count 200) ;; 200 is the max allowed by the Twitter API
(def screen-name  "sebasbensu")
(def filename     "dev/friends-sebasbensu.edn")

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Helpers ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn store-to-file [data]
  (let [result (with-out-str (pr data))]
    (spit filename result)))

(defn read-from-file []
  (read-string (slurp filename)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn fetch-friends-from-twitter-api []
  (loop [cursor nil
         result-so-far []]
    (let [api-response    (api/friends-list creds :params {:screen_name screen-name
                                                           :count       result_count
                                                           :cursor      cursor})
          page-of-friends (:users api-response)
          new-result      (concat result-so-far page-of-friends)
          screen-names    (map :screen_name (:users api-response))
          next-cursor     (:next_cursor api-response)]

      (comment
        (pp/pprint "(first screen-names): " (first screen-names))
        (pp/pprint "(count screen-names): " (count screen-names))
        (pp/pprint "next-cursor:          " next-cursor)
        (pp/pprint "friends count so far: " (count result-so-far))
        (pp/pprint "----------------------------------------"))

      (if (= next-cursor 0)
        ;; return final result if Twitter returns a cursor of 0
        new-result
        ;; else, recur by appending the page to the result so far
        (recur next-cursor new-result)))))

;; ;; Don't run this too often! You will hit the Twitter rate limit very quickly.
;; (store-to-file (fetch-friends-from-twitter-api))

(def friends-from-storage (read-from-file)) ;; TODO: store this in their local storage
(def n-friends (count (map :screen_name friends-from-storage)))
(str "@" screen-name " is following " n-friends " accounts")

(defroutes app
  (GET "/test" [] "the app has now been renamed to smallworld!!!!!!!!")
  (GET "/friends" []
    (generate-string
     (map (fn [friend]
            (select-keys friend [:name :screen_name :location]))
          friends-from-storage)))

  (GET "/" []
    (slurp (io/resource "public/index.html")))

  (route/resources "/")

  (ANY "*" []
    (route/not-found "<h1>404 Not found</h1>")))

(defonce server* (atom nil))

(defn start! [port]
  (some-> @server* (.stop))
  (let [port (Integer. (or port (env :port) 5000))
        server
        (jetty/run-jetty (site #'app)
                         {:port port :join? false})]
    (reset! server* server)))

(defn stop! []
  (.stop @server*))

(defn -main [& [port]]
  (start! port))