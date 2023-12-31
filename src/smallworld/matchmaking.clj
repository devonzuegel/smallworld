(ns smallworld.matchmaking
  (:require [clojure.core.memoize :as memoize]
            [clojure.pprint       :as pp]
            [clojure.walk :refer [keywordize-keys]]
            [smallworld.airtable  :as airtable]
            [smallworld.util      :as util]
            [cheshire.core :refer [generate-string]]
            [clojure.data.json :as json]
            [meetcute.util :as mc.util]))

(def airtable-base {:api-key (util/get-env-var "AIRTABLE_BASE_API_KEY")
                    :base-id "appF2K8ThWvtrC6Hs"})

(defn seconds [n] (* n 1000))
(defn minutes [n] (* n 60 (seconds 1)))

(defn fetch-all-bios! []
  (println "⚠️ fetching all bios from Airtable... (avoid this if possible! airtable will get grumpy if we hit the api too often)")
  (airtable/get-in-base airtable-base ["bios-devons-test-2"]))

(def fetch-all-bios-memoized
  (memoize/ttl fetch-all-bios! {} :ttl/threshold (minutes 1 #_(* 60 24 7)))) ; TODO: set to 1 week just for testing

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

(defn move-to-end [item lst]
  (let [idx (index-of item lst)]
    (concat (take idx lst) (drop (inc idx) lst) [item])))

(defn update-todays-cutie [profile bios]
  (let [profile         (keywordize-keys profile)
        included-bios   (keywordize-keys (mc.util/included-bios profile bios))
        unseen-ids      (:unseen-cuties profile)
        todays-cutie-id (first (:todays-cutie profile))
        todays-cutie    (first (filter #(= (:id %) todays-cutie-id)
                                       included-bios))
        selected-ids    (:selected-cuties profile)
        rejected-ids    (:rejected-cuties profile)

        todays-cutie-unseen?   (some #(= todays-cutie-id %) unseen-ids)
        todays-cutie-selected? (some #(= todays-cutie-id %) selected-ids)
        todays-cutie-rejected? (some #(= todays-cutie-id %) rejected-ids)]

    ; if todays-cutie-id is still in unseen-ids, then move it to the end of the list:
    (when todays-cutie-unseen?
      (println "todays-cutie-id is still in unseen-ids, so moving it to the end of the list:")
      (println "before: " unseen-ids)
      (println " after: "  (move-to-end todays-cutie-id unseen-ids)))

    (when todays-cutie-selected?
      (println "todays-cutie-id was selected!"))

    (when todays-cutie-rejected?
      (println "todays-cutie-id was rejected........"))
     ;
    )

  #_(airtable/update-in-base airtable-base
                             ["bios-devons-test-2" (:id bio)]
                             {:fields fields-to-change})

  #_(pp/pprint {:0-included        included-bios
                :1-unseen           unseen-ids
                :2-todays-cutie-id todays-cutie-id
                :2-todays-cutie    todays-cutie
                :3-selected        selected-ids
                :4-rejected        rejected-ids}))
