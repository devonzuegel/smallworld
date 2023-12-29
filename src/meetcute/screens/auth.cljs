(ns meetcute.screens.auth
  (:require [reagent.core :as r]
            [goog.dom :as dom]
            [clojure.string :as str]
            [smallworld.util :as util]))

;; ============================================================================
;; Helper components

(defn css-spinner []
  (let [speed 1 ; lower is faster
        rotation (r/atom 0)]
    (js/setInterval #(swap! rotation + 1) speed)
    (fn []
      [:div {:style {:width "20px"
                     :height "20px"
                     :margin "auto" ; center the spinner
                     :margin-top "80px"
                     :border "6px solid rgba(0, 0, 0, 0.1)"
                     :border-top-color "white"
                     :border-radius "50%"
                     :transform (str "rotate(" @rotation "deg)")}
             :key @rotation}])))

(defn loading-iframe [src]
  (let [loading? (r/atom true)] ; State to track loading status
    (fn []
      [:div {:style {:position "relative"
                     :align-self "center"
                     :width "100%"}}
       (when @loading? [css-spinner])
       [:iframe {:src src
                 :style {:display (if @loading? "none" "block")
                         :frameborder "0"
                         :onmousewheel ""
                         :border-radius "8px"
                         :width "100%"
                         :height (str (- (.-innerHeight (dom/getWindow)) 180) "px")}
                 :on-load #(reset! loading? false)}]])))


;; ============================================================================
;; State

(defonce phone (r/atom "(111) 111-1111"))

(def phone-input-error (r/atom nil))

;; ============================================================================
;; Screens

(defn signup-screen [{:keys [to-signin]}]
  [:div  {:style {:display "flex" :flex-direction "column" :height "100vh"
                  :align-items "center" ; center horizontally
                  :font-family "sans-serif" :font-size "1.2em" :line-height "1.6em" :text-align "center" :overflow "hidden" :padding "0 12px"
                  :vertical-align "top" ; vertically align flex items to the top, make them stick to the top even if they don't take the whole height
                  ; TODO:: this flexbox and its contents should resize when the page size changes
                  }}
   [:div {:style {:padding-top "36px" :padding-bottom "36px"}}
    [:h1 {:style {:font-size 48 :line-height "1.6em"}} "Sign up"]
    [:p
     "Already have an account? "
     [:a {:on-click (fn [_] (to-signin))
          :href "#"}
      "Sign in"]]]
   [:div {:style {:width "100%"}}
    [:script {:src "https://static.airtable.com/js/embed/embed_snippet_v1.js"}]
    [loading-iframe "https://airtable.com/embed/appF2K8ThWvtrC6Hs/shrdeJxeDgrYtcEe8"]]])
