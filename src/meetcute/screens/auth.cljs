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

(defn simple-iframe [{:keys [src loading?]}]
  [:iframe {:src src
            :style {:display (if @loading? "none" "block")
                    :frameborder "0"
                    :onmousewheel ""
                    :border-radius "8px"
                    :width "100%"
                    :height (str (- (.-innerHeight (dom/getWindow)) 180) "px")}
            :on-load #(reset! loading? false)}])

(defn loading-iframe [src]
  (let [loading? (r/atom true)] ; State to track loading status
    (fn []
      [:div {:style {:position "relative"
                     :align-self "center"
                     :width "100%"}}
       (when @loading? [css-spinner])
       (simple-iframe {:src src :loading? loading?})])))

