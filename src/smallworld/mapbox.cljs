(ns smallworld.mapbox ; Mapbox GL docs: https://docs.mapbox.com/mapbox-gl-js/api/map
  (:require [cljsjs.mapbox]
            [clojure.pprint         :as pp]
            [clojure.string         :as str]
            [clojure.walk :as walk]
            [goog.dom]
            [goog.object :as obj]
            [reagent.core           :as r]
            [reagent.dom.server]
            [smallworld.decorations :as decorations]
            [smallworld.util :as util]))

; not defonce because we want to reset it to closed upon refresh
(def expanded (r/atom false))
(def *loading  (r/atom true))
(def the-map (r/atom nil)) ; can't name it `map` since that's taken by the standard library
(def *friends-computed (r/atom []))
(def *popups (r/atom nil))

(defn coords-to-mapbox-array [coords]
  #js[(:lng coords) (:lat coords)])

(defn assert-long-lat [-coordinates]
  (let [[long lat] -coordinates]
    (assert (not (nil? -coordinates))
            (str "expected coordinates to be a list of [long lat], but received nil"))
    (assert (and (number? lat)
                 (number? long))
            (str "[lat long] must be numbers, but received [" lat " " long "]"))
    (assert (and (>= lat -90)
                 (<= lat 90))
            (str "lat must be between -90 & 90, but received [" lat "]"))
    (assert (and (>= long -180)
                 (<= long 180))
            (str "long must be between -180 & 180, but received [" lat "]"))))

(defn random-offset [] (- (rand 0.6) 0.3))

(defn round [num] (js/parseFloat (pp/cl-format nil "~,0f" num)))

(defn Popup-Content [{location    :location
                      info        :info
                      lng-lat     :lng-lat
                      user-name   :user-name
                      screen-name :screen-name}]
  [:<>
   [:div.top-row
    [:b.user-name user-name]
    [:a.screen-name {:href   (str "http://twitter.com/" screen-name)
                     :target "_blank"
                     :rel    "noopener"}
     (str "@" screen-name)]]
   ; TODO: display latest Tweet too (this requires some backend work)
   [:div.bottom-row {:title info}
    (decorations/location-icon)
    (when-not (clojure.string/blank? location) [:span.location location])]])

; note – the mapbox access-token is paired with each style, rather than a per-account basis
(def config {:curios-bright {:access-token "pk.eyJ1IjoiZGV2b256dWVnZWwiLCJhIjoickpydlBfZyJ9.wEHJoAgO0E_tg4RhlMSDvA"
                             :style "mapbox://styles/devonzuegel/cj8rx2ti3aw2z2rnzhwwy3bvp"}
                    ; for some reason, this is broken right now...
             :smallworld {:style "mapbox://styles/devonzuegel/ckzsrsamc000l15mubod1gev5"
                          :access-token "pk.eyJ1IjoiZGV2b256dWVnZWwiLCJhIjoickpydlBfZyJ9.wEHJoAgO0E_tg4RhlMSDvA"}})
(def style :curios-bright)

(set! (.-accessToken js/mapboxgl) (get-in config [style :access-token]))

(def Miami [-80.1947021484375 25.775083541870117])

(def min-zoom 1)

(defn component-did-mount [current-user] ; this should be called just once when the component is mounted
  ; create the map
  (reset! the-map
          (new js/mapboxgl.Map
               #js{:container "mapbox"
                   :key    (get-in config [style :access-token])
                   :style  (get-in config [style :style])
                   :center (clj->js (or (:lng-lat current-user) Miami))
                   :attributionControl false ; removes the Mapbox copyright symbol
                   :zoom 0 ; this will almost immediately be updated to the true `min-zoom`; we're only starting at zero as a hacky way to force the images to load
                   :maxZoom 9
                   :minZoom 1}))
  ; minimize the map when the user hits ESCAPE
  (.addEventListener js/document "keyup"
                     #(when (= (.-key %) "Escape") (reset! expanded false))
                     false))


; this cannot be an anonymous function.  it needs to be a named function, because otherwise
; reagent won't know that it shouldn't be re-rendered on expand/collapse.
(defn mapbox-dom [current-user] (r/create-class
                                 {:component-did-mount #(component-did-mount current-user)
                                  :reagent-render      (fn [] [:div#mapbox])}))

(def *groups (r/atom {}))

(defn mapbox [current-user]
  [:div#mapbox-container {:data-tap-disabled "true"
                          :class (if @expanded "expanded" "not-expanded")}
   [:div.loading {:class (when-not @*loading "hidden")}
    (decorations/simple-loading-animation) "fetching your Twitter friends..."]

   [:a.expand-me
    {:on-click (fn []
                 (swap! expanded not)
                 (doall
                  (for [i (range 105)]
                    (js/setTimeout #(.resize @the-map) (* i 10)))))}
    (if @expanded (decorations/minimize-icon) (decorations/fullscreen-icon))]
   [:a {:on-click #(.zoomTo @the-map (+ (.getZoom @the-map) 2))} (decorations/zoom-in-icon)]
   [:a {:on-click #(.zoomTo @the-map (- (.getZoom @the-map) 2))} (decorations/zoom-out-icon)]

   [mapbox-dom current-user]])

(defn load-img [img]
  (new js/Promise
       (fn [resolve _reject]
         (if (.hasImage @the-map (:id img))
           (resolve)
           (.loadImage @the-map (:url img)
                       (fn [error result]
                         (when error (throw error))
                         (.addImage @the-map (:id img) result)
                         (resolve)))))))

(defn get-coords-from-curr-user [curr-user]
  (let [coords (get-in curr-user [:locations 0 :coords])
        lat (:lat coords)
        lng (:lng coords)]
    (if (and lng lat)
      [lng lat]
      nil)))

(defn set-map-cursor [cursor-style]
  (set! (.. (.getCanvas @the-map) -style -cursor) cursor-style))

(defn add-popup-to-map [e]
  (set-map-cursor "pointer")

  ; these `obj/get` calls are hacks – should really be using externs to enable .-features
  (let [feature (first (obj/get e "features"))
        properties (-> feature
                       (obj/get "properties")
                       js->clj
                       walk/keywordize-keys)
        coordinates (-> feature
                        (obj/get "geometry")
                        (obj/get "coordinates"))]
    (swap! *popups conj
           (doto (js/mapboxgl.Popup. #js{:offset 30
                                         :closeButton false
                                         :anchor "left"})
             (.setLngLat coordinates)
             (.setHTML (reagent.dom.server/render-to-string
                        (Popup-Content {:location    (:location properties)
                                        :user-name   (:name properties)
                                        :screen-name (:screen-name properties)
                                        :lng-lat     coordinates})))
             (.addTo @the-map)))))

(defn add-friends-to-map [friends curr-user]
  (reset! *groups {})
  (reset! *friends-computed [])

  (when @the-map ; don't add friends to map if there is no map
    (doseq [friend (conj friends ; TODO: updating the image/screen-name this way is hacky (should align the keys upstream instead)
                         (merge curr-user {:profile_image_url_large (:twitter_avatar curr-user)
                                           :screen-name (:screen_name curr-user)}))]
      (doseq [location (:locations friend)]
        ; round to 1 decimal place so that metro regions are grouped together
        (when (and (:lng (:coords location))
                   (:lat (:coords location)))
          (let [lng (round (:lng (:coords location)))
                lat (round (:lat (:coords location)))
                has-coord (and (:coords location) lng lat)
                group (when has-coord (get @*groups [lng lat]))]
            (when has-coord
              (swap! *groups assoc [lng lat] (conj group {:lng-lat [(:lng (:coords location))
                                                                    (:lat (:coords location))]
                                                          :classname (when (= (:screen-name friend)
                                                                              (:screen-name curr-user))
                                                                       "current-user")
                                                          :location       (:name location)
                                                          :img-url        (:profile_image_url_large friend)
                                                          :user-name      (:name friend)
                                                          :screen-name    (:screen-name friend)
                                                          :special-status (:special-status location)})))))))

    (doseq [[group-key markers] @*groups]
      (let [#_markers #_(sort-by #(if (= "current-user" (:classname %))
                                    1 (rand 2)) ; put the current-user in roughly the middle of the honeycomb cluster
                                 markers)
            diameter (.sqrt js/Math (count markers)) ; # of avatars to show in each row/column
            avg-lng (util/average (map #(first  (:lng-lat %)) markers))
            avg-lat (util/average (map #(second (:lng-lat %)) markers))
            [_lng _lat] group-key]
        ; group the markers according to 1x1 lat-lng coordinates
        (swap! *friends-computed concat (map-indexed
                                         (fn [i marker-data]
                                           (let  [row (.ceil js/Math (mod i diameter))
                                                  col (.floor js/Math (/ i diameter))
                                                  col (if (even? row) ; to make the honeycomb effect
                                                        (+ col .5)
                                                        col)
                                                  new-lat-lng [(+ avg-lng (* 1.5 0.05 col) (* -0.5 0.070 diameter))
                                                               (+ avg-lat (* 1   0.05 row) (* -0.5 0.055 diameter))]]
                                           ;; (pp/pprint (merge marker-data {:lng-lat new-lat-lng}))
                                             (merge marker-data {:lng-lat new-lat-lng})
                                           ;
                                             ))
                                         markers))))
    (let [source-name "friends-data"
          cluster-max-zoom 7
          images (map (fn [friend] {:url (-> (or (:img-url friend) "") ; use the smaller image size to improve performance
                                             (str/replace #"(?i)\.png"  "_200x200.png")
                                             (str/replace #"(?i)\.jpg"  "_200x200.jpg")
                                             (str/replace #"(?i)\.jpeg" "_200x200.jpeg"))
                                    :id  (:screen-name friend)})
                      @*friends-computed)]

      (.on @the-map "sourcedata" (fn []
                                   (when (and (.getSource @the-map source-name)
                                              @*loading ; only run this the first time that sourcedata is loaded
                                              (.isSourceLoaded @the-map source-name))
                                     (js/setTimeout (fn []
                                                      (reset! *loading false)
                                                      (.flyTo @the-map #js{:center (clj->js (or (get-coords-from-curr-user curr-user)
                                                                                                Miami))
                                                                           :zoom (+ (.getZoom @the-map) 1.5)
                                                                           :speed .6
                                                                           :essential true})
                                                      (js/setTimeout #(.setMinZoom @the-map min-zoom) 1000))
                                                    300))))

      (.then (.all js/Promise
                   (clj->js
                    (mapv load-img images)))

             (let [features #js[]]
               (doseq [friend @*friends-computed]
                 (.push features (clj->js
                                  {:type "Feature"
                                   :geometry {:type "Point" ; round coords to 5 decimal places to improve performance: https://docs.mapbox.com/help/troubleshooting/working-with-large-geojson-data/#cleaning-up-your-data
                                              :coordinates [(.toFixed (first  (:lng-lat friend)) 5)
                                                            (.toFixed (second (:lng-lat friend)) 5)]}
                                   :properties {:icon        (:screen-name friend)
                                                :name        (:user-name   friend)
                                                :location    (:location    friend)
                                                :screen-name (:screen-name friend)}})))

               (if (.getSource @the-map source-name)
                 (.setData (.getSource @the-map source-name) #js{:type "FeatureCollection"
                                                                 :features features})
                 (.addSource @the-map source-name
                             #js{:type "geojson"
                                 :cluster true
                                 :clusterMaxZoom cluster-max-zoom
                                 :clusterRadius 15
                                 :data #js{:type "FeatureCollection"
                                           :features features}})))

             (when-not (.getLayer @the-map "img-layer")
               (.addLayer @the-map ; order matters – the white circles layer needs to go on top of the symbol layer
                          #js{:id "img-layer"
                              :source source-name
                              :type "symbol"
                              :layout #js{:icon-allow-overlap true
                                          :icon-ignore-placement true
                                          :icon-size #js{:base .5
                                                         :stops #js[#js[(+ cluster-max-zoom 0) .13]
                                                                    #js[(+ cluster-max-zoom 1) .09]
                                                                    #js[(+ cluster-max-zoom 2) .17]]}
                                          :icon-image #js["get" "icon"]}}))

             (when-not (.getLayer @the-map "white-borders-layer")
               (.addLayer @the-map ; order matters – the white circles layer needs to go on top of the symbol layer
                          #js{:id "white-borders-layer"
                              :source source-name
                              :type "circle"
                              :filter #js["!" #js["has" "point_count"]]
                              :paint #js{:circle-radius #js{:stops #js[#js[(+ cluster-max-zoom 0) 14]
                                                                       #js[(+ cluster-max-zoom 1) 10]
                                                                       #js[(+ cluster-max-zoom 2) 18]]}
                                         :circle-color "transparent"
                                         :circle-stroke-color "white"
                                         :circle-stroke-width #js{:stops #js[#js[(+ cluster-max-zoom 0) 4]
                                                                             #js[(+ cluster-max-zoom 1) 3]
                                                                             #js[(+ cluster-max-zoom 2) 6]]}}}))
             (when-not (.getLayer  @the-map "cluster-layer")
               (.addLayer @the-map #js{:id "cluster-layer"
                                       :type "circle"
                                       :source source-name
                                       :filter #js["has" "point_count"]
                                       :paint #js{:circle-color "white"
                                                  :circle-opacity 0.4
                                                  :circle-radius #js["step" #js["get" "point_count"]
                                                                     15    ; base radius
                                                                     5 25  ; count of 5  -> radius of 25
                                                                     10 35 ; count of 10 -> radius of 35
                                                                     50 45 ; ... etc
                                                                     100 55]}}))

             (when-not (.getLayer  @the-map "cluster-count-layer")
               (.addLayer @the-map
                          #js{:id "cluster-count-layer"
                              :type "symbol"
                              :source source-name
                              :filter #js["has" "point_count"]
                              :layout #js{:text-field "{point_count_abbreviated}"
                                          :text-font #js["DIN Offc Pro Medium" "Arial Unicode MS Bold"],
                                          :text-size 14}}))

             #_(.on @the-map "zoom" #(util/debounce (println (.getZoom @the-map)) 500)) ; for debugging

             (.on @the-map "click" "cluster-layer"
                  (fn [event]
                    (let [feature (first (obj/get event "features"))
                          coordinates (-> feature
                                          (obj/get "geometry")
                                          (obj/get "coordinates"))]
                      (.flyTo @the-map #js {:zoom (+ (.getZoom @the-map) 10)
                                            :center coordinates
                                            :speed 1.5}))))

             (.on @the-map "mouseover" "cluster-layer" #(set-map-cursor "pointer"))
             (.on @the-map "mouseout"  "cluster-layer" #(set-map-cursor ""))

             (.on @the-map "click"     "img-layer" add-popup-to-map)
             (.on @the-map "mouseover" "img-layer" add-popup-to-map)
             (.on @the-map "mouseout"  "img-layer" #(do
                                                      (set-map-cursor "")
                                                      (doseq [popup @*popups]
                                                        (.remove popup))))

             ; make sure the map is properly sized + the markers are placed
             (js/setTimeout #(.resize @the-map) 500)))))