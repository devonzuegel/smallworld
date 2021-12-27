(ns smallworld.memoize)

(defonce cache (atom {}))

(defn expensive-fn [request-str] 123)

(defn test [request-str]
  ;; check if we've seen the request before
  (if (nil? (get @cache request-str))

    (let [result (expensive-fn request-str)]
      (println "fetch the result for the first time! " result)
      (swap! cache #(assoc % request-str result))
      result)

    (let [result (get @cache request-str)]
      (println "retrieving stored result: " result)
      result)))