(ns smallworld.screens.meetcute (:require [reagent.core    :as r]
                                          [smallworld.util :as util]))

(def debug? (r/atom false))
(def bios   (r/atom nil))

(defn fetch-bios []
  (println "fetching bios")
  (util/fetch "/api/matchmaking/bios" (fn [result]
                                        (println "result:" result)
                                        (swap! bios (fn [_] result)))))

(defn stringify-all-symbol-keys [obj]
  (if (map? obj)
    (into {} (map (fn [[k v]]
                    #_(println "k:" k)
                    #_(println "v:" v)
                    #_(println "")
                    [(str k) (stringify-all-symbol-keys v)]) obj))
    obj))

(defn get-field [bio field]
  (get-in bio [(keyword field)]))

(defn get-key-names [bio] (map first bio))

(defn -screen []
  [:div {:style {:margin-left "auto" :margin-right "auto" :width "80%"}}
   [:h1 {:style {:font-size 80 :line-height "2em"}} (str "All bios " (when-not (nil? @bios) (str "(" (count @bios) ")")))]
   [:button {:on-click #(reset! debug? (not @debug?)) :style {:color "white" :border "3px solid #ffffff33" :padding "12px" :border-radius "8px" :cursor "pointer"}}
    (str "Debug: " @debug?)]
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

(defn screen []
  (r/create-class
   {:component-did-mount (fn []
                           (println "component-did-mount")
                           (fetch-bios))
    :reagent-render (fn [] [-screen])}))
