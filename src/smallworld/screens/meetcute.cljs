(ns smallworld.screens.meetcute (:require [reagent.core    :as r]
                                          [clojure.string :as str]
                                          [smallworld.util :as util]))

(def debug? (r/atom false))
(def bios   (r/atom nil))
(def phone (r/atom nil))
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

(defn -screen []
  [:div {:style {:margin-left "auto" :margin-right "auto" :width "80%"}}
   [:h1 {:style {:font-size 80 :line-height "2em"}} (str "All bios " (when-not (nil? @bios) (str "(" (count @bios) ")")))]
   [:br]
   [:br]
   (if (nil? @bios)
     [:p "Loading..."]
     [:div
      (map-indexed
       (fn [i bio]
         [:div {:key i :style {:background "#ffffff11"
                               :border (when (get-field bio "Exclude from gallery?") "3px solid red")
                               :margin "16px 0"
                               :border-radius "8px"
                               :padding "16px"
                               :line-height "1.8em"}}
          (when @debug? [:pre (str (get-key-names bio))])
          [:h1 {:style {:font-size "32px" :margin-bottom "24px"}} (str (get-field bio "First name") " " (get-field bio "Last name"))]
          [:p {:style {:margin-bottom "12px"}}                    (str (get-field bio "Phone") " · " (get-field bio "Email"))]
          [:p {:style {:margin-bottom "12px"}}                    [:b "I'm interested in...: "] (get-field bio "I'm interested in...")]
          [:p {:style {:margin-bottom "12px"}}                    [:b "Gender: "] (get-field bio "Gender")]
          [:p {:style {:margin-bottom "12px"}}                    [:b "Home base city: "] (get-field bio "Home base city")]
          [:p {:style {:margin-bottom "12px"}}                    [:b "Other cities where you spend time: "] (get-field bio "Other cities where you spend time")]
          [:p {:style {:margin-bottom "12px"}}                    [:b "Social media links: "] (get-field bio "Social media links")]
          [:p {:style {:margin-bottom "12px"}}                    [:b "Anything else you'd like your potential matches to know?: "] (get-field bio "Anything else you'd like your potential matches to know?")]
          [:p                                                     (map-indexed (fn [k2 v2]
                                                                                 [:img {:src (:url v2) :key k2 :style {:height "120px" :margin "8px 8px 0 0"}}])
                                                                               (get-field bio "Pictures"))]])
       @bios)])])

(def phone-input-error (r/atom nil))

(defn valid-phone [phone]
  ; strip the phone number of all non-numeric characters, then check if it's a valid phone number. if yes, return true; if not, return false:
  (let [phone (or phone "")
        phone (str/replace phone #"[^0-9]" "")]
    (and (not (empty? phone)) (re-find #"^\d{10}$" phone))))


(defn profile-tab []
  [:div {:style {:margin-left "auto" :margin-right "auto" :width "80%"}}
   [:h1 {:style {:font-size 80 :line-height "2em"}} "Your profile"]

   [:div {:style {:color "red" :min-height "1.4em"}} @phone-input-error]
   [:p "Your phone number: " [:input {:type "text"
                                      :value @phone
                                      :on-change #(reset! phone (-> % .-target .-value))
                                      :style {:background "#ffffff22" :border-radius "8px" :padding "6px 8px" :margin-right :4px}}]
    [:button {:style btn-styles
              :on-click (fn [] (if (valid-phone @phone)
                                 (println "TODO: find bio with associated phone number")
                                 (reset! phone-input-error "Please enter a valid phone number")))} "Sign in / sign up"]]
   ;
   ])

(defn nav-btns []
  [:div {:style {:margin "12px"}}
   [:button {:on-click #(reset! current-tab :home) :style btn-styles} "All bios"]
   [:button {:on-click #(reset! current-tab :profile) :style btn-styles} "Your profile"]
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
                         :home (-screen)
                         (-screen))])}))
