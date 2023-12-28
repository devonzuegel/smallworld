(ns smallworld.screens.meetcute
  (:require [clojure.string :as str]
            [reagent.core    :as r]
            [smallworld.util :as util]
            [meetcute.screens.styles :as mc.styles]
            [meetcute.screens.auth :as mc.screens.auth]
            [markdown.core :as md]
            [cljs.pprint :as pp]))

(defonce debug? (r/atom false))
(defonce bios   (r/atom nil))
(defonce profile (r/atom nil))
(defonce current-tab (r/atom :home))

(def btn-styles mc.styles/btn)

(defn small-text [str & [styles]]
  [:p {:style (merge {:padding-top "4px" :padding-bottom "4px" :font-size ".7em" :opacity ".6"}
                     styles)} str])

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
                                "I'm interested in..."
                                "selections"
                                "rejections"
                                "What makes this person awesome?"
                                "Pictures"
                                "Gender"])

(defn format-phone [phone]
  (assert phone)
  (let [digits-only (str/replace phone #"[^0-9]" "")]
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
              :style {:cursor "pointer"}}
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
   [:label {:for (str value-name "-radio") :style {:cursor "pointer"}} value-name]])

(defn radio-btns-component [all-values selected-value update-selected-value]
  [:div {:style {:display "inline-flex" :justify "space-between" :margin-left "-16px"}}
   (for [value all-values] ^{:key value} [radio-btn-component value selected-value update-selected-value])
   (when @debug? [:pre "Selected Value: " (str selected-value)])])

(defn update-selections []
  (println "updating selections")
  (util/fetch-post "/meetcute/api/matchmaking/profile"
                   (select-keys @profile (map #(keyword %)
                                              (concat fields-changeable-by-user ; Phone is not editable, but it's needed as the key to find the record to update
                                                      ["Phone"])))
                   #(println "Done updating selections")))

(defn bio-row [i [key-name value]]
  [:div {:key i :className "bio-row" :style {:width "fit-content"}}
   [:div {:style {:padding "24px 12px 0 12px"
                  :opacity ".75"
                  :font-weight "bold"
                  :text-transform "uppercase"
                  :font-style "italic"
                  :color "#bebebe"
                  :font-size ".8em"}}
    key-name]
   [:div {:style {:padding "12px" :padding-top "6px" :min-width "380px"}}
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
                                                         (get-field @profile "selections")))]
                              (reset! profile (assoc @profile
                                                     (keyword "selections")
                                                     now-selected))
                              (when checked?
                                (reset! profile (assoc @profile
                                                       (keyword "rejections")
                                                       (remove (fn [v] (= bio-id v))
                                                               (get-field @profile "rejections")))))
                              #_(update-selections))))}]
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
                     :background (if (in? currently-rejected-ids bio-id) "#42b72a" "#42b72a22")
                     :border "5px solid #42b72a"}}
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
                                                         (get-field @profile "rejections")))]
                              (println "now-rejected: " now-rejected)
                              (reset! profile (assoc @profile
                                                     (keyword "rejections")
                                                     now-rejected))
                              (when checked?
                                (reset! profile (assoc @profile
                                                       (keyword "selections")
                                                       (remove (fn [v] (= bio-id v))
                                                               (get-field @profile "selections")))))
                              #_(update-selections))))}]
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
                     :background (if (in? currently-rejected-ids bio-id) "#aaaaaa" "#aaaaaa22")
                     :border "5px solid #aaaaaa"}}
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
        ;; (pp/pprint "profile keys: ")
        ;; (pp/pprint (keys @profile))
        ;; (redirect! "/meetcute")
        ;
        )))
3
(defn fetch-my-profile! []
  (println "\nfetching my profile...")
  (util/fetch-post "/meetcute/api/matchmaking/me"
                   {}
                   update-profile-with-result))

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
                   :border "3px solid #eee"
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
                      :border "3px solid #eee"
                      ;; :background "#66666620"
                      ;; :border "1px solid #66666622"
                      :border-radius "8px"
                      :resize "vertical" ; disallow horizontal resize
                      :padding "6px 8px"
                      :margin-right "12px"
                      :min-height "140px"
                      :color "#333"
                      :font-size ".9em !important" ; TODO: this is overridden by styles.css, need to fix
                      :width "98%"}}])

(defn saved-toast []
  [:div {:style {:position "fixed"
                 :bottom "30px"
                 :right "30px"
                 :background-color "#42b72a"
                 :color "#fff"
                 :font-weight "bold"
                 :padding "12px"
                 :border-radius "8px"
                 :opacity (if @show-toast 1 0)
                 :transition "opacity 0.3s"}}
   "Saved!"])

(defn profile-tab []
  [:div {:style {:border-radius "8px" :padding "12px" :margin-left "auto" :margin-right "auto" :width "90%" :max-width "850px"}}

   [saved-toast]

   [:h1 {:style {:font-size 36 :line-height "1.3em" :padding "0 12px"}} "Your profile"]

   [:div {:style {:margin "24px 0"
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
                                                           [:div {:style {:background "#f5f5f5"
                                                                          :border "3px solid #eee"
                                                                          :cursor "not-allowed"
                                                                          :border-radius "8px"
                                                                          :padding "6px 8px"
                                                                          :margin-right "4px"
                                                                          :width "95%"
                                                                          :max-width "380px"}}
                                                            (format-phone (get-field @profile "Phone"))] ; don't make this editable, because it's the key to find the record to update. in the future, we can use the ID instead if we do want to make the phone editable
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
                                                            [:div {:style {:border-left "5px solid #eee" :background "#f9f9f9" :margin-left "8px" :max-width "90%"}}
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
   [:div {:style {:font-weight "bold" :text-transform "uppercase" :font-style "italic" :color "#bebebe" :font-size ".8em"}}
    title]
   [:div
    content]])

(defn render-profile [bio]
  [:div {:style {:padding "12px 12px 24px 12px"
                 :margin "12px"
                 :border-radius "8px"
                 :background "white"
                 :border "2px solid #ddd"}}

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
     (get-field @profile "selections")
     (get-field @profile "rejections")]]])

(def reviewed-bios-expanded? (r/atom false))

(defn fa-icon [icon-name & {:keys [outlined] :or {outlined false}}]
  [:i {:className (str/join " " [(if outlined "far" "fas")
                                 (str "fa-" icon-name)])
       :style {:min-width "40px"}}])

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
          (let [currently-selected-ids (get-field @profile "selections")
                currently-rejected-ids (get-field @profile "rejections")
                new-bios      (filter #(let [bio-id (get-field % "id")] (not (or (in? currently-selected-ids bio-id)
                                                                                 (in? currently-rejected-ids bio-id))))
                                      included-bios)
                reviewed-bios (filter #(let [bio-id (get-field % "id")] (or (in? currently-selected-ids bio-id)
                                                                            (in? currently-rejected-ids bio-id)))
                                      included-bios)
                new-bio-count (str (count new-bios))]
            [:div {:style {:width "95%" :margin "auto"}}
             [:h1 {:style {:font-size "36px" :line-height "1.3em" :padding "32px 16px 16px 16px" :text-align "left"}}
              (if (= (count new-bios) 0)  [fa-icon "check"] [fa-icon "heart" :outlined true])
              " " new-bio-count " new " (if (= (count new-bios) 1) "profile" "profiles") " to review"]
             (if (= 0 (count new-bios))
               [:p {:style {:padding "8px 16px 24px 16px"}} "You've reviewed all the profiles for today. Check back later for more!"]
               (doall (map-indexed profile-with-buttons new-bios)))

             (when (> (count reviewed-bios) 0)
               [:div {:style {:padding-top "24px" :padding-bottom "24px"}}
                [:h1 {:on-click #(reset! reviewed-bios-expanded? (not @reviewed-bios-expanded?))
                      :style {:display "inline"
                              :margin-left "8px"
                              :font-size 36
                              :line-height "1.3"
                              :cursor "pointer"}}
                 [:span {:style {:transition "all .3s"
                                 :margin-top "8px"}} (if @reviewed-bios-expanded?
                                                       [fa-icon "chevron-down"]
                                                       [fa-icon "chevron-right"])]
                 " Profiles you've already reviewed "]
                (when @reviewed-bios-expanded?
                  [:div {:style {:margin-top "24px"}} (doall (map-indexed profile-with-buttons reviewed-bios))])])
             [:br] [:br]])])])))

(defn nav-btns []
  [:div {:style {:margin "12px" :margin-bottom "0"}}
   [:button {:on-click #(reset! current-tab :home)
             :style (merge btn-styles (if (= @current-tab :home) {:border  "3px solid #cccccc88"} {}))}
    "Home"]
   [:button {:on-click #(reset! current-tab :profile)
             :style (merge btn-styles (if (= @current-tab :profile) {:border  "3px solid #cccccc88"} {}))}
    "Profile"]
  ;;  [:button {:on-click #(reset! debug? (not @debug?)) :style (merge btn-styles {:float "right"})} (str "Debug: " @debug?)]

   ;; TODO(sebas): make this a post request to clear the cookie
   [:form {:action "/meetcute/api/auth/logout" :method "post" :style {:float "right"}}
    [:input {:type "submit"
             :on-click #(reset! profile nil)
             :value "Logout"
             :style (merge btn-styles {:float "right"})}]]
   [:br]])

(defn screen []
  (r/create-class
   {:component-did-mount (fn []
                           (fetch-my-profile!)
                           (fetch-bios))
    :reagent-render (fn []

                      #_[:div
                         [:p "HOME"]
                         [:pre "@profile: " (pr-str @profile)]]


                      (if (nil? @profile)
                        (case @current-tab
                          :signup (mc.screens.auth/signup-screen {:to-signin #(reset! current-tab :signin)})
                          :signin (mc.screens.auth/signin-screen {:to-signup #(reset! current-tab :signup)})
                          (mc.screens.auth/signin-screen {:to-signup #(reset! current-tab :signup)}))

                        [:div
                         [nav-btns]

                         (case @current-tab
                           :profile (profile-tab)
                           :home (home-tab)
                           (home-tab))]))}))
