(ns smallworld.screens.home
  (:require [clojure.string :as str]
            [goog.dom :as dom]
            [reagent.core           :as r]
            [smallworld.decorations :as decorations]
            [smallworld.mapbox      :as mapbox]
            [smallworld.session     :as session]
            [smallworld.screens.settings :as settings]
            [smallworld.user-data   :as user-data]
            [smallworld.util        :as util]))

(def         *debug? (r/atom false))
(def *settings-open? (r/atom false))
(defonce   *minimaps (r/atom {}))

(defn nav []
  [:div.nav {:class (when (:impersonation? @session/*store) "admin-impersonation")}
   [:a#logo-animation.logo {:on-click #(reset! *settings-open? false)}
    (decorations/animated-globe)

    [:div.logo-text "small world"]]
   [:span.fill-nav-space]
   [:a {:on-click #(reset! *settings-open? true) :style {:cursor "pointer"}}
    [:b.screen-name " @" (:screen-name @session/*store)]]])

(defn minimap [minimap-id location-name coords]
  (r/create-class {:component-did-mount
                   (fn [] ; this should be called just once when the component is mounted
                     (swap! *minimaps assoc minimap-id
                            (new js/mapboxgl.Map
                                 #js{:container minimap-id
                                     :key    (get-in mapbox/config [mapbox/style :access-token])
                                     :style  (get-in mapbox/config [mapbox/style :style])
                                     :center (clj->js [(:lng coords) (:lat coords)])
                                     :interactive false ; makes the map not zoomable or draggable
                                     :attributionControl false ; removes the Mapbox copyright symbol
                                     :zoom 1
                                     :maxZoom 8
                                     :minZoom 0}))
                     ; zoom out if they haven't provided a location
                     (when (clojure.string/blank? location-name)
                       (.setZoom (get @*minimaps minimap-id) 0)))
                   :reagent-render (fn [] [:div {:id minimap-id}])}))

(defn debugger-btn []
  (when (= (.. js/window -location -hash) "#debug")
    [:p {:style {:text-align "center"}}
     [:a {:on-click #(reset! *debug? (not @*debug?)) :href "#" :style {:border-bottom "2px solid #ffffff33"}}
      "toggle debug – currently " (if @*debug? "on 🟢" "off 🔴")]]))

(defn debugger-info []
  (when @*debug?
    [:<> [:br] [:br] [:hr]
     [:div.refresh-friends {:style {:margin-top "64px" :text-align "center"}}
      [:a.btn {:href "#"
               :on-click #(util/fetch "/api/v1/friends/refresh-atom"
                                      (fn [result]
                                        (reset! user-data/*friends result)
                                        (println settings/*settings)
                                        (js/setTimeout (mapbox/add-friends-to-map @user-data/*friends @settings/*settings) 2000))
                                      :retry? true)}
       "pull friends again (without refreshing from Twitter!)"]]

     [:br]
     [:div.refresh-friends {:style {:margin-top "64px" :text-align "center"}}
      [:div {:style {:margin-bottom "12px" :font-size "0.9em"}}
       "does the data for your friends look out of date?"]
      [:a.btn {:href "#" :on-click user-data/refresh-friends}
       "refresh friends"]
      [:div {:style {:margin-top "12px" :font-size "0.8em" :opacity "0.6" :font-family "Inria Serif, serif" :font-style "italic"}}
       "note: this takes several seconds to run"]]

     [:br]
     [:b "current-user:"]
     [:pre (util/preify @session/*store)]  [:br]
     [:b "settings:"]
     [:pre (util/preify @settings/*settings)] [:br]
     [:b "@user-data/*friends:"]
     (if (= @user-data/*friends :loading)
       [:pre "@user-data/*friends is still :loading"]
       [:pre "count:" (count @user-data/*friends) "\n\n" (util/preify @user-data/*friends)])]))

; TODO: lots of low-hanging fruit to improve performance
(defn fetch-coordinates! [minimap-id location-name-input i]
  (if (str/blank? location-name-input) ; don't fetch from the API if the input is blank
    (.flyTo (get @*minimaps minimap-id) #js{:essential true ; this animation is essential with respect to prefers-reduced-motion
                                            :zoom 0})
    (util/fetch-post "/api/v1/coordinates" {:location-name location-name-input}
                     (fn [result]
                       (.flyTo (get @*minimaps minimap-id)
                               #js{:essential true ; this animation is essential with respect to prefers-reduced-motion
                                   :zoom 1
                                   :center #js[(:lng result) (:lat result)]})
                       (when (and (:lat result) (:lng result))
                         (swap! settings/*settings assoc-in [:locations i :coords] result)
                         (user-data/recompute-friends
                          #(swap! settings/*settings assoc-in [:locations i :loading] false))
                         (util/fetch-post "/api/v1/settings/update" ; persist the changes to the server
                                          {:locations (:locations (assoc-in @settings/*settings [:locations i :loading] false))}))))))

(def fetch-coordinates-debounced! (util/debounce fetch-coordinates! 400))

(defn from-twitter? [location-data]
  (or (= (:special-status location-data) "twitter-location")
      (= (:special-status location-data) "from-display-name")))

(defn -screen []
  [:<>
   (nav)
   (let [curr-user-locations (remove nil? (:locations @settings/*settings))
         update [:a {:href "https://twitter.com/settings/location" :target "_blank"} "update"]
         track-new-location-btn [:div#track-new-location-field
                                 {:on-click (fn []
                                              (let [updated-locations (vec (concat curr-user-locations ; using concat instead of conj so it adds to the end
                                                                                   [{:special-status "added-manually"
                                                                                     :name "" ; the value starts out blank
                                                                                     :coords nil}]))]
                                                (swap! settings/*settings assoc :locations updated-locations)

                                                (js/setTimeout #(do
                                                                  (-> (last (util/query-dom ".friends-list input"))
                                                                      .focus)
                                                                  (-> (last (util/query-dom ".category"))
                                                                      (.scrollIntoView #js{:behavior "smooth" :block "center" :inline "center"})))
                                                               50)))}
                                 (decorations/plus-icon "scale(0.15)") "follow a new location"]]
     [:<>
      (if @*settings-open?
        (settings/settings-screen)

        [:div.home-page
         (let [top-location (first (remove nil? (:locations @settings/*settings)))]
           [util/error-boundary
            [mapbox/mapbox
             {:lng-lat  (:coords top-location)
              :location (:name top-location)
              :user-img (:profile_image_url_large @session/*store)
              :user-name (:name @session/*store)
              :screen-name (:screen-name @session/*store)}]])

         (if (= 0 (count curr-user-locations))
           [:div.no-locations-info
            [:p "3 ways to start following your first location:"]
            [:ul
             [:li update " your Twitter profile location"]
             [:li update " your Twitter display name (e.g. \"Devon in Miami Beach\")"]
             [:li "add a location manually:"]]
            track-new-location-btn]
           track-new-location-btn)

         (doall (map-indexed
                 (fn [i location-data]
                   (let [minimap-id (str "minimap-location-" i)]
                     [:div.category {:key i}
                      [:div.friends-list.header

                       [:div.left-side.mapbox-container
                        [minimap minimap-id (:name location-data) (:coords location-data)]
                        (when-not (str/blank? (:name location-data)) [:div.center-point])]

                       [:div.right-side
                        [:div.based-on (condp = (:special-status location-data)
                                         "twitter-location"  "based on your Twitter location, you live in:"
                                         "from-display-name" "based on your Twitter name, you're visiting:"
                                         nil)]
                        [:input {:type "text"
                                 :value (:name location-data)
                                 :autoComplete "off"
                                 :auto-complete "off"
                                 :style {:cursor (when-not (from-twitter? location-data) "pointer")}
                                 :readOnly (from-twitter? location-data) ; don't allow them to edit the Twitter locations
                                 :placeholder "type a location to follow"
                                 :on-change #(let [input-elem (.-target %)
                                                   new-value  (.-value input-elem)]
                                               (swap! settings/*settings assoc-in [:locations i :loading] true)
                                               (fetch-coordinates-debounced! minimap-id new-value i)
                                               (swap! settings/*settings assoc-in [:locations i :name] new-value)
                                               (util/fetch-post "/api/v1/settings/update" {:locations (:locations @settings/*settings)}))}]
                        #_(when (from-twitter? location-data) ; no longer needed because they aren't editable
                            [:div.small-info-text "this won't update your Twitter profile :)"])]

                       [:div.delete-location-btn {:title "delete this location"
                                                  :on-click #(when (js/confirm "are you sure that you want to delete this location?  don't worry, you can add it back later any time")
                                                               (let [updated-locations (util/rm-from-list curr-user-locations i)]
                                                                 (println "settings/*settings :locations (BEFORE)")
                                                                 (println (:locations @settings/*settings))
                                                                 (swap! settings/*settings assoc :locations updated-locations)
                                                                 (println "settings/*settings :locations (AFTER)")
                                                                 (println (:locations @settings/*settings))
                                                                 (util/fetch-post "/api/v1/settings/update" {:locations updated-locations})))}
                        (decorations/cancel-icon)]]

                      (if (or (get-in @settings/*settings [:locations i :loading]) (= [] @user-data/*friends))
                        [:div.friends-list [:div.loading (decorations/simple-loading-animation) "fetching your Twitter friends..."]]
                        (when-not (nil? (:coords location-data))
                          [:<>
                           (user-data/render-friends-list i "from-display-name" "visiting"   (:name location-data))
                           (user-data/render-friends-list i "twitter-location"  "based near" (:name location-data))]))]))
                 curr-user-locations))

         (when (not= 0 (count curr-user-locations))
           track-new-location-btn)

         [:br]
         (debugger-btn)

         (util/info-footer (:screen-name @session/*store)
                           user-data/recompute-friends) ; TODO: replace with doseq, which is for side effects
         (debugger-info)])])])

(defn screen []
  (r/create-class
   {:component-did-mount
    (fn []
      (settings/refresh-friends-atom) ; refresh immediately
      (doall (for [i (range 1 10)] ; then refresh it again, with exponential backoff
               (js/setTimeout settings/refresh-friends-atom (* (util/exponent 2 i) 1000)))))
    :reagent-render (fn [] [-screen])}))
