(ns meetcute.util
  (:require [clojure.string :as str]))

(defn clean-phone [phone]
  (some-> phone (str/replace #"[^0-9]" "")))

(defn valid-phone?
  "Strips the phone number of all non-numeric characters, then check if it's a valid phone number. "
  [phone]
  (let [phone (or (some-> phone clean-phone) "")]
    (and (not-empty phone) (re-find #"^\d{10}$" phone))))

