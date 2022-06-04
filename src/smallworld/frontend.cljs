
(ns smallworld.frontend
  (:require [reagent.core                :as r]
            [reitit.frontend             :as rf]
            [reitit.frontend.easy        :as rfe]
            [reitit.frontend.controllers :as rfc]
            [reitit.ring                 :as ring]
            [reitit.coercion.schema      :as rsc]
            [schema.core                 :as s]
            [clojure.test                :refer [deftest is]]
            [fipp.edn                    :as fedn]
            [smallworld.session          :as session]
            [smallworld.decorations      :as decorations]
            [smallworld.screens.settings :as settings]
            [smallworld.util             :as util]
            [smallworld.screens.home     :as home]
            [clojure.pprint              :as pp]
            [cljsjs.mapbox]
            [goog.dom]
            [smallworld.admin :as admin]))

(def *debug? (r/atom false))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(util/fetch "/api/v1/settings" (fn [result]
                                 (when @*debug?
                                   (println "/api/v1/settings:")
                                   (pp/pprint result))
                                 (reset! settings/*settings      result)
                                 (reset! settings/*email-address (:email_address result))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn signin-page []
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

(defn home-page []
  (if (:welcome_flow_complete @settings/*settings)
    [home/screen]
    [settings/welcome-flow-screen]))

(defn not-found-404-page []
  [:p {:style {:margin "30vh auto 0 auto" :text-align "center" :font-size "2em"}}
   "404 not found"])

(defonce match (r/atom nil))

(defn redirect! [path]
  (.replace (.-location js/window) path))

(defn current-page [] ; TODO: handle logged out state
  (if (= :loading @session/*store)
    (decorations/loading-screen)
    (if (nil? @match)
      not-found-404-page
      (let [view (:view (:data @match))]
        [view @match]))))

(defn if-session-loading [next-steps-fn]
  #(if (= :loading @session/*store)
     (util/fetch "/api/v1/session" next-steps-fn)
     (next-steps-fn @session/*store)))

(def require-blank-session
  [{:start (if-session-loading #(if (empty? %)
                                  (session/update! %)
                                  (redirect! "/")))}])

(def require-session
  [{:start (if-session-loading #(if (empty? %)
                                  (redirect! "/signin")
                                  (session/update! %)))}])

(def require-admin
  [{:start (if-session-loading #(when (not= admin/screen-name (:screen-name %))
                                  (redirect! "/not-found")))}])

(def routes
  (rf/router
   ["/"
    ["signin"   {:name ::signin    :view signin-page     :controllers require-blank-session}]
    [""         {:name ::home      :view home-page       :controllers require-session}]
    ["settings" {:name ::settings  :view settings/screen :controllers require-session}]
    ["admin"    {:name ::admin     :view admin/screen    :controllers require-admin}]]
   {:data {:coercion rsc/coercion}}))

(deftest test-routes ; note – this will not get run at the same time as the clj tests
  (is (=    (rf/match-by-path routes "/no-match")  nil))
  (is (not= (rf/match-by-path routes "/settings")  nil))
  (is (not= (rf/match-by-path routes "/settings/") nil)))

(defn init! []
  (rfe/start!
   routes
   (fn [new-match]
     (swap! match (fn [old-match]
                    (when new-match
                      (assoc new-match :controllers (rfc/apply-controllers (:controllers old-match) new-match)))))
     (util/fetch "/api/v1/session" session/update!))
   {:use-fragment false})
  (r/render [current-page] (.getElementById js/document "app")))

(init!)