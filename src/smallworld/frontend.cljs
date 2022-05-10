(ns smallworld.frontend
  (:require [reagent.core            :as r]
            [smallworld.session      :as session]
            [smallworld.decorations  :as decorations]
            [smallworld.screens.settings     :as settings]
            [smallworld.util         :as util]
            [smallworld.screens.home :as home]
            [clojure.pprint          :as pp]
            [cljsjs.mapbox]
            [goog.dom]
            [smallworld.admin :as admin]))

(def *debug? (r/atom false))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(util/fetch "/api/v1/session" (fn [result]
                                (when @*debug?
                                  (println "/api/v1/session:")
                                  (pp/pprint result))
                                (session/update! result)))

(util/fetch "/api/v1/settings" (fn [result]
                                 (when @*debug?
                                   (println "/api/v1/settings:")
                                   (pp/pprint result))
                                 (reset! settings/*settings      result)
                                 (reset! settings/*email-address (:email_address result))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn logged-out-screen []
  [:div.welcome
   [:div.hero
    [:p.serif {:style {:font-size "1.5em" :margin-top "8px" :margin-bottom "4px"}}
     "welcome to"]
    [:h1 {:style {:font-weight "bold" :font-size "2.6em"}}
     "Small World"]
    [:div#logo-animation.logo (decorations/animated-globe)]
    [:h2
     [:a#login-btn {:href "login"} (decorations/twitter-icon) "log in with Twitter"]]]
   [:div.steps
    [:p [:b "step 1:"] " log in with Twitter"]
    [:p [:b "step 2:"] " update what city you're in"]
    [:p [:b "step 3:"] " see a map of who's nearby"]]
   [:div.info
    [:p "Small World uses the location from your" [:br]
     [:a {:href "https://twitter.com/settings/profile" :target "_blank"}
      "Twitter profile"] " to find nearby friends"]]
   #_[:div.faq
      [:div.question
       [:p [:b "Q: how does small world work?"]]
       [:p "small world checks to see if the people you follow on Twitter have updated their location.  it looks at two places:"]
       [:ul
        [:li "their display name, which small world interprets as they're traveling to that location"]
        [:li "their location, which small world interprets as they're living in that location"]]]

      [:hr]

      [:div.question
       [:p [:b "Q: why isn't my friend showing up"]]
       [:p "they may not have their location set on Twitter (either in their name or in the location field), or small world may not be able to parse the location yet."]
       [:p "if they have their location set but it's just not showing up in the app, please " [:a {:href "https://github.com/devonzuegel/smallworld"} "open a GitHub issue"] " and share more so I can improve the city parser."]]]])

(defn not-found-404-screen []
  [:p {:style {:margin "30vh auto 0 auto" :text-align "center" :font-size "2em"}}
   "404 not found"])

(defn app-container []
  (condp = (.-pathname (.-location js/window))
    "/" (condp = @session/*store
          :loading (decorations/loading-screen)
          session/blank (logged-out-screen)
          (if (= :loading @settings/*settings)
            (decorations/loading-screen)
            (if (:welcome_flow_complete @settings/*settings)
              (home/screen)
              (settings/welcome-flow-screen))))
    "/admin" (admin/summary-screen)
    (not-found-404-screen)))

(r/render-component
 (fn [] [util/error-boundary [app-container]])
 (goog.dom/getElement "app"))

