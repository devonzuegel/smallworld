(ns smallworld.memoize
  (:refer-clojure :exclude [memoize]))

(defprotocol ICache
  (update! [this request-key value])
  (read!   [this request-key]))

(extend-protocol ICache
  clojure.lang.Atom
  (update! [this request-key value] (swap! this #(assoc % request-key value)))
  (read!   [this request-key]       (get @this request-key ::not-found)))

(extend-protocol ICache
  java.io.File
  ; assume that the client has given me a file that exists
  (update! [this request-key value] (spit this (assoc (read-string (slurp this)) request-key value)))
  (read!   [this request-key]       (get (read-string (slurp this)) request-key ::not-found)))

(defn my-memoize
  ([expensive-fn cache]
   (fn [request-key]
     (assert (string? request-key)
             "my-memoize requires the request key to be a string")

     (if (= ::not-found (read! cache request-key)) ;; check if we've seen the request before

       (let [result (expensive-fn request-key)]
        ;;  (println "🟢 fetch for first time: " request-key " → " result)
         (update! cache request-key result)
         result)

       (let [result (read! cache request-key)]
        ;;  (println "🟡 retrieving stored result: " request-key " → " result)
         result)))))
