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

(defn get-smallest-distance [friend]
  (let [x (apply min (remove nil? [9999999999999999 ; if distance couldn't be calculated, treat as very distant
                                   (get-in friend [:distance :name-name])
                                   (get-in friend [:distance :name-main])
                                   (get-in friend [:distance :main-name])
                                   (get-in friend [:distance :main-main])]))]
    (println "distance: " x)
    x))

(defn closer-than [max-distance dist-key]
  (fn [friend]
    (let [smallest-distance (get-in friend [:distance dist-key])]
      (and (< smallest-distance max-distance)
           (not (nil? smallest-distance))))))

(def round-two-decimals #(pp/cl-format nil "~,2f" %))

(defn preify [obj] (with-out-str (pp/pprint obj)))

(defn Friend [k friend]
  (let [twitter-pic    (:profile_image_url_large friend)
        twitter-name   (:name friend)
        twitter-handle (:screen_name friend)
        twitter-link   (str "http://twitter.com/" twitter-handle)
        location       (:main-location friend)
        twitter-href   {:href twitter-link :target "_blank"}
        lat            (round-two-decimals (:lat (:main-coords friend)))
        lng            (round-two-decimals (:lng (:main-coords friend)))]
    [:div.friend
     [:a twitter-href
      [:div.twitter-pic [:img {:src twitter-pic :key k}]]]
     [:div.right-section
      [:a.top twitter-href
       [:span.name twitter-name]
       [:span.handle "@" twitter-handle]]
      ;; [:span.name " dist: " (round-two-decimals (get-smallest-distance friend))]
      [:div.bottom
       [:a {:href (str "https://www.google.com/maps/search/" lat "%20" lng "?hl=en&source=opensearch")
            :target "_blank"}
        [:span.location location]
        [:span.coordinates [:span.coord lat] " " [:span.coord lng]]]
       [:pre (preify friend)]]]]))

(defn app-container []
  (let [friends-sorted-by-distance (sort-by #(get-in % [:distance :main-main]) @friends)
        ;; friends-sorted-by-distance (sort-by #(min (get-in % [:distance :main-main])
        friends-close-by           (filter (closer-than 200 :main-main) friends-sorted-by-distance)
        main-location              (:main-location @current-user)
        name-location              (:name-location @current-user)]
    [:div
     (nav)
     [:div.container
      [:pre (preify @current-user)]
      ;; [:hr]
      ;; [:pre (preify @friends)]
      [:hr]
      [:br] (Friend nil @current-user)

      [:div.location-info
       [:p "your current location: " [:span.location name-location]]
       [:p "you are based in: "      [:span.location main-location]]]

      [:hr] [:br]

      [:p.location-info "friends based near " [:span.location main-location] ":"]
      [:hr]

      [:div.friends (map-indexed Friend friends-close-by)]

      [:br] [:br] [:br] [:br]

      [:p.location-info "friends who may be near " [:span.location main-location] " right now:"]
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

