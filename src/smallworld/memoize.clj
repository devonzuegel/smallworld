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
  (update! [this request-key value]
    (when (.createNewFile this) ;; creates new file & returns true iff it doesn't exist
      (spit this "{}"))
    (spit this (assoc (read-string (slurp this)) request-key value)))
  (read! [this request-key]
    (when (.createNewFile this) ;; creates new file & returns true iff it doesn't exist
      (spit this "{}"))
    (get (read-string (slurp this)) request-key ::not-found)))

(def debug? false)

(defn my-memoize
  ([expensive-fn cache]
   (fn [& [request-key & optional-args :as all-args]]
     (assert (string? request-key) "my-memoize requires the request key to be a string")

     (when debug?
       (println "")
       (println "all-args:")
       (println all-args)
       (println "")
       (println "optional-args:")
       (println (or optional-args "  no optional args"))
       (println ""))

     (if (= ::not-found (read! cache request-key))

       ;; if we haven't seen the request before, then we need to compute the value
       (let [result (if optional-args
                      (apply expensive-fn all-args)
                      (expensive-fn request-key))]
         (if (= :failed result)
           ;; if the expensive function failed, don't cache the result
           (do (when debug? (println "🔴 failed to fetch result for:" request-key))
               :failed)
           ;; if the expensive function succeeded, cache the result
           (do (when debug? (println "🟢 fetch for first time:" request-key #_"→" #_result))
               (update! cache request-key result)
               result)))

       ;; if we've seen the request before, then just return the cached value
       (let [result (read! cache request-key)]
         (when debug? (println "🟡 retrieving stored result:" request-key #_"→" #_result))
         result)))))
