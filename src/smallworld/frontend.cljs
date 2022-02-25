(ns smallworld.frontend
  (:require [reagent.core           :as r]
            [smallworld.session     :as session]
            [smallworld.mapbox      :as mapbox]
            [smallworld.decorations :as decorations]
            [smallworld.settings    :as settings]
            [smallworld.util        :as util]
            [clojure.pprint         :as pp]
            [cljsjs.mapbox]
            [goog.dom]
            [smallworld.user-data :as user-data]))

(def *debug? (r/atom false))

(defn nav []
  [:div.nav
   [:a#logo-animation.logo {:href "/"}
    (decorations/animated-globe)

    [:div.logo-text "small world"]]
   [:span.fill-nav-space]
   [:a {:href "/logout"}
    "log out" [:b.screen-name " @" (:screen-name @session/store*)]]])

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

; TODO: only do this on first load of logged-in-screen, not on every re-render
; and not for all the other pages
(util/fetch "/friends" (fn [result]
                         (reset! user-data/*friends result)
                         ; TODO: only run this on the main page, otherwise you'll get errors
                         ; wait for the map to load – this is a hack & may be a source of errors ;)
                         (js/setTimeout (mapbox/add-friends-to-map @user-data/*friends)
                                        2000))
            :retry? true)

(util/fetch "/session" #(session/update! %))

(util/fetch "/settings" (fn [result]
                          (reset! settings/*settings      result)
                          (reset! settings/*email-address (:email result))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn loading-screen []
  [:div.center-vh (decorations/simple-loading-animation)])

(defn logged-out-screen []
  [:div.welcome
   [:div.hero
    [:h1 "welcome to" [:br] "Small World"]
    [:div#logo-animation.logo (decorations/animated-globe)]
    [:h2
     [:a#login-btn {:href "login"} (decorations/twitter-icon) "log in with Twitter"]]]
   [:div.steps
    [:p [:b "step 1:"] " log in with Twitter"]
    [:p [:b "step 2:"] " update what city you're in"]
    [:p [:b "step 3:"] " see a map of who's nearby"]]
   [:div.info
    [:p "Small World uses the location from your" [:br]
     [:a {:href "https://twitter.com/settings/profile"} "Twitter profile"] " to find nearby friends"]]
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

(defn logged-in-screen []
  [:<>
   (nav)
   (let [main-location (or (:main_location_corrected @settings/*settings) (:main-location @session/store*))
         name-location (or (:name_location_corrected @settings/*settings) (:name-location @session/store*))]
     [:<>
      [:div.container
       [:div.current-user (user-data/render-user nil @session/store*)]

       (when @*debug?
         [:br]
         [:pre (util/preify @settings/*settings)])

       [:<>
        (when (and (empty? main-location)
                   (empty? name-location))
          [:div.no-locations-info
           ; TODO: improve the design of this state
           [:p "you haven't shared a location on Twitter, so we can't show you friends who are close by"]
           [:br]
           [:p "we pull from two places to find your location:"]
           [:ol
            [:li "your Twitter profile location"]
            [:li "your Twitter display name"]]
           [:br]
           [:p "update these fields in your " [:a {:href "https://twitter.com/settings/location"} "Twitter settings"]]
           [:br]
           [:p "if you don't want to update your Twitter settings, you can still explore the map below"]])

        (when (not= :loading @user-data/*friends)
          [:div.category {:style {:line-height "1.5em" :padding "12px 6px"}}
           [:div.you-are-following-count-info
            [:p "you follow " [:b (count @user-data/*friends)] " people on Twitter:"]
            [:ul
             [:li
              "if they're near the location you set on your Twitter profile or "
              "in your display name, they'll show up in the lists below"]
             [:li "if they've shared a location, they'll show up on the map"]]]])

        (when-not (empty? main-location)
          [:div.category
           [:span.current-user-location main-location]
           (user-data/render-friends-list :main-main "living near" main-location)
           (user-data/render-friends-list :main-name "visiting"    main-location)])

        (when-not (empty? name-location)
          [:div.category
           [:span.current-user-location name-location]
           (user-data/render-friends-list :name-main "living near" name-location)
           (user-data/render-friends-list :name-name "visiting"    name-location)])

        (when (= :loading @user-data/*friends)
          [:pre {:style {:margin "24px auto" :max-width "360px"}}
           "🚧  this wil take a while to load, apologies.  I'm working on "
           "making it faster.  thanks for being Small World's first user!"])

        (when @*debug?
          [:div.refresh-friends {:style {:margin-top "64px" :text-align "center"}}
           [:div {:style {:margin-bottom "12px" :font-size "0.9em"}}
            "does the data for your friends look out of date?"]
           [:a.btn {:href "#" :on-click user-data/refresh-friends}
            "refresh friends"]
           [:div {:style {:margin-top "12px" :font-size "0.8em" :opacity "0.6" :font-family "Inria Serif, serif" :font-style "italic"}}
            "note: this takes several seconds to run"]])

        (when @*debug?
          [:<>
           [:br]
           [:pre "@current-user:\n\n"  (util/preify @session/store*)]
           [:br]
           (if (= @user-data/*friends :loading)
             [:pre "@user-data/*friends is still :loading"]
             [:pre "@user-data/*friends (" (count @user-data/*friends) "):\n\n" (util/preify @user-data/*friends)])])

        [:br] [:br] [:br]
        [:p {:style {:text-align "center"}}
         [:a {:on-click #(reset! *debug? (not @*debug?)) :href "#" :style {:border-bottom "2px solid #ffffff33"}}
          "toggle debug – currently " (if @*debug? "on 🟢" "off 🔴")]]]]

      (let [main-coords (:main-coords @session/store*)
            name-coords (:name-coords @session/store*)]
        (mapbox/mapbox
         {:lng-lat (or (when name-coords [(:lng name-coords) (:lat name-coords)])
                       (when main-coords [(:lng main-coords) (:lat main-coords)]))
          :location (or name-location
                        main-location)
          :user-img (:profile_image_url_large @session/store*)
          :user-name (:name @session/store*)
          :screen-name (:screen-name @session/store*)}))])])

(defonce admin-summary* (r/atom :loading))
(defn admin-screen [] ; TODO: fetch admin data on screen load – probably needs react effects to do it properly
  [:div.admin-screen
   (if-not (= "devonzuegel" (:screen-name @session/store*))

     (if (= :loading @session/store*)
       (loading-screen)
       [:p {:style {:margin "30vh auto 0 auto" :text-align "center" :font-size "2em"}}
        "whoops, you don't have access to this page"])

     [:<>
      [:a.btn {:href "#"
               :on-click #(util/fetch "/admin-summary" (fn [result]
                                                         (pp/pprint result)
                                                         (reset! admin-summary* result)))}
       "load admin data"]
      [:br] [:br] [:br]
      (when (not= :loading @admin-summary*)
        (map (fn [key] [:details {:open false} [:summary [:b key]]
                        [:pre "count: " (count (get @admin-summary* key))]
                        [:pre "keys: " (util/preify (map #(or (:request_key %) (:screen_name %))
                                                         (get @admin-summary* key)))]
                        [:pre {:id key} (util/preify (get @admin-summary* key))]])
             (reverse (sort (keys @admin-summary*)))))])])

(defn not-found-404-screen []
  [:p {:style {:margin "30vh auto 0 auto" :text-align "center" :font-size "2em"}}
   "404 not found"])

(defn app-container []
  (condp = (.-pathname (.-location js/window))
    "/" (condp = @session/store*
          :loading (loading-screen)
          session/blank (logged-out-screen)
          (if (= :loading @settings/*settings)
            (loading-screen)
            (if (:welcome_flow_complete @settings/*settings)
              (logged-in-screen)
              (settings/welcome-flow-screen))))
    "/admin" (admin-screen)
    (not-found-404-screen)))

(r/render-component [app-container] (goog.dom/getElement "app"))

