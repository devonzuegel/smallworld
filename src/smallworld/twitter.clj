(ns smallworld.twitter
  (:require [twttr.auth :refer [env->UserCredentials]]
            [yesql.core :refer [defqueries]]))

;; (def screen-name "sebasbensu")

;; ; Define a database connection spec. (This is standard clojure.java.jdbc.)
;; (def db-spec {:classname "org.postgresql.Driver"
;;               :subprotocol "postgresql"
;;               :subname "//localhost:5432/smallworld-local"
;;               :user "devonzuegel"})

;; ; Import the SQL query as a function.
;; (defqueries "queries/users.sql" {:connection db-spec})


; read credentials from environment variables, namely:
; CONSUMER_KEY, CONSUMER_SECRET, ACCESS_TOKEN, and ACCESS_TOKEN_SECRET
(def creds (env->UserCredentials))
(def result_count 200) ;; 200 is the max allowed by the Twitter API


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

;; ;; ;; Don't run this too often! You will hit the Twitter rate limit very quickly.
;; ;; (store-to-file ((:sebas-friends filenames) fetch-friends-from-twitter-api))



;; ;; put this back into frontend.clj
;; (defn music []
;;   [:iframe {:src "https://open.spotify.com/embed/track/3fWTQXs897m4H1zsai8SOk?utm_source=generator&theme=0"
;;             :width "100%"
;;             :height "80"
;;             :frameBorder "0"
;;             :allowFullScreen ""
;;             :allow "autoplay; clipboard-write; encrypted-media; fullscreen; picture-in-picture"}])
