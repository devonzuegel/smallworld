(ns smallworld.util (:require [clojure.pprint :as pp]))

(defn store-to-file [filename data]
  (let [result (with-out-str (pr data))]
    (spit filename result)))

(defn read-from-file [filename]
  (read-string (slurp (clojure.java.io/resource filename))))

(defn get-env-var [key]
  (let [value (System/getenv key)]
    (when (nil? value) (throw (Throwable. (str "Environment variable not set: " key))))
    value))

(defn server-logger [handler]
  (fn [request]
    (println "\n=======================================================")
    (println "request:")
    (pp/pprint request)
    (println "")
    (let [response (handler request)]
      (println "response:")
      (pp/pprint response)
      (println "=======================================================\n")
      response)))