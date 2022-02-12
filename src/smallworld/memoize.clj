(ns smallworld.memoize
  (:refer-clojure :exclude [memoize])
  (:require [smallworld.db :as db]
            [clojure.pprint :as pp]
            [clojure.walk :as walk]))

(def debug? false)

(defprotocol ICache
  ; TODO: consider additing a #validate method, which I'd use for the db version
  (fetch! [this request-key value]) ; manages a newly fetched value
  (read!  [this request-key])) ; retrieves a value that was previously fetched

(extend-protocol ICache
  clojure.lang.Atom
  (fetch! [this request-key value] (swap! this #(assoc % request-key value)))
  (read!  [this request-key]       (get @this request-key ::not-found))

  java.io.File
  (fetch! [this request-key value]
    (when (.createNewFile this) ;; creates new file & returns true iff it doesn't exist
      (spit this "{}"))
    (spit this (assoc (read-string (slurp this)) request-key value)))
  (read! [this request-key]
    (when (.createNewFile this) ;; creates new file & returns true iff it doesn't exist
      (spit this "{}"))
    (get (read-string (slurp this)) request-key ::not-found))

  clojure.lang.Keyword
  (fetch! [table-name request-key result]
    (when debug?
      (println)
      (println "---------- fetch! was called ---------------")
      (println)
      (println "        table-name: " table-name)
      (println "       request-key: " request-key)
      (println "            result: " (count result))
      (println table-name))
    ; store result so it doesn't have to be fetched again
    (db/insert! table-name {:request_key request-key :data result})
    result)
  (read! [table-name request-key]
    (let [results (db/select-by-request-key table-name request-key)]
      (when debug?
        (println)
        (println "---------- read! was called ---------------")
        (println)
        (println "        table-name: " table-name)
        (println "       request-key: " request-key)
        ;; (println "           results: " results)
        )
      (if (= 0 (count results))
        ::not-found
        (walk/keywordize-keys (:data (first results)))))))

(defn my-memoize
  ([expensive-fn cache]
   (when debug? (println "\ninitializing cache: " (str cache)))
   (fn [& [request-key & optional-args :as all-args]]
     (assert (string? request-key) "my-memoize requires the request key to be a string")

     (when debug?
       (println "          all-args: " all-args)
       (println "     optional-args: " (or optional-args "no optional args")))

     (if (= ::not-found (read! cache request-key))
       ;; if we haven't seen the request before, then we need to compute the value
       (let [result (if optional-args
                      (apply expensive-fn all-args)
                      (expensive-fn request-key))]
         (if (= :failed result)
           ;; if the expensive function failed, don't cache the result
           (do (when debug?
                 (println "\n🔴 failed to fetch result for: " request-key)
                 (println))
               :failed)
           ;; else if the expensive function succeeded, cache the result
           (do (when debug?
                 (println "\n🟢 fetch for first time: " request-key #_" → " #_result)
                 (println))
               (fetch! cache request-key result)
               result)))

       ;; else if we've seen the request before, then just return the cached value
       (let [result (read! cache request-key)]
         (when debug?
           (println "\n🟡 retrieving stored result: " request-key #_" → " #_result)
           (println))
         result)))))
