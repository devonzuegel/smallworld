(ns smallworld.util (:require [clojure.pprint  :as pp]
                              [clojure.data.json :as json]
                              [clojure.java.io :as io]))

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(defn store-to-file [filename data]
  (let [result (with-out-str (pr data))]
    (spit filename result)))

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(defn read-from-file [filename]
  (read-string (slurp (io/resource filename))))

(defn read-json-from-file [filename]
  (json/read-str (slurp (io/resource filename)) :key-fn keyword))

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

(defn timestamp [] (str (new java.util.Date)))

(defn log [string]
  (println (timestamp) "--" string))

(def ENVIRONMENTS {:prod  "prod-heroku"
                   :local "dev-m1-macbook"})