(ns meetcute.util
  (:require [clojure.string :as str]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; phone utils

(defn clean-phone
  "The standard format is +{AREA}{NUMBER} without separators. Examples:

   +14159499931
   +16507919090
   +5491137560419"
  [phone]
  (let [only-numbers (some-> phone (str/replace #"[^0-9]" ""))]
    (some->> only-numbers (str "+"))))

;; TODO: use a proper validation function
(defn valid-phone?
  "Strips the phone number of all non-numeric characters, then check if it's a valid phone number. "
  [phone]
  (let [phone (or (some-> phone clean-phone) "")]
    (and (not-empty phone)
         (<= 9 (count phone)))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; profile utils

(defn get-field [bio field]
  (get-in bio [(keyword field)]))

(defn get-gender-filter [bio]
  (case (get-field bio "I'm interested in...")
    [] []
    ["Women"] ["Woman"]
    ["Men"] ["Man"]
    ["Woman" "Man"] ; default
    ))

(defn included-bios [profile cuties]
  (let [matches-preferences? (fn [cutie]
                               (and (= (get-field cutie "Include in gallery?")  "include in gallery")         ; don't include bios that have been explicitly excluded
                                    (not   (= (:id cutie "id")                  (get-field profile "id")))    ; check the cutie is not themself
                                    (some #(= (:Gender cutie) %)                (get-gender-filter profile))  ; only show the gender that the user is interested in dating
                                    (some #(= (:Gender profile) %)              (get-gender-filter cutie)     ; only show someone if they're interested in dating someone of the gender of the current user:
                                          )))]
    (filter matches-preferences? cuties)))
