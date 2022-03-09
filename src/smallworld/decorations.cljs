(ns smallworld.decorations
  (:require [goog.dom]
            [reagent.core :as r]
            [goog.dom.classlist :as gc]))

(def debug? false)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; icons ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn twitter-icon []
  [:svg.twitter-icon {:viewBox "0 0 24 24"}
   [:path {:d "M24 4.557c-.883.392-1.832.656-2.828.775 1.017-.609 1.798-1.574 2.165-2.724-.951.564-2.005.974-3.127 1.195-.897-.957-2.178-1.555-3.594-1.555-3.179 0-5.515 2.966-4.797 6.045-4.091-.205-7.719-2.165-10.148-5.144-1.29 2.213-.669 5.108 1.523 6.574-.806-.026-1.566-.247-2.229-.616-.054 2.281 1.581 4.415 3.949 4.89-.693.188-1.452.232-2.224.084.626 1.956 2.444 3.379 4.6 3.419-2.07 1.623-4.678 2.348-7.29 2.04 2.179 1.397 4.768 2.212 7.548 2.212 9.142 0 14.307-7.721 13.995-14.646.962-.695 1.797-1.562 2.457-2.549z"}]])

(defn edit-icon []
  [:svg.edit-icon {:viewBox "0 0 24 24"}
   [:path {:d "M14.078 4.232l-12.64 12.639-1.438 7.129 7.127-1.438 12.641-12.64-5.69-5.69zm-10.369 14.893l-.85-.85 11.141-11.125.849.849-11.14 11.126zm2.008 2.008l-.85-.85 11.141-11.125.85.85-11.141 11.125zm18.283-15.444l-2.816 2.818-5.691-5.691 2.816-2.816 5.691 5.689z"}]])

(defn info-icon []
  [:svg {:class "info" :view-box "0 0 16 16" :width "16px" :height "16px"}
   [:g [:path {:style {:transform "scale(0.25)"} :d "m32 2c-16.568 0-30 13.432-30 30s13.432 30 30 30 30-13.432 30-30-13.432-30-30-30m5 49.75h-10v-24h10v24m-5-29.5c-2.761 0-5-2.238-5-5s2.239-5 5-5c2.762 0 5 2.238 5 5s-2.238 5-5 5"}]]])

(defn location-icon []
  [:svg {:class "location" :view-box "0 0 6 6" :width "16px" :height "16px"}
   [:g [:path {:style {:transform "scale(0.25)"} :d "M12 0c-4.198 0-8 3.403-8 7.602 0 4.198 3.469 9.21 8 16.398 4.531-7.188 8-12.2 8-16.398 0-4.199-3.801-7.602-8-7.602zm0 11c-1.657 0-3-1.343-3-3s1.343-3 3-3 3 1.343 3 3-1.343 3-3 3z"}]]])

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
; #logo-animation is the id of the parent wrapper where this animation is placed
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
  (js/setTimeout #(gc/remove (goog.dom/getElement "little-plane") "hidden") 200)
  [:div {:class "globe-loader fas fa-globe-americas"}
   [:i.fas.fa-plane {:class "hidden" ; this will get removed after the timeout is completed
                     :id "little-plane"}]])
