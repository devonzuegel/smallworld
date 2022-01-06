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
  java.io.File ; assume that the client has given me a file that exists
  (update! [this request-key value] (spit this (assoc (read-string (slurp this)) request-key value)))
  (read!   [this request-key]       (get (read-string (slurp this)) request-key ::not-found)))

(defn my-memoize
  ([expensive-fn cache]
   (fn [request-key]
     (assert (string? request-key) "my-memoize requires the request key to be a string")

     (if (= ::not-found (read! cache request-key))

       ;; if we haven't seen the request before, then we need to compute the value
       (let [result (expensive-fn request-key)]
         (if (= :failed result)
           ;; if the expensive function failed, don't cache the result
           (do #_(println "🔴 failed to fetch result for:" request-key)
               :failed)
           ;; if the expensive function succeeded, cache the result
           (do #_(println "🟢 fetch for first time:" request-key #_"→" #_result)
               (update! cache request-key result)
               result)))

       ;; if we've seen the request before, then just return the cached value
       (let [result (read! cache request-key)]
         #_(println "🟡 retrieving stored result:" request-key #_"→" #_result)
         result)))))
