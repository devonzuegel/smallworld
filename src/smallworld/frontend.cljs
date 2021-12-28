(ns smallworld.frontend
  (:require [reagent.core :as r]
            [clj-fuzzy.metrics :as fuzzy]
            [clojure.pprint :as pp]
            [goog.dom]))

(defonce friends (r/atom []))
(def current-user
  {:name "Devon in Buenos Aires"
   :screen_name "devonzuegel"
   :location "Miami Beach"
   :profile_image_url_large "http://pbs.twimg.com/profile_images/1360636520655331329/9mpyJLkK.jpg"
   :coordinates {:lat 25.775083541870117
                 :lng -80.1947021484375}
   :distance 7102.906300799643})

(defn nav []
  [:div.nav
   [:div.logo
    [:div.logo-globe]
    [:div.logo-text "small world"]]
   [:div.links
    [:a "about"]
    [:span.links-spacer "·"]
    [:a "log out " [:b "@" (:screen_name current-user)]]]])

(def friend-row-headers ["" "name" "handle" "location" "coordinates" "distance" "profile_image_url_large"])

(defn location-name-similarity [friend]
  (fuzzy/jaro-winkler (.toLowerCase (:location friend))
                      (.toLowerCase (:location current-user))))

(defn friend-row [i friend]
  (let [twitter-handle (:screen_name friend)
        twitter-link   (str "http://twitter.com/" twitter-handle)]
    [:tr {:key i}
     [:td i]
     [:td (:name friend)]
     [:td [:a {:href twitter-link} (str "@" twitter-handle)]]
     [:td (:location friend)]
     [:td (pr-str (:coordinates friend))]
     [:td (:distance friend)]
     [:td (:profile_image_url_large friend)]
     [:td [:pre (prn-str)]]
     #_[:td (location-name-similarity friend)]]))

(def table-header (map-indexed (fn [i header] [:th {:key i} header])
                               friend-row-headers))

(defn closer-than [distance] #(and (< (:distance %) distance)
                                   (not (nil? (:distance %)))))

(defn get-distance [friend]
  (if (nil? (:distance friend))
    9999999999999999 ; if distance couldn't be calculated, treat as very distant
    (:distance friend)))

(defn app-container []
  (let [friends-sorted-by-distance (sort-by get-distance @friends)
        friends-close-by (filter (closer-than 1000) friends-sorted-by-distance)]
    [:div
     (nav)
     [:div.container
      [:br]

      [:p.location-info "friends based near " [:span.location (:location current-user)] ":"]
      [:hr]

      (map-indexed
       (fn [k friend]
         (let [twitter-pic    (:profile_image_url_large friend)
               twitter-name   (:name friend)
               twitter-handle (:screen_name friend)
               twitter-link   (str "http://twitter.com/" twitter-handle)
               location       (:location friend)
               twitter-href   {:href twitter-link :target "_blank"}
               format-coord   #(pp/cl-format nil "~,2f" %)
               lat            (format-coord (:lat (:coordinates friend)))
               lng            (format-coord (:lng (:coordinates friend)))]
           [:div.friend
            [:a twitter-href
             [:img {:src twitter-pic :key k}]]
            [:div.right-section
             [:a.top twitter-href
              [:span.name twitter-name]
              [:span.handle "@" twitter-handle]]
             [:div.bottom
              [:a {:href (str "https://www.google.com/maps/search/" lat "%20" lng "?hl=en&source=opensearch")
                   :target "_blank"}
               [:span.location location]
               [:span.coordinates [:span.coord lat] " " [:span.coord lng]]]]]]))
       friends-close-by)

      [:br] [:br] [:br] [:br]

      [:p.location-info "friends who may be near " [:span.location (:location current-user)] " right now:"]
      [:hr]
      [:table
       [:tbody
        [:tr table-header]
        (map-indexed friend-row friends-close-by)]]
      [:br] [:br] [:br] [:br]

      [:p.location-info "all of your friends with their locations:"]
      [:hr]
      [:table
       [:tbody
        [:tr table-header]
        (map-indexed friend-row friends-sorted-by-distance)]]]

     #_[:div.sticky-footer (music)]]))

(r/render-component [app-container] (goog.dom/getElement "app"))

(-> (.fetch js/window "/friends")
    (.then #(.json %))
    (.then #(js->clj % :keywordize-keys true))
    (.then (fn [result]
             (println "result:")
             (println result)
             (reset! friends result))))
