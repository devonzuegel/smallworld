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
    [:div.friend {:key twitter-name}
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
        verb-gerund     [:span.verb-gerund
                         {:title verb-gerund-info-text
                          :on-click #(js/alert verb-gerund-info-text)}
                         verb-gerund]
        friends-list    (if (= :loading @*friends)
                          []
                          (get-close-friends curr-user-location-name friend-location-key 100))
        list-count        (count friends-list)
        friend-pluralized (if (= list-count 1) "friend is" "friends are")
        expanded?        (boolean (get @*expanded? key-pair))]

    [util/error-boundary
     [:div.friends-list
      (if (= :loading @*friends)
        [:div.loading (decorations/simple-loading-animation) "fetching your Twitter friends..."]
        (if (> list-count 0)
          [:<>
           [:p.location-info
            {:on-click ; toggle collapsed state
             #(swap! *expanded? assoc key-pair (not expanded?))}
            (decorations/triangle-icon (clojure.string/join " "  ["caret" (if expanded? "right" "down")]))
            [:<>
             list-count " "
             friend-pluralized " "
             verb-gerund " " curr-user-location-name ":"]]
           (when-not expanded?
             [:div.friends (map-indexed render-user friends-list)])]

          [:div.no-friends-found
           (decorations/x-icon)
           "0 friends are " verb-gerund " " curr-user-location-name]))]]))

; TODO: consider running every 10 mins... might create rate-limiting issues
(defn refresh-friends []
  (util/fetch "/api/v1/friends/refetch-twitter"
              (fn [result]
                (doall (map (mapbox/remove-friend-marker) @mapbox/markers))
                (reset! *friends result)
                (mapbox/add-friends-to-map @*friends @session/*store))))

(defn recompute-friends [& [callback]]
  (util/fetch "/api/v1/friends/recompute-locations"
              (fn [result]
                (when callback (callback))
                (doall (map (mapbox/remove-friend-marker) @mapbox/markers))
                (reset! *friends result)
                (mapbox/add-friends-to-map @*friends @session/*store))))