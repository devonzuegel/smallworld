(ns smallworld.screens.meetcute
  (:require [clojure.string :as str]
            [reagent.core    :as r]
            [smallworld.util :as util]
            [markdown.core :as md]
            [cljs.pprint :as pp]))

(defonce debug? (r/atom false))
(defonce current-tab (r/atom :home))
(def bios   (r/atom nil))
(def profile (r/atom nil))

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

(defn get-field [bio field]
  (get-in bio [(keyword field)]))

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

(defn update-selected-cuties []
  (println "updating selected-cuties")
  (util/fetch-post "/meetcute/api/matchmaking/profile"
                   (select-keys @profile (map #(keyword %)
                                              (concat fields-changeable-by-user ; Phone is not editable, but it's needed as the key to find the record to update
                                                      ["Phone"])))
                   #(println "Done updating selected-cuties")))

(defn bio-row [i [key-name value]]
  [:div {:key i :className "bio-row" :style {:width "fit-content"}}
   [:style "@media screen and (min-width: 600px) { .bio-row-value { min-width: 380px; }  }"]
   [:div {:style {:padding "24px 12px 0 12px"
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
  [:div {:style {:display "flex" :flex-wrap "wrap" :flex-direction "column" :width "100%" :margin-right "8px"}}
   [:style (str ".select-reject-btn { opacity: .8; }"
                ".select-reject-btn:hover { opacity: 1; transition: opacity .3ms ease-in }"
                "@media screen and (min-width: 805px) { .select-reject-btn { min-width: 160px; max-width: 260px; } }")]
   [:div {:style {:margin "6px"}}
    [:input {:type "checkbox"
             :id (str bio-id "-select")
             :value bio-id
             :checked (boolean (in? currently-selected-ids bio-id))
             :style {:display "none"}
             :on-change (fn [event]
                          (if (or (nil? event) (nil? (.-target event)))
                            (println "event: " event)
                            (let [checked? (.-checked (.-target event))
                                  now-selected (if checked?
                                                 (if (in? currently-selected-ids bio-id)
                                                   currently-selected-ids
                                                   (conj currently-selected-ids bio-id))
                                                 (remove (fn [v] (= bio-id v))
                                                         (get-field @profile "selected-cuties")))]
                              (reset! profile (assoc @profile
                                                     (keyword "selected-cuties")
                                                     now-selected))
                              (when checked?
                                (reset! profile (assoc @profile
                                                       (keyword "rejected-cuties")
                                                       (remove (fn [v] (= bio-id v))
                                                               (get-field @profile "rejected-cuties")))))
                              (update-selected-cuties))))}]
    [:label {:for (str bio-id "-select")
             :className "select-reject-btn"
             :style {:padding "20px"
                     :color "#42b72a"
                     :font-size "1.1em"
                     :line-height "1.3em"
                     :font-weight "600"
                     :cursor "pointer"
                     :text-align "center"
                     :display "block"
                     :flex-grow 1
                     :border-radius "3000px"
                     :background "#42b72a22"
                    ;;  :background (if (in? currently-selected-ids bio-id) "#42b72a" "#42b72a22")
                    ;;  :border "5px solid #42b72a"
                     :border (if (in? currently-selected-ids bio-id) "5px solid #42b72a" "5px solid #42b72a0d")
                     ;
                     }}
     "I'd like to" [:br]
     "meet this person!"]]

   [:div {:style {:margin "6px"}}
    [:input {:type "checkbox"
             :id (str bio-id "-reject")
             :value bio-id
             :checked (boolean (in? currently-rejected-ids bio-id))
             :style {:display "none"}
             :on-change (fn [event]
                          (if (or (nil? event) (nil? (.-target event)))
                            (println "event: " event)
                            (let [checked? (.-checked (.-target event))
                                  now-rejected (if checked?
                                                 (if (in? currently-rejected-ids bio-id)
                                                   currently-rejected-ids
                                                   (conj currently-rejected-ids bio-id))
                                                 (remove (fn [v] (= bio-id v))
                                                         (get-field @profile "rejected-cuties")))]
                              (println "now-rejected: " now-rejected)
                              (reset! profile (assoc @profile
                                                     (keyword "rejected-cuties")
                                                     now-rejected))
                              (when checked?
                                (reset! profile (assoc @profile
                                                       (keyword "selected-cuties")
                                                       (remove (fn [v] (= bio-id v))
                                                               (get-field @profile "selected-cuties")))))
                              (update-selected-cuties))))}]
    [:label {:for (str bio-id "-reject")
             :className "select-reject-btn"
             :style {:padding "20px"
                     :color "#aaaaaa"
                     :font-size "1.1em"
                     :line-height "1.3em"
                     :font-weight "600"
                     :cursor "pointer"
                     :text-align "center"
                     :display "block"
                     :flex-grow 1
                     :border-radius "3000px"
                     :background "#aaaaaa22"
                    ;;  :background (if (in? currently-rejected-ids bio-id) "#aaaaaa" "#aaaaaa22")
                    ;;  :border "5px solid #aaaaaa"
                     :border (if (in? currently-rejected-ids bio-id) "5px solid #aaaaaa" "5px solid #aaaaaa0d")
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
  (if (:error result)
    (reset! phone-input-error (:error result))
    (do (reset! profile (merge (:fields result)
                               {:id (:id result)}))
        (println "finished updating profile with result!")
        ;; (pp/pprint "profile keys: ")
        ;; (pp/pprint (keys @profile))
        ;; (redirect! "/meetcute")
        )))

(def profile-error (r/atom nil))

(defn fetch-my-profile! []
  (println "\nfetching my profile...")
  (util/fetch-post
   "/meetcute/api/matchmaking/me"
   {}
   (fn [result]
     (if-let [error (:error result)]
       (reset! profile-error error)
       (do
         (reset! profile (merge (:fields result)
                                {:id (:id result)}))
         (println "finished updating profile with result!"))))))

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
           :value (or (get-field @profile field-name) "") #_(trim-trailing-whitespace (or (get-field @profile field-name) ""))
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
  [:textarea {:value (or (get-field @profile field-name) "") #_(trim-trailing-whitespace (or (get-field @profile field-name) ""))
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

   [:div {:style {:margin "0"
                  :border-radius "8px"
                  :padding "6px"
                  :vertical-align "top"
                  :line-height "1.2em"
                  :display "flex"
                  :justify-content "space-between"
                  :flex-wrap "wrap"
                  :width "100%"}}
    (let [key-values [["First name"                       (editable-input "First name")]
                      ["Last name"                        (editable-input "Last name")]
                      ["My gender" (radio-btns-component ["Man" "Woman"]
                                                         (get-field @profile "Gender")
                                                         (fn [foobar]
                                                           (reset! profile (assoc @profile (keyword "Gender") foobar))
                                                           (update-profile-debounced!)))]
                      ["I'm interested in..." (checkboxes-component ["Men" "Women"]
                                                                    (get-field @profile "I'm interested in...")
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
                                                            ;; (format-phone (get-field @profile "Phone")) ; don't make this editable, because it's the key to find the record to update. in the future, we can use the ID instead if we do want to make the phone editable
                                                            (get-field @profile "Phone")]
                                                           [small-text [:span "If you'd like to change your phone number, email "
                                                                        [:a {:href "mailto:lei@turpentine.co"} "lei@turpentine.co"] "."]]]]
                      ["Email"                            [:div {:style {:max-width "380px"}}
                                                           (editable-input "Email")
                                                           [small-text "We will only share your contact info when you match with someone. It will not be shown on your profile."]]]
                      ["Home base city"                    (editable-input "Home base city")]
                      ["Other cities where you spend time" (editable-input "Other cities where you spend time")]
                      ["About me"                          (editable-textbox "Anything else you'd like your potential matches to know?")]
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
                                   [small-text [:span "Here are the pictures you added when you signed up. If you'd like to add or remove pictures, email "
                                                [:a {:href "mailto:lei@turpentine.co"} "lei@turpentine.co"] "."]]
                                   (map-indexed (fn [k2 v2] [:img {:src (:url v2) :key k2 :style {:height "200px" :margin "8px 8px 0 0" :border-radius "8px" :border "1px solid #ffffff33"}}])
                                                (get-field @profile "Pictures"))]]]]
      (map-indexed bio-row key-values))]

   [:br]])


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;; HOME TAB ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn fetch-bios []
  (println "fetching bios")
  (util/fetch "/meetcute/api/matchmaking/bios" (fn [result]
                                                 ;; TODO: if success, then navigate to profile
                                                 (swap! bios (fn [_] result)))))

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
    (get-field bio "First name")]

   [:style ".profile-item:first-of-type { margin-top: 0 !important}"] ; if first child of profile-section, 0 margin on top

   [:div  {:style {:column-count 2
                   :column-width "300px"
                   :column-gap "12px"}}

      ;;  [profile-section [:<>
      ;;                    [profile-item "Gender" "Woman"]
      ;;                    [profile-item "Looking for" "Man"]]]    

    (when (get-field bio "Anything else you'd like your potential matches to know?")
      [profile-item (str "About " (get-field bio "First name"))  (md->hiccup (get-field bio "Anything else you'd like your potential matches to know?"))])

    [profile-item "Home base"                                    (get-field bio "Home base city")]

    (when (get-field bio "Other cities where you spend time")
      [profile-item "Frequently visits"                           (get-field bio "Other cities where you spend time")])

    (when (get-field bio "Social media links")
      [profile-item "Social media"                               (md->hiccup (get-field bio "Social media links"))])

    (when (get-field bio "What makes this person awesome?")
      [profile-item "Vouch from a friend"                        (md->hiccup (get-field bio "What makes this person awesome?"))])

    [profile-item "Pictures" (if (empty? (get-field bio "Pictures"))
                               [:p "No pictures yet :)"]
                               (map-indexed (fn [k2 v2] [:img {:src (:url v2) :key k2 :style {:height "150px" :margin "8px 8px 0 0" :border-radius "4px"}}])
                                            (get-field bio "Pictures")))]]])

(defn how-it-works []
  [:div {:style {:margin-top "24px" :font-style "italic"}}
   [:p {:style {:padding "2px 16px"}} "How MeetCute works:"]
   [:ul {:padding "0 16px"}
    [:li {:style {:padding "2px 8px" :margin-left "16px"}} "Every day, we'll send you an email with one new person"]
    [:li {:style {:padding "2px 8px" :margin-left "16px"}} "You let us know if you're interested in meeting them"]
    [:li {:style {:padding "2px 8px" :margin-left "16px"}} "If they're interested too, we'll introduce you!"]]])

(defn profile-with-buttons [i bio]

  [:div {:style {:display "flex" ;(if (= i 0) "flex" "none")
                 :flex-direction "row"
                 :flex-wrap "wrap"
                 :width "100%"
                 :margin "auto"}
         :key i}

   ; Left column takes all available space
   [:style "@media screen and (min-width: 600px) { .profile-column { min-width: 500px } }"]
   [:div {:style {:flex 1} :className "profile-column"}
    [render-profile bio]]

   ; Right column with fixed width
   [:style "@media screen and (min-width: 805px) { .btns-column { max-width: 250px; } }"]
   [:div {:style {:flex 1} :className "btns-column"}
    [select-reject-btns
     (get-field bio "id")
     (get-field @profile "selected-cuties")
     (get-field @profile "rejected-cuties")]]])

(defn render-obj [obj] (js/JSON.stringify (clj->js obj) nil 2))

(def reviewed-bios-expanded? (r/atom false))
(defn home-tab []
  (let [interested-in (get-field @profile "I'm interested in...")
        gender-filter (case interested-in
                        [] []
                        ["Women"] ["Woman"]
                        ["Men"] ["Man"]
                        ["Woman" "Man"] ; default
                        )
        included-bios (filter (fn [bio]
                                (let [bio-gender-filter (case (get-field bio "I'm interested in...")
                                                          [] []
                                                          ["Women"] ["Woman"]
                                                          ["Men"] ["Man"]
                                                          ["Woman" "Man"] ; default
                                                          )]
                                  (and (= (get-field bio "Include in gallery?") "include in gallery") ; don't include bios that have been explicitly excluded
                                       (not (= (get-field bio "id") (get-field @profile "id"))) ; check it's not self
                                       (some #(= (get-field bio "Gender") %) gender-filter) ; only show the gender that the user is interested in dating
                                       (some #(= (get-field @profile "Gender") %) bio-gender-filter ; only show someone if they're interested in dating someone of the gender of the current user:
                                             ))))

                              @bios)]
    (when @profile
      [:div {:style {:padding "12px" :padding-top "0"}}
       (if (nil? included-bios)
         [:p "Loading..."]
         [:div
          (let [currently-selected-ids (get-field @profile "selected-cuties")
                currently-rejected-ids (get-field @profile "rejected-cuties")
                new-bios      (filter #(let [bio-id (get-field % "id")] (not (or (in? currently-selected-ids bio-id)
                                                                                 (in? currently-rejected-ids bio-id))))
                                      included-bios)
                reviewed-bios (filter #(let [bio-id (get-field % "id")] (or (in? currently-selected-ids bio-id)
                                                                            (in? currently-rejected-ids bio-id)))
                                      included-bios)
                todays-cutie-id (first (get-field @profile "todays-cutie"))
                todays-cutie (first (filter #(= (get-field % "id")
                                                todays-cutie-id)
                                            included-bios))]

             ; problem context:
             ; i have a list of bios, and i want to show the first one that hasn't been reviewed yet
             ; once it has been reviewed, i still want it to show up as "today's bio" until tomorrow, when the next bio is shown

            [:div {:style {:width "95%" :margin "auto"}}
             [how-it-works]

             #_[:pre {:style {:background "#ff00ff11" :margin "24px" :padding "24px"}}
                [:b "       new-bios: "] (count new-bios) "\n"
                [:b "  reviewed-bios: "] (count reviewed-bios) "\n\n"
                [:b "  included-bios: "] (count included-bios) "\n\n"]

             #_[:pre {:style {:background "#ff00ff11" :margin "24px" :padding "24px"}}
                "included-bios: " (render-obj (map :id included-bios))]

             #_[:pre {:style {:background "#ff00ff11" :margin "24px" :padding "24px"}}
                (render-obj (select-keys @profile [:unseen-cuties
                                                   :todays-cutie
                                                   :selected-cuties
                                                   :rejected-cuties])) "\n\n"
                [:b "todays-cutie-id: "] todays-cutie-id "\n\n"
                [:b "   todays-cutie: "] (render-obj (select-keys todays-cutie [(keyword "First name")
                                                                                :Phone
                                                                                :id]))]

             [:h1 {:style {:font-size "36px" :line-height "1.3em" :padding "32px 16px 16px 16px"}} "Today's cutie:"]

             (if (nil? todays-cutie)
               [:p {:style {:padding "6px 16px"}} "No profiles to review right now. We'll email you with more people to meet soon!"]
               [:<>
                (if (in? currently-selected-ids todays-cutie-id)
                  [:p {:style {:padding "6px 16px"}} "You've selected this person! We'll let you know if they select you too :)"]
                  (if (in? currently-rejected-ids todays-cutie-id)
                    [:p {:style {:padding "6px 16px"}} "Sounds like this cutie is not your main squeeze. No worries, we'll send you another cutie soon!"]
                    [:p {:style {:padding "6px 16px"}} "Are you interested to meet this cutie?"]))
                (profile-with-buttons 0 todays-cutie)])

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

             [:div {:style {:padding "24px 0 0 0" :margin-left "40px" :margin-top "24px"}}
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
             (when @reviewed-bios-expanded?
               [:div {:style {:margin-top "24px"}}
                (if (> (count reviewed-bios) 0)
                  (doall (map-indexed profile-with-buttons reviewed-bios))
                  [:p {:style {:margin-left "8px"}} "You haven't reviewed any profiles yet :)"])])
             [:br] [:br]])])])))

(defn nav-btns []
  [:div {:style {:margin "12px" :margin-bottom "0"}}
   [:button {:on-click #(reset! current-tab :home)
             :className (if (= @current-tab :home)    "btn primary" "btn")}
    "Home"]
   [:button {:on-click #(reset! current-tab :profile)
             :className (if (= @current-tab :profile) "btn primary" "btn")}
    "Profile"]

   ;; TODO(sebas): make this a post request to clear the cookie
   [:form {:action "/meetcute/logout" :method "post"
           :style {:float "right"}}
    [:input {:type "submit"
             :className "btn"
             :on-click #(reset! profile nil)
             :value "Logout"
             :style {:float "right"}}]]
   [:br]])

;; TODO: better loading page
(defn loading-profile []
  [:div
   [:div#loading-spinner.spinner {:style {:display "block"}}]])

(defn error-screen [error]
  [:div
   [:p "Error: " error]
   [:a {:href "/meetcute/signin"}
    "Sign in"]])

(defn screen []
  (r/create-class
   {:component-did-mount (fn []
                           (fetch-my-profile!)
                           (fetch-bios))
    :reagent-render (fn []
                      (cond
                        ;; TODO: profile could be nil because of some access problem
                        (some? @profile-error) (if (not= "/meetcute/signin" (.-pathname js/location))
                                                 (js/location.assign "/meetcute/signin")
                                                 (error-screen @profile-error))

                        (nil? @profile) (loading-profile)

                        :else [:div
                               [nav-btns]
                               (case @current-tab
                                 :profile (profile-tab)
                                 :home (home-tab)
                                 (home-tab))]))})) ""
