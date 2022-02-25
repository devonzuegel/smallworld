(ns smallworld.util (:require [clojure.pprint :as pp]
                              [clojure.java.io :as io]))

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(defn store-to-file [filename data]
  (let [result (with-out-str (pr data))]
    (spit filename result)))

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(defn read-from-file [filename]
  (read-string (slurp (io/resource filename))))

(defn get-env-var [key]
  (let [value (System/getenv key)]
    (when (nil? value) (throw (Throwable. (str "Environment variable not set: " key))))
    value))

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
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

(defn rand-str [len]
  (apply str
         (for [i (range len)]
           (char (+ (rand 26) 65)))))