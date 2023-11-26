(ns smallworld.screens.meetcute (:require [reagent.core    :as r]
                                          [smallworld.util :as util]))

(def bios (r/atom nil))

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

(defn get-field [field bio]
  (get-in bio [field])
  ;; (get-in bio [:fields field])
  )

(defn get-key-names [bio]
  (map first (get bio :fields)))

(defn -screen []
  [:div {:style {:margin-left "auto"
                 :margin-right "auto"
                 :width "80%"}}
   [:h1 {:style {:font-size 80 :line-height "2em"}} "All bios"]
   (if (nil? @bios)
     [:p "Loading..."]
     [:div
      (map-indexed
       (fn [i bio]
         [:div {:key i :style {:background "#ffffff11" :margin "16px" :border-radius "8px" :padding "16px"}}
          [:pre "First name: " (get-field (keyword "First name") bio)]
          [:pre " Last name: " (get-field (keyword "Last name") bio)]
          [:pre "Created By: " (get-field (keyword "Created By") bio)]
          [:pre "     Email: " (get-field (keyword "Email") bio)]
          [:pre "     Phone: " (get-field (keyword "Phone") bio)]
          [:pre "Who invited you to this matchmaking experiment?" (get-field (symbol "Who invited you to this matchmaking experiment?") bio)]
          [:hr]
          [:pre (str "bio:")]
          [:pre (pr-str bio)]
          [:pre "-------------------------------------------"]
          (map (fn [j] [:p {:style {:margin "16px"} :key (str i "-" j)}
                        (str j) [:br] [:br]
                        (name j) [:br] [:br]
                        (get-field (name j) bio)
                        (str (type j)) [:br] [:br] [:hr] [:br]])
               (get-key-names bio))
          (let [fields (get bio :fields)]
            [:div
             [:hr]
             (map-indexed (fn [j [k v]]
                            [:div {:key (str i "-" j)}
                             [:b (str k)]
                             (if (= k :Pictures)
                               [:p (map-indexed (fn [k2 v2] [:img {:src (:url v2) :key k2 :style {:height "120px" :margin "8px"}}]) v)]
                               [:p (str v)])
                             [:br]])
                          fields)])])
       @bios)])])

(defn screen []
  (r/create-class
   {:component-did-mount (fn []
                           (println "component-did-mount")
                           (fetch-bios))
    :reagent-render (fn [] [-screen])}))
