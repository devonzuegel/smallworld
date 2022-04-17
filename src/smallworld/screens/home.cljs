(ns smallworld.screens.home
  (:require [clojure.string :as str]
            [goog.dom :as dom]
            [reagent.core           :as r]
            [smallworld.decorations :as decorations]
            [smallworld.mapbox      :as mapbox]
            [smallworld.session     :as session]
            [smallworld.settings    :as settings]
            [smallworld.user-data   :as user-data]
            [smallworld.util        :as util]))

(def     *debug?    (r/atom false))
(defonce *minimaps  (r/atom {}))

; TODO: only do this on first load of logged-in-screen, not on every re-render
; and not for all the other pages – use component-did-mount
(util/fetch "/friends" (fn [result]
                         (reset! user-data/*friends result)
                         ; TODO: only run this on the main page, otherwise you'll get errors
                         ; wait for the map to load – this is a hack & may be a source of errors ;)
                         (js/setTimeout (mapbox/add-friends-to-map @user-data/*friends @session/*store)
                                        2000))
            :retry? true)

(defn nav []
  [:div.nav
   [:a#logo-animation.logo {:href "/"}
    (decorations/animated-globe)

    [:div.logo-text "small world"]]
   [:span.fill-nav-space]
   [:b.screen-name " @" (:screen-name @session/*store)]])

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

; TODO: lots of low-hanging fruit to improve performance
(defn fetch-coordinates! [minimap-id location-name-input i]
  (if (str/blank? location-name-input) ; don't fetch from the API if the input is blank
    (.flyTo (get @*minimaps minimap-id) #js{:essential true ; this animation is essential with respect to prefers-reduced-motion
                                            :zoom 0})
    (util/fetch-post "/coordinates" {:location-name location-name-input}
                     (fn [result]
                       (.flyTo (get @*minimaps minimap-id)
                               #js{:essential true ; this animation is essential with respect to prefers-reduced-motion
                                   :zoom 1
                                   :center #js[(:lng result) (:lat result)]})
                       (when (and (:lat result) (:lng result))
                         (swap! session/*store assoc-in [:locations i :coords] result)
                         (user-data/recompute-friends
                          #(swap! session/*store assoc-in [:locations i :loading] false))
                         (util/fetch-post "/settings/update" ; persist the changes to the server
                                          {:locations (:locations (assoc-in @session/*store [:locations i :loading] false))}))))))

(def fetch-coordinates-debounced! (util/debounce fetch-coordinates! 300))

(defn from-twitter? [location-data]
  (or (= (:special-status location-data) "twitter-location")
      (= (:special-status location-data) "from-display-name")))

(defn screen []
  [:<>
   (nav)
   (let [curr-user-locations (remove nil? (:locations @session/*store))]
     [:<>
      [:div.home-page

       [:div ; this div is here to allow for the alignment of the footer
        [:br]
        [:pre (util/preify (:locations @session/*store))]
        [:br]
        (when (= 0 (count curr-user-locations))
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
                                        "you added this location manually:")]
                       [:input {:type "text"
                                :value (:name location-data)
                                :autoComplete "off"
                                :auto-complete "off"
                                :style {:cursor (when-not (from-twitter? location-data) "pointer")}
                                :readOnly (from-twitter? location-data) ; don't allow them to edit the Twitter locations
                                :placeholder "type a location to follow"
                                :on-change #(let [input-elem (.-target %)
                                                  new-value  (.-value input-elem)]
                                              (swap! session/*store assoc-in [:locations i :loading] true)
                                              (fetch-coordinates-debounced! minimap-id new-value i)
                                              (swap! session/*store assoc-in [:locations i :name] new-value))}]
                       #_(when (from-twitter? location-data) ; no longer needed because they aren't editable
                           [:div.small-info-text "this won't update your Twitter profile :)"])]

                      [:div.delete-location-btn {:title "delete this location"
                                                 :on-click #(when (js/confirm "are you sure you want to delete this location?")
                                                              (let [updated-locations (util/rm-from-list curr-user-locations i)]
                                                                (swap! session/*store assoc :locations updated-locations)
                                                                (util/fetch-post "/settings/update" {:locations updated-locations})))}
                       (decorations/cancel-icon)]]

                     (if (get-in @session/*store [:locations i :loading])
                       [:div.friends-list [:div.loading (decorations/simple-loading-animation)]]
                       (when-not (nil? (:coords location-data))
                         [:<>
                          (user-data/render-friends-list i "twitter-location"  "based near" (:name location-data))
                          (user-data/render-friends-list i "from-display-name" "visiting"   (:name location-data))]))]))
                curr-user-locations))
        [:br]
        [:div#track-new-location-field
         {:on-click (fn []
                      (let [updated-locations (vec (concat curr-user-locations ; using concat instead of conj so it adds to the end
                                                           [{:special-status "added-manually"
                                                             :name "" ; the value starts out blank
                                                             :coords nil}]))]
                        (swap! session/*store assoc :locations updated-locations)

                        (js/setTimeout #(do
                                          (-> (last (util/query-dom ".friends-list input"))
                                              .focus)
                                          (-> (last (util/query-dom ".category"))
                                              (.scrollIntoView #js{:behavior "smooth" :block "center" :inline "center"})))
                                       50)))}
         (decorations/plus-icon "scale(0.15)") "follow another location"]

        (when (= :loading @user-data/*friends)
          [:pre {:style {:margin "24px auto" :max-width "360px"}}
           "🚧  this wil take a while to load, apologies.  I'm working on "
           "making it faster.  thanks for being Small World's first user!"])

        [:br] [:br] [:br]
        [:p {:style {:text-align "center"}}
         [:a {:on-click #(reset! *debug? (not @*debug?)) :href "#" :style {:border-bottom "2px solid #ffffff33"}}
          "toggle debug – currently " (if @*debug? "on 🟢" "off 🔴")]]]

       util/info-footer

       (when @*debug?
         [:<> [:br] [:br] [:hr]
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
            [:pre "count:" (count @user-data/*friends) "\n\n" (util/preify @user-data/*friends)])])]

      (let [top-location (first (remove nil? (:locations @session/*store)))]
        [util/error-boundary
         [mapbox/mapbox
          {:lng-lat  (:coords top-location)
           :location (:name top-location)
           :user-img (:profile_image_url_large @session/*store)
           :user-name (:name @session/*store)
           :screen-name (:screen-name @session/*store)}]])])])
