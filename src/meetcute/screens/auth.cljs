(ns meetcute.screens.auth
  (:require [reagent.core :as r]
            [goog.dom :as dom]
            [clojure.string :as str]
            [smallworld.util :as util]
            [meetcute.screens.styles :as mc.styles]))

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
                     :border "6px solid rgba(255, 255, 255, 0.1)"
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

(defn valid-phone [phone]
  ;; strip the phone number of all non-numeric characters, then check if it's a valid phone number. if yes, return true; if not, return false:
  (let [phone (or phone "")
        phone (str/replace phone #"[^0-9]" "")]
    (and (not-empty phone) (re-find #"^\d{10}$" phone))))

(defn request-sms-token! [phone]
  (println "phone" phone)
  (util/fetch-post "/meetcute/api/auth/signin" {:phone phone} (fn [data] (println "response" data))))

(defn signin []
  (if (valid-phone @phone)
    (do
      (reset! phone-input-error nil)
      (request-sms-token! @phone))
    (reset! phone-input-error "Please enter a valid phone number")))

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

(defn signin-screen [{:keys [to-signup]}]
  [:form {:method "post" :action "/meetcute/api/auth/signin"}
   [:div {:style {:margin-left "auto"
                  :margin-right "auto"
                  :width "90%"
                  :padding-top "48px"
                  :text-align "center"}}
    [:h1 {:style {:font-size 48 :line-height "1.6em" :margin-bottom "18px"}} "Sign in"]
    [:div {:style {:color "red" :min-height "1.4em" :margin-bottom "8px"}}
     @phone-input-error]
    [:label {:for "phone"}
     [:p {:style {:line-height "2.5em"}} "Your phone number:"]]
    [:input {:type "text"
             :name "phone"
             :value @phone
             :on-change #(reset! phone (-> % .-target .-value))
             :on-key-press #(when (= (.-key %) "Enter")
                              (signin))
             :style {:background "#ffffff22" :border-radius "8px" :padding "6px 8px" :margin-right "4px"}}]
    [:div {:style {:margin-bottom "12px"}}]
    [:button {:style mc.styles/btn
              :type "submit"
            ;;   :on-click signin
              }
     "Sign in"]
    [:a {:on-click (fn [_] (to-signup))
         :style {:margin-left "12px" :margin-right "12px"}
         :href "#"}
     "Sign up"]]])
