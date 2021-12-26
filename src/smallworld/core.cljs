(ns smallworld.core
  (:require [reagent.core :as r]
            [goog.dom]))

(def friends (atom ()))

(def by-id goog.dom.getElement)

(defn app-container []
  [:div.container
   [:h1 {}
    "hello small world!!!!!!!!"]
   [:p "foo bar"]
   [:pre @friends]])

(r/render-component [app-container] (by-id "app"))

(-> (.fetch js/window "http://localhost:3001/friends")
    (.then #(.json %))
    (.then (fn [result]
             (js/console.log result)
             (swap! friends conj result)
             (js/console.log "friends: " friends))))

