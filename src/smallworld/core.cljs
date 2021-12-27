(ns smallworld.core
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

(defn music []
  [:iframe {:src "https://open.spotify.com/embed/track/3fWTQXs897m4H1zsai8SOk?utm_source=generator&theme=0"
            :width "100%"
            :height "80"
            :frameBorder "0"
            :allowFullScreen ""
            :allow "autoplay; clipboard-write; encrypted-media; fullscreen; picture-in-picture"}])

(def friend-row-headers ["" "name" "handle" "location" "coordinates" "distance"])

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
     [:td [:pre (prn-str )]]
     #_[:td (location-name-similarity friend)]]))

(defn app-container []
  [:div
   (nav)
   [:div.container

    [:pre "count"]
    [:pre (count @friends)]

    [:table
     [:tbody
      [:tr
       (map-indexed (fn [i header] [:th {:key i} header]) friend-row-headers)]
      (map-indexed friend-row (sort-by #(if (nil? (:distance %))
                                          9999999999999999
                                          (:distance %))
                                       @friends))]]]

   #_[:div.sticky-footer (music)]])

(r/render-component [app-container] (by-id "app"))

(-> (.fetch js/window "/friends")
    (.then #(.json %))
    (.then #(js->clj % :keywordize-keys true))
    (.then #(swap! friends concat %)))
