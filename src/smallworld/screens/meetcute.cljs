(ns smallworld.screens.meetcute
  (:require [cljs.pprint            :as pp]
            [clojure.string         :as str]
            [clojure.walk :as walk]
            [goog.dom               :as dom]
            [markdown.core          :as md]
            [meetcute.util          :as mc.util]
            [reagent.core           :as r]
            [smallworld.decorations :as decorations]
            [smallworld.mapbox      :as mapbox]
            [smallworld.util        :as util]))

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

(defn format-phone [phone] ; TODO: replace this with google-libphonenumber fn, which is probably more robust
  (if (re-matches #"\+\d{11}" phone)
    (let [digits-only (str/replace phone #"[^0-9]" "")]
      (str "+" (subs digits-only 0 1) " (" (subs digits-only 1 4) ") " (subs digits-only 4 7) "-" (subs digits-only 7 11)))
    phone))

(defn checkbox-component [value-name selected-values update-selected-values]
  (let [checked? (boolean (mc.util/in? selected-values value-name))]
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
  [:div {:style {:margin-left "-16px" :margin-top "4px" :font-size "1.3em"}}
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
  [:div {:style {:display "inline-flex" :justify "space-between" :margin-left "-16px" :margin-top "4px" :font-size "1.3em"}}
   (for [value all-values] ^{:key value} [radio-btn-component value selected-value update-selected-value])
   (when @debug? [:pre "Selected Value: " (str selected-value)])])

(defn update-selected-cuties! []
  (util/fetch-post "/meetcute/api/matchmaking/profile"
                   (select-keys @profile (map #(keyword %)
                                              (concat mc.util/fields-changeable-by-user ; Phone is not editable, but it's needed as the key to find the record to update
                                                      ["Phone"])))
                   #(println "Done updating selected-cuties")))

;; (defn bio-row [i [key-name value & [options]]]
(defn bio-row [i [key-name value & [{:keys [required?] :as options}]]]
  [:div.bio-row {:key i}
   [:div.title key-name (when required? [:span.required-note "*"])]
   [:div {:style {:padding "12px" :padding-top "6px"} :className (str/join " " ["bio-row-value"
                                                                                (if required? "required" "not-required")])}
    value]])

(defn select-reject-btns [bio-id currently-selected-ids currently-rejected-ids]
  [:div {:style {:display "flex" :flex-wrap "wrap" :flex-direction "row" #_"column" :width "100%" :margin-right "8px" :justify-content "center"}}

   [:style (str ".select-reject-btn { opacity: .9; transition: opacity .3ms ease-in; cursor: pointer !important }"
                ".select-reject-btn:hover { opacity: 1 }"
                "@media screen and (min-width: 805px) { .select-reject-btn { min-width: 160px; max-width: 260px; } }")]

   [:div {:style {:margin "6px" :cursor "pointer"}}
    [:input {:type "checkbox"
             :id (str bio-id "-select")
             :value bio-id
             :checked (boolean (mc.util/in? currently-selected-ids bio-id))
             :style {:display "none"}
             :on-change (fn [event] ; TODO: this is almost identical to the on-change fn for the reject button. refactor to share code
                          (if (or (nil? event) (nil? (.-target event)))
                            (println "event: " event)
                            (let [selected-chosen? (.-checked (.-target event))
                                  old-rejected-cuties (mc.util/get-field @profile "rejected-cuties")
                                  now-selected (if selected-chosen?

                                                 (if (mc.util/in? currently-selected-ids bio-id) ; add to selected-cuties only if it's not already there
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

                                               (if (mc.util/in? now-rejected bio-id) ; add it to unseen-cuties if it's neither selected nor rejected:
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
                     :border        (if   (mc.util/in? currently-selected-ids bio-id) "8px solid rgb(0 157 49)" "8px solid white")
                     :box-shadow    (when (mc.util/in? currently-selected-ids bio-id) "0 0 0 4px white inset")
                    ;;  :background (if (mc.util/in? currently-selected-ids bio-id) "#42b72a" "#42b72a22")
                    ;;  :border "8px solid #42b72a"
                     ;
                     }}
     "I'd like to" [:br]
     "meet this person!"]]

   [:div {:style {:margin "6px" :cursor "pointer"}}
    [:input {:type "checkbox"
             :id (str bio-id "-reject")
             :value bio-id
             :checked (boolean (mc.util/in? currently-rejected-ids bio-id))
             :style {:display "none"}
             :on-change (fn [event] ; TODO: this is almost identical to the on-change fn for the select button. refactor to share code
                          (if (or (nil? event) (nil? (.-target event)))
                            (println "event: " event)
                            (let [rejected-chosen? (.-checked (.-target event))
                                  old-selected-cuties (mc.util/get-field @profile "selected-cuties")
                                  now-rejected (if rejected-chosen?

                                                 (if (mc.util/in? currently-rejected-ids bio-id) ; add to selected-cuties only if it's not already there
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

                                               (if (mc.util/in? now-selected bio-id) ; add it to unseen-cuties if it's neither selected nor rejected:
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
                     :border        (if   (mc.util/in? currently-rejected-ids bio-id) "8px solid #aaa" "8px solid white")
                     :box-shadow    (when (mc.util/in? currently-rejected-ids bio-id) "0 0 0 4px white inset")

                    ;;  :background (if (mc.util/in? currently-rejected-ids bio-id) "#aaaaaa" "#aaaaaa22")
                    ;;  :border "8px solid #aaaaaa"
                     ;
                     }}
     "Not interested," [:br] "but thanks"]]])

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;; PROFILE TAB ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def *locations-new  (r/atom :loading))

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
  (println "made it to fetch-my-profile!!!!!!!!!!!!!!!!!!")
  (if mock-data?
    (reset! profile {:id "123123123"
                     (keyword "First name") "Foobar"})
    (util/fetch-post "/meetcute/api/matchmaking/me"
                     {}
                     (fn [result]
                       (if-let [error (:error result)]
                         (reset! profile-error error)
                         (let [profile-data (if (:id result)
                                              (merge (:fields result)
                                                     {:id (:id result)})
                                              (:fields result))]
                           (reset! profile profile-data)
                           (reset! *locations-new (or (try (let [x (:locations-json profile-data)]
                                                             (if (str/blank? x)
                                                               []
                                                               (walk/keywordize-keys (js->clj (js/JSON.parse x)))))
                                                           (catch js/Error e
                                                             (println "error parsing locations-json: " e)
                                                             []))
                                                      []))
                           (println "finished updating profile with result!")))))))

(defn update-profile! []
  (let [profile-editable-fields-only (merge (select-keys @profile (map #(keyword %)
                                                                       (concat mc.util/fields-changeable-by-user ; Phone is not editable, but it's needed as the key to find the record to update
                                                                               ["Phone"])))
                                            {:locations-json (.stringify js/JSON (clj->js @*locations-new))} ; this is super hacky, but it's the easiest way to update the locations-json field rather than updating the field inside of @profile
                                            )]
    (reset! show-toast true)
    (js/setTimeout #(reset! show-toast false) 2000)
    (util/fetch-post "/meetcute/api/matchmaking/profile"
                     profile-editable-fields-only)))

(def update-profile-debounced! (util/debounce update-profile! 1500))

(defn change-profile-field [field-name]
  (fn [event]
    (reset! profile (assoc @profile (keyword field-name) (-> event .-target .-value)))
    (update-profile-debounced!)))

(defn trim-trailing-whitespace [str]
  (str/replace str #"\s+$" ""))

(defn editable-input [field-name]
  [:input.editable-input
   {:type "text"
    :className (when (str/blank? (mc.util/get-field @profile field-name)) "empty")
    :value (or (mc.util/get-field @profile field-name) "") #_(trim-trailing-whitespace (or (mc.util/get-field @profile field-name) ""))
    :on-change (change-profile-field field-name)}])

(defn editable-textbox [field-name]
  [:textarea {:value (or (mc.util/get-field @profile field-name) "") #_(trim-trailing-whitespace (or (mc.util/get-field @profile field-name) ""))
              :on-change (change-profile-field field-name)}])

(defn editable-date-input [field-name]
  (let [date (mc.util/get-field @profile field-name)]
    [:div {:style {:width "95%"}
           :className (if (str/blank? date)
                        "editable-input required-but-empty"
                        "editable-input required-not-empty")}
     [:div.input-date-overlay
      (if (str/blank? date)
        "YYYY-MM-DD"
        date)]

     [:input.editable-input.input-container
      {:type "date"
    ; TODO: format it so that it displays the date like February 14, 2021
       :value (or (mc.util/get-field @profile field-name) "")
       :on-change  (fn [event]
                     (let [new-value (-> event .-target .-value)]
                       (reset! profile (assoc @profile (keyword field-name) new-value))
                       (if (str/blank? new-value)
                         (println "not updating the date in airtable because it's blank")
                         (update-profile-debounced!))))}]]))

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

(def *minimaps  (r/atom {}))

(defn fetch-coordinates! [minimap-id location-name-input index]
  (if (str/blank? location-name-input)
    (.flyTo (get @*minimaps minimap-id) #js{:essential true ; this animation is essential with respect to prefers-reduced-motion
                                            :zoom 0})
    (util/fetch-post "/api/v1/coordinates" {:location-name location-name-input}
                     (fn [result]
                       (if (or (nil? result)
                               (nil? (:lat result))
                               (nil? (:lng result)))
                         (println "no coordinates found for '" location-name-input "', so not updating the map")
                         (do (.flyTo (get @*minimaps minimap-id)
                                     #js{:essential true ; this animation is essential with respect to prefers-reduced-motion
                                         :zoom 3
                                         :center (mapbox/coords-to-mapbox-array result)})
                             (swap! *locations-new assoc index (merge (get @*locations-new index)
                                                                      {:coords result}))))))))

(def fetch-coordinates-debounced! (util/debounce fetch-coordinates! 300))

(defn minimap [minimap-id location-name coords]
  (r/create-class {:component-did-mount
                   (fn [] ; this should be called just once when the component is mounted
                     (swap! *minimaps assoc minimap-id
                            (new js/mapboxgl.Map
                                 #js{:container minimap-id
                                     :key    (get-in mapbox/config [mapbox/style :access-token])
                                     :style  (get-in mapbox/config [mapbox/style :style])
                                     :center (or (mapbox/coords-to-mapbox-array coords)
                                                 (clj->js mapbox/Miami))
                                     :interactive false ; makes the map not zoomable or draggable
                                     :attributionControl false ; removes the Mapbox copyright symbol
                                     :zoom 3
                                     :maxZoom 8
                                     :minZoom 0}))
                     ; zoom out if they haven't provided a location
                     (when (clojure.string/blank? location-name)
                       (.setZoom (get @*minimaps minimap-id) 0)))
                   :reagent-render (fn [] [:div {:id minimap-id}])}))

(defn location-field [{index         :index
                       auto-focus    :auto-focus
                       label         :label
                       location-type :location-type
                       placeholder   :placeholder
                       value         :value
                       coords        :coords
                       update!       :update!}]
  (let [id         (str "location-" index)
        minimap-id (str "minimap--" id)]
    [:div.field.location-field {:id id :key id}
     [:div.delete-location-btn {:title "Delete this location"
                                :on-click #(do (reset! *locations-new (vec (util/rm-from-list @*locations-new index)))
                                               (update-profile-debounced!))}
      (decorations/cancel-icon)]

     [:div.mapbox-container.location-field-top
      [minimap minimap-id value coords]
      (when-not (clojure.string/blank? value)
        [:div.center-point "🍊"])]

     [:div.location-field-bottom
      [:div
       [:input.location-input
        {:type "text"
         :tab-index "0"
         :auto-focus auto-focus
         :id   (str id "-input")
         :key  (str id "-input")
         :name (str id "-input")
         :value value
         :autoComplete "off"
         :auto-complete "off"
         :on-change #(let [new-value  (-> % .-target .-value)
                           _tmp       (fetch-coordinates-debounced! minimap-id new-value index)]
                       (update! new-value))
         :placeholder placeholder}]]

      [:div
       [:div.location-type-radio
        [:input {:type "radio" :name (str "location-type--" minimap-id) :value "home-base" :default-checked (= "home-base" location-type)
                 :id (str "location-type--" minimap-id "-home-base")
                 :on-change (fn [event]
                              (let [new-value (-> event .-target .-value)]
                                (println "new-value: " new-value)
                                (swap! *locations-new update index assoc :location-type new-value)
                                (update-profile-debounced!)))}]
        [:label {:for (str "location-type--" minimap-id "-home-base")}
         "This is my home base"]]
       [:div.location-type-radio
        [:input {:type "radio" :name (str "location-type--" minimap-id) :value "visit-often" :default-checked (= "visit-often" location-type)
                 :id (str "location-type--" minimap-id "-visit-often")
                 :on-change (fn [event]
                              (let [new-value (-> event .-target .-value)]
                                (println "new-value: " new-value)
                                (swap! *locations-new update index assoc :location-type new-value)
                                (update-profile-debounced!)))}]
        [:label {:for (str "location-type--" minimap-id "-visit-often")}
         "I visit here often"]]]]


     [:br]]))

(defn upload-photos-form []
  [:form {:method "post" :enctype "multipart/form-data" :action "/meetcute/tmp-upload"}
   [:input {:type "file" :name "file" :accept "image/*"}]
   [:input {:type "submit" :value "Upload image"}]])

(defn profile-tab []
  (let [filling-out-profile? (= "filling out profile"
                                (mc.util/get-field @profile "Include in gallery?"))
        waiting-for-review? (= "not yet reviewed"
                               (mc.util/get-field @profile "Include in gallery?"))]
    [:div {:style {:border-radius "8px" :padding "12px" :margin-left "auto" :margin-right "auto" :width "90%" :max-width "850px"}}

     [saved-toast]

     [:style "@media screen and (max-width: 1300px) { .your-profile { margin-top: 24px; } }"]
     [:h1 {:style {:font-size 36 :line-height "1.3em" :padding "0 12px"} :className "your-profile"} "Your profile"]

     (when filling-out-profile? ; only show this when the user's status is 'not yet reviewed '
       [:div {:style {:padding "12px 0 0 12px"}}
        [:div.welcome-message
         [:p {:style {:font-size "1.8em"}} "👋"]
         [:p "Hi there, welcome to MeetCute! We can't wait to intro you to some cuties. Once you fill in your profile, you can submit it for review. "
          "— " [:a {:href "https://twitter.com/devonzuegel" :target "_blank"} "Devon"] " & " [:a {:href "https://twitter.com/eriktorenberg" :target "_blank"} "Erik"]]]])

     (when waiting-for-review?
       [:div {:style {:padding "12px 0 0 12px"}}
        [:div.welcome-message
         [:p {:style {:font-size "1.8em"}} "🍊"]
         [:p "Thanks for submitting your profile! We're reviewing it now. You'll start getting Cuties of the Day in your email inbox when it's live. "
          "— " [:a {:href "https://twitter.com/devonzuegel" :target "_blank"} "Devon"] " & " [:a {:href "https://twitter.com/eriktorenberg" :target "_blank"} "Erik"]]]])

     (let [key-values [["Basic details"
                        {:open true}
                        [["First name" (editable-input "First name") {:required? true}]
                         ["Last name"      (editable-input "Last name")]
                         ["My gender"  [:div {:className (if (str/blank? (mc.util/get-field @profile "Gender"))
                                                           "required-but-empty"
                                                           "required-not-empty")}
                                        (radio-btns-component ["Man" "Woman"]
                                                              (mc.util/get-field @profile "Gender")
                                                              (fn [foobar]
                                                                (reset! profile (assoc @profile (keyword "Gender") foobar))
                                                                (update-profile-debounced!)))]
                          {:required? true}]
                         ["I'm interested in..." [:div {:className (if (or (str/blank? (mc.util/get-field @profile "I'm interested in..."))
                                                                           (empty?     (mc.util/get-field @profile "I'm interested in...")))
                                                                     "required-but-empty"
                                                                     "required-not-empty")}
                                                  (checkboxes-component ["Men" "Women"]
                                                                        (mc.util/get-field @profile "I'm interested in...")
                                                                        (fn [foobar]
                                                                          (reset! profile (assoc @profile (keyword "I'm interested in...") foobar))
                                                                          (update-profile-debounced!)))]
                          {:required? true}]
                         ["Phone" [:div {:style {:max-width "380px"}}
                                   [:div {:style {:background "rgb(188, 181, 175, .1)"
                                                  :border "3px solid rgb(188, 181, 175, .3)"
                                                  :cursor "not-allowed"
                                                  :border-radius "8px"
                                                  :padding "6px 8px"
                                                  :margin-right "4px"
                                                  :width "95%"
                                                  :max-width "380px"}}
                                                              ;; (format-phone (mc.util/get-field @profile "Phone")) ; don't make this editable, because it's the key to find the record to update. in the future, we can use the ID instead if we do want to make the phone editable
                                    (format-phone (mc.util/get-field @profile "Phone"))]
                                   [small-text [:span "If you'd like to change your phone number, email "
                                                [:a {:href "mailto:hello@smallworld.kiwi"} "hello@smallworld.kiwi"] "."]]]
                          {:required? true}]
                         ["Email" [:div {:style {:max-width "380px"}}
                                   (editable-input "Email")
                                   [small-text "We will only share your contact info when you match with someone. It will not be shown on your profile."]]
                          {:required? true}]
                         ["Who invited you to MeetCute?" (editable-input "If 'Other', who invited you?") {:required? true}]
                         ["What's your birthday?" (editable-date-input "Birthday") {:required? true}]

                         (let [include-in-gallery-status? (mc.util/get-field @profile "Include in gallery?")]
                           (when (or (= include-in-gallery-status? "include in gallery")
                                     (= include-in-gallery-status? "they asked to be removed"))
                             ["Profile visibility" [:div.profile-visibility
                                                    [:div {:style {:display "flex" :align-items "center" :text-align "left" :padding "4px 0"}}
                                                     [:input {:type "radio"
                                                              :id "include-in-gallery-yes"
                                                              :checked (not= "they asked to be removed" include-in-gallery-status?) ; there are other states, but we're only showing two options here
                                                              :style {:margin-right "10px"}
                                                              :on-change (fn []
                                                                           (reset! profile (assoc @profile (keyword "Include in gallery?") "include in gallery"))
                                                                           (util/fetch-post "/meetcute/api/matchmaking/profile" (merge (select-keys @profile (map #(keyword %)
                                                                                                                                                                  (concat mc.util/fields-changeable-by-user ; Phone is not editable, but it's needed as the key to find the record to update
                                                                                                                                                                          ["Phone"])))
                                                                                                                                       {(keyword "Include in gallery?") "include in gallery"})))}]
                                                     [:label {:for "include-in-gallery-yes"} "Show my profile to other cuties"]]
                                                    [:div {:style {:display "flex" :align-items "center" :padding "4px 0"}}
                                                     [:input {:type "radio"
                                                              :id "include-in-gallery-no"
                                                              :checked (= "they asked to be removed" include-in-gallery-status?)
                                                              :style {:margin-right "10px"}
                                                              :on-change (fn []
                                                                           (reset! profile (assoc @profile (keyword "Include in gallery?") "they asked to be removed")) ; reset to "not yet reviewed" so that the user can submit it for review again
                                                                           (util/fetch-post "/meetcute/api/matchmaking/profile" (merge (select-keys @profile (map #(keyword %)
                                                                                                                                                                  (concat mc.util/fields-changeable-by-user ; Phone is not editable, but it's needed as the key to find the record to update
                                                                                                                                                                          ["Phone"])))
                                                                                                                                       {(keyword "Include in gallery?") "they asked to be removed"})))}]
                                                     [:label {:for "include-in-gallery-no"} "Make my profile private"]]]]))]]
                       ["Locations"
                        {:open true
                         :className "bio-row-locations"}
                        [["Where would you like to meet cuties? (ALPHA FEATURE)"
                          [:<>
                           (if (= @*locations-new :loading)
                             [:p "Loading locations..."]
                             [:div.location-fields
                              (for [[index location] (map-indexed vector @*locations-new)]
                                (location-field {:index       index
                                                 :label       ""
                                                 :placeholder "Place"
                                                 :value       (:name location)
                                                 :location-type (:location-type location)
                                                 :coords      (:coords location)
                                                 :update!     (fn [new-value]
                                                                (swap! *locations-new update index assoc :name new-value)
                                                                (fetch-coordinates-debounced! (str "minimap--location-" index) new-value index)
                                                                (update-profile-debounced!))}))
                              [:button
                               {:className (if (zero? (count @*locations-new)) "add-location-btn empty" "add-location-btn")
                                :on-click (fn []
                                            (swap! *locations-new conj {:location-type (if (zero? (count @*locations-new)) "home-base" "visit-often")})
                                            (js/setTimeout (fn []
                                                             (.scrollIntoView
                                                              (last (array-seq (goog.dom/getElementsByClass "location-field")))
                                                              #js{:behavior "smooth" :block "center" :inline "center"})

                                                                                                                 ; focus on the input inside of that last location-field:
                                                             (-> (last (array-seq (goog.dom/getElementsByClass "location-field")))
                                                                 (.querySelector "input.location-input")
                                                                 (.focus)))
                                                           50))}
                               [:span.inner
                                "+ New location"]]])]
                          {:required? true}]
                         ["Home base" (editable-input "Home base city")]
                         ["Other cities where you spend time" (editable-input "Other cities where you spend time")]]]

                       ["Other"
                        {:open true}
                        [["About me"                        (editable-textbox "Anything else you'd like your potential matches to know?")  {:required? true}]
                         ["Social media links"              (editable-textbox "Social media links")]
                         ["What makes this person awesome?" [:div
                                                             [:div {:style {:margin-bottom "4px"}}
                                                              [small-text (md->hiccup "Ask a friend to write a few sentences about you. <a href='https://bit.ly/matchmaking-vouch-examples' target='_blank'>Here are some examples.</a>")]]
                                                             (editable-textbox "What makes this person awesome?")
                                                             [:div {:style {:margin-top "8px"}}] ; spacer
                                                             [small-text (md->hiccup "Here's a template for asking a friend to write you a vouch:")]
                                                             [:div {:style {:border-left "5px solid rgb(188, 181, 175, .3)" :background "rgb(188, 181, 175, .1)"  :max-width "90%"}}
                                                              [small-text (md->hiccup (str "*\"Hey `FRIEND NAME`, some friends invited me to a small matchmaking experiment, and I need a friend to write a blurb recommending me. <br/><br/>"
                                                                                           "Would you write one today or tomorrow? It can be short (2-3 paragraphs), should take just a few mins. Here are some examples: [https://bit.ly/matchmaking-vouch-examples](https://bit.ly/matchmaking-vouch-examples)\" ***"))
                                                               {:background "#ffffff10" :margin-top "2px" :padding "8px 12px 14px 12px"}]]]]

                         ["Pictures" [:div
                                      [small-text [:span "If you'd like to add or remove pictures, email "
                                                   [:a {:href "mailto:hello@smallworld.kiwi"} "hello@smallworld.kiwi"] ". (In the future, we'll add a way to do this yourself!)"]]
                                      (map-indexed (fn [k2 v2] [:img {:src (:url v2) :key k2 :style {:height "200px" :margin "8px 8px 0 0" :border-radius "8px" :border "1px solid #ffffff33"}}])
                                                   (mc.util/get-field @profile "Pictures"))
                                      (upload-photos-form)]]]]]]
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

     (when filling-out-profile?
       (let [errors (remove nil? [(when (str/blank? (mc.util/get-field @profile "First name"))                                               "First name")
                                  (when (str/blank? (mc.util/get-field @profile "Gender"))                                                   "Gender")
                                  (when (empty?     (mc.util/get-field @profile "I'm interested in..."))                                     "Sexual orientation")
                                  (when (str/blank? (mc.util/get-field @profile "Phone"))                                                    "Phone")
                                  (when (str/blank? (mc.util/get-field @profile "Email"))                                                    "Email")
                                  (when (str/blank? (mc.util/get-field @profile "Anything else you'd like your potential matches to know?")) "About me")
                                  (when (or (empty? @*locations-new)
                                          ; return true all of the location names are non-blank:
                                            (not (every? (comp not str/blank?) (map :name @*locations-new))))
                                    "At least 1 location")])]
         [:div.ready-for-review

          [:button {:className (if (empty? errors) "enabled" "disabled")
                    :title (if (empty? errors)
                             "Submit your profile for review"
                             "You're missing a few required fields")
                    :on-click (fn []
                                (when (empty? errors)
                                  (reset! profile (assoc @profile (keyword "Include in gallery?") "not yet reviewed"))
                                  (println (merge (select-keys @profile (map #(keyword %)
                                                                             (concat mc.util/fields-changeable-by-user ; Phone is not editable, but it's needed as the key to find the record to update
                                                                                     ["Phone"])))
                                                  {(keyword "Include in gallery?") "not yet reviewed"}))
                                  (util/fetch-post "/meetcute/api/matchmaking/profile" (merge (select-keys @profile (map #(keyword %)
                                                                                                                         (concat mc.util/fields-changeable-by-user ; Phone is not editable, but it's needed as the key to find the record to update
                                                                                                                                 ["Phone"])))
                                                                                              {(keyword "Include in gallery?") "not yet reviewed"}))
                                  #_(update-profile!)))}
           "Submit for review"]

          [:div.errors-list
           [:p
            (if (seq errors)
              "Hold up, we need a bit more info before you submit your profile for review"
              "🍊 Now THAT'S a cute profile! Submit it & we'll start sending you cuties once we review it 🍊")]]

          #_(if (seq errors)
              [:div.errors-list
               [:p "Once you fill out all the required fields, you can submit your profile for review"]]
              [:p.errors-list "Once your profile is reviewed, we'll start sending you Cuties of the Day!"])]))


     [:br] [:br] [:br]
     [:br] [:br] [:br]
     [:br]]))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;; HOME TAB ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn fetch-bios []
  (println "fetching bios")
  (if mock-data?
    (swap! bios (fn [_] [{:id "2"
                          (keyword "First name") "Alice"}]))
    (util/fetch "/meetcute/api/matchmaking/bios" (fn [result]
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
     [:td {:style {:text-align "right" :padding "16px 4px"}}
      (truncate-string full-name) ": "]
     [:td {:style {:padding "16px 4px"}}
      [:a {:href "#"
           :on-click (fn []
                       (reset! loading-message (str "Refreshing todays-cutie for " full-name "..."))
                       (util/fetch-post "/meetcute/api/refresh-todays-cutie"
                                        {:id id}
                                        #(do
                                           (reset! loading-message false)
                                           #_(js/location.reload true))))}
       " Refresh todays-cutie for just this person"]]]))

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

   (let [refreshable-cuties (filter #(or (:admin? %)
                                         (= (mc.util/get-field % "Include in gallery?") "include in gallery"))
                                    @bios)]
     [:details {:open false}
      [:summary " Refresh todays-cutie for each user individually: (" (count refreshable-cuties) " 'include in gallery' OR admins)"]
      [:table [:tbody (map-indexed refresh-todays-cutie-link refreshable-cuties)]]
      [:div]])])

(defn list-mutual-selections []
  [:div {:style {:margin "48px 0" :background "#eee" :border-radius "8px" :padding "24px"}}
   [:h2 {:style {:font-size "2em" :line-height "2em"}} "List of matches:"]
   (let [mutual-selections (set (flatten
                                 (map (fn [this-cutie] (let [my-id (:id this-cutie)
                                                             my-selected-cuties (mc.util/get-field this-cutie "selected-cuties") ; ids only
                                                             my-selected-cuties (filter #(mc.util/in? my-selected-cuties (mc.util/get-field % "id")) @bios)
                                                             matches (filter #(mc.util/in? (mc.util/get-field % "selected-cuties") my-id) my-selected-cuties)
                                                             matches-list (map #(set [% this-cutie]) matches)]
                                                         matches-list))
                                      @bios)))]
     [:div
      [:table {:style {:text-align "center"}}
       [:tbody
        (map-indexed (fn [index match]
                       [:<> {:key index} ; the comma column is there to make it easier to copy/paste into a spreadsheet / iMessage / etc
                        [:tr {:key (str index "-1")} [:td {:colSpan 3 :style {:text-align "left" :padding-top "32px" :font-size ".85em" :font-weight "bold" :border-bottom "2px solid #ddd"}} "match #" (+ 1 index)]]
                        [:tr {:key (str index "-2")} [:td {:style {:text-align "right"}} (str (mc.util/get-field (first match) "First name") " " (mc.util/get-field (first match) "Last name"))] [:td.invisible ", "] [:td (str (mc.util/get-field (second match) "First name") " " (mc.util/get-field (second match) "Last name"))]]
                        [:tr {:key (str index "-3")} [:td {:style {:text-align "right"}} (mc.util/get-field (first match) "Phone")]                                                              [:td.invisible ", "] [:td (mc.util/get-field (second match) "Phone")]]
                        [:tr {:key (str index "-4")} [:td {:style {:text-align "right"}} (mc.util/get-field (first match) "Email")]                                                              [:td.invisible ", "] [:td (mc.util/get-field (second match) "Email")]]])
                     mutual-selections)]

       ;
       ]])])

(defn render-obj [obj] (js/JSON.stringify (clj->js obj) nil 2))

(defn find-cutie [cutie-id bios]
  (let [result (first (filter #(= (mc.util/get-field % "id")
                                  cutie-id)
                              bios))]
    (if (nil? result)
      :no-cutie
      result)))

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
                ;; reviewed-bios (filter #(let [bio-id (mc.util/get-field % "id")] (or (mc.util/in? currently-selected-ids bio-id)
                ;;                                                                     (mc.util/in? currently-rejected-ids bio-id)))
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

             (if (not= "include in gallery" (mc.util/get-field @profile "Include in gallery?"))
               [:<>
                [:p {:style {:padding "8px" :text-align "center" :font-size "1.2em" :text-wrap "balance" :max-width "700px" :margin "auto" :line-height "1.6em" :margin-top "36px"}}
                 "Your profile isn't active, so we can't send you cuties yet"]
                [:p {:style {:padding "8px" :text-align "center" :font-size "1.2em" :text-wrap "balance" :max-width "700px" :margin "auto" :line-height "1.6em" :margin-bottom "12px"}}
                 "Go to your " [:a {:href "/meetcute/settings"} "settings"] " to activate your profile!"]]
               [:<> [:h1 {:style {:font-size "36px" :line-height "1.3em" :padding "8px" :text-align "center"}} "Today's cutie"]
                [:style "@media screen and (min-width: 600px) { .profile-column { min-width: 500px } }"]

                (if (= :no-cutie todays-cutie)
                  [:p {:style {:padding "6px 16px" :text-align "center"}}
                   "No profiles to review right now. We'll email you with more people to meet soon!"]
                  (if (nil? todays-cutie)
                    [:p "Loading..."]
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
                       (if (mc.util/in? currently-selected-ids todays-cutie-id)
                         [:p {:style {}} "You've selected this person!" [:br] "We'll let you know if they select you too :)"]
                         (if (mc.util/in? currently-rejected-ids todays-cutie-id)
                           [:p {:style {}} "Sounds like this cutie is not your main squeeze." [:br] "No worries, we'll send you another cutie soon!"]
                           [:p {:style {}} "So, are you interested in meeting this cutie?"]))]

                      [:div {:style {:flex 1}}
                       [select-reject-btns
                        (mc.util/get-field todays-cutie "id")
                        (mc.util/get-field @profile "selected-cuties")
                        (mc.util/get-field @profile "rejected-cuties")]]]]))])

            ;;  [:p "count of included-bios: " (count included-bios)]

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

(defonce airtable-db-name (r/atom nil))
(def airtable-db-name-options ["cuties-live-data"
                               "cuties-TEST-data"])
(defn admin-tab []
  (r/create-class
   {:component-did-mount #(util/fetch "/meetcute/api/get-airtable-db-name"
                                      (fn [result] (reset! airtable-db-name result)))
    :reagent-render
    (fn [] (if-not (:admin? @profile)
             (js/location.assign "/meetcute") ; if not admin, redirect to home

             [:div {:style {:border-radius "8px" :padding "36px 12px" :margin "auto" :width "90%" :max-width "850px"}}
              [saved-toast]
              [:span {:style {:padding "4px 12px" :border-radius "8px" :color "white" :background (if (:admin? @profile) "green" "red") :float "right"}}
               (if (:admin? @profile)
                 "You are an admin"
                 "You are not an admin")]

              (when (re-find #"localhost" (.-hostname js/location)) ; only show this input field on localhost; we should not be able to update which airtable db we're using in production!
                [:div {:style {:margin-top "64px"}}
                 [:b "Update the Airtable db: (ONLY AVAILABLE IN LOCALHOST!)"]
                 [:select {:style {:padding "6px 8px"
                                   :background "#ffeb3b"
                                   :margin "12px"
                                   :border-radius "8px"
                                   :border "3px solid rgb(245 220 0)"
                                   :width "100%"
                                   :max-width "175px"}
                           :on-change (fn [event]
                                        (let [new-value (-> event .-target .-value)]
                                          (reset! loading-message (str "Updating Airtable db name to: " new-value))
                                          (reset! airtable-db-name new-value)
                                          (util/fetch-post "/meetcute/api/admin/update-airtable-db"
                                                           {:airtable-db-name new-value}
                                                           (fn [] (js/location.reload true)))))
                           :value (or @airtable-db-name "NOT-YET-DEFINED")} ; value cannot be nil, so we use "NOT-YET-DEFINED" instead
                  (map-indexed (fn [i option] [:option {:key i :value option} option])
                               airtable-db-name-options)]])

              [refresh-todays-cutie-btns]
              [list-mutual-selections]]))}))

(defn nav-btns []
  [:div {:style {:margin "12px" :margin-bottom "0"}}
   (when (= "include in gallery" (mc.util/get-field @profile "Include in gallery?"))
     [:a {:href "/meetcute"
          :className (if (= "/meetcute" (.-pathname js/location))
                       "btn primary"
                       "btn")}
    ;; [:i {:className "fas fa-heart"}] " Home"
      "Home"])
   [:a {:href "/meetcute/settings"
        ; if current url is /meetcute/settings, then set className to "btn primary", otherwise set it to "btn":
        :className (if (= "/meetcute/settings" (.-pathname js/location))
                     "btn primary"
                     "btn")}
    ;; [:i {:className "fas fa-user"}] " Profile"
    "Profile"]

   ; let's just not show the admin button for now to make sure no one has access to it when they shouldn't
   #_(when (:admin? @profile)
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
                                        :profile [profile-tab]
                                        :admin   [admin-tab]
                                        :home    [home-tab]
                                        (home-tab))]))}))) ""
