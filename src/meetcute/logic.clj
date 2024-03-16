(ns meetcute.logic
  (:require [cheshire.core        :refer [generate-string]]
            [clojure.core.memoize :as memoize]
            [clojure.data         :as data]
            [clojure.data.json    :as json]
            [clojure.pprint       :as pp]
            [clojure.string       :as str]
            [clojure.test         :refer [deftest is]]
            [clojure.walk         :refer [keywordize-keys]]
            [markdown.core        :as md]
            [meetcute.util        :as mc.util]
            [smallworld.airtable  :as airtable]
            [smallworld.coordinates :as coordinates]
            [smallworld.email     :as email]
            [smallworld.util      :as util]))

(def airtable-base {:api-key (util/get-env-var "AIRTABLE_BASE_API_KEY")
                    :base-id "appF2K8ThWvtrC6Hs"})

(def airtable-cuties-db-name (atom "cuties-live-data"))
(def airtable-matches-db-name (atom "matches-live-data"))

(println)
(println "🍊🍊🍊 airtable-db-name: " @airtable-cuties-db-name " 🍊🍊🍊")
(println)

(defn seconds [n] (* n 1000))
(defn minutes [n] (* n 60 (seconds 1)))

(defn fetch-all-bios! []
  (println)
  (println "⚠️   Fetching all bios from Airtable... (Airtable will get grumpy if we hit the API too often)")
  (println)
  (airtable/get-in-base airtable-base [@airtable-cuties-db-name]))

(def fetch-all-bios-memoized
  (memoize/ttl fetch-all-bios! {} :ttl/threshold 30 #_(minutes 1 #_(* 60 24 7)))) ; TODO: set to 1 week just for testing

(defn find-cutie [cutie-id bios]
  (let [result (first (filter #(= (mc.util/get-field % "id")
                                  cutie-id)
                              bios))]
    (if (nil? result)
      :no-cutie
      result)))

(defn req->parsed-jwt [req]
  (:auth/parsed-jwt req))

(defn find-first-match [match-fn items]
  (some (fn [item]
          ;; (println "looking at: " item) ; confirm that the function only checks items until it finds a match, and then stops
          (when (match-fn item) item)) items))

(defn get-all-bios [& {:keys [force-refresh?] :or {force-refresh? false}}]
  ;; (println (str "force-refresh?  " (if force-refresh? "🔴 " "🟢 ") (str force-refresh?)))
  (let [all-bios-raw  (if force-refresh? (fetch-all-bios!) (fetch-all-bios-memoized))
        all-bios-flat (map (fn [bio] (merge (:fields bio)
                                            {:id (:id bio)}))
                           all-bios-raw)]
    all-bios-flat))


(defn get-needed-bios [req]
  (let [all-bios (get-all-bios :force-refresh? false)

        phone (some-> (req->parsed-jwt req) :auth/phone mc.util/clean-phone)
        cutie-me (find-first-match (fn [bio]
                                     (= (mc.util/clean-phone phone)
                                        (mc.util/clean-phone (get-in bio ["Phone"]))))
                                   all-bios)
        my-id (mc.util/get-field cutie-me "id")
        todays-cutie-id (first (get-in cutie-me ["todays-cutie"]))
        todays-cutie (find-cutie todays-cutie-id all-bios)
        todays-cutie-abridged (select-keys todays-cutie [:id
                                                         "Anything else you'd like your potential matches to know?"
                                                         "First name"
                                                         "Gender"
                                                        ;;  "Home base city"
                                                        ;;  "Other cities where you spend time"
                                                         "locations-json"
                                                         "locations-notes"
                                                         "I'm interested in..."
                                                         "If 'Other', who invited you?"
                                                         "Include in gallery?"
                                                         "Pictures"
                                                         "Social media links"
                                                         "What makes this person awesome?"
                                                        ;;  "Birthday"
                                                        ;;  "Transcribed from original Google Docs from mid Oct"
                                                        ;;  "Who invited you to this matchmaking experiment?"
                                                        ;;  "Email"
                                                        ;;  "Their email"
                                                        ;;  "matches-live-data-1"
                                                        ;;  "todays-cutie"
                                                        ;;  "matches-live-data-1--count"
                                                        ;;  "rejected-cuties"
                                                        ;;  "Created By"
                                                        ;;  "Who is another person we should invite to this experiment?"
                                                        ;;  "matches"
                                                        ;;  "cuties-last-refreshed"
                                                        ;;  "Last name"
                                                        ;;  "Phone"
                                                        ;;  "matches-live-data-2--count"
                                                        ;;  "Anything you'd like the organizers to know?"
                                                        ;;  "unseen-cuties"
                                                        ;;  "selected-cuties"
                                                        ;;  "Yes / Maybe / No"
                                                         ])]
    (vec [cutie-me todays-cutie-abridged])))

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
  (let [new-data (:params req)
        all-bios (get-all-bios)
        bio-id (mc.util/get-field new-data "id")
        phone  (mc.util/get-field new-data "Phone")
        old-bio (first (filter (fn [this-bio]
                                 (or (= bio-id
                                        (mc.util/get-field this-bio "id"))
                                     (= (mc.util/clean-phone phone)
                                        (mc.util/clean-phone (get-in this-bio ["Phone"])))))
                               all-bios))
        fields-to-change  (util/exclude-keys new-data [:id])]
    (if (nil? old-bio)
      (generate-string {:error "We couldn't find a profile with that phone number. You probably need to sign up!"})
      (let [new-bio (-> (airtable/update-in-base airtable-base
                                                 [@airtable-cuties-db-name (:id old-bio)]
                                                 {:fields fields-to-change})
                        :body
                        json/read-str
                        clojure.walk/keywordize-keys)
            new-data-printable (merge (select-keys new-data
                                                   (map #(keyword %)
                                                        mc.util/fields-changeable-by-user))
                                      {:unseen-cuties "[REDACTED FOR BREVITY]"})
            old-bio-printable (merge (select-keys (clojure.walk/keywordize-keys old-bio)
                                                  (map #(keyword %)
                                                       mc.util/fields-changeable-by-user))
                                     {:unseen-cuties "[REDACTED FOR BREVITY]"})
            name (str (mc.util/get-field old-bio-printable "First name") " "
                      (mc.util/get-field old-bio-printable "Last name"))
            [old new no-change] (data/diff old-bio-printable new-data-printable)
            changed-keys (keys (merge old new))
            log-message (if (and (some #{:selected-cuties :rejected-cuties :unseen-cuties} changed-keys)
                                         ; at least one of them is not nil or an empty list:
                                 (some (fn [key] (not (empty? (get new-data key))))
                                       [:selected-cuties :rejected-cuties :unseen-cuties]))
                          (str "🗳️ " name " has updated their selections/rejections")
                          (str "📝 " name " has updated these fields in their profile"))]
        (util/log (str log-message ": " changed-keys))

        ; if the user updated their status to "not yet reviewed" from "filling out profile", then that means they just submitted their profile for the first time
        (when (and (some #{:status} changed-keys)
                   (= (get old-bio "Status") "filling out profile")
                   (= (get new-bio "Status") "not yet reviewed"))
          (email/send-email {:to        "hello@smalworld.kiwi"
                             :from-name "MeetCute"
                             :subject   "Thanks for submitting your profile for review!"
                             :body      "We'll review your profile shortly. Once we make it live, you'll start receiving your daily cutie emails! 🍊"}))

        (when (= (util/get-env-var "ENVIRONMENT")
                 (:prod util/ENVIRONMENTS))
          (email/send-email {:to        "hello@smallworld.kiwi"
                             :from-name "MeetCute logs"
                             :subject   log-message
                             :body      (str "<div style='line-height: 1.6em; font-family: Roboto Mono, monospace !important; margin: 24px 0'>"
                                             "<b><u>Here's what's changed:</u></b><br><br>"
                                             "OLD:      <br><pre>" (with-out-str (pp/pprint old))       "</pre><br>"
                                             "NEW:      <br><pre>" (with-out-str (pp/pprint new))       "</pre><br>"
                                             "NO CHANGE:<br><pre>" (with-out-str (pp/pprint no-change)) "</pre><br>"
                                             "</div>")}))
        (generate-string (airtable/kwdize new-bio))))))

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

(defn sort-by-selected-rejected [bios profile]
  (fn [id] (let [cutie (find-cutie id bios)]
             (if (some #(= (mc.util/get-field profile "id") %)
                       (:selected-cuties cutie))
               0
               (if (some #(= (mc.util/get-field profile "id") %)
                         (:rejected-cuties cutie))
                 2
                 1)))))

(defn min-distance-between-locations [locations1 locations2]
  (let [locations1 (keywordize-keys (json/read-str locations1))
        locations2 (keywordize-keys (json/read-str locations2))
        distances (for [loc1 locations1
                        loc2 locations2]
                    (coordinates/distance-btwn (:coords loc1) (:coords loc2)))
        distances (remove nil? distances)]
    ;; (println "distances: ")
    ;; (pp/pprint distances)
    (if (empty? distances)
      9999999999 ; if there are no distances, then the cutie is very far away
      (apply min distances))))

; TODO: consider prioritizing the cuties who are closest to the user's HOME BASE, rather than weighting all locations equally
(defn sort-by-distance-by-nearest-location [bios profile]
  (fn [id] (let [cutie (find-cutie id bios)
                 cutie-locations (mc.util/get-field cutie "locations-json")
                 my-locations (mc.util/get-field profile "locations-json")]
             (min-distance-between-locations my-locations
                                             cutie-locations))))

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
        unseen-ids--tmp (sort-by (sort-by-distance-by-nearest-location bios profile) unseen-ids--tmp)
        unseen-ids--tmp (sort-by (sort-by-selected-rejected            bios profile) unseen-ids--tmp)
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

(defn update-cutie-picture [cutie-id picture-url]
  (airtable/update-in-base airtable-base
                           [@airtable-cuties-db-name cutie-id]
                           {:fields {:Pictures [{:url picture-url}]}}))

(defn add-pictures-to-cutie-airtable [cutie-id picture-urls]
  (let [cutie (find-cutie cutie-id (get-all-bios))
        old-pictures (get-in cutie ["Pictures"])]
    (println "      :Pictures – " (:Pictures cutie))
    (println "get-in Pictures – " (get-in cutie ["Pictures"]))
    (airtable/update-in-base airtable-base
                             [@airtable-cuties-db-name cutie-id]
                             {:fields {:Pictures (concat old-pictures
                                                         (map (fn [url] {:url url})
                                                              picture-urls))}})))

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
                             [@airtable-cuties-db-name (:id profile)]
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
                                          "Hey " (mc.util/get-field profile "First name") ", " (if cutie-first-name
                                                                                                 (str "your cutie of the day is " cutie-first-name "! ")
                                                                                                 (str "your cutie of the day is ready! "))
                                          "<br><br>"
                                          "<a href='https://smallworld.kiwi/meetcute' style='; font-family: Roboto Mono, monospace !important'>"
                                          "View " (mc.util/get-field new-todays-cutie-profile "First name") "'s profile & let us know if you'd like to meet them!"
                                          "</a>"
                                          "<br><br>"
                                          "(Don't worry — if you miss them today, they'll come back around as your cutie of the day later!)"
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
                                          "If you have questions/feedback or find bugs, email us at <a href='mailto:hello@smallworld.kiwi'>hello@smallworld.kiwi</a>. <br/>"
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
  @airtable-cuties-db-name)

; if current user is not an admin, return 401
(defn update-airtable-db [req]
  (let [parsed-jwt  (:auth/parsed-jwt req)
        phone       (some-> parsed-jwt :auth/phone mc.util/clean-phone)
        new-db-name (mc.util/get-field (:params req) "airtable-db-name")]
    ;; (pp/pprint (my-profile phone))
    (println "      phone: " phone)
    (println "old-db-name: " @airtable-cuties-db-name)
    (println "new-db-name: " new-db-name)

    (when (not= @airtable-cuties-db-name new-db-name)
      (get-all-bios :force-refresh? true) ; force a refresh of the bios now that the db name has changed
      (reset! airtable-cuties-db-name new-db-name))
    (generate-string {:success true
                      :message (str "Successfully updated Airtable db to " @airtable-cuties-db-name)}))
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

(defn cutie-profile-rendered-for-email [cutie]
  (str "<pre>"
       (with-out-str (pp/pprint (select-keys cutie ["What makes this person awesome?"
                                                    "Home base city"
                                                    "Other cities where you spend time"
                                                    "Phone"
                                                    "Email"
                                                    "Social media links"
                                                    "Anything else you'd like your potential matches to know?"
                                                 ;; "Pictures"
                                                    ])))
       "</pre>"))

(defn send-match-profile-email [recipient-email recipient-first-name cutie]
  (let [cutie (keywordize-keys cutie)
        email-config {:to        (str/trim recipient-email)
                      :from-name "MeetCute"
                      :subject   (str "You got a match! 🍊🍊 Meet " (mc.util/get-field cutie "First name"))
                      :body      (str "<div style='line-height: 1.6em; font-family: Roboto Mono, monospace !important; margin: 24px 0'>"
                                      "Hey " recipient-first-name ", you matched with " (mc.util/get-field cutie "First name") "!"
                                      "<br><br>"
                                      "Here's their contact info so you can reach out directly:<br><br>"
                                      "<b><u>Phone:</u></b> " (mc.util/get-field cutie "Phone") "<br><br>"
                                      "<b><u>Email:</u></b> " (mc.util/get-field cutie "Email") "<br><br>"
                                      "<b><u>A bit about them:</u></b><br>" (md/md-to-html-string (mc.util/get-field cutie "Anything else you'd like your potential matches to know?")) "<br>"
                                      "<b><u>Vouch from a friend:</u></b><br>" (md/md-to-html-string (mc.util/get-field cutie "What makes this person awesome?")) "<br><br>"
                                      "<b><u>Home base city:</u></b><br>" (mc.util/get-field cutie "Home base city") "<br><br>"
                                      "<b><u>Frequently visits:</u></b><br>" (mc.util/get-field cutie "Other cities where you spend time") "<br><br>"
                                      "<b><u>Social media links:</u></b><br>" (md/md-to-html-string (mc.util/get-field cutie "Social media links")) "<br><br>"
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
                                      "If you have questions/feedback or find bugs, email us at <a href='mailto:hello@smallworld.kiwi'>hello@smallworld.kiwi</a>. <br/>"
                                      "</div>"
                                      "</div>")}]
    (email/send-email email-config)))


(defn update-matches-in-airtable []
  ; list all cuties that both picked each other, i.e. where cutie-1's id is in the selected-cuties of cutie-2 AND cutie-2's id is in the selected-cuties of cutie-1
  (let [matches-already-in-airtable (airtable/get-in-base airtable-base [@airtable-matches-db-name])
        all-cuties (get-all-bios :force-refresh? false)
        matches-recomputed (->> (map (fn [cutie-1]
                                       (let [cutie-1-id (:id cutie-1)
                                             cutie-1-selections (get-in cutie-1 ["selected-cuties"])]
                                         (map (fn [cutie-2-id]
                                                (let [cutie-2 (find-cutie cutie-2-id all-cuties)
                                                      cutie-2-selections (get-in cutie-2 ["selected-cuties"])]
                                                  (if (and (some #(= cutie-1-id %) cutie-2-selections)
                                                           (not (nil? cutie-1-id))
                                                           (not (nil? cutie-2-id)))
                                                    (set [cutie-1-id cutie-2-id])
                                                    nil)))
                                              cutie-1-selections))) all-cuties)
                                (mapcat identity)
                                (remove nil?)
                                (remove #(not= 2 (count %))) ; remove if length of the match is not 2 (this shouldn't happen but sometimes does so let's remove them just to be careful)
                                (set))]

    ; for each match, check if they're already in the matches-in-airtable; if not, add them
    (doseq [match matches-recomputed]
      (let [cutie-1 (find-cutie (first match) all-cuties)
            cutie-2 (find-cutie (second match) all-cuties)
            cutie-1-name (str (get-in cutie-1 ["First name"]) " " (get-in cutie-1 ["Last name"]))
            cutie-2-name (str (get-in cutie-2 ["First name"]) " " (get-in cutie-2 ["Last name"]))
            match-in-airtable? (some (fn [match-in-airtable]
                                       (= (set match)
                                          (set [(first (get-in match-in-airtable [:fields "cutie-1"]))
                                                (first (get-in match-in-airtable [:fields "cutie-2"]))])))
                                     matches-already-in-airtable)
            match-to-add-to-airtable {"cutie-1" [(first match)]
                                      "cutie-2" [(second match)]
                                      "status" "no 3rd person intro yet, but they have received an email with the cutie's profile"
                                      "created" (current-timestamp-for-airtable)}]
        (if match-in-airtable?
          (println "🔵 match already in airtable: " cutie-1-name " + " cutie-2-name)
          (do
            (println "🟢 add new match to airtable: " cutie-1-name " + " cutie-2-name)
            (send-match-profile-email (get-in cutie-1 ["Email"]) (get-in cutie-1 ["First name"]) cutie-2)
            (send-match-profile-email (get-in cutie-2 ["Email"]) (get-in cutie-2 ["First name"]) cutie-1)
            (email/send-email {:to "hello@smallworld.kiwi"
                               :from-name "MeetCute"
                               :subject (str "New match 🍊🍊 " cutie-1-name " + " cutie-2-name)
                               :body (str "<b>cutie-1:</b>"
                                          "<br>" (cutie-profile-rendered-for-email cutie-1) "<br><br>"
                                          "<b>cutie-2:</b>"
                                          "<br>" (cutie-profile-rendered-for-email cutie-2))})
            (airtable/create-in-base airtable-base
                                     [@airtable-matches-db-name]
                                     {:fields match-to-add-to-airtable})))))))

(defn updated-in-last-N-mins? [cutie N]
  (let [last-updated (get-in cutie ["cuties-last-refreshed"])]
    (if (empty? last-updated)
      false
      (let [last-updated (java.time.ZonedDateTime/parse last-updated)
            now          (java.time.ZonedDateTime/now java.time.ZoneOffset/UTC)
            mins-ago     (.toMinutes (java.time.Duration/between last-updated now))]
        (< mins-ago N)))))

(defn updated-in-last-24h? [cutie]
  (updated-in-last-N-mins? cutie (+ (* 60 24) 10))) ; the +10 is to account for the fact the job runs every 10 minutes

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

    (update-matches-in-airtable)

    (generate-string {:success true :message "Successfully refreshed todays-cutie for " (count all-cuties) " cuties"})))

;; this is meant to be run only in the REPL
#_(defn backfill-locations [cuties] ; TODO: (get-all-bios :force-refresh? false)
    (doseq [cutie cuties]
      (let [home-base (get-in cutie ["Home base city"])
            other-cities-str (get-in cutie ["Other cities normalized"])
            other-cities-list (if (empty? other-cities-str) [] (clojure.string/split other-cities-str #"[,|]"))
            locations (concat (if (str/blank? home-base) [] [{:location-type "home-base"   :name home-base       :coords (coordinates/memoized home-base)}])
                              (map (fn [city]                {:location-type "visit-often" :name (str/trim city) :coords (coordinates/memoized (str/trim city))})
                                   other-cities-list))]
        (airtable/update-in-base airtable-base
                                 [@airtable-cuties-db-name (:id cutie)]
                                 {:fields {:locations-json (json/write-str locations)}}))))

