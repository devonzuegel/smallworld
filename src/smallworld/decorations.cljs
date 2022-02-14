(ns smallworld.decorations
  (:require [smallworld.mapbox]
            [cljsjs.mapbox]
            [goog.dom]
            [reagent.core :as r]
            [goog.dom.classlist :as gc]))

(def debug? false)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; simple spinner ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn simple-loading-animation []
  [:svg.loader
   [:path {:fill "#fff"
           :d "M73,50c0-12.7-10.3-23-23-23S27,37.3,27,50 M30.9,50c0-10.5,8.5-19.1,19.1-19.1S69.1,39.5,69.1,50"}]])

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; plane/globe animation ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defonce plane-animation-iterations (r/atom 0))

; this needs to get re-called in each function to get its fresh values
(defn get-animation-elem [] (goog.dom/getElement "logo-animation"))

(defn start-animation []
  (when debug? (println "start-animation"))
  (gc/remove (get-animation-elem) "no-animation")
  (reset! plane-animation-iterations 0))

(defn stop-animation []
  (when debug? (println "stop-animation:" @plane-animation-iterations))
  (swap! plane-animation-iterations inc)
  (when (>= @plane-animation-iterations 3)
    (gc/add (get-animation-elem) "no-animation")))

(defn animated-globe []
  (js/setTimeout #(let [elem (get-animation-elem)]
                    (.addEventListener elem "mouseover" start-animation)
                    (.addEventListener elem "webkitAnimationIteration" stop-animation) ;; for Chrome
                    (.addEventListener elem "animationiteration" stop-animation) ;; for Firefox
                    (.addEventListener elem "MSAnimationIteration" stop-animation) ;; for IE
                    (.addEventListener elem "animationiteration" stop-animation)
                    (when debug? (println "adding listeners to elem")))
                 1000) ; give time to load the animation
  [:div {:class "globe-loader fas fa-globe-americas"} [:i.fas.fa-plane]])
