(ns smallworld.core
  (:require [reagent.core :as r]
            [goog.dom]))

(defonce friends (r/atom ()))
(defonce count- (r/atom 1))

(def by-id goog.dom.getElement)

(defn app-container []
  [:div
   [:div.nav
    [:div.logo
     [:div.logo-globe]
     [:div.logo-text "small world"]]
    [:div.links
     [:a "about"]
     [:span.links-spacer "·"]
     [:a "log out " [:b "@username"]]]]

   [:div.container
    [:p "foo bar"]
    [:hr]
    [:pre "@count:"]
    [:pre @count-]
    [:p.links-spacer]
    [:pre "@friends!!!!!!!"]
    [:pre (pr-str @friends)]]])

(r/render-component [app-container] (by-id "app"))

(-> (.fetch js/window "/friends")
    (.then #(.json %))
    (.then (fn [result]
             (swap! friends conj result)
             (swap! count- inc)
             (js/console.log "friends: ")
             (js/console.log result))))

