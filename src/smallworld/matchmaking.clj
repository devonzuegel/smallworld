(ns smallworld.matchmaking
  (:require [cheshire.core        :refer [generate-string]]
            [clojure.walk         :refer [keywordize-keys]]
            [clojure.core.memoize :as memoize]
            [clojure.data.json    :as json]
            [clojure.pprint       :as pp]
            [clojure.set          :as set]
            [smallworld.email     :as email]
            [meetcute.util        :as mc.util]
            [smallworld.airtable  :as airtable]
            [smallworld.util      :as util]))

(def airtable-base {:api-key (util/get-env-var "AIRTABLE_BASE_API_KEY")
                    :base-id "appF2K8ThWvtrC6Hs"})

(defn seconds [n] (* n 1000))
(defn minutes [n] (* n 60 (seconds 1)))

(defn fetch-all-bios! []
  (println "⚠️ Fetching all bios from Airtable...")
  (println "  (Avoid if possible!  Airtable will get grumpy if we hit the API too often)")
  (airtable/get-in-base airtable-base ["bios-devons-test-2"]))

(def fetch-all-bios-memoized
  (memoize/ttl fetch-all-bios! {} :ttl/threshold 1 #_(minutes 1 #_(* 60 24 7)))) ; TODO: set to 1 week just for testing

(defn get-all-bios []
  (let [all-bios-raw  (fetch-all-bios-memoized)
        all-bios-flat (map (fn [bio] (merge (:fields bio)
                                            {:id (:id bio)}))
                           all-bios-raw)]
    all-bios-flat))

(defn get-all-phones []
  (->> (get-all-bios)
       (map (fn [bio]
              (get-in bio ["Phone"])))
       (map mc.util/clean-phone)
       set))

(defn existing-phone-number? [phone]
  (let [phone (mc.util/clean-phone phone)
        all-phones (get-all-phones)]
    (contains? all-phones phone)))

(defn find-first-match [match-fn items]
  (some (fn [item]
          ;; (println "looking at: " item) ; confirm that the function only checks items until it finds a match, and then stops
          (when (match-fn item) item)) items))

(defn my-profile [phone]
  (let [all-bios (get-all-bios)
        my-bio (find-first-match (fn [bio]
                                   (= (mc.util/clean-phone phone)
                                      (mc.util/clean-phone (get-in bio ["Phone"]))))
                                 all-bios)]
    (airtable/kwdize my-bio)))

(defn update-profile [req]
  (pp/pprint "req:")
  (pp/pprint req)
  (let [parsed-body (:params req)
        all-bios (get-all-bios)
        bio-id (mc.util/get-field parsed-body "id")
        phone (mc.util/get-field parsed-body "Phone")]
    (println "")
    (println "      phone : " phone)
    (println "parsed-body : ")
    (pp/pprint parsed-body)
    (println "")

    (let [bio (first (filter (fn [this-bio]
                               (or (= bio-id
                                      (mc.util/get-field this-bio "id"))
                                   (= (mc.util/clean-phone phone)
                                      (mc.util/clean-phone (get-in this-bio ["Phone"])))))
                             all-bios))
          fields-to-change  (util/exclude-keys parsed-body [:id])]
      (if (nil? bio)
        (generate-string {:error "We couldn't find a profile with that phone number. You probably need to sign up!"})
        (do
          ;; (println "")
          ;; (pp/pprint "bio:")
          ;; (pp/pprint bio)
          (println "")
          (println "bio id: ")
          (println "  " bio-id)
          (println "")
          (println "Anything else you'd like your potential matches to know?")
          (println "  " (mc.util/get-field parsed-body "Anything else you'd like your potential matches to know?"))
          (pp/pprint "fields-to-change:")
          (pp/pprint fields-to-change)

          (let [data (-> (airtable/update-in-base airtable-base
                                                  ["bios-devons-test-2" (:id bio)]
                                                  {:fields fields-to-change})
                         :body
                         json/read-str)]
            (pp/pprint "data:")
            (pp/pprint data)
            (generate-string (airtable/kwdize data))))))))

; new feature: nightly job that update's each user's todays-cutie, prioritizing cuties who've selected them:
;   1. build the feature for just 1 user
;      - pull `todays-cutie` + the lists of `unseen-cuties`, `selected-cuties`, and `rejected-cuties` from the user's profile in AirTable
;      - if `todays-cutie` hasn't been selected yet, then put them at the *end* of the `unseen-cuties` list (TODO: check if sorting is possible/trustworthy in AirTable)
;          - note: the old `todays-cutie` will always be in one of the lists, so we don't need to change which list it's in, we just need to change the sort order so that they aren't shown again until everyone else has been shown
;      - take the first cutie from the `unseen-cuties` list and set them as `todays-cutie`
;   2. iterate through all users
;   3. make it a nightly job to update the cuties
;   4. send a daily email to each user with their todays-cutie
;   5. add a button for admin to force update todays-cutie for a single user, including sending the email

#_(defn html-response [hiccup-body] ; TODO: duplcate in auth.clj, move to util.clj
    {:status 200
     :headers {"Content-Type" "text/html"}
     :body (base-index (str (hiccup/html hiccup-body)))})

#_(defn todays-cutie [req]
    (let [my-profile (keywordize-keys (my-profile "+1 (650) 906-7099"))]
      (println "todays-cutie:" (get-in my-profile ["todays-cutie"]))
    ;; (println "about to run:      (matchmaking/update-todays-cutie @profile @bios)")
    ;; (matchmaking/update-todays-cutie @profile @bios)
    ;; (println "finished running:  (matchmaking/update-todays-cutie @profile @bios)")
      #_(html-response "TODO: refresh-cutie was just successfully run")))

(defn index-of [e coll] (first (keep-indexed #(if (= e %2)
                                                %1) coll)))

(defn move-to-end [item list]
  (if (nil? (index-of item list))
    list
    (let [index (index-of item list)]
      (concat (take index list) (drop (inc index) list) [item]))))

(defn first-name-bold [cutie]
  (str "<b>" (mc.util/get-field cutie "First name") "</b>"))

(defn refresh-todays-cutie [profile bios]
  (let [profile         (keywordize-keys profile)
        included-bios   (keywordize-keys (mc.util/included-bios profile bios))
        unseen-ids      (:unseen-cuties profile)
        todays-cutie-id (first (:todays-cutie profile))
        todays-cutie-unseen? (some #(= todays-cutie-id %) unseen-ids)]

    ; if todays-cutie-id is still in unseen-ids, then move it to the end of the list:
    (when todays-cutie-unseen?
      (println "todays-cutie-id is still in unseen-ids (i.e. the user didn't respond), so moving it to the end of the list..."))

    (let [included-ids        (map :id included-bios)
          fresh-unseen-ids    (filter #(not-any? (set (concat (:selected-cuties profile)
                                                              (:rejected-cuties profile)
                                                              (:unseen-cuties   profile))) %)
                                      included-ids)
          todays-cutie-in-selected-or-rejected? (some #(= todays-cutie-id %) (concat (:selected-cuties profile)
                                                                                     (:rejected-cuties profile)))
          unseen-ids-combined (vec (distinct (concat unseen-ids fresh-unseen-ids)))
          unseen-ids-updated  (if todays-cutie-in-selected-or-rejected? ; if todays-cutie was selected or rejected, then they are no longer unseen
                                (filter #(not= todays-cutie-id %) unseen-ids-combined)
                                (move-to-end todays-cutie-id unseen-ids-combined))
          new-todays-cutie-id (first unseen-ids-updated)
          new-todays-cutie-profile (first (filter #(= (:id %) new-todays-cutie-id) included-bios))
          new-values          {:unseen-cuties unseen-ids-updated
                               :todays-cutie  [new-todays-cutie-id]}]

      (println (count fresh-unseen-ids) "fresh-unseen-ids added to unseen-ids")

      (when (= unseen-ids-updated [])                 ; TODO: handle this case more gracefully
        (println "🟡 there are 0 unseen cuties! we should not send the user an email today, since there are no cuties to show them!"))

      (when (= unseen-ids-updated [todays-cutie-id])  ; TODO: handle this case more gracefully
        (println "🟡 todays-cutie was the only unseen-cutie, so we should not send the user emails every day, since it'll be the same cutie every day!"))

      (println "included-bios:" (count included-bios))

      (pp/pprint {:id (:id profile)
                  :old {:unseen-cuties unseen-ids
                        :todays-cutie  todays-cutie-id}
                  :fresh-unseen-ids    fresh-unseen-ids ; this will often be zero, especially when we don't have many signups
                  :new new-values})

      (airtable/update-in-base airtable-base
                               ["bios-devons-test-2" (:id profile)]
                               {:fields new-values})

      (let [email-config {:to      "avery.sara.james@gmail.com"
                           ;; :to   (:Email profile)
                          :from-name "MeetCute"
                          :subject (str "Fresh cutie! 🍊")
                          :body    (str "<div style='line-height: 1.6em'>"
                                        "Your cutie of the day is " (first-name-bold new-todays-cutie-profile) "! Would you like to meet them? <a href='https://smallworld.kiwi/meetcute'>Let us know today!</a>"
                                        "<div style='background: #eee;  color: #444;  padding: 16px 16px 8px 16px;  margin: 24px 0;  border-radius: 12px'>"
                                        "How MeetCute works:"
                                        "<ol style='padding-inline-start: 16px'>"
                                        "<li style='padding-left: 8px'>We'll send you a daily email with one new person</li>"
                                        "<li style='padding-left: 8px'>You let us know if you're interested in meeting them</li>"
                                        "<li style='padding-left: 8px'>If they're interested too, we'll introduce you!</li>"
                                        "</ol>"
                                        "</div>"
                                        "</div>")
                                          ;
                          }]

        (println "preparing to send email....... ======================")
        (pp/pprint email-config)
        (println)
        (email/send-email email-config))
      ;
      )))

(defn refresh-todays-cutie-TEST []
  (let [-my-profile {:id                              "me"
                     :Gender                          "Woman"
                     :unseen-cuties                   ["A" "B"]
                     :todays-cutie                    ["A"]
                     :selected-cuties                 []
                     :rejected-cuties                 []
                     (keyword "I'm interested in...") ["Men"]}
        -matching-criteria {:Gender                          "Man"
                            (keyword "I'm interested in...") ["Women"]
                            (keyword "Include in gallery?")  "include in gallery"}
        -bios [(merge {:id "A"} -matching-criteria)
               (merge {:id "B"} -matching-criteria)
               (merge {:id "C"} -matching-criteria)]]
    (refresh-todays-cutie -my-profile -bios)))

(defn req->parsed-jwt [req]
  (:auth/parsed-jwt req))

(defn refresh-todays-cutie-route-mine [req]
  (let [phone   (some-> (req->parsed-jwt req) :auth/phone mc.util/clean-phone)]
    (refresh-todays-cutie (my-profile phone)
                          (get-all-bios))
    (generate-string {:success true :message (str "Successfully refreshed todays-cutie for " phone)})))

(defn refresh-todays-cutie-route-all [req]
  ; TODO: only the admin should be able to hit this route
  (let [bios (get-all-bios)]
    (println "all ids")
    (pp/pprint (map :id bios)))
  (generate-string {:success true :message "TODO: need to implement /refresh-todays-cutie/all"}))
