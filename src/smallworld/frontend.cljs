(ns smallworld.frontend
  (:require [reagent.core :as r]
            [clj-fuzzy.metrics :as fuzzy]
            [clojure.pprint :as pp]
            [goog.dom]))

(defonce friends (r/atom []))
(defonce my-location "Argentina, Buenos Aires")

(def by-id goog.dom/getElement)

(defn nav []
  [:div.nav
   [:div.logo
    [:div.logo-globe]
    [:div.logo-text "small world"]]
   [:div.links
    [:a "about"]
    [:span.links-spacer "·"]
    [:a "log out " [:b "@username"]]]])

;; (defn music []
;;   [:iframe {:src "https://open.spotify.com/embed/track/3fWTQXs897m4H1zsai8SOk?utm_source=generator&theme=0"
;;             :width "100%"
;;             :height "80"
;;             :frameBorder "0"
;;             :allowFullScreen ""
;;             :allow "autoplay; clipboard-write; encrypted-media; fullscreen; picture-in-picture"}])

(def friend-row-headers ["" "name" "handle" "location" "coordinates" "distance" "profile_image_url_large"])

(defn location-name-similarity [friend]
  (fuzzy/jaro-winkler (.toLowerCase (:location friend))
                      (.toLowerCase my-location)))

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

    [:p.location-info "friends based near " [:span.location my-location] ":"]
    [:hr]
    [:table
     [:tbody
      [:tr table-header]
      (map-indexed friend-row friends-close-by)]]
    [:br] [:br] [:br] [:br]

    [:p.location-info "friends who may be near " [:span.location my-location] " right now:"]
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

(r/render-component [app-container] (by-id "app"))

(-> (.fetch js/window "/friends")
    (.then #(.json %))
    (.then #(js->clj % :keywordize-keys true))
    (.then (fn [result]
             (println "result:")
             (println result)
             (reset! friends result))))
