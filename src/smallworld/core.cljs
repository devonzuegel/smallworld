(ns smallworld.core
  (:require [reagent.core :as r]
            [goog.dom]))

(defonce friends (r/atom []))
(defonce count- (r/atom 1))

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

(def friend-row-headers ["" "name" "screen_name" "location"])

(defn friend-row [i friend]
  [:tr {:key i}
   [:td i]
   [:td (:name friend)]
   [:td [:a {:href (str "http://twitter.com/" (:screen_name friend))}
         (str "@" (:screen_name friend))]]
   [:td (:location friend)]])

(defn app-container []
  [:div
   (nav)
   [:div.container
    [:pre "@count:"]
    [:pre @count-]
    [:hr]
    [:table
     [:tbody
      [:tr
       (map-indexed (fn [i header] [:th header]) friend-row-headers)]
      (map-indexed friend-row @friends)]]]])

(r/render-component [app-container] (by-id "app"))

(-> (.fetch js/window "/friends")
    (.then #(.json %))
    (.then (fn [result]
             (let [result (js->clj result :keywordize-keys true)]
               (js/console.log "friends count:  ")
               (js/console.log (count result))
               (js/console.log "friends: ")
               (js/console.log result)
               (swap! friends concat result)
               (swap! count- inc)))))