(ns smallworld.core
  (:require [reagent.core :as r]
            [clj-fuzzy.metrics :as fuzzy]
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

(def friend-row-headers ["" "name" "handle" "location" "similarity"])

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
     [:td (location-name-similarity friend)]]))

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
      (map-indexed friend-row (sort-by #(* -1 (location-name-similarity %))
                                    @friends))]]]])

(r/render-component [app-container] (by-id "app"))

(-> (.fetch js/window "/friends")
    (.then #(.json %))
    (.then #(js->clj % :keywordize-keys true))
    (.then #(swap! friends concat %)))