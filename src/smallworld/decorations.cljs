(ns smallworld.decorations
  (:require [goog.dom]
            [reagent.core :as r]
            [smallworld.admin :as admin]
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

(defn question-icon []
  [:svg {:class "question" :view-box "0 0 18 18" :width "22px" :height "22px"}
   [:g [:path {:style {:transform "scale(.75)"} :d "M14.601 21.5c0 1.38-1.116 2.5-2.499 2.5-1.378 0-2.499-1.12-2.499-2.5s1.121-2.5 2.499-2.5c1.383 0 2.499 1.119 2.499 2.5zm-2.42-21.5c-4.029 0-7.06 2.693-7.06 8h3.955c0-2.304.906-4.189 3.024-4.189 1.247 0 2.57.828 2.684 2.411.123 1.666-.767 2.511-1.892 3.582-2.924 2.78-2.816 4.049-2.816 7.196h3.943c0-1.452-.157-2.508 1.838-4.659 1.331-1.436 2.986-3.222 3.021-5.943.047-3.963-2.751-6.398-6.697-6.398z"}]]])

(defn fullscreen-icon []
  [:svg {:class "fullscreen" :view-box "0 0 18 18" :width "22px" :height "22px"}
   [:g [:path {:style {:transform "scale(.75)"} :d "M10 5h-3l5-5 5 5h-3v3h-4v-3zm4 14h3l-5 5-5-5h3v-3h4v3zm-9-5v3l-5-5 5-5v3h3v4h-3zm14-4v-3l5 5-5 5v-3h-3v-4h3z"}]]])

(defn minimize-icon []
  [:svg {:class "fullscreen" :view-box "0 0 18 18" :width "22px" :height "22px"}
   [:g [:path {:style {:transform "scale(.75)"} :d "M14 3h3l-5 5-5-5h3v-3h4v3zm-4 18h-3l5-5 5 5h-3v3h-4v-3zm-7-11v-3l5 5-5 5v-3h-3v-4h3zm18 4v3l-5-5 5-5v3h3v4h-3z"}]]])

(defn location-icon []
  [:svg {:class "location" :view-box "0 0 6 6" :width "16px" :height "16px"}
   [:g [:path {:style {:transform "scale(0.25)"} :d "M12 0c-4.198 0-8 3.403-8 7.602 0 4.198 3.469 9.21 8 16.398 4.531-7.188 8-12.2 8-16.398 0-4.199-3.801-7.602-8-7.602zm0 11c-1.657 0-3-1.343-3-3s1.343-3 3-3 3 1.343 3 3-1.343 3-3 3z"}]]])

(defn triangle-icon [& [classes]] ; a.k.a. caret
  [:svg {:class (str "triangle " classes) :view-box "0 0 6 6" :width "16px" :height "16px"}
   [:g [:path {:style {:transform "scale(0.25)"} :d "M23.677 18.52c.914 1.523-.183 3.472-1.967 3.472h-19.414c-1.784 0-2.881-1.949-1.967-3.472l9.709-16.18c.891-1.483 3.041-1.48 3.93 0l9.709 16.18z"}]]])

(defn zoom-in-icon []
  [:svg {:class "zoom-in" :view-box "0 0 7 7" :width "22px" :height "22px"}
   [:g [:path {:style {:transform "scale(0.25)"} :d "M13 10h-3v3h-2v-3h-3v-2h3v-3h2v3h3v2zm8.172 14l-7.387-7.387c-1.388.874-3.024 1.387-4.785 1.387-4.971 0-9-4.029-9-9s4.029-9 9-9 9 4.029 9 9c0 1.761-.514 3.398-1.387 4.785l7.387 7.387-2.828 2.828zm-12.172-8c3.859 0 7-3.14 7-7s-3.141-7-7-7-7 3.14-7 7 3.141 7 7 7z"}]]])

(defn zoom-out-icon []
  [:svg {:class "zoom-out" :view-box "0 0 7 7" :width "22px" :height "22px"}
   [:g [:path {:style {:transform "scale(0.25)"} :d "M13 10h-8v-2h8v2zm8.172 14l-7.387-7.387c-1.388.874-3.024 1.387-4.785 1.387-4.971 0-9-4.029-9-9s4.029-9 9-9 9 4.029 9 9c0 1.761-.514 3.398-1.387 4.785l7.387 7.387-2.828 2.828zm-12.172-8c3.859 0 7-3.14 7-7s-3.141-7-7-7-7 3.14-7 7 3.141 7 7 7z"}]]])

(defn x-icon []
  [:svg {:class "x" :view-box "0 0 6 6" :width "16px" :height "16px"}
   [:g [:path {:style {:transform "scale(0.25)"} :d "M24 20.188l-8.315-8.209 8.2-8.282-3.697-3.697-8.212 8.318-8.31-8.203-3.666 3.666 8.321 8.24-8.206 8.313 3.666 3.666 8.237-8.318 8.285 8.203z"}]]])

(defn plus-icon [& [transform]]
  [:svg {:class "plus" :view-box "0 0 6 6" :width "16px" :height "16px"}
   [:g [:path {:style {:transform (or transform "scale(0.25)")} :d "M24 9h-9v-9h-6v9h-9v6h9v9h6v-9h9z"}]]])

(defn cancel-icon [& [transform]]
  [:svg {:class "cancel-icon" :view-box "0 0 6 6" :width "16px" :height "16px"}
   [:g [:path {:style {:transform (or transform "scale(0.25)")} :d "M12 0c-6.627 0-12 5.373-12 12s5.373 12 12 12 12-5.373 12-12-5.373-12-12-12zm4.151 17.943l-4.143-4.102-4.117 4.159-1.833-1.833 4.104-4.157-4.162-4.119 1.833-1.833 4.155 4.102 4.106-4.16 1.849 1.849-4.1 4.141 4.157 4.104-1.849 1.849z"}]]])

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; simple spinner ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn simple-loading-animation []
  [:svg.loader
   [:path {:fill "#fff"
           :d "M73,50c0-12.7-10.3-23-23-23S27,37.3,27,50 M30.9,50c0-10.5,8.5-19.1,19.1-19.1S69.1,39.5,69.1,50"}]])

(defn loading-screen []
  [:div.center-vh (simple-loading-animation)])

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

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; footer ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;


(defn info-footer [screen-name recompute-friends]
  [:div.info-footer
   [:a {:href "https://devonzuegel.com" :target "_blank"} "made in Miami Beach"]
   [:span.dot-separator " · "]
   [:a {:href "https://github.com/devonzuegel/smallworld/issues" :target "_blank"} "report a bug"]
   (when (admin/is-admin {:screen-name screen-name})
     [:<> [:span.dot-separator " · "]
      [:a {:href "#" :on-click recompute-friends} "recompute locations (admin only)"]])])

