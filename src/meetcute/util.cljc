(ns meetcute.util
  (:require [clojure.string :as str]))

(defn clean-phone [phone]
  (if (nil? phone)
    nil
    (str/replace phone #"[^0-9]" "")))

