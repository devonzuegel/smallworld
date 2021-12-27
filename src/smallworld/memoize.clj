(ns smallworld.memoize
  (:refer-clojure :exclude [memoize]))

(defprotocol ICache
  (update! [this request-key value])
  (read!   [this request-key]))

(extend-protocol ICache
  clojure.lang.Atom
  (update! [this request-key value] (swap! this #(assoc % request-key value)))
  (read!   [this request-key]       (get @this request-key)))

(defn my-memoize [expensive-fn cache]
  (fn [request-key]
    (assert (string? request-key)
            "my-memoize requires the request key to be a string")

    (if (nil? (read! cache request-key)) ;; check if we've seen the request before

      (let [result (expensive-fn request-key)]
        (println "fetch the result for the first time! " result)
        (update! cache request-key result)
        result)

      (let [result (read! cache request-key)]
        (println "retrieving stored result: " result)
        result))))