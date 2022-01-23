(ns smallworld.decorations
  (:require [reagent.core :as r]
            [smallworld.current-user :as cu]
            [smallworld.mapbox]
            [clj-fuzzy.metrics :as fuzzy]
            [clojure.pprint :as pp]
            ;; [clojure.string :as str]
            [cljsjs.mapbox]
            [goog.dom]
            [goog.dom.classlist :as gc]))

(defn animated-globe []
  (let [handle-hover (fn [] (let [elem (goog.dom/getElement "logo-animation")
                                  start-animation #(gc/remove elem "no-animation")
                                  stop-animation #(gc/add elem "no-animation")
                                  stop-after-iteration #((.addEventListener elem "webkitAnimationIteration" stop-animation) ;; for Chrome
                                                         (.addEventListener elem "animationiteration" stop-animation) ;; for Firefox
                                                         (.addEventListener elem "MSAnimationIteration" stop-animation) ;; for IE
                                                         (.addEventListener elem "animationiteration" stop-animation))]
                              (.addEventListener elem "mouseover" start-animation)
                              (.addEventListener elem "mouseout" stop-after-iteration)))]
    ;; give time to load the animation
    (js/setTimeout handle-hover 1000))
  [:div {:class "globe-loader fas fa-globe-americas"} [:i.fas.fa-plane]])

(defn simple-loading-animation []
  [:svg.loader
   [:path {:fill "#fff"
           :d "M73,50c0-12.7-10.3-23-23-23S27,37.3,27,50 M30.9,50c0-10.5,8.5-19.1,19.1-19.1S69.1,39.5,69.1,50"}]])
