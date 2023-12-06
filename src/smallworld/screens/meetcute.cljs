(ns smallworld.screens.meetcute (:require [clojure.string :as str]
                                          [reagent.core    :as r]
                                          [smallworld.util :as util]))

(def debug? (r/atom false))
(def bios   (r/atom nil))
(def phone (r/atom "650-906-7099")) ; TODO: remove me
(def profile (r/atom nil))
(def current-tab (r/atom :home))

(defn fetch-bios []
  (println "fetching bios")
  (util/fetch "/api/matchmaking/bios" (fn [result]
                                        (swap! bios (fn [_] result)))))

(defn get-field [bio field]
  (get-in bio [(keyword field)]))

(defn get-key-names [bio] (map first bio))

(def btn-styles {:color "white" :border "3px solid #ffffff33" :padding "12px" :border-radius "8px" :cursor "pointer" :margin "6px"})

(def fields-changeable-by-user ["Anything else you'd like your potential matches to know?"
                                "Social media links"
                                "Created By"
                                "Email"
                                "First name"
                                "Last name"
                                "Phone"
                                "Home base city"
                                "I'm interested in..."
                                "What makes this person awesome?"
                                "Gender"])

(defn render-bio [i bio]
  [:div {:key i :style {:background "#ffffff11"
                        :border (when (get-field bio "Exclude from gallery?") "3px solid orange")
                        :margin "16px 0"
                        :border-radius "8px"
                        :padding "16px"
                        :line-height "1.8em"}}
   (let [key-names fields-changeable-by-user
         fields (map (fn [key-name]
                       [:tr {:style {:border-bottom "1px solid #ffffff22" :padding "24px"}}
                        [:td {:style {:padding "12px" :text-align "right" :font-size ".9em" :opacity ".8"}} key-name]
                        [:td {:style {:padding "12px" :text-align "left"}} (str (get-field bio key-name))]])
                     key-names)]
     [:div
      [:pre (get-key-names bio)]
      [:table {:style {:margin-top "12px" :border "1px solid #ffffff22" :border-radius "8px" :padding "12px" :vertical-align "top"}}
       [:tbody
        fields
        [:tr
         [:td {:style {:padding-right "12px" :text-align "right"}}
          "Pictures"]
         [:td (map-indexed (fn [k2 v2]
                             [:img {:src (:url v2) :key k2 :style {:height "120px" :margin "8px 8px 0 0"}}])
                           (get-field bio "Pictures"))]]]]])]

  #_[:div {:key i :style {:background "#ffffff11"
                          :border (when (get-field bio "Exclude from gallery?") "3px solid orange")
                          :margin "16px 0"
                          :border-radius "8px"
                          :padding "16px"
                          :line-height "1.8em"}}
     [:h1 {:style {:font-size "32px" :margin-bottom "24px"}} (str (get-field bio "First name") " " (get-field bio "Last name"))]
     [:p {:style {:margin-bottom "12px"}} [:b "id: "] [:code (get-field bio "id")]]
     [:p {:style {:margin-bottom "12px"}} [:b "Phone: "] [:code (get-field bio "Phone")]]
     [:p {:style {:margin-bottom "12px"}} [:b "Email: "] [:code (get-field bio "Email")]]
     [:br]
     [:p {:style {:margin-bottom "12px"}} [:b "I'm interested in...: "] (str/join ", " (get-field bio "I'm interested in..."))]
     [:p {:style {:margin-bottom "12px"}} [:b "Gender: "] (get-field bio "Gender")]
     [:p {:style {:margin-bottom "12px"}} [:b "Home base city: "] (get-field bio "Home base city")]
     [:p {:style {:margin-bottom "12px"}} [:b "Other cities where you spend time: "] (get-field bio "Other cities where you spend time")]
     [:p {:style {:margin-bottom "12px"}} [:b "Social media links: "] (get-field bio "Social media links")]
     [:p {:style {:margin-bottom "12px"}} [:b "Anything else you'd like your potential matches to know?: "] (get-field bio "Anything else you'd like your potential matches to know?")]
     [:p                                  (map-indexed (fn [k2 v2]
                                                         [:img {:src (:url v2) :key k2 :style {:height "120px" :margin "8px 8px 0 0"}}])
                                                       (get-field bio "Pictures"))]
     (when @debug? [:pre (. js/JSON (stringify (clj->js bio) nil 2))])])

(defn home-tab []
  (let [included-bios (filter (fn [bio] (not (get-field bio "Exclude from gallery?"))) @bios)]
    [:div {:style {:margin-left "auto" :margin-right "auto" :width "80%"}}
     [:h1 {:style {:font-size 80 :line-height "2em"}} (str "All bios " (when-not (nil? included-bios) (str "(" (count included-bios) ")")))]
     [:br]
     [:br]
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

(defn update-anything-else []
  (let [new-anything-else (js/prompt "Anything else you'd like your potential matches to know?"
                                     (get-field @profile "Anything else you'd like your potential matches to know?"))]
    (println "new-anything-else: " new-anything-else)
    (util/fetch-post "/api/matchmaking/profile" {"Phone" @phone
                                                 "id" (get-field @profile "id")
                                                 "Email" "1@gmail.com"
                                                 "Anything else you'd like your potential matches to know?" new-anything-else}
                     update-profile-with-result)))

(defn render-my-bio []
  [:div
   [:button {:style btn-styles :on-click update-anything-else} "update 'Anything else you'd like your potential matches to know?'"]
   (let [key-names (get-key-names @profile)
         fields (map (fn [key-name]
                       [:tr
                        [:td {:style {:padding-right "12px" :text-align "right"}} key-name]
                        [:td (get-field @profile key-name)]])
                     key-names)]
     [:table {:style {:margin-top "12px" :border "1px solid #ffffff22" :border-radius "8px" :padding "12px"}}
      [:tbody fields]])])

(defn profile-tab []
  [:div {:style {:margin-left "auto" :margin-right "auto" :width "80%"}}
   [:h1 {:style {:font-size 80 :line-height "2em"}} "Your profile"]

   [:pre "@profile: " (. js/JSON (stringify (clj->js @profile) nil 2))]

   [:div {:style {:color "red" :min-height "1.4em"}} @phone-input-error]
   [:p "Your phone number: " [:input {:type "text"
                                      :value @phone
                                      :on-change #(reset! phone (-> % .-target .-value))
                                      :on-key-press #(when (= (.-key %) "Enter") (sign-in))
                                      :style {:background "#ffffff22" :border-radius "8px" :padding "6px 8px" :margin-right :4px}}]
    (when (nil? @profile)
      [:button {:style btn-styles :on-click sign-in} "Sign in / sign up"])]

   (when (exists? @profile)
     (render-my-bio))])

(defn nav-btns []
  [:div {:style {:margin "12px"}}
   [:button {:on-click #(reset! current-tab :home) :style (merge btn-styles (if (= @current-tab :home) {:border  "3px solid #ffffff88"} {}))} "All bios"]
   [:button {:on-click #(reset! current-tab :profile) :style (merge btn-styles (if (= @current-tab :profile) {:border  "3px solid #ffffff88"} {}))} "Your profile"]
   [:button {:on-click #(reset! debug? (not @debug?)) :style (merge btn-styles {:float "right"})} (str "Debug: " @debug?)]
   [:br]])

(defn screen []
  (r/create-class
   {:component-did-mount (fn []
                           (println "component-did-mount")
                           (fetch-bios))
    :reagent-render (fn []
                      [:div
                       [nav-btns]

                       (case @current-tab
                         :profile (profile-tab)
                         :home (home-tab)
                         (home-tab))])}))
