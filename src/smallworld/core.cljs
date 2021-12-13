(ns smallworld.core
  (:require [reagent.core :as r]
            [goog.dom]))

(def by-id goog.dom.getElement)

(defn app-container []
  [:h1 {}
   "hello small world!"])

(r/render-component [app-container] (by-id "app"))
