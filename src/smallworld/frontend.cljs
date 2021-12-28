(ns smallworld.frontend
  (:require [reagent.core :as r]
            [clj-fuzzy.metrics :as fuzzy]
            [clojure.pprint :as pp]
            [clojure.string :as str]
            [goog.dom]))

(defonce friends (r/atom []))
(defonce current-user (r/atom {:name nil
                               :screen_name nil
                               :location nil
                               :profile_image_url_large nil
                               :name-location {:location nil :coordinates nil :distance nil}
                               :main-location {:location nil :coordinates nil :distance nil}}))

(defn fetch [route callback]
  (-> (.fetch js/window route)
      (.then #(.json %))
      (.then #(js->clj % :keywordize-keys true))
      (.then (fn [result]
               (println route ":")
               (println result)
               (callback result)))))

(fetch "/friends" #(reset! friends %))
(fetch "/current-user" #(reset! current-user %))

(defn nav []
  [:div.nav
   [:div.logo
    [:div.logo-globe]
    [:div.logo-text "small world"]]
   [:div.links
    [:a "about"]
    [:span.links-spacer "·"]
    [:a "log out " [:b "@" (:screen_name @current-user)]]]])

(def friend-row-headers ["" "name" "handle" "location" "coordinates" "distance" "profile_image_url_large"])

(defn location-name-similarity [friend]
  (fuzzy/jaro-winkler (.toLowerCase (:location friend))
                      (.toLowerCase (:location @current-user))))

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

(defn Friend [k friend]
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
      [:div.twitter-pic [:img {:src twitter-pic :key k}]]]
     [:div.right-section
      [:a.top twitter-href
       [:span.name twitter-name]
       [:span.handle "@" twitter-handle]]
      [:div.bottom
       [:a {:href (str "https://www.google.com/maps/search/" lat "%20" lng "?hl=en&source=opensearch")
            :target "_blank"}
        [:span.location location]
        [:span.coordinates [:span.coord lat] " " [:span.coord lng]]]]]]))

; TODO: duplicated from backend
(defn location-from-name [name]
  (let [split-name (str/split name #" in ")]
    (if (= 1 (count split-name))
      nil
      (last split-name))))

(defn preify [obj] (with-out-str (pp/pprint obj)))

(defn app-container []
  (let [friends-sorted-by-distance (sort-by get-distance @friends)
        friends-close-by (filter (closer-than 1000) friends-sorted-by-distance)]
    [:div
     (nav)
     [:div.container
      [:pre (preify @current-user)]
      [:hr]
      [:pre (preify @friends)]
      [:hr]
      [:br] (Friend nil @current-user)
      [:div.location-info
       [:p "your current location: " [:span.location (or (location-from-name (:name @current-user))
                                                         (:location @current-user))]]
       [:p "you are based in: " [:span.location (:location @current-user)]]]

      [:hr] [:br]

      [:p.location-info "friends based near " [:span.location (:location @current-user)] ":"]
      [:hr]

      (map-indexed Friend friends-close-by)

      [:br] [:br] [:br] [:br]

      [:p.location-info "friends who may be near " [:span.location (:location @current-user)] " right now:"]
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

