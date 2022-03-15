(ns smallworld.screens.home
  (:require [reagent.core           :as r]
            [smallworld.decorations :as decorations]
            [smallworld.settings    :as settings]
            [smallworld.mapbox      :as mapbox]
            [smallworld.user-data   :as user-data]
            [smallworld.util        :as util]
            [smallworld.session     :as session]))

(def     *debug?    (r/atom false))
(defonce *locations (r/atom :loading))
(defonce *minimaps  (r/atom {}))

; TODO: only do this on first load of logged-in-screen, not on every re-render
; and not for all the other pages – use component-did-mount
(util/fetch "/friends" (fn [result]
                         (reset! user-data/*friends result)
                         ; TODO: only run this on the main page, otherwise you'll get errors
                         ; wait for the map to load – this is a hack & may be a source of errors ;)
                         (js/setTimeout (mapbox/add-friends-to-map @user-data/*friends @session/store*)
                                        2000))
            :retry? true)

(defn nav []
  [:div.nav
   [:a#logo-animation.logo {:href "/"}
    (decorations/animated-globe)

    [:div.logo-text "small world"]]
   [:span.fill-nav-space]
   [:b.screen-name " @" (:screen-name @session/store*)]])

(defn minimap [minimap-id location-name]
  (r/create-class {:component-did-mount
                   (fn [] ; this should be called just once when the component is mounted
                     (swap! *minimaps assoc minimap-id
                            (new js/mapboxgl.Map
                                 #js{:container minimap-id
                                     :key    (get-in mapbox/config [mapbox/style :access-token])
                                     :style  (get-in mapbox/config [mapbox/style :style])
                                     :center (clj->js mapbox/Miami) ; TODO: center on location they provide to Twitter
                                     :interactive false ; makes the map not zoomable or draggable
                                     :attributionControl false ; removes the Mapbox copyright symbol
                                     :zoom 3
                                     :maxZoom 8
                                     :minZoom 0}))
                     ; zoom out if they haven't provided a location
                     (when (clojure.string/blank? location-name)
                       (.setZoom (get @*minimaps minimap-id) 0)))
                   :reagent-render (fn [] [:div {:id minimap-id}])}))

(defn screen []
  [:<>
   (nav)
   (let [main-location (or (:main_location_corrected @settings/*settings) (:main-location @session/store*))
         name-location (or (:name_location_corrected @settings/*settings) (:name-location @session/store*))]
     [:<>
      [:div.home-page
       #_[:div.current-user [user-data/render-user nil @session/store*]] ; TODO: cleanup

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

        (when-not (empty? main-location)
          [:div.category
           [:div.friends-list.header
            [:div.left-side.mapbox-container {:style {:width "90px"}}
             [minimap "main-location-map" main-location]
             (when-not (clojure.string/blank? main-location) [:div.center-point])]
            [:div.right-side
             [:div.based-on "based on your location, you live in:"]
             [:input {:type "text"
                      :value main-location
                      :autoComplete "off"
                      :auto-complete "off"
                      :on-change #(print "TODO:")}]
             [:div.small-info-text "don't worry, this won't update your Twitter profile :)"]]]
           (user-data/render-friends-list :main-main "living near" main-location)
           (user-data/render-friends-list :main-name "visiting"    main-location)])

        (when-not (empty? name-location)
          [:div.category
           [:div.friends-list.header
            [:div.left-side.mapbox-container {:style {:width "90px"}}
             [minimap "name-location-map" name-location]
             (when-not (clojure.string/blank? name-location) [:div.center-point])]
            [:div.right-side
             [:div.based-on "based on your name, you live in:"]
             [:input {:type "text"
                      :value name-location
                      :autoComplete "off"
                      :auto-complete "off"
                      :on-change #(print "TODO:")}]
             [:div.small-info-text "don't worry, this won't update your Twitter profile :)"]]]
           (user-data/render-friends-list :name-main "living near" name-location)
           (user-data/render-friends-list :name-name "visiting"    name-location)])

        (when (= :loading @user-data/*friends)
          [:pre {:style {:margin "24px auto" :max-width "360px"}}
           "🚧  this wil take a while to load, apologies.  I'm working on "
           "making it faster.  thanks for being Small World's first user!"])

        (when (not= :loading @user-data/*friends)
          [:<>
           [:br]
           [:div.twitter-data-explanation
            [:div.twitter-data
             [:img {:src (:profile_image_url_large @session/store*)}]
             [:div
              [:div.name     (:name @session/store*)]
              [:div.location (:main_location_corrected @settings/*settings)]]]
            [:div.explanation
             (decorations/twitter-icon)
             [:span "Small World looks at the name & location on your "
              [:a {:href "https://twitter.com/settings/profile" :target "_blank"} "Twitter profile"]
              " to find nearby friends"]]]])

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
        [util/error-boundary
         [mapbox/mapbox
          {:lng-lat (or (when name-coords [(:lng name-coords) (:lat name-coords)])
                        (when main-coords [(:lng main-coords) (:lat main-coords)]))
           :location (or (:name-location @session/store*)
                         (:main-location @session/store*))
           :user-img (:profile_image_url_large @session/store*)
           :user-name (:name @session/store*)
           :screen-name (:screen-name @session/store*)}]])])
   [:div.bug-report "report a bug"]])
