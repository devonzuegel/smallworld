(ns meetcute.util
  (:require [clojure.string :as str]))

(defn clean-phone [phone]
  (if (nil? phone)
    nil
    (str/replace phone #"[^0-9]" "")))

(defn valid-phone? [phone]
  {:post [(boolean? %)]}
  (let [phone (some-> phone clean-phone)]
    (and (string? phone)
         (not (empty? phone))
         (< 9 (count phone)))))
