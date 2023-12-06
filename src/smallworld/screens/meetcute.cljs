(ns smallworld.screens.meetcute (:require [clojure.string :as str]
                                          [reagent.core    :as r]
                                          [smallworld.util :as util]))

(def debug? (r/atom false))
(def bios   (r/atom nil))
(def phone (r/atom "123-123-1234")) ; TODO: remove me
(def profile (r/atom nil))
(def current-tab (r/atom :profile))

(defn fetch-bios []
  (println "fetching bios")
  (util/fetch "/api/matchmaking/bios" (fn [result]
                                        (swap! bios (fn [_] result)))))

(defn get-field [bio field]
  (get-in bio [(keyword field)]))

(defn get-key-names [bio] (map first bio))

(def btn-styles {:color "white" :border "3px solid #ffffff33" :padding "12px" :border-radius "8px" :cursor "pointer" :margin "6px"})

(def fields-changeable-by-user [; Phone is intentionally not included because it's used as the key to find the record to update, so we don't want to overwrite it
                                "Anything else you'd like your potential matches to know?"
                                "Social media links"
                                "Email"
                                "First name"
                                "Last name"
                                "Phone"
                                "Home base city"
                                "I'm interested in..."
                                "What makes this person awesome?"
                                "Pictures"
                                "Gender"])

(defn format-phone [phone]
  (let [digits-only (str/replace phone #"[^0-9]" "")]
    (str "(" (subs digits-only 0 3) ") " (subs digits-only 3 6) "-" (subs digits-only 6 10))))

(defn bio-row [i [key-name value]]
  [:tr {:key i :style {:padding "24px" :background "#ffffff11" :vertical-align "top"}}
   [:td {:style {:padding "8px" :text-align "right" :font-size ".85em" :opacity ".75" :max-width "200px"}} key-name]
   [:td {:style {:padding "8px" :text-align "left"}} value]])

(defn render-bio [i bio]
  [:div {:key i :style {:margin "16px 0 24px 0" :background "#ffffff11"}}
   (let [key-values [["Anything else you'd like your potential matches to know?" (get-field bio "Anything else you'd like your potential matches to know?")]
                     ["Social media links"               [:pre (get-field bio "Social media links")]]
                     ["Email"                            (get-field bio "Email")]
                     ["First name"                       (get-field bio "First name")]
                     ["Last name"                        (get-field bio "Last name")]
                     ["Phone"                            (format-phone (get-field bio "Phone"))]
                     ["Home base city"                   (get-field bio "Home base city")]
                     ["I'm interested in..."             (get-field bio "I'm interested in...")]
                     ["What makes this person awesome?"  (get-field bio "What makes this person awesome?")]
                     ["Gender"                           (get-field bio "Gender")]
                     ["Pictures"                         (map-indexed (fn [k2 v2] [:img {:src (:url v2) :key k2 :style {:height "180px" :margin "8px 8px 0 0"}}])
                                                                      (get-field bio "Pictures"))]]]
     [:table {:style {:margin-top "12px" :border-radius "8px" :padding "6px" :line-height "1.2em"}}
      [:tbody
       (map-indexed bio-row key-values)]])])

(defn home-tab []
  (let [included-bios (filter (fn [bio] (not (get-field bio "Exclude from gallery?"))) @bios)]
    [:div {:style {:margin-left "auto" :margin-right "auto" :width "80%"}}
     [:h1 {:style {:font-size 48 :line-height "2em"}} (str "All bios " (when-not (nil? included-bios) (str "(" (count included-bios) ")")))]
     (if (nil? included-bios)
       [:p "Loading..."]
       [:div
        (map-indexed render-bio included-bios)])]))

(def phone-input-error (r/atom nil))

(defn valid-phone [phone]
  ; strip the phone number of all non-numeric characters, then check if it's a valid phone number. if yes, return true; if not, return false:
  (let [phone (or phone "")
        phone (str/replace phone #"[^0-9]" "")]
    (and (not-empty phone) (re-find #"^\d{10}$" phone))))

(defn update-profile-with-result [result]
  (reset! profile (merge (:fields result)
                         {:id (:id result)})))

(defn fetch-profile [phone]
  (println "\nfetching profile...")
  (let [clean-phone (str/replace phone #"[^0-9]" "")]
    (println "clean-phone: " clean-phone)
    (util/fetch-post "/api/matchmaking/profile" {"Phone" clean-phone} update-profile-with-result)))

(defn sign-in []
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

(defn in? [list str] (some #(= str %) list))

(defn change-interested-in-checkbox [-e]
  (let [men-checkbox (js/document.getElementById "men-checkbox")
        women-checkbox (js/document.getElementById "women-checkbox")]
    (js/console.log "  men: " (.-checked men-checkbox))
    (js/console.log "women: " (.-checked women-checkbox))))

(def checkbox-values (r/atom ["A"]))

(defn handle-checkbox-change [value]
  (fn [event]
    (let [checked? (.-checked (.-target event))]
      (swap! checkbox-values
             (if checked?
               #(conj % value)
               #(remove (fn [v] (= value v)) %))))))

(defn checkbox [value]
  [:div
   [:input {:type "checkbox"
            :value value
            :checked (in? @checkbox-values value)
            :style {:margin "12px"}
            :on-change (handle-checkbox-change value)}]
   [:label {:for (str value "-checkbox")} value] ; TODO: make this a component that takes a value and returns the checkbox and label
   ])

(defn checkboxes-component []
  (let [values ["A" "B" "C"]]
    [:div
     (for [value values] ^{:key value} [checkbox value])
     [:div "Selected Values: " (str @checkbox-values)]]))

(defn profile-tab []
  [:div {:style {:margin-left "auto" :margin-right "auto" :width "80%"}}
   [:h1 {:style {:font-size 48 :line-height "2em"}} "Your profile"]

   [:div {:style {:color "red" :min-height "1.4em"}} @phone-input-error]
   [:p "Your phone number: " [:input {:type "text"
                                      :value @phone
                                      :on-change #(reset! phone (-> % .-target .-value))
                                      :on-key-press #(when (= (.-key %) "Enter") (sign-in))
                                      :style {:background "#ffffff22" :border-radius "8px" :padding "6px 8px" :margin-right :4px}}]
    (when (nil? @profile)
      [:button {:style btn-styles :on-click sign-in} "Sign in / sign up"])]

   (when (exists? @profile)
     [:div
      [:button {:style btn-styles :on-click update-profile!} "Save changes"]
      [:pre "@profile:" (pr-str (select-keys @profile [;(keyword "Anything else you'd like your potential matches to know?")
                                                       ;(keyword "Social media links")
                                                       ;(keyword "Email")
                                                       ;(keyword "First name")
                                                       ;(keyword "Last name")
                                                       ;(keyword "Phone")
                                                       (keyword "Home base city")
                                                       (keyword "I'm interested in...")
                                                       ;(keyword "What makes this person awesome?")
                                                       (keyword "Gender")]))]
      [:div {:style {:margin "16px 0 24px 0"}}
       (let [key-values [["Phone"                            (format-phone (get-field @profile "Phone"))] ; do not make this editable!
                         ["Anything else you'd like your potential matches to know?" (editable-textbox "Anything else you'd like your potential matches to know?")]
                         ["Social media links"               (editable-textbox "Social media links")]
                         ["Email"                            (editable-input "Email")]
                         ["First name"                       (editable-input "First name")]
                         ["Last name"                        (editable-input "Last name")]
                         ["Home base city"                   (editable-input "Home base city")]
                         ["I'm interested in..."
                          (let [interested-in-list (get-field @profile "I'm interested in...")]
                            (println "fields: " interested-in-list)
                            (println "women?: " (in? interested-in-list "Women"))
                            (checkboxes-component)

                            #_[:div
                               [:pre "current value:" (pr-str (get-field @profile "I'm interested in..."))]
                               [:div {:style {:margin-right "12px"}}
                                [:input {:type "checkbox" :id "men-checkbox"   :checked (in? interested-in-list "Men")   :on-change change-interested-in-checkbox}]
                                [:label {:for "men-checkbox"} "Men"]]
                               [:div {:style {:margin-right "12px"}}
                                [:input {:type "checkbox" :id "women-checkbox" :checked (in? interested-in-list "Women") :on-change change-interested-in-checkbox}]
                                [:label {:for "women-checkbox"} "Women"]]
                                ;
                               ])]
                         ["What makes this person awesome?"  (editable-textbox "What makes this person awesome?")]
                         ["Gender"                           (editable-input "Gender")]
                         ["Pictures"                         (map-indexed (fn [k2 v2] [:img {:src (:url v2) :key k2 :style {:height "180px" :margin "8px 8px 0 0"}}])
                                                                          (get-field @profile "Pictures"))]]]
         [:table {:style {:margin-top "12px" :border-radius "8px" :padding "6px" :vertical-align "top" :line-height "1.2em" :width "100%"}}
          [:tbody
           (map-indexed bio-row key-values)]])]])])

(defn nav-btns []
  [:div {:style {:margin "12px"}}
   [:button {:on-click #(reset! current-tab :home) :style (merge btn-styles (if (= @current-tab :home) {:border  "3px solid #ffffff88"} {}))} "All bios"]
   [:button {:on-click #(reset! current-tab :profile) :style (merge btn-styles (if (= @current-tab :profile) {:border  "3px solid #ffffff88"} {}))} "Your profile"]
   [:button {:on-click #(reset! debug? (not @debug?)) :style (merge btn-styles {:float "right"})} (str "Debug: " @debug?)]
   [:br]])

(defn screen []
  (r/create-class
   {:component-did-mount (fn [] (fetch-bios))
    :reagent-render (fn []
                      [:div
                       [nav-btns]

                       (case @current-tab
                         :profile (profile-tab)
                         :home (home-tab)
                         (home-tab))])}))
