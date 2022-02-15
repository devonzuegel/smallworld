(ns smallworld.decorations
  (:require [smallworld.mapbox]
            [cljsjs.mapbox]
            [goog.dom]
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
