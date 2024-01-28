(ns smallworld.screens.meetcute
  (:require [clojure.set :as set]
            [clojure.string :as str]
            [markdown.core :as md]
            [cljs.pprint :as pp]
            [meetcute.util :as mc.util]
            [reagent.core    :as r]
            [smallworld.util :as util]))

(def mock-data?          false)
(defonce debug?          (r/atom false))
(defonce loading-message (r/atom nil))
(def bios                (r/atom nil))
(def profile             (r/atom nil))

(defn small-text [str & [styles]]
  [:div {:style (merge {:padding-top "4px"
                        :padding-bottom "4px"
                        :font-size ".7em"
                        :color "rgb(119, 103, 89, 1)"}
                       styles)}
   str])

(defn md->hiccup [md-string]
  [:div {:dangerouslySetInnerHTML {:__html (md/md->html md-string)}
         :style {:line-height "1.4"}}])

(def fields-changeable-by-user [; Phone is intentionally not included because it's used as the key to find the record to update, so we don't want to overwrite it
                                "Anything else you'd like your potential matches to know?"
                                "Social media links"
                                "Email"
                                "First name"
                                "Last name"
                                "Phone"
                                "Home base city"
                                "Other cities where you spend time"
                                "I'm interested in..."
                                "unseen-cuties"
                                "todays-cutie"
                                "selected-cuties"
                                "rejected-cuties"
                                "What makes this person awesome?"
                                "Pictures"
                                "Gender"])

(defn format-phone [phone]
  (assert phone)
  phone
  #_(let [digits-only (str/replace phone #"[^0-9]" "")]
      (str "(" (subs digits-only 0 3) ") " (subs digits-only 3 6) "-" (subs digits-only 6 10))))

(defn in? [list str] (some #(= str %) list))

(defn checkbox-component [value-name selected-values update-selected-values]
  (let [checked? (boolean (in? selected-values value-name))]
    [:span
     [:input {:type "checkbox"
              :value value-name
              :checked checked?
              :style {:margin-left "17px" :margin-right "11px"
                      :background (if checked? "white" "transparent")
                      :cursor "pointer"

                      :border (if checked? "3px solid green" "3px solid white")}
              ;; :style {:display "none"}
              :on-change (fn [] (update-selected-values (if checked?
                                                          (remove (fn [v] (= value-name v)) selected-values)
                                                          (conj selected-values value-name))))}]
     [:label {:for (str value-name "-checkbox")
              :style {:cursor "pointer" :margin-right "12px"}}
      value-name]]))

(defn checkboxes-component [all-values selected-values update-selected-values]
  [:div {:style {:margin-left "-16px"}}
   (for [value all-values] ^{:key value} [checkbox-component value selected-values update-selected-values])
   (when @debug? [:pre "Selected Value: " (str selected-values)])])

(defn radio-btn-component [value-name selected-value update-selected-value]
  [:span {:style {:display "flex" :align-items "center" :cursor "pointer"}}
   [:input {:type "radio"
            :id (str value-name "-radio")
            :value value-name
            :checked (= selected-value value-name)
            :style {:margin-left "16px" :margin-right "10px" :cursor "pointer"}
            :on-change (fn [] (update-selected-value value-name))}]
   [:label {:for (str value-name "-radio") :style {:cursor "pointer" :margin-right "12px"}}
    value-name]])

(defn radio-btns-component [all-values selected-value update-selected-value]
  [:div {:style {:display "inline-flex" :justify "space-between" :margin-left "-16px"}}
   (for [value all-values] ^{:key value} [radio-btn-component value selected-value update-selected-value])
   (when @debug? [:pre "Selected Value: " (str selected-value)])])

(defn update-selected-cuties! []
  (util/fetch-post "/meetcute/api/matchmaking/profile"
                   (select-keys @profile (map #(keyword %)
                                              (concat fields-changeable-by-user ; Phone is not editable, but it's needed as the key to find the record to update
                                                      ["Phone"])))
                   #(println "Done updating selected-cuties")))

(defn bio-row [i [key-name value]]
  [:div {:key i :className "bio-row" :style {:width "fit-content"}}
   [:style "@media screen and (min-width: 600px) { .bio-row-value { min-width: 380px; }  }"]
   [:div {:style {:padding "6px 12px 0 12px"
                  :opacity ".75"
                  :font-weight "bold"
                  :text-transform "uppercase"
                  :font-style "italic"
                  :color "#bcb5af"
                  :font-size ".8em"}}
    key-name]
   [:div {:style {:padding "12px" :padding-top "6px"} :className "bio-row-value"}
    value]]
  #_(let [width "170px"]
      [:div {:key i :style {:vertical-align "top" :display "flex" :flex-wrap "wrap"}}
    ;;  [:style (str "@media screen and (min-width: 805px) { .flex-item { max-width: " width "; text-align: right; } }")]
       [:div {:className "flex-item"
              :style {:flex 1 :padding "12px" :padding-bottom "0" :font-size ".85em" :opacity ".75" :width width :min-width width :text-transform "uppercase"}} key-name]
       [:div {:style {:flex 1 :padding "12px" :padding-top "6px" :min-width "300px"}} value]]))

(defn select-reject-btns [bio-id currently-selected-ids currently-rejected-ids]
  [:div {:style {:display "flex" :flex-wrap "wrap" :flex-direction "row" #_"column" :width "100%" :margin-right "8px" :justify-content "center"}}

   [:style (str ".select-reject-btn { opacity: .9; transition: opacity .3ms ease-in; cursor: pointer !important }"
                ".select-reject-btn:hover { opacity: 1 }"
                "@media screen and (min-width: 805px) { .select-reject-btn { min-width: 160px; max-width: 260px; } }")]

   [:div {:style {:margin "6px" :cursor "pointer"}}
    [:input {:type "checkbox"
             :id (str bio-id "-select")
             :value bio-id
             :checked (boolean (in? currently-selected-ids bio-id))
             :style {:display "none"}
             :on-change (fn [event] ; TODO: this is almost identical to the on-change fn for the reject button. refactor to share code
                          (if (or (nil? event) (nil? (.-target event)))
                            (println "event: " event)
                            (let [selected-chosen? (.-checked (.-target event))
                                  old-rejected-cuties (mc.util/get-field @profile "rejected-cuties")
                                  now-selected (if selected-chosen?

                                                 (if (in? currently-selected-ids bio-id) ; add to selected-cuties only if it's not already there
                                                   currently-selected-ids
                                                   (conj currently-selected-ids bio-id))

                                                 (remove (fn [v] (= bio-id v)) ; remove from selected-cuties selected-chosen?=false
                                                         (mc.util/get-field @profile "selected-cuties")))

                                  now-rejected (if selected-chosen? ; make sure it's not in rejected-cuties if selected-chosen?=true
                                                 (remove (fn [v] (= bio-id v)) old-rejected-cuties)
                                                 old-rejected-cuties)

                                  now-unseen (if selected-chosen? ; make sure it's not in unseen-cuties if selected-chosen?=true

                                               (remove (fn [v] (= bio-id v))
                                                       (mc.util/get-field @profile "unseen-cuties"))

                                               (if (in? now-rejected bio-id) ; add it to unseen-cuties if it's neither selected nor rejected:
                                                 (mc.util/get-field @profile "unseen-cuties")
                                                 (conj (mc.util/get-field @profile "unseen-cuties") bio-id)))]

                              (reset! profile (assoc @profile :unseen-cuties   now-unseen))
                              (reset! profile (assoc @profile :selected-cuties now-selected))
                              (reset! profile (assoc @profile :rejected-cuties now-rejected))

                              (update-selected-cuties!))))}]
    [:label {:for (str bio-id "-select")
             :className "select-reject-btn"
             :style {:padding       "22px 28px"
                     :background    "rgb(0 157 49)" #_"#42b72a22"
                     :color         "white" #_"#42b72a"
                     :font-size     "1.1em"
                     :line-height   "1.3em"
                     :font-weight   "600"
                     :cursor        "pointer !important"
                     :text-align    "center"
                     :display       "block"
                     :border-radius "3000px"
                     :flex-grow     1
                     :border        (if   (in? currently-selected-ids bio-id) "8px solid rgb(0 157 49)" "8px solid white")
                     :box-shadow    (when (in? currently-selected-ids bio-id) "0 0 0 4px white inset")
                    ;;  :background (if (in? currently-selected-ids bio-id) "#42b72a" "#42b72a22")
                    ;;  :border "8px solid #42b72a"
                     ;
                     }}
     "I'd like to" [:br]
     "meet this person!"]]

   [:div {:style {:margin "6px" :cursor "pointer"}}
    [:input {:type "checkbox"
             :id (str bio-id "-reject")
             :value bio-id
             :checked (boolean (in? currently-rejected-ids bio-id))
             :style {:display "none"}
             :on-change (fn [event] ; TODO: this is almost identical to the on-change fn for the select button. refactor to share code
                          (if (or (nil? event) (nil? (.-target event)))
                            (println "event: " event)
                            (let [rejected-chosen? (.-checked (.-target event))
                                  old-selected-cuties (mc.util/get-field @profile "selected-cuties")
                                  now-rejected (if rejected-chosen?

                                                 (if (in? currently-rejected-ids bio-id) ; add to selected-cuties only if it's not already there
                                                   currently-rejected-ids
                                                   (conj currently-rejected-ids bio-id))

                                                 (remove (fn [v] (= bio-id v)) ; remove from rejected-cuties rejected-chosen?=false
                                                         (mc.util/get-field @profile "rejected-cuties")))

                                  now-selected (if rejected-chosen? ; make sure it's not in rejected-cuties if rejected-chosen?=true
                                                 (remove (fn [v] (= bio-id v)) old-selected-cuties)
                                                 old-selected-cuties)

                                  now-unseen (if rejected-chosen? ; make sure it's not in unseen-cuties if rejected-chosen?=true

                                               (remove (fn [v] (= bio-id v))
                                                       (mc.util/get-field @profile "unseen-cuties"))

                                               (if (in? now-selected bio-id) ; add it to unseen-cuties if it's neither selected nor rejected:
                                                 (mc.util/get-field @profile "unseen-cuties")
                                                 (conj (mc.util/get-field @profile "unseen-cuties") bio-id)))]

                              (reset! profile (assoc @profile :unseen-cuties   now-unseen))
                              (reset! profile (assoc @profile :selected-cuties now-selected))
                              (reset! profile (assoc @profile :rejected-cuties now-rejected))

                              (update-selected-cuties!))))}]
    [:label {:for (str bio-id "-reject")
             :className "select-reject-btn"
             :style {:padding       "22px 28px"
                     :color         "white"
                     :background    "#aaa"
                     :font-size     "1.1em"
                     :line-height   "1.3em"
                     :font-weight   "600"
                     :cursor        "pointer !important"
                     :text-align    "center"
                     :display       "block"
                     :border-radius "3000px"
                     :flex-grow      1
                     :border        (if   (in? currently-rejected-ids bio-id) "8px solid #aaa" "8px solid white")
                     :box-shadow    (when (in? currently-rejected-ids bio-id) "0 0 0 4px white inset")

                    ;;  :background (if (in? currently-rejected-ids bio-id) "#aaaaaa" "#aaaaaa22")
                    ;;  :border "8px solid #aaaaaa"
                     ;
                     }}
     "Not interested," [:br] "but thanks"]]])

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;; PROFILE TAB ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;


(def phone-input-error (r/atom nil))

(defn redirect! [path]
  (.replace (.-location js/window) path))

(def show-toast (r/atom false))

(defn update-profile-with-result [result]
  (pp/pprint "result ===============================")
  (pp/pprint result)
  (pp/pprint "======================================")
  (if (:error result)
    (reset! phone-input-error (:error result))
    (do (reset! profile (merge (:fields result)
                               {:id (:id result)}))
        ;; (pp/pprint "profile keys: ")
        ;; (pp/pprint (keys @profile))
        ;; (redirect! "/meetcute")
        )))

(def profile-error (r/atom nil))

(defn fetch-my-profile! []
  (if mock-data?
    (reset! profile {:id "123123123"
                     (keyword "First name") "Foobar"})
    (util/fetch-post "/meetcute/api/matchmaking/me"
                     {}
                     (fn [result]
                       (if-let [error (:error result)]
                         (reset! profile-error error)
                         (do
                           (reset! profile (if (:id result)
                                             (merge (:fields result)
                                                    {:id (:id result)})
                                             (:fields result)))
                           (println "finished updating profile with result!")))))))

(defn update-profile! []
  (let [profile-editable-fields-only (select-keys @profile (map #(keyword %)
                                                                (concat fields-changeable-by-user ; Phone is not editable, but it's needed as the key to find the record to update
                                                                        ["Phone"])))]
    (reset! show-toast true)
    (js/setTimeout #(reset! show-toast false) 2000)
    (util/fetch-post "/meetcute/api/matchmaking/profile"
                     profile-editable-fields-only
                     update-profile-with-result)))

(def update-profile-debounced! (util/debounce update-profile! 500))

(defn change-profile-field [field-name]
  (fn [event]
    (reset! profile (assoc @profile (keyword field-name) (-> event .-target .-value)))
    (update-profile-debounced!)))

(defn trim-trailing-whitespace [str]
  (str/replace str #"\s+$" ""))

(defn editable-input [field-name]
  [:input {:type "text"
           :value (or (mc.util/get-field @profile field-name) "") #_(trim-trailing-whitespace (or (mc.util/get-field @profile field-name) ""))
           :on-change (change-profile-field field-name)
           :style {:background "white"
                   :border "3px solid rgb(188, 181, 175, .3)"
                  ;;  :background "#66666620"
                  ;;  :border "1px solid #66666622"
                   :border-radius "8px"
                   :padding "6px 8px"
                   :margin-right "4px"
                   :width "95%"
                   :max-width "380px"}}])

(defn editable-textbox [field-name]
  [:textarea {:value (or (mc.util/get-field @profile field-name) "") #_(trim-trailing-whitespace (or (mc.util/get-field @profile field-name) ""))
              :on-change (change-profile-field field-name)
              :style {:background "white"
                      :border "3px solid rgb(188, 181, 175, .3)"
                      ;; :background "#66666620"
                      ;; :border "1px solid #66666622"
                      :border-radius "8px"
                      :resize "vertical" ; disallow horizontal resize
                      :padding "16px 20px"
                      :margin-right "12px"
                      :min-height "140px"
                      :color "#333"
                      :font-size ".9em !important" ; TODO: this is overridden by styles.css, need to fix
                      :width "98%"}}])

(defn fa-icon [icon-name & {:keys [outlined style] :or {outlined false}}]
  [:i {:className (str/join " " [(if outlined "far" "fas")
                                 (str "fa-" icon-name)])
       :style (merge {:min-width "40px" :margin-right "24px" :text-align "right"} style)}])

(defn saved-toast []
  [:div {:style {:position "fixed"
                 :top "30px"
                 :left "50%"
                 :transform "translateX(-50%)"
                 :background-color "#42b72a"
                 :font-weight "bold"
                 :padding "20px 28px"
                 :color "rgb(66, 183, 42)"
                 :background "rgb(235 255 231)"
                 :border "3px solid rgb(66, 183, 42)"
                 :border-radius "8px"
                 :opacity (if @show-toast 1 0)
                 :transition "opacity 0.3s"}}
   [fa-icon "check-circle" :style {:min-width "auto" :margin-right "12px"}]
   "Saved!"])

(defn profile-tab []
  [:div {:style {:border-radius "8px" :padding "12px" :margin-left "auto" :margin-right "auto" :width "90%" :max-width "850px"}}

   [saved-toast]

   [:style "@media screen and (max-width: 1300px) { .your-profile { margin-top: 24px; } }"]
   [:h1 {:style {:font-size 36 :line-height "1.3em" :padding "0 12px"} :className "your-profile"} "Your profile"]

   (let [key-values [["Basic details"
                      {:open false}
                      [["First name"                       (editable-input "First name")]
                       ["Last name"                        (editable-input "Last name")]
                       ["My gender" (radio-btns-component ["Man" "Woman"]
                                                          (mc.util/get-field @profile "Gender")
                                                          (fn [foobar]
                                                            (reset! profile (assoc @profile (keyword "Gender") foobar))
                                                            (update-profile-debounced!)))]
                       ["I'm interested in..." (checkboxes-component ["Men" "Women"]
                                                                     (mc.util/get-field @profile "I'm interested in...")
                                                                     (fn [foobar]
                                                                       (reset! profile (assoc @profile (keyword "I'm interested in...") foobar))
                                                                       (update-profile-debounced!)))]
                       ["Phone"                            [:div {:style {:max-width "380px"}}
                                                            [:div {:style {:background "rgb(188, 181, 175, .1)"
                                                                           :border "3px solid rgb(188, 181, 175, .3)"
                                                                           :cursor "not-allowed"
                                                                           :border-radius "8px"
                                                                           :padding "6px 8px"
                                                                           :margin-right "4px"
                                                                           :width "95%"
                                                                           :max-width "380px"}}
                                                              ;; (format-phone (mc.util/get-field @profile "Phone")) ; don't make this editable, because it's the key to find the record to update. in the future, we can use the ID instead if we do want to make the phone editable
                                                             (mc.util/get-field @profile "Phone")]
                                                            [small-text [:span "If you'd like to change your phone number, email "
                                                                         [:a {:href "mailto:lei@turpentine.co"} "lei@turpentine.co"] "."]]]]
                       ["Email"                            [:div {:style {:max-width "380px"}}
                                                            (editable-input "Email")
                                                            [small-text "We will only share your contact info when you match with someone. It will not be shown on your profile."]]]]]
                     ["Location"
                      {:open true}
                      [["Home base city"                    (editable-input "Home base city")]
                       ["Other cities where you spend time" (editable-input "Other cities where you spend time")]]]
                     ["Other"
                      {:open true}
                      [["About me"                          (editable-textbox "Anything else you'd like your potential matches to know?")]
                       ["Social media links"                (editable-textbox "Social media links")]
                       ["What makes this person awesome?"   [:div
                                                             [:div {:style {:margin-bottom "4px"}}
                                                              [small-text (md->hiccup "Ask a friend to write a few sentences about you. [Here are some examples.](https://bit.ly/matchmaking-vouch-examples)")]]
                                                             (editable-textbox "What makes this person awesome?")
                                                             [:div {:style {:margin-top "8px"}}] ; spacer
                                                             [small-text (md->hiccup "Here's a template for asking a friend to write you a vouch:")]
                                                             [:div {:style {:border-left "5px solid rgb(188, 181, 175, .3)" :background "rgb(188, 181, 175, .1)"  :max-width "90%"}}
                                                              [small-text (md->hiccup (str "*\"Hey `FRIEND NAME`, some friends invited me to a small matchmaking experiment, and I need a friend to write a blurb recommending me. <br/><br/>"
                                                                                           "Would you write one today or tomorrow? It can be short (2-3 paragraphs), should take just a few mins. Here are some examples: [https://bit.ly/matchmaking-vouch-examples](https://bit.ly/matchmaking-vouch-examples)\"*"))
                                                               {:background "#ffffff10" :margin-top "2px" :padding "8px 12px 14px 12px"}]]]]

                       ["Pictures" [:div
                                    [small-text [:span "If you'd like to add or remove pictures, email "
                                                 [:a {:href "mailto:lei@turpentine.co"} "lei@turpentine.co"] ". (In the future, we'll add a way to do this yourself!)"]]
                                    (map-indexed (fn [k2 v2] [:img {:src (:url v2) :key k2 :style {:height "200px" :margin "8px 8px 0 0" :border-radius "8px" :border "1px solid #ffffff33"}}])
                                                 (mc.util/get-field @profile "Pictures"))]]]]]]
     (map-indexed (fn [i [title options items]] [:details (merge options {:key i})
                                                 [:summary [:span.title title]]
                                                 [:div {:style {:margin "0"
                                                                :border-radius "8px"
                                                                :padding "6px"
                                                                :vertical-align "top"
                                                                :line-height "1.2em"
                                                                :display "flex"
                                                                :justify-content "space-between"
                                                                :flex-wrap "wrap"
                                                                :width "100%"}}
                                                  (map-indexed bio-row items)]])
                  key-values))

   [:br]])


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;; HOME TAB ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn fetch-bios []
  (println "fetching bios")
  (if mock-data?
    (swap! bios (fn [_] [{:id "2"
                          (keyword "First name") "Alice"}]))
    (util/fetch "/meetcute/api/matchmaking/bios" (fn [result]
                                                  ;; TODO: if success, then navigate to profile
                                                   (swap! bios (fn [_] result))))))

(defn profile-section [contents]
  [:div {:className "profile-section"
         :style {:break-inside "avoid"
                 :column-count 2
                 :column-width "100px"
                 :margin-top "24px"}}
   [:style ".profile-section > .profile-item:first-of-type { margin-top: 0 !important}"] ; if first child of profile-section, 0 margin on top
   contents])

(defn profile-item [title content]
  [:div {:className "profile-item"
         :style {:margin "24px 12px 12px 12px" :break-inside "avoid"
                ;;  :border "2px solid #ff00ff11" ; for debugging only
                 }}
   [:div {:style {:font-weight "bold" :text-transform "uppercase" :font-style "italic" :color "#bcb5af" :font-size ".8em"}}
    title]
   [:div
    content]])

(defn render-profile [bio]
  [:div {:style {:padding "12px 12px 24px 12px"
                 :margin "12px"
                 :border-radius "8px"
                 :background "white"
                 :border "3px solid rgba(188, 181, 175, .3)"}}

   [:h2 {:style {:font-size "1.5em" :margin "12px 12px 24px 12px"}}
    (mc.util/get-field bio "First name")]

   [:style (str ".profile-item:first-of-type { margin-top: 0 !important}" ; if first child of profile-section, 0 margin on top
                ".profile-item {line-break: anywhere;}")]

   [:div  {:style {:column-count 2
                   :column-width "300px"
                   :column-gap "12px"}}

      ;;  [profile-section [:<>
      ;;                    [profile-item "Gender" "Woman"]
      ;;                    [profile-item "Looking for" "Man"]]]    

    (when (mc.util/get-field bio "Anything else you'd like your potential matches to know?")
      [profile-item (str "About " (mc.util/get-field bio "First name"))  (md->hiccup (mc.util/get-field bio "Anything else you'd like your potential matches to know?"))])

    [profile-item "Home base"                                    (mc.util/get-field bio "Home base city")]

    (when (mc.util/get-field bio "Other cities where you spend time")
      [profile-item "Frequently visits"                           (mc.util/get-field bio "Other cities where you spend time")])

    (when (mc.util/get-field bio "Social media links")
      [profile-item "Social media"                               (md->hiccup (mc.util/get-field bio "Social media links"))])

    (when (mc.util/get-field bio "What makes this person awesome?")
      [profile-item "Vouch from a friend"                        (md->hiccup (mc.util/get-field bio "What makes this person awesome?"))])

    [profile-item "Pictures" (if (empty? (mc.util/get-field bio "Pictures"))
                               [:p "No pictures yet :)"]
                               (map-indexed (fn [k2 v2] [:img {:src (:url v2) :key k2 :style {:height "150px" :margin "8px 8px 0 0" :border-radius "4px"}}])
                                            (mc.util/get-field bio "Pictures")))]]])

(def how-it-works-dismissed? (r/atom false))

(defn how-it-works []
  [:div {:className (when @how-it-works-dismissed? "how-it-works-dismissed")
         :style {:font-style    "italic"
                 :transition    "opacity 0.2s, visibility 0.2s, max-height 0.2s"
                 :opacity       1
                 :background    "rgba(170, 170, 170, 0.2)"
                 :visibility    "visible"
                 :color         "#777"
                 :max-width     "600px"
                 :margin        "8px auto 12px auto"
                 :border-radius "8px"
                 :padding       "16px 16px 16px 8px"
                 :font-size     "0.9em"
                 :line-height   "1.4em"}}

   [:style ".how-it-works-dismissed { display: none; opacity: 0 !important; visibility: hidden !important; max-height: 0 }"]

   [:div {:style {:float "right"
                  :cursor "pointer"
                  :padding "8px"
                  :font-size "1.2em"}
          :on-click #(do (js/console.log "clicked dismiss")
                         (reset! how-it-works-dismissed? true)
                         (js/event.preventDefault %))}
    [:i {:className "fas fa-times"}]]

   [:p {:style {:padding "2px 16px"}} "How MeetCute works:"]
   [:ul {:padding "0 16px"}
    [:li {:style {:padding "2px 8px" :margin-left "16px"}} "Every day, we'll send you an email with one new person"]
    [:li {:style {:padding "2px 8px" :margin-left "16px"}} "You let us know if you're interested in meeting them"]
    [:li {:style {:padding "2px 8px" :margin-left "16px"}} "If they're interested too, Erik or Devon will personally introduce you!"]]])

(defn truncate-string [s]
  (if (> (count s) 20)
    (str (subs s 0 20) "...")
    s))

(defn refresh-todays-cutie-link [i user]
  (let [id (mc.util/get-field user "id")
        full-name (str (mc.util/get-field user "First name") " " (mc.util/get-field user "Last name"))]
    [:tr {:key i}
     [:td {:style {:text-align "right"}}
      (truncate-string full-name)]
     [:td [:a {:href "#"
               :on-click (fn []
                           (reset! loading-message (str "Refreshing todays-cutie for " full-name "..."))
                           (util/fetch-post "/meetcute/api/refresh-todays-cutie"
                                            {:id id}
                                            #(do
                                               (reset! loading-message false)
                                               #_(js/location.reload true))))}
           "Refresh todays-cutie for just this person →"]]]))

(defn refresh-todays-cutie-btns []
  [:div {:style {:margin "48px 0" :background "#eee" :border-radius "8px" :padding "6px 24px"}}
   [:h2 {:style {:font-size "2em" :line-height "2em"}} "Manual override:"]
   [:div {:style {:margin "24px 0"}}
    [:p {:style {:margin "16px 0"}} "This is the manual override for the action that we'll run nightly with a cron job."]
    [:p {:style {:margin "16px 0"}} "Usually, we should not touch these buttons, but if you have a reason you need to refresh the todays-cutie for yourself or for everyone, you can do it here."]]

   #_[:div {:style {:margin "24px 0"}}
      [:a {:href "#"
           :on-click (fn []
                       (reset! loading-message "Refreshing todays-cutie for JUST ME...")
                       (util/fetch-post "/meetcute/api/refresh-todays-cutie/mine"
                                        {}
                                        #(do
                                           (reset! loading-message false)
                                           #_(js/location.reload true))))}
       "Refresh todays-cutie for JUST ME →"]]



   [:div {:style {:margin "24px 0"}}
    [:a {:href "#"
         :on-click (fn []
                     (js/console.log "clicked refresh todays-cutie for everyone")
                     (reset! loading-message "Refreshing todays-cutie for EVERYONE...")
                     (util/fetch-post "/meetcute/api/refresh-todays-cutie/all"
                                      {}
                                      #(do
                                         (println "done refreshing todays-cutie for everyone. result:")
                                         (js/console.log %)
                                         (reset! loading-message false)))
                     ;
                     )}
     "Refresh todays-cutie for EVERYONE →"]]

   ; list all bios where include-in-nightly-job-TMP = true:
   (let [bios-with-include-in-nightly-job-TMP (filter #(mc.util/get-field % "include-in-nightly-job-TMP") @bios)]
     [:div
      [:h1 "Refresh todays-cutie for all users with include-in-nightly-job-TMP=true:"]
      [:pre (count bios-with-include-in-nightly-job-TMP)]])

   [:details
    [:summary "Refresh todays-cutie for each user individually:"]
    [:table [:tbody (map-indexed refresh-todays-cutie-link @bios)]]
    [:div]]])

(defn list-mutual-selections []
  [:div {:style {:margin "48px 0" :background "#eee" :border-radius "8px" :padding "24px"}}
   [:h2 {:style {:font-size "2em" :line-height "2em"}} "List of matches:"]
   (let [mutual-selections (set (flatten
                                 (map (fn [this-cutie] (let [my-id (:id this-cutie)
                                                             my-selected-cuties (mc.util/get-field this-cutie "selected-cuties") ; ids only
                                                             my-selected-cuties (filter #(in? my-selected-cuties (mc.util/get-field % "id")) @bios)
                                                             matches (filter #(in? (mc.util/get-field % "selected-cuties") my-id) my-selected-cuties)
                                                             matches-list (map #(set [% this-cutie]) matches)]
                                                         matches-list))
                                      @bios)))]
     [:div
      [:table {:style {:text-align "center"}}
       [:tbody
        (map-indexed (fn [index match]
                       [:<> {:key index} ; the comma column is there to make it easier to copy/paste into a spreadsheet / iMessage / etc
                        [:tr {:key (str index "-1")} [:td {:colspan 3 :style {:text-align "left" :padding-top "32px" :font-size ".85em" :font-weight "bold" :border-bottom "2px solid #ddd"}} "match #" (+ 1 index)]]
                        [:tr {:key (str index "-2")} [:td {:style {:text-align "right"}} (str (mc.util/get-field (first match) "First name") " " (mc.util/get-field (first match) "Last name"))] [:td.invisible ", "] [:td (str (mc.util/get-field (second match) "First name") " " (mc.util/get-field (second match) "Last name"))]]
                        [:tr {:key (str index "-3")} [:td {:style {:text-align "right"}} (mc.util/get-field (first match) "Phone")]                                                              [:td.invisible ", "] [:td (mc.util/get-field (second match) "Phone")]]
                        [:tr {:key (str index "-4")} [:td {:style {:text-align "right"}} (mc.util/get-field (first match) "Email")]                                                              [:td.invisible ", "] [:td (mc.util/get-field (second match) "Email")]]])
                     mutual-selections)]

       ;
       ]])])

(defn render-obj [obj] (js/JSON.stringify (clj->js obj) nil 2))

(defn find-cutie [cutie-id bios]
  (first (filter #(= (mc.util/get-field % "id")
                     cutie-id)
                 bios)))

(def reviewed-bios-expanded? (r/atom false))

(defn home-tab []
  (let [included-bios (mc.util/included-bios @profile @bios)]
    (when @profile
      [:div {:style {:padding "12px" :padding-top "0"}}
       (if (nil? included-bios)
         [:p "Loading..."]
         [:div
          (let [currently-selected-ids (mc.util/get-field @profile "selected-cuties")
                currently-rejected-ids (mc.util/get-field @profile "rejected-cuties")
                ;; get-cuties-name #(get-in (find-cutie % included-bios) [(keyword "First name")])
                ;; reviewed-bios (filter #(let [bio-id (mc.util/get-field % "id")] (or (in? currently-selected-ids bio-id)
                ;;                                                                     (in? currently-rejected-ids bio-id)))
                ;;                       included-bios)
                todays-cutie-id (first (mc.util/get-field @profile "todays-cutie"))
                todays-cutie (find-cutie todays-cutie-id included-bios)
                ;; union-of-lists (set/union (set (:unseen-cuties   @profile))
                ;;                           (set (:todays-cutie    @profile))
                ;;                           (set (:selected-cuties @profile))
                ;;                           (set (:rejected-cuties @profile)))
                ]

            [:div {:style {:width "95%" :margin "auto" :max-width "900px"}}
             #_[:<>
              ;; [:pre {:style {:background "#ff00ff11" :margin "24px" :padding "24px"}}
              ;;                     ;; [:b "       new-bios: "] (count new-bios) "\n"
              ;;  [:b "  reviewed-bios: "] (count reviewed-bios) "\n\n"
              ;;  [:b "  included-bios: "] (count included-bios) "\n\n"]

              ;; [:pre {:style {:background "#ff00ff11" :margin "24px" :padding "24px"}}
              ;;  "included-bios: " (render-obj (map :id included-bios))]

                [:pre {:style {:background "#ff00ff11" :margin "24px" :padding "24px" :line-height "1.4em"}}
                 (render-obj {:assert {:count-distinct (count union-of-lists)
                                       :constant-count (if (= (count union-of-lists) 7)                                                "✅" "🔴")
                                       :unseen-selected-no-overlap (if (empty? (set/intersection (set (:unseen-cuties   @profile))
                                                                                                 (set (:selected-cuties @profile))))   "✅" "🔴")
                                       :unseen-rejected-no-overlap (if (empty? (set/intersection (set (:unseen-cuties   @profile))
                                                                                                 (set (:rejected-cuties @profile))))   "✅" "🔴")
                                       :selected-rejected-no-overlap (if (empty? (set/intersection (set (:selected-cuties @profile))
                                                                                                   (set (:rejected-cuties @profile)))) "✅" "🔴")}
                              :lists {:unseen   (map get-cuties-name (:unseen-cuties   @profile))
                                      :today    (map get-cuties-name (:todays-cutie    @profile))
                                      :selected (map get-cuties-name (:selected-cuties @profile))
                                      :rejected (map get-cuties-name (:rejected-cuties @profile))
                                            ;;  :unseen   (map #(select-keys (find-cutie % included-bios) [(keyword "First name") :id]) (:unseen-cuties @profile))
                                            ;;  :today    (map #(select-keys (find-cutie % included-bios) [(keyword "First name") :id]) (:todays-cutie @profile))
                                            ;;  :selected (map #(select-keys (find-cutie % included-bios) [(keyword "First name") :id]) (:selected-cuties @profile))
                                            ;;  :rejected (map #(select-keys (find-cutie % included-bios) [(keyword "First name") :id]) (:rejected-cuties @profile))
                                             ;
                                      }})
              ;;  "\n\n"
              ;;  [:b "todays-cutie: "] (render-obj (select-keys todays-cutie [(keyword "First name")
              ;;                                                               :Phone
              ;;                                                               :id]))
                 ]
                [refresh-todays-cutie-btns]]


             [how-it-works]
             [:h1 {:style {:font-size "36px" :line-height "1.3em" :padding "8px" :text-align "center"}} "Today's cutie"]
             [:style "@media screen and (min-width: 600px) { .profile-column { min-width: 500px } }"]

             (if (nil? todays-cutie)
               [:p {:style {:padding "6px 16px" :text-align "center"}}
                "No profiles to review right now. We'll email you with more people to meet soon!"]


               [:div {:style {:display "flex" ;(if (= i 0) "flex" "none")
                              :flex-direction "column" ; "row"
                              :flex-wrap "wrap"
                              :width "100%"
                              :margin "auto"}}

                ; Left column takes all available space
                [:style "@media screen and (min-width: 600px) { .profile-column { min-width: 500px } }"]
                [:div {:style {:flex 1} :className "profile-column"}
                 [render-profile todays-cutie]]

                [:div {:style {:flex 1 :display "flex" :align-items "center" :flex-direction "column" :text-align "center"}}

                 [:div {:style {:margin      "16px 0 12px 0"
                                :flex        1
                                :line-height 1.6
                                :min-height  "3.4em"
                                :align-items "center"
                                :display     "flex"}}
                  (if (in? currently-selected-ids todays-cutie-id)
                    [:p {:style {}} "You've selected this person!" [:br] "We'll let you know if they select you too :)"]
                    (if (in? currently-rejected-ids todays-cutie-id)
                      [:p {:style {}} "Sounds like this cutie is not your main squeeze." [:br] "No worries, we'll send you another cutie soon!"]
                      [:p {:style {}} "So, are you interested in meeting this cutie?"]))]

                 [:div {:style {:flex 1}}
                  [select-reject-btns
                   (mc.util/get-field todays-cutie "id")
                   (mc.util/get-field @profile "selected-cuties")
                   (mc.util/get-field @profile "rejected-cuties")]]]])



             #_(if (= 0 (count new-bios))
                 [:<>
                  [:p {:style {:padding "6px 16px"}} "No profiles to review right now"]
                  [:p {:style {:padding "6px 16px"}} "We'll email you with more people to meet soon!"]]

                 [:<>
                  [:h1 {:style {:font-size "36px" :line-height "1.3em" :padding "32px 16px 16px 16px"}} "Today's person:"]
                  (profile-with-buttons 0 todays-bio)])


             #_(if (= 0 (count new-bios))
                 [:<> [:p {:style {:padding "8px 16px 24px 16px"}} "No profiles to review right now"]
                  [:p {:style {:padding "8px 16px 24px 16px"}} "We'll email you with more people to meet soon!"]]
                 (doall (map-indexed profile-with-buttons new-bios)))

             #_[:div {:style {:padding "24px 0 0 0" :margin-left "40px" :margin-top "24px"}}
                [:h1 {:on-click #(reset! reviewed-bios-expanded? (not @reviewed-bios-expanded?))
                      :style {:display "inline"
                              :margin-left "8px"
                              :font-size 36
                              :line-height "1.3"
                              :cursor "pointer"}}
                 [:span {:style {:margin-left "-40px"
                                 :transition "all .3s"
                                 :margin-top "8px"}} (if @reviewed-bios-expanded?
                                                       [fa-icon "chevron-down"]
                                                       [fa-icon "chevron-right"])]
                 "Profiles you've already reviewed "]]
             #_(when @reviewed-bios-expanded?
                 [:div {:style {:margin-top "24px"}}
                  (if (> (count reviewed-bios) 0)
                    (doall (map-indexed profile-with-buttons reviewed-bios))
                    [:p {:style {:margin-left "8px"}} "You haven't reviewed any profiles yet :)"])])
             [:br] [:br]])])])))

(defn admin-tab []
  (if (:admin? @profile)
    [:div {:style {:border-radius "8px" :padding "36px 12px" :margin "auto" :width "90%" :max-width "850px"}}
     [:span {:style {:padding "4px 12px" :border-radius "8px" :color "white" :background (if (:admin? @profile) "green" "red") :float "right"}}
      (if (:admin? @profile)
        "You are an admin"
        "You are not an admin")]
     [refresh-todays-cutie-btns]
     [list-mutual-selections]]
    (js/location.assign "/meetcute") ; if not admin, redirect to home
    ))

(defn nav-btns []
  [:div {:style {:margin "12px" :margin-bottom "0"}}
   [:a {:href "/meetcute"
        :className (if (= "/meetcute" (.-pathname js/location))
                     "btn primary"
                     "btn")}
    ;; [:i {:className "fas fa-heart"}] " Home"
    "Home"]
   [:a {:href "/meetcute/settings"
        ; if current url is /meetcute/settings, then set className to "btn primary", otherwise set it to "btn":
        :className (if (= "/meetcute/settings" (.-pathname js/location))
                     "btn primary"
                     "btn")}
    ;; [:i {:className "fas fa-user"}] " Profile"
    "Profile"]

   (when (:admin? @profile)
     [:a {:href "/meetcute/admin"
          :className (if (= "/meetcute/admin" (.-pathname js/location))
                       "btn primary"
                       "btn")}
      ;; [:i {:className "fas fa-cog"}] " Admin"
      "Admin"])

   ;; TODO(sebas): make this a post request to clear the cookie
   [:form {:action "/meetcute/logout" :method "post"
           :style {:float "right"}}
    [:input {:type "submit"
             :className "btn"
             :on-click #(reset! profile nil)
             :value "Logout"
             :style {:float "right"}}]]
   [:br]])

(defn loading-page [loading-message]
  [:div.loading-container
   [:div#loading-spinner.spinner {:style {:display "block"}}]
   (when loading-message
     [:p {:style {:margin-top "24px" :max-width "200px" :text-align "center"}}
      loading-message])])

(defn error-screen [error]
  [:div
   [:p "Error: " error]
   [:a {:href "/meetcute/signin"}
    "Sign in"]])

(defn screen [tab-name]
  (fn [] (r/create-class
          {:component-did-mount (fn []
                                  (fetch-my-profile!)
                                  (fetch-bios))
           :reagent-render (fn []
                             (cond
                        ;; TODO: profile could be nil because of some access problem
                               (some? @profile-error) (if (not= "/meetcute/signin" (.-pathname js/location))
                                                        (js/location.assign "/meetcute/signin")
                                                        (error-screen @profile-error))

                               (nil? @profile) (loading-page nil)

                               @loading-message (loading-page @loading-message)

                               :else [:div
                                      [nav-btns]
                                      (case tab-name
                                        :profile (profile-tab)
                                        :admin   (admin-tab)
                                        :home    (home-tab)
                                        (home-tab))]))}))) ""
