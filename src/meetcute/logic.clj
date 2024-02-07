(ns meetcute.logic
  (:require [cheshire.core        :refer [generate-string]]
            [clojure.walk         :refer [keywordize-keys]]
            [clojure.core.memoize :as memoize]
            [clojure.data.json    :as json]
            [clojure.test         :refer [deftest is]]
            [clojure.string       :as str]
            [clojure.pprint       :as pp]
            [clojure.set          :as set]
            [smallworld.email     :as email]
            [meetcute.util        :as mc.util]
            [smallworld.airtable  :as airtable]
            [smallworld.util      :as util]))

(def airtable-base {:api-key (util/get-env-var "AIRTABLE_BASE_API_KEY")
                    :base-id "appF2K8ThWvtrC6Hs"})

(def airtable-db-name (atom (if (= (:prod util/ENVIRONMENTS) (util/get-env-var "ENVIRONMENT"))
                              "cuties-live-data"
                              "cuties-TEST-data")))
(println)
(println "🍊🍊🍊 airtable-db-name: " @airtable-db-name " 🍊🍊🍊")
(println)

(defn seconds [n] (* n 1000))
(defn minutes [n] (* n 60 (seconds 1)))

(defn fetch-all-bios! []
  (println)
  (println "⚠️   Fetching all bios from Airtable... (Airtable will get grumpy if we hit the API too often)")
  (println)
  (airtable/get-in-base airtable-base [@airtable-db-name]))

(def fetch-all-bios-memoized
  (memoize/ttl fetch-all-bios! {} :ttl/threshold 1 #_(minutes 1 #_(* 60 24 7)))) ; TODO: set to 1 week just for testing

(defn get-all-bios [& {:keys [force-refresh?] :or {force-refresh? false}}]
  ;; (println (str "force-refresh?  " (if force-refresh? "🔴 " "🟢 ") (str force-refresh?)))
  (let [all-bios-raw  (if force-refresh? (fetch-all-bios!) (fetch-all-bios-memoized))
        all-bios-flat (map (fn [bio] (merge (:fields bio)
                                            {:id (:id bio)}))
                           all-bios-raw)]
    all-bios-flat))

(defn get-all-phones []
  (let [all-bios  (get-all-bios)]
    (->> all-bios
         (map (fn [bio]
                (get-in bio ["Phone"])))
         (map mc.util/clean-phone)
         set)))

(defn existing-phone-number? [phone]
  (let [phone (mc.util/clean-phone phone)
        all-phones (get-all-phones)]
    (contains? all-phones phone)))

(defn find-first-match [match-fn items]
  (some (fn [item]
          ;; (println "looking at: " item) ; confirm that the function only checks items until it finds a match, and then stops
          (when (match-fn item) item)) items))

(defn find-profile [id & {:keys [force-refresh?] :or {force-refresh? false}}]
  (let [all-bios (get-all-bios :force-refresh? force-refresh?)
        my-bio (find-first-match (fn [bio] (= id (:id bio)))
                                 all-bios)]
    (airtable/kwdize my-bio)))

(defn my-profile [phone & {:keys [force-refresh?] :or {force-refresh? false}}]
  (let [all-bios (get-all-bios :force-refresh? force-refresh?)
        my-bio (find-first-match (fn [bio]
                                   (= (mc.util/clean-phone phone)
                                      (mc.util/clean-phone (get-in bio ["Phone"]))))
                                 all-bios)]
    (airtable/kwdize my-bio)))

(defn update-profile [req]
  (let [parsed-body (:params req)
        all-bios (get-all-bios)
        bio-id (mc.util/get-field parsed-body "id")
        phone  (mc.util/get-field parsed-body "Phone")
        bio (first (filter (fn [this-bio]
                             (or (= bio-id
                                    (mc.util/get-field this-bio "id"))
                                 (= (mc.util/clean-phone phone)
                                    (mc.util/clean-phone (get-in this-bio ["Phone"])))))
                           all-bios))
        fields-to-change  (util/exclude-keys parsed-body [:id])]
    (if (nil? bio)
      (generate-string {:error "We couldn't find a profile with that phone number. You probably need to sign up!"})
      (let [data (-> (airtable/update-in-base airtable-base
                                              [@airtable-db-name (:id bio)]
                                              {:fields fields-to-change})
                     :body
                     json/read-str
                     clojure.walk/keywordize-keys)]
        #_(pp/pprint (select-keys (:fields data) [:unseen-cuties
                                                  :todays-cutie
                                                  :selected-cuties
                                                  :rejected-cuties]))
        (generate-string (airtable/kwdize data))))))

(defn index-of [e coll] (first (keep-indexed #(if (= e %2)
                                                %1) coll)))

(defn move-to-end [item list]
  (if (nil? (index-of item list))
    list
    (let [index (index-of item list)]
      (concat (take index list) (drop (inc index) list) [item]))))

(defn first-name-bold [cutie]
  (str "<b>" (mc.util/get-field cutie "First name") "</b>"))

(defn remove-from-lists [a & lists]
  (let [lists (map set lists)]
    (filter (fn [x] (not (some #(% x) lists))) a)))

(defn find-cutie [cutie-id bios]
  (first (filter #(= (mc.util/get-field % "id")
                     cutie-id)
                 bios)))

(defn compute-todays-cutie [profile bios]
  (let [profile         (keywordize-keys profile)
        included-bios   (keywordize-keys (mc.util/included-bios profile bios))
        included-ids    (map :id included-bios)
        unseen-ids--old (:unseen-cuties profile) ; TODO: it seems that somehow this has users that it shouldn't have in it (i.e. people who are not in the included-bios)
        unseen-ids--added-recently (shuffle (remove-from-lists included-ids
                                                               (:unseen-cuties  profile)
                                                               (:selected-cuties profile)
                                                               (:rejected-cuties profile))) ; the shuffle is so that each user gets a different order of cuties, so that the same cutie doesn't get shown to everyone on the same day
        unseen-ids--tmp (vec (distinct (concat unseen-ids--old
                                               unseen-ids--added-recently)))
        todays-id--old       (first (:todays-cutie profile))
        todays-cutie-still-unseen? (some #(= todays-id--old %) unseen-ids--old)

        selected-ids (:selected-cuties profile) ; selected-ids don't change, so no need for -old & -new
        rejected-ids (:rejected-cuties profile) ; rejected-ids don't change, so no need for -old & -new

        unseen-ids--new  (if todays-cutie-still-unseen? ; if todays-cutie was is still unseen, then move it to the end of the list so it isn't shown again until everyone else has been shown
                           (move-to-end todays-id--old unseen-ids--tmp)
                           (filter #(not= todays-id--old %) unseen-ids--tmp))

        ; next, remove ids of bios that are not (= "include in gallery"
        ;                                          (get-field cutie "Include in gallery?"))
        ; this is necessary in part because cuties' status can change. they might have been included in the past, but were removed
        ; for some reason (either by the cutie themself or by an admin)
        unseen-ids--new (->> unseen-ids--new
                             (map #(find-cutie % bios))
                             (mc.util/included-bios profile)
                             (map :id)
                             vec)

        todays-id--new (first unseen-ids--new)
        todays-cutie  (if (nil? todays-id--new)
                        []
                        [todays-id--new])]

    (when false
       ;; each id should only show up in one of unseen-ids, selected-cuties, or rejected-cuties
      (assert (= (count (set (concat unseen-ids--new selected-ids rejected-ids)))
                 (+ (count unseen-ids--new)
                    (count selected-ids)
                    (count rejected-ids))))

       ;; unseen-ids--new does not include any ids from selected-ids
      (assert (every? (fn [x] (not (some (fn [y] (= x y)) selected-ids))) unseen-ids--new)
              "Assertion failed: Some elements in 'unseen-ids--new' are also present in 'selected-ids'")

       ;; unseen-ids--new does not include any ids from selected-ids or rejected-ids
      (assert (every? (fn [x] (not (some (fn [y] (= x y)) rejected-ids))) unseen-ids--new)
              "Assertion failed: Some elements in 'unseen-ids--new' are also present in 'rejected-ids'"))

    {:old {:unseen-cuties unseen-ids--old  :todays-cutie todays-id--old :selected-cuties selected-ids  :rejected-cuties rejected-ids}
     :new {:unseen-cuties unseen-ids--new  :todays-cutie todays-cutie   :selected-cuties selected-ids  :rejected-cuties rejected-ids}}))

; TODO: move this to utils
(defn current-timestamp-for-airtable []
  (let [utc (java.time.ZonedDateTime/ofInstant (java.time.Instant/now)
                                               (java.time.ZoneId/of "UTC"))
        formatter (java.time.format.DateTimeFormatter/ofPattern "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")]
    (.format utc formatter)))

; TODO: move this to utils
(defn utc-to-local [utc-string]
  (println "utc-string: " utc-string)
  (if (empty? utc-string)
    nil
    (let [utc-time (java.time.ZonedDateTime/parse utc-string)
          local-time-zone (java.time.ZoneId/systemDefault)
          local-time (-> utc-time
                         (.withZoneSameInstant local-time-zone))
          formatter (java.time.format.DateTimeFormatter/ofPattern "yyyy-MM-dd HH:mm:ss zz")]
      (.format local-time formatter))))

(defn refresh-todays-cutie [profile bios]
  (let [bios                     (clojure.walk/keywordize-keys bios)
        profile                  (clojure.walk/keywordize-keys profile)
        computed                 (compute-todays-cutie profile bios)
        ;; get-cuties-name         #(get-in (find-cutie % bios) [(keyword "First name")])
        old-todays-cutie-id     (first (:todays-cutie (:old computed)))
        new-todays-cutie-id     (first (:todays-cutie (:new computed)))
        new-todays-cutie-profile (clojure.walk/keywordize-keys (find-cutie (first (:todays-cutie (:new computed))) bios))
        new-values               (clojure.walk/keywordize-keys (merge (:new computed)
                                                                      {:cuties-last-refreshed (current-timestamp-for-airtable)}))
        my-first-name  (get-in profile [(keyword "First name")])
        my-last-name   (get-in profile [(keyword "Last name")])
        my-airtable-id (get-in profile [(keyword "id")])]

    ;; (println "computed: ======================================================================================")
    ;; (pp/pprint {:profile         (select-keys profile [(keyword "First name")
    ;;                                                    :todays-cutie
    ;;                                                    :unseen-cuties
    ;;                                                    :selected-cuties
    ;;                                                    :rejected-cuties])
    ;;             :todays-cutie    (map get-cuties-name (:todays-cutie    new-values))
    ;;             :unseen-cuties   (map get-cuties-name (:unseen-cuties   new-values))
    ;;             :selected-cuties (map get-cuties-name (:selected-cuties new-values))
    ;;             :rejected-cuties (map get-cuties-name (:rejected-cuties new-values))})
    ;; (pp/pprint "==============================================================================================")

    (airtable/update-in-base airtable-base
                             [@airtable-db-name (:id profile)]
                             {:fields new-values})

    (if (or (empty? (:todays-cutie (:new computed)))
            (= old-todays-cutie-id new-todays-cutie-id))
      (do (println (str "no new cutie for " my-first-name " " my-last-name " [id: " my-airtable-id "]"))
          (println (str "   old-todays-cutie-id: " (or old-todays-cutie-id "nil")))
          (println (str "   new-todays-cutie-id: " (or new-todays-cutie-id "nil"))))
      (let [cutie-first-name (first-name-bold new-todays-cutie-profile)
            email-config {:to        (str/trim (:Email profile))
                          :from-name "MeetCute"
                          :subject   (str "Fresh cutie! 🍊 Meet " (mc.util/get-field new-todays-cutie-profile "First name"))
                          :body      (str "<div style='line-height: 1.6em; font-family: Roboto Mono, monospace !important; margin: 24px 0'>"
                                          "Hey " (mc.util/get-field profile "First name") ", "
                                          (if cutie-first-name
                                            (str "your cutie of the day is " cutie-first-name "! ")
                                            (str "your cutie of the day is ready! "))
                                          "<br><br>"
                                          "Would you like to meet them? <a href='https://smallworld.kiwi/meetcute' style='; font-family: Roboto Mono, monospace !important'>"
                                          "View " (mc.util/get-field new-todays-cutie-profile "First name") "'s profile and let us know today!</a>"
                                          "<div style='border: 3px solid #eee;  color: #888; width: fit-content;  padding: 16px 24px 8px 20px;  margin: 24px 0;  border-radius: 12px; font-family: Roboto Mono, monospace !important'>"
                                          "How MeetCute works:"
                                          "<ol style='padding-inline-start: 12px !important; padding-left: 32px !important; font-family: Roboto Mono, monospace !important'>"
                                          "      <li style='padding-left: 4px !important; margin-left: 4px !important'>We send you a daily email with one new person at a time</li>"
                                          "      <li style='padding-left: 4px !important; margin-left: 4px !important'>You let us know if you're interested in meeting them</li>"
                                          "      <li style='padding-left: 4px !important; margin-left: 4px !important'>If they're interested too, we introduce you!</li>"
                                          "</ol>"
                                          "Make sure <a href='https://smallworld.kiwi/meetcute/settings'>your profile</a> is up-to-date!"
                                          "<br><br>"
                                          "</div>"
                                          "<div style='font-size: .8em; line-height: 1.7em'>"
                                          "MeetCute is a little project by <a href='https://devonzuegel.com'>Devon</a> & <a href='https://eriktorenberg.com/'>Erik</a> "
                                          "meant as a gift to our friends. <br/>"
                                          "We both have full-time jobs, so we might be slow to respond. <br/>"
                                          "If you have questions or find bugs, email us at <a href='mailto:hello@smallworld.kiwi'>hello@smallworld.kiwi</a>. <br/>"
                                          "</div>"
                                          "</div>")}]
        (email/send-email email-config)))))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; for testing purposes:

(def -my-matching-criteria {:id                              "me"
                            :Gender                          "Woman"
                            (keyword "I'm interested in...") ["Men"]})

(def -cutie-matching-criteria {(keyword "Gender")               "Man"
                               (keyword "Include in gallery?")  "include in gallery"
                               (keyword "I'm interested in...") ["Women"]})

(defn -compute-todays-cutie-test [my-cuties-lists]
  (:new (compute-todays-cutie (merge my-cuties-lists -my-matching-criteria)
                              (map #(merge {:id %} -cutie-matching-criteria) ; build a list of cuties with the matching criteria
                                   (:all-cuties my-cuties-lists)))))

(deftest test--compute-todays-cutie
  ; when todays-cutie ("1") is still unseen, it should be moved to the end of `unseen-cuties`
  (is (= (-compute-todays-cutie-test
          {:todays-cutie [1]  :unseen-cuties [1 2]  :selected-cuties []  :rejected-cuties []       :all-cuties [1 2]})
         {:todays-cutie  [2]  :unseen-cuties [2 1]  :selected-cuties []  :rejected-cuties []}))

  ; when todays-cutie (1) is selected, it should stay in `selected-cuties` and the next unseen cutie (2) should be moved to `todays-cutie`
  (is (= (-compute-todays-cutie-test
          {:todays-cutie [1]  :unseen-cuties [2]  :selected-cuties [1]  :rejected-cuties []        :all-cuties [1 2]})
         {:todays-cutie  [2]  :unseen-cuties [2]  :selected-cuties [1]  :rejected-cuties []}))

  ; when todays-cutie (1) is selected, it should stay in `selected-cuties` and the next unseen cutie (2) should be moved to `todays-cutie`
  (is (= (-compute-todays-cutie-test
          {:todays-cutie [1]  :unseen-cuties [2 3 4 5]  :selected-cuties [1]  :rejected-cuties []  :all-cuties [1 2 3 4 5]})
         {:todays-cutie  [2]  :unseen-cuties [2 3 4 5]  :selected-cuties [1]  :rejected-cuties []}))

  ; when todays-cutie (1) is rejected, it should be moved to `rejected-cuties` and the next unseen cutie (2) should be moved to `todays-cutie`
  (is (= (-compute-todays-cutie-test
          {:todays-cutie [1]  :unseen-cuties [2]  :rejected-cuties [1]  :selected-cuties []        :all-cuties [1 2]})
         {:todays-cutie  [2]  :unseen-cuties [2]  :rejected-cuties [1]  :selected-cuties []}))

  ; when todays-cutie (1) is selected and another cutie (2) has previously been selected, todays-cutie should stay in `selected-cuties` and the next unseen cutie (3) should be moved to `todays-cutie`
  (is (= (-compute-todays-cutie-test
          {:todays-cutie [1]  :unseen-cuties [3]  :selected-cuties [1 2]  :rejected-cuties []      :all-cuties [1 2 3]})
         {:todays-cutie  [3]  :unseen-cuties [3]  :selected-cuties [1 2]  :rejected-cuties []}))

  ; when included-bios include a bio that the user hasn't seen before (3), it should be added to end of `unseen-cuties`;
  ; when todays-cutie (1) is still unseen, it should be moved to the end of `unseen-cuties`
  (is (= (-compute-todays-cutie-test
          {:todays-cutie [1]  :unseen-cuties [1 2]    :selected-cuties []  :rejected-cuties []     :all-cuties [1 2 3]})
         {:todays-cutie  [2]  :unseen-cuties [2 3 1]  :selected-cuties []  :rejected-cuties []}))

  ; when included-bios include multiple bios that the user hasn't seen before (3), it should be added to end of `unseen-cuties`;
  (is (= (-compute-todays-cutie-test
          {:todays-cutie [1]  :unseen-cuties [2 3]      :selected-cuties [1]  :rejected-cuties []  :all-cuties [1 2 3 4 5]})
         {:todays-cutie  [2]  :unseen-cuties [2 3 4 5]  :selected-cuties [1]  :rejected-cuties []})))

; TODO: add tests that check that cuties are not included if "Include in gallery?" is not "include in gallery"
;; (clojure.test/run-tests)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn get-airtable-db-name []
  @airtable-db-name)

; if current user is not an admin, return 401
(defn update-airtable-db [req]
  (let [parsed-jwt  (:auth/parsed-jwt req)
        phone       (some-> parsed-jwt :auth/phone mc.util/clean-phone)
        new-db-name (mc.util/get-field (:params req) "airtable-db-name")]
    ;; (pp/pprint (my-profile phone))
    (println "      phone: " phone)
    (println "old-db-name: " @airtable-db-name)
    (println "new-db-name: " new-db-name)

    (when (not= @airtable-db-name new-db-name)
      (get-all-bios :force-refresh? true) ; force a refresh of the bios now that the db name has changed
      (reset! airtable-db-name new-db-name))
    (generate-string {:success true
                      :message (str "Successfully updated Airtable db to " @airtable-db-name)}))
  #_(let [parsed-jwt (:auth/parsed-jwt req)
          admin?     (get-in parsed-jwt ["auth" "admin?"])]
      (if (not admin?)
        (generate-string {:error "You are not authorized to update the Airtable db"})
        (do
          (println "old airtable-db-name: " @airtable-db-name)
          (reset! airtable-db-name (mc.util/get-field (:params req) "airtable-db-name"))
          (println "new airtable-db-name: " @airtable-db-name)
          (generate-string {:success true :message (str "Successfully updated Airtable db to " @airtable-db-name)})))))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn req->parsed-jwt [req]
  (:auth/parsed-jwt req))

(defn updated-in-last-N-mins? [cutie N]
  (let [last-updated (get-in cutie ["cuties-last-refreshed"])]
    (if (empty? last-updated)
      false
      (let [last-updated (java.time.ZonedDateTime/parse last-updated)
            now          (java.time.ZonedDateTime/now java.time.ZoneOffset/UTC)
            mins-ago     (.toMinutes (java.time.Duration/between last-updated now))]
        (< mins-ago N)))))

(defn updated-in-last-24h? [cutie]
  (updated-in-last-N-mins? cutie (* 60 24)))

(defn refresh-todays-cutie-from-id [id]
  (let [cutie-profile (find-profile id :force-refresh? true)
        last-refreshed (utc-to-local (get-in cutie-profile ["cuties-last-refreshed"]))]
    (if (updated-in-last-24h? cutie-profile)
      (do
        (println (str "🔵 skipping refresh-todays-cutie for " id " because they were last updated at " last-refreshed))
        (generate-string {:success true :message (str "Skipping refresh-todays-cutie for " id " (" (get-in cutie-profile ["First name"]) ")")}))
      (do
        (println (str "🟢 refresh-todays-cutie for "
                      (get-in cutie-profile ["First name"]) " "
                      (get-in cutie-profile ["Last name"]) ": "
                      (get-in cutie-profile ["Phone"])))
        (refresh-todays-cutie cutie-profile (get-all-bios :force-refresh? false)) ; don't need to refresh again because we just did it above
        (generate-string {:success true :message (str "Successfully refreshed todays-cutie for " id " (" (get-in cutie-profile ["First name"]) ")")})))))

; TODO: remove this route
(defn refresh-todays-cutie-route-mine [req]

  (let [phone (some-> (req->parsed-jwt req) :auth/phone mc.util/clean-phone)
        cutie (my-profile phone :force-refresh? true)]
    (println "refresh-todays-cutie for JUST ME")
    (refresh-todays-cutie cutie (get-all-bios :force-refresh? true))
    (generate-string {:success true :message (str "Successfully refreshed todays-cutie for " phone)})))

(defn refresh-todays-cutie-route-all [_req]
  ; TODO: only an admin should be able to hit this route
  (let [actually-do-the-refresh true ; toggle me to turn off the refresh/email
        bios (get-all-bios :force-refresh? true)
        all-cuties (filter #(or (get-in % ["admin?"])
                                (= (get-in % ["Include in gallery?"]) "include in gallery"))
                           bios)]

    (println)
    (println (str "------------------------------------------------------------\n"
                  "preparing to refresh todays-cutie for " (count all-cuties) " cuties"))
    (doseq [cutie all-cuties]
      (let [phone             (get-in cutie ["Phone"])
            first-name        (get-in cutie ["First name"])
            last-name         (get-in cutie ["Last name"])
            reason-for-including (if (= (get-in cutie ["Include in gallery?"]) "include in gallery")
                                   "include in gallery"
                                   "          admin 🔔")
            cutie-info-str    (str reason-for-including  "  ·  " phone "  ·  " first-name " " last-name)]
        (when-not (empty? phone)
          (if (updated-in-last-24h? cutie)
            (println " 🔵 skipping refresh-todays-cutie for" cutie-info-str)
            (println " 🟢 running  refresh-todays-cutie for" cutie-info-str))

          (when actually-do-the-refresh
            (when-not (updated-in-last-24h? cutie) ; don't need to refresh again because we just did it above
              (refresh-todays-cutie (my-profile phone :force-refresh? false)
                                    (get-all-bios :force-refresh? false)))))))
    (when-not actually-do-the-refresh
      (println)
      (println "not actually refreshing todays-cutie for any cuties")
      (println))
    (println (str "finished refreshing todays-cutie for " (count all-cuties) " cuties\n"
                  "------------------------------------------------------------"))
    (println)

    (generate-string {:success true :message "Successfully refreshed todays-cutie for " (count all-cuties) " cuties"})))
