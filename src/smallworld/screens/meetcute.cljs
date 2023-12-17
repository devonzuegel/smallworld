(ns smallworld.screens.meetcute (:require [clojure.string :as str]
                                          [reagent.core    :as r]
                                          [smallworld.util :as util]
                                          [goog.dom :as dom]
                                          [cljs.pprint :as pp]))

(defonce debug? (r/atom false))
(defonce bios   (r/atom nil))
(defonce phone (r/atom "(111) 111-1111")) ; TODO: remove me
(defonce profile (r/atom nil))
(defonce current-tab (r/atom :home))

(def btn-styles {:color "white" :border "3px solid #ffffff33" :padding "12px" :border-radius "8px" :cursor "pointer" :margin "6px"})

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

(defn get-key-names [bio] (map first bio))

(defn format-phone [phone]
  (let [digits-only (str/replace phone #"[^0-9]" "")]
    (str "(" (subs digits-only 0 3) ") " (subs digits-only 3 6) "-" (subs digits-only 6 10))))

(defn in? [list str] (some #(= str %) list))

(defn checkbox-component [value-name selected-values update-selected-values]
  [:span
   [:input {:type "checkbox"
            :value value-name
            :checked (boolean (in? selected-values value-name))
            :style {:margin "12px"}
            :on-change (fn [] (update-selected-values (if (in? selected-values value-name)
                                                        (remove (fn [v] (= value-name v)) selected-values)
                                                        (conj selected-values value-name))))}]
   [:label {:for (str value-name "-checkbox")} value-name]])

(defn checkboxes-component [all-values selected-values update-selected-values]
  [:div
   (for [value all-values] ^{:key value} [checkbox-component value selected-values update-selected-values])
   (when @debug? [:pre "Selected Value: " (str selected-values)])])

(defn radio-btn-component [value-name selected-value update-selected-value]
  [:span {:style {:display "flex" :align-items "center" :margin-left "10px"}}
   [:input {:type "radio"
            :id (str value-name "-radio")
            :value value-name
            :checked (= selected-value value-name)
            :style {:margin-right "8px"}
            :on-change (fn [] (update-selected-value value-name))}]
   [:label {:for (str value-name "-radio")} value-name]])

(defn radio-btns-component [all-values selected-value update-selected-value]
  [:div {:style {:display "inline-flex" :justify "space-between"}}
   (for [value all-values] ^{:key value} [radio-btn-component value selected-value update-selected-value])
   (when @debug? [:pre "Selected Value: " (str selected-value)])])

(defn bio-row [i [key-name value]]
  [:tr {:key i :style {:padding "24px" :background "#ffffff11" :vertical-align "top"}}
   [:td {:style {:padding "8px" :text-align "right" :font-size ".85em" :opacity ".75" :max-width "200px"}} key-name]
   [:td {:style {:padding "8px" :text-align "left"}} value]])

(defn render-bio [i bio]
  [:div {:key i :style {:margin "16px 0 24px 0" :background "#ffffff11"}}
   (let [key-values [["First name"                       (get-field bio "First name")]
                     #_["Last name"                        (get-field bio "Last name")]
                     #_["Social media links"               [:pre (get-field bio "Social media links")]]
                     #_["Email"                            (get-field bio "Email")]
                     #_["Phone"                            (format-phone (get-field bio "Phone"))]
                     #_["Home base city"                   (get-field bio "hHome base city")]
                     #_["Anything else you'd like your potential matches to know?" (get-field bio "Anything else you'd like your potential matches to know?")]
                     #_["What makes this person awesome?"  (get-field bio "What makes this person awesome?")]
                     ["Gender"                           (get-field bio "Gender")]
                     ["I'm interested in..."             (pr-str (get-field bio "I'm interested in..."))]
                     #_["Pictures"                         (map-indexed (fn [k2 v2] [:img {:src (:url v2) :key k2 :style {:height "180px" :margin "8px 8px 0 0"}}])
                                                                        (get-field bio "Pictures"))]]]
     [:table {:style {:margin-top "12px" :border-radius "8px" :padding "6px" :line-height "1.2em"}}
      [:tbody
       [:tr
        [:td
         (let [bio-id (get-field bio "id")
               currently-selected-ids (get-field @profile "selections")
               currently-rejected-ids (get-field @profile "rejections")]
           [:div
            [:span
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
                                                                        (get-field @profile "rejections"))))))))}]
             [:label {:for (str bio-id "-select")
                      :style (merge {:display "inline-block"
                                     :padding "10px 20px"
                                     :background-color "#4CAF50"
                                     :color "white"
                                     :opacity (if (or (in? currently-selected-ids bio-id)
                                                      (not (in? currently-rejected-ids bio-id))) "1" ".7")
                                     :margin "5px"
                                     :cursor "pointer"
                                     :border-radius "5px"
                                     :border (if (in? currently-selected-ids bio-id) "3px solid white" "3px solid transparent")}
                                    (if (in? currently-selected-ids bio-id) {:box-shadow "0 4px 8px 0 rgba(0,0,0,0.2)"} {}))}
              "Select"]]

            [:span
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
                                       (reset! profile (assoc @profile
                                                              (keyword "rejections")
                                                              now-rejected))
                                       (when checked?
                                         (reset! profile (assoc @profile
                                                                (keyword "selections")
                                                                (remove (fn [v] (= bio-id v))
                                                                        (get-field @profile "selections"))))))))}]
             [:label {:for (str bio-id "-reject")
                      :style (merge {:display "inline-block"
                                     :padding "10px 20px"
                                     :background-color "#D32F2F"
                                     :color "white"
                                     :opacity (if (or (in? currently-rejected-ids bio-id)
                                                      (not (in? currently-selected-ids bio-id))) "1" ".7")
                                     :margin "5px"
                                     :cursor "pointer"
                                     :border-radius "5px"
                                     :border (if (in? currently-rejected-ids bio-id) "3px solid white" "3px solid transparent")}
                                    (if (in? currently-rejected-ids bio-id) {:box-shadow "0 4px 8px 0 rgba(0,0,0,0.2)"} {}))}
              "Reject"]]])]
        [:td]]

       (map-indexed bio-row key-values)]])])

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;; PROFILE TAB ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;


(def phone-input-error (r/atom nil))

(defn valid-phone [phone]
  ; strip the phone number of all non-numeric characters, then check if it's a valid phone number. if yes, return true; if not, return false:
  (let [phone (or phone "")
        phone (str/replace phone #"[^0-9]" "")]
    (and (not-empty phone) (re-find #"^\d{10}$" phone))))


(defn redirect! [path]
  (.replace (.-location js/window) path))

(defn update-profile-with-result [result]
  (if (:error result)
    (reset! phone-input-error (:error result))
    (do (reset! profile (merge (:fields result)
                               {:id (:id result)}))
        (pp/pprint "profile keys: ")
        (pp/pprint (keys @profile))
        ;; (redirect! "/meetcute")
        ;
        )))

(defn fetch-profile [phone]
  (println "\nfetching profile...")
  (let [clean-phone (str/replace phone #"[^0-9]" "")]
    (util/fetch-post "/api/matchmaking/profile" {"Phone" clean-phone} update-profile-with-result)))

(defn signin []
  (if (valid-phone @phone)
    (do
      (reset! phone-input-error nil)
      (fetch-profile @phone))
    (reset! phone-input-error "Please enter a valid phone number")))

(defn update-profile! []
  (let [profile-editable-fields-only (select-keys @profile (map #(keyword %)
                                                                (concat fields-changeable-by-user ; Phone is not editable, but it's needed as the key to find the record to update
                                                                        ["Phone"])))]
    (js/console.log "profile-editable-fields-only: " profile-editable-fields-only)
    (util/fetch-post "/api/matchmaking/profile"
                     profile-editable-fields-only
                     update-profile-with-result)))

(defn change-profile-field [field-name]
  #(reset! profile
           (assoc @profile (keyword field-name) (-> % .-target .-value))))

(defn editable-input [field-name]
  [:input {:type "text"
           :value (get-field @profile field-name)
           :on-change (change-profile-field field-name)
           :style {:background "#ffffff22" :border-radius "8px" :padding "6px 8px" :margin-right :4px :width "95%"}}])

(defn editable-textbox [field-name]
  [:textarea {:value (get-field @profile field-name)
              :on-change (change-profile-field field-name)
              :style {:background "#ffffff22"
                      :border-radius "8px"
                      :padding "6px 8px"
                      :margin-right "12px"
                      :min-height "150px"
                      :color "#d9d3cc"
                      :font-size ".9em"
                      :width "95%"}}])

(defn profile-tab []
  [:div {:style {:border "3px solid #ffffff33" :border-radius "8px" :padding "12px" :margin-left "auto" :margin-right "auto" :width "80%" :margin-top "48px"}}
   [:h1 {:style {:font-size 32 :line-height "2em"}} "Your profile"]

   (when (exists? @profile)
     [:div
      [:button {:style (merge btn-styles {:float "right" :margin-top 0}) :on-click update-profile!} "Save changes"]
      (when @debug? [:pre "@profile: " (pr-str (select-keys @profile [;(keyword "Anything else you'd like your potential matches to know?")
                                                                     ;(keyword "Social media links")
                                                                     ;(keyword "Email")
                                                                     ;(keyword "First name")
                                                                     ;(keyword "Last name")
                                                                     ;(keyword "Phone")
                                                                     ;(keyword "Home base city")
                                                                     ;(keyword "I'm interested in...")
                                                                      (keyword "selections")
                                                                     ;(keyword "What makes this person awesome?")
                                                                     ;(keyword "Gender")
                                                                      ]))])
      [:div {:style {:margin "16px 0 24px 0"}}
       (let [key-values [["First name"                       (editable-input "First name")]
                        ;;  ["Last name"                        (editable-input "Last name")]
                         ["I'm interested in..." (checkboxes-component ["Men" "Women"]
                                                                       (get-field @profile "I'm interested in...")
                                                                       #(reset! profile (assoc @profile (keyword "I'm interested in...") %)))]
                         ["Gender" (radio-btns-component ["Man" "Woman"]
                                                         (get-field @profile "Gender")
                                                         #(reset! profile (assoc @profile (keyword "Gender") %)))]
                         ["Phone"                            (format-phone (get-field @profile "Phone"))] ; do not make this editable!
                         ["Anything else you'd like your potential matches to know?" (editable-textbox "Anything else you'd like your potential matches to know?")]
                         ["Social media links"               (editable-textbox "Social media links")]
                         ["Email"                            (editable-input "Email")]
                         ["Home base city"                   (editable-input "Home base city")]
                         ["What makes this person awesome?"  (editable-textbox "What makes this person awesome?")]

                         ["Pictures" (map-indexed (fn [k2 v2] [:img {:src (:url v2) :key k2 :style {:height "180px" :margin "8px 8px 0 0"}}])
                                                  (get-field @profile "Pictures"))]]]
         [:table {:style {:margin-top "12px" :border-radius "8px" :padding "6px" :vertical-align "top" :line-height "1.2em" :width "100%"}}
          [:tbody
           (map-indexed bio-row key-values)]])]])
   [:br]])


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;; HOME TAB ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn fetch-bios []
  (println "fetching bios")
  (util/fetch "/api/matchmaking/bios" (fn [result]
                                        (swap! bios (fn [_] result)))))

(defn update-selections []
  (println "updating selections")
  (util/fetch-post "/api/matchmaking/profile"
                   (select-keys @profile (map #(keyword %)
                                              (concat fields-changeable-by-user ; Phone is not editable, but it's needed as the key to find the record to update
                                                      ["Phone"])))
                   #(println "Done updating selections")))

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
                                  (and (not (get-field bio "Exclude from gallery?")) ; don't include bios that have been explicitly excluded
                                       (not (= (get-field bio "id") (get-field @profile "id"))) ; check it's not self
                                       (some #(= (get-field bio "Gender") %) gender-filter) ; only show the gender that the user is interested in dating
                                       (some #(= (get-field @profile "Gender") %) bio-gender-filter ; only show someone if they're interested in dating someone of the gender of the current user:
                                             ))))

                              @bios)]
    [:div {:style {:margin-left "auto" :margin-right "auto" :width "80%" :margin-top "48px"}}
     (when @profile
       [:div
        (if (nil? included-bios)
          [:p "Loading..."]
          [:div
           [:button {:style btn-styles :on-click update-selections} "Save selections"]
           (let [currently-selected-ids (get-field @profile "selections")
                 currently-rejected-ids (get-field @profile "rejections")
                 new-bios      (filter #(let [bio-id (get-field % "id")] (not (or (in? currently-selected-ids bio-id)
                                                                                  (in? currently-rejected-ids bio-id))))
                                       included-bios)
                 reviewed-bios (filter #(let [bio-id (get-field % "id")] (or (in? currently-selected-ids bio-id)
                                                                             (in? currently-rejected-ids bio-id)))
                                       included-bios)
                 new-bio-count (str (count new-bios))]
             [:div
              [:div
               [:h1 {:style {:font-size 32 :line-height "2em"}}
                new-bio-count " new profiles to review"]
               (if (= 0 (count new-bios))
                 [:p "You've reviewed all the profiles in your area. Check back later for more!"]
                 (doall (map-indexed render-bio new-bios)))]

              [:div {:style {:margin-top "24px"}}
               [:details {:style {:border "3px solid #ffffff33" :border-radius "8px" :padding "12px"}}
                [:summary {:style {:font-size 32 :line-height "2em"}} "Profiles you've already reviewed"]
                [:div {:style {:margin-top "24px"}}
                 (doall (map-indexed render-bio reviewed-bios))]]]])])])]))

(defn css-spinner []
  (let [speed 1 ; lower is faster
        rotation (r/atom 0)]
    (js/setInterval #(swap! rotation + 1) speed)
    (fn []
      [:div {:style {:width "20px"
                     :height "20px"
                     :margin "auto" ; center the spinner
                     :margin-top "80px"
                     :border "6px solid rgba(255, 255, 255, 0.1)"
                     :border-top-color "white"
                     :border-radius "50%"
                     :transform (str "rotate(" @rotation "deg)")}
             :key @rotation}])))

(defn loading-iframe [src]
  (let [loading? (r/atom true)] ; State to track loading status
    (fn []
      [:div {:style {:position "relative"
                     :align-self "center"
                     :width "100%"}}
       (when @loading? [css-spinner])
       [:iframe {:src src
                 :style {:display (if @loading? "none" "block")
                         :frameborder "0"
                         :onmousewheel ""
                         :border-radius "8px"
                         :width "100%"
                         :height (str (- (.-innerHeight (dom/getWindow)) 180) "px")}
                 :on-load #(reset! loading? false)}]])))

(defn signup-screen []
  [:div  {:style {:display "flex" :flex-direction "column" :height "100vh"
                  :align-items "center" ; center horizontally
                  :color "white" :font-family "sans-serif" :font-size "1.2em" :line-height "1.5em" :text-align "center" :overflow "hidden" :padding "0 12px"
                  :vertical-align "top" ; vertically align flex items to the top, make them stick to the top even if they don't take the whole height
                  ; TODO:: this flexbox and its contents should resize when the page size changes
                  }}
   [:div {:style {:padding-top "36px" :padding-bottom "36px"}}
    [:h1 {:style {:font-size 48 :line-height "1.6em"}} "Sign up"]
    [:p {:style {:color "white"}}
     "Already have an account? "
     [:a {:on-click #(reset! current-tab :signin)
          :href "#"
          :style {:margin-left "8px" :color "white" :border-bottom "3px solid #ffffff33" :padding-bottom "2px"}}
      "Sign in"]]]

   [:div {:style {:width "100%"}}
    [:script {:src "https://static.airtable.com/js/embed/embed_snippet_v1.js"}]
    [loading-iframe "https://airtable.com/embed/appF2K8ThWvtrC6Hs/shrdeJxeDgrYtcEe8"]]])

(defn signin-screen []
  [:div {:style {:margin-left "auto" :margin-right "auto" :width "80%" :margin-top "48px"}}
   [:div {:style {:color "red" :min-height "1.4em"}} @phone-input-error]
   [:p "Your phone number: " [:input {:type "text"
                                      :value @phone
                                      :on-change #(reset! phone (-> % .-target .-value))
                                      :on-key-press #(when (= (.-key %) "Enter") (signin))
                                      :style {:background "#ffffff22" :border-radius "8px" :padding "6px 8px" :margin-right :4px}}]
    [:button {:style btn-styles :on-click signin} "Sign in"]
    [:a {:on-click #(reset! current-tab :signup)
         :href "#"
         :style {:margin-left "12px" :color "white" :border-bottom "3px solid #ffffff33" :padding-bottom "2px"}}
     "Sign up"]
    [:br]
    [:br]]
   [:pre "@profile: " (pr-str @profile)]])

(defn nav-btns []
  [:div {:style {:margin "12px"}}
   [:button {:on-click #(reset! current-tab :home) :style (merge btn-styles (if (= @current-tab :home) {:border  "3px solid #ffffff88"} {}))} "All bios"]
   [:button {:on-click #(reset! current-tab :profile) :style (merge btn-styles (if (= @current-tab :profile) {:border  "3px solid #ffffff88"} {}))} "Your profile"]
   [:button {:on-click #(reset! debug? (not @debug?)) :style (merge btn-styles {:float "right"})} (str "Debug: " @debug?)]
   [:button {:on-click #(reset! profile nil) :style (merge btn-styles {:float "right"})} (str "Log out")]
   [:br]])

(defn screen []
  (r/create-class
   {:component-did-mount (fn [] (fetch-bios))
    :reagent-render (fn []

                      #_[:div
                         [:p "HOME"]
                         [:pre "@profile: " (pr-str @profile)]]


                      (if (nil? @profile)
                        (case @current-tab
                          :signup (signup-screen)
                          :signin (signin-screen)
                          (signin-screen))

                        [:div
                         [nav-btns]

                         (case @current-tab
                           :profile (profile-tab)
                           :home (home-tab)
                           (home-tab))]))}))
