(ns smallworld.user-data
  (:require [reagent.core   :as r]
            [clojure.pprint :as pp]
            [clojure.string :as str]
            [smallworld.util :as util]
            [smallworld.mapbox :as mapbox]
            [smallworld.session :as session]
            [smallworld.decorations :as decorations]))

(def debug? false)
(defonce *friends (r/atom :loading))

(defn render-user [k user]
  (let [twitter-pic    (:profile_image_url_large user)
        twitter-name   (:name user)
        twitter-handle (:screen-name user)
        twitter-link   (str "http://twitter.com/" twitter-handle)
        twitter-href   {:href twitter-link :target "_blank" :title "Twitter"}
        first-location (first (:locations user)) ; consider pulling from the "Twitter location" location or from the nearest location to the current user, instead of simply pulling the first location in the array
        lat            (when first-location (:lat (:coords first-location)))
        lng            (when first-location (:lng (:coords first-location)))]
    [:div.friend {:key twitter-handle}
     [:a twitter-href
      [:div.twitter-pic [:img {:src twitter-pic :key k}]]]
     [:div.right-section
      [:a.top twitter-href
       [:span.name twitter-name]
       [:span.handle "@" twitter-handle]]
      [:div.bottom
       [:a {:href (str "https://www.google.com/maps/search/" lat "%20" lng "?hl=en&source=opensearch")
            :title "Google Maps"
            :target "_blank"}
        [:span.location (:name first-location)]]]]]))

(defn render-user-bubble [k user]
  (let [twitter-pic    (:profile_image_url_large user)
        twitter-handle (:screen-name user)]
    [:div.friend {:key twitter-handle}
     [:a ; TODO: on click center map on their face
      [:div.twitter-pic [:img {:src twitter-pic :key k}]]]]))

; TODO: the logic in this needs some serious cleanup; probably requires refactoring the data model too
(defn get-close-friends [curr-user-location-name friend-location-key max-distance]
  (->> @*friends

       ; not all friends will have both LOCATION and DISPLAY NAME LOCATION set, so filter those out
       (filter (fn [friend]
                 (let [friend-locations (:locations friend)
                       has-location? (-> #(= (:special-status %) friend-location-key)
                                         (filter friend-locations)
                                         first
                                         nil?
                                         not)]
                   has-location?)))

       (filter (fn [friend] (let [friend-locations (:locations friend)
                                  friend-location  (-> #(= (:special-status %) friend-location-key)
                                                       (filter friend-locations)
                                                       first)
                                  distance-to-curr-user-location (get-in friend-location
                                                                         [:distances (keyword curr-user-location-name)])]
                              (when debug?
                                (println)
                                (println "       curr-user-location-name: " curr-user-location-name)
                                (println "               friend-location: " friend-location)
                                (println "distance-to-curr-user-location: " distance-to-curr-user-location)
                                (println "                       boolean:" (and (not (nil? distance-to-curr-user-location))
                                                                                (> max-distance distance-to-curr-user-location))))
                              (and (not (nil? distance-to-curr-user-location))
                                   (> max-distance distance-to-curr-user-location)))))

       (sort-by (fn [friend] (let [friend-locations (:locations friend)
                                   friend-location  (-> #(= (:special-status %) friend-location-key)
                                                        (filter friend-locations)
                                                        first)
                                   distance-to-curr-user-location (get-in friend-location
                                                                          [:distances (keyword curr-user-location-name)])]
                               distance-to-curr-user-location)))))

(def *expanded? (r/atom {}))

(defn render-friends-list [curr-user-location-i friend-location-key verb-gerund curr-user-location-name]
  (assert (or (= friend-location-key "twitter-location")
              (= friend-location-key "from-display-name"))) ; TODO: add Scheme to encode this more nicely
  (let [verb-gerund-info-text (if (= verb-gerund "visiting")
                                "when a friend includes a nearby location in their display name, they'll show up on this list"
                                "when a friend's Twitter location is nearby, they'll show up on this list")
        key-pair        [curr-user-location-i friend-location-key]
        verb-gerund     [:span.verb-gerund verb-gerund]
        friends-list    (if (= :loading @*friends)
                          []
                          (get-close-friends curr-user-location-name friend-location-key 100))
        list-count        (count friends-list)
        friend-pluralized (if (= list-count 1) "friend is" "friends are")
        expanded?         (boolean (get @*expanded? key-pair))]

    [util/error-boundary
     [:div.friends-list
      (if (or (= :loading @*friends) (and @mapbox/*loading (= 0 (count @*friends))))
        [:div.loading (decorations/simple-loading-animation) "fetching your Twitter friends..."]
        (if (> list-count 0)
          [:<>
           [:p.location-info
            [:span {:title "expand for details"
                    :on-click #(swap! *expanded? assoc key-pair (not expanded?))}
             (decorations/triangle-icon (clojure.string/join " "  ["caret" (if expanded? "down" "right")]))
             [:<>
              list-count " "
              friend-pluralized " "
              verb-gerund " " curr-user-location-name]]
            [:a {:data-tooltip verb-gerund-info-text
                 :class (if (< (.-innerWidth js/window) 500)
                          "tooltip-left"
                          "tooltip-right")}
             (decorations/info-icon)]]

           [:div.friends {:style (when-not expanded? {:visibility "collapse" :height 0 :margin 0})}  (map-indexed render-user friends-list)]
           [:div.friend-bubbles {:style (when expanded? {:visibility "collapse" :height 0 :margin 0})
                                 :title "expand for details"
                                 :on-click #(swap! *expanded? assoc key-pair (not expanded?))}
            (map-indexed render-user-bubble (take 200 friends-list))]]

          [:div.no-friends-found
           (decorations/x-icon)
           "0 friends are " verb-gerund " " curr-user-location-name
           [:a {:data-tooltip verb-gerund-info-text
                :class (if (< (.-innerWidth js/window) 500)
                         "tooltip-left"
                         "tooltip-right")}
            (decorations/info-icon)]]))]]))

(defn refetch-friends []
  (util/fetch "/api/v1/friends/refetch-twitter"
              (fn [result]
                (reset! *friends result)
                (mapbox/add-friends-to-map @*friends @session/*store))))

(defn recompute-friends [& [callback]]
  (util/fetch "/api/v1/friends/recompute-locations"
              (fn [result]
                (when callback (callback))
                (reset! *friends result)
                (mapbox/add-friends-to-map @*friends @session/*store))))