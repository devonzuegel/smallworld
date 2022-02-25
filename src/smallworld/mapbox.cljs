(ns smallworld.mapbox
  (:require [reagent.core           :as r]
            [smallworld.util        :as util]
            [smallworld.decorations :as decorations]
            [reagent.dom.server]
            [cljsjs.mapbox]
            [goog.dom]))

; Mapbox API docs: https://docs.mapbox.com/mapbox-gl-js/api/map

(util/load-stylesheet "./css/mapbox-gl.inc.css")

; not defonce because we want to reset it to closed upon refresh
(def expanded (r/atom true))
(def the-map (r/atom nil)) ; can't name it `map` since that's taken by the standard library
(defonce markers (r/atom []))

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

(defn with-offset [lng-lat]
  [(+ (random-offset) (first lng-lat))
   (+ (random-offset) (second lng-lat))])

(defn toggle-expanded [elem-id]
  #(.toggle (.-classList (goog.dom/getElement elem-id))
            "expanded"))

(defn User-Marker [{lng-lat     :lng-lat
                    location    :location
                    img-url     :img-url
                    user-name   :user-name
                    screen-name :screen-name
                    classname   :classname}]
  [:div.marker {:class classname
                :on-click (toggle-expanded screen-name)}
   [:div.avatar {:style {:background-image (str "url(" img-url ")")}}]
   #_[:div.user-name user-name]])

(defn add-user-marker [{lng-lat     :lng-lat
                        location    :location
                        img-url     :img-url
                        user-name   :user-name
                        screen-name :screen-name
                        info        :info
                        classname   :classname}]
  (try  (assert-long-lat lng-lat)
        (let [element  (.createElement js/document "div")
              marker   (new js/mapboxgl.Marker element)
              popup    (new js/mapboxgl.Popup #js{:offset 38
                                                  :id (str "popup--" screen-name)
                                                  :closeButton false
                                                  :anchor "left"})]

          (.setLngLat marker (clj->js (if (= classname "current-user")
                                        lng-lat
                                        (with-offset lng-lat))))

          (.addTo marker @the-map)
          (swap! markers conj marker)
          (set! (.-id element) screen-name)
          (r/render-component [User-Marker {:lng-lat     lng-lat
                                            :location    location
                                            :img-url     img-url
                                            :user-name   user-name
                                            :screen-name screen-name
                                            :info        info
                                            :classname   classname}]
                              (goog.dom/getElement screen-name))

          ; add the popup
          (.setHTML popup (reagent.dom.server/render-to-string
                           [:<> ; TODO: extract into Popup-Content component
                            [:div.top-row
                             [:b.user-name user-name]
                             [:a.screen-name {:href (str "http://twitter.com/" screen-name)}
                              "@" screen-name]]
                            ; TODO: display latest Tweet too (this requires some backend work)
                            (when-not (clojure.string/blank? location)
                              [:div.bottom-row {:title info}
                               (decorations/location-icon)
                               [:span.location location]])]))
          (.setPopup marker popup))
        (catch js/Error e (js/console.error e))))

; only remove marker if it's not the current user
(defn remove-friend-marker [current-user-screen-name]
  (fn [marker]
    (let [friend-screen-name (.-id (.getElement marker))]
      (when (not= current-user-screen-name
                  friend-screen-name)
        (.remove marker)))))


; note – the mapbox access-token is paired with each style, rather than a per-account basis
(def config {:curios-bright {:access-token "pk.eyJ1IjoiZGV2b256dWVnZWwiLCJhIjoickpydlBfZyJ9.wEHJoAgO0E_tg4RhlMSDvA"
                             :style "mapbox://styles/devonzuegel/cj8rx2ti3aw2z2rnzhwwy3bvp"}
                    ; for some reason, this is broken right now...
             :smallworld {:style "mapbox://styles/devonzuegel/ckzsrsamc000l15mubod1gev5"
                          :access-token "pk.eyJ1IjoiZGV2b256dWVnZWwiLCJhIjoickpydlBfZyJ9.wEHJoAgO0E_tg4RhlMSDvA"}})
(def style :curios-bright)

(set! (.-accessToken js/mapboxgl) (get-in config [style :access-token]))

(def middle-of-USA [-90, 40])
(def Miami [-80.1947021484375 25.775083541870117])

(defn update-markers-size [& [to-print]]
  (let [scale   (+ .4 (* (.getZoom @the-map) 0.15))
        markers (array-seq (goog.dom/getElementsByClass "marker"))]
    (doall (for [marker markers]
             (let [new-diameter (str (* scale 30) "px")] ; min & max set in CSS
               (println "new-diameter:" new-diameter
                        " | scale:" scale
                        "| (count markers):" (count markers))
               (.setProperty (.-style marker.firstChild) "width" new-diameter)
               (.setProperty (.-style marker.firstChild) "height" new-diameter))))))

(defn component-did-mount [current-user] ; this should be called just once when the component is mounted
  ; create the map
  (reset! the-map
          (new js/mapboxgl.Map
               #js{:container "mapbox"
                   :key    (get-in config [style :access-token])
                   :style  (get-in config [style :style])
                   :center (clj->js (or (:lng-lat current-user) middle-of-USA))
                   :attributionControl false ; removes the Mapbox copyright symbol
                   :zoom 2
                   :maxZoom 8
                   :minZoom 0}))

  (js/setInterval #(.resize @the-map) (* 10 1000)) ; make sure the map is properly sized
  ;; TODO: removed this bc it was bad for performance; try to find a way to put it back!
  ;; (.on @the-map "zoom" update-markers-size) ; calibrate markers' size according to the zoom
  (.on @the-map "load" ; add the current user to the map
       #(when (:lng-lat current-user) (add-user-marker {:lng-lat     (:lng-lat current-user)
                                                        :location    (or (:name-location current-user)
                                                                         (:main-location current-user)
                                                                         (:location current-user))
                                                        :img-url     (:user-img current-user)
                                                        :user-name   (:user-name current-user)
                                                        :screen-name (:screen-name current-user)
                                                        :classname   "current-user"}))))


; this cannot be an anonymous function.  it needs to be a named function, because otherwise
; reagent won't know that it shouldn't be re-rendered on expand/collapse.
(defn mapbox-dom [current-user] (r/create-class
                                 {:component-did-mount #(component-did-mount current-user)
                                  :reagent-render      (fn [] [:div#mapbox])}))

(defn mapbox [current-user]
  [:<>
   [:div#mapbox-container {:class (if @expanded "expanded" "not-expanded")}
    [:a.expand-me
     {:on-click (fn []
                  (reset! expanded (not @expanded))
                  (doall
                   (for [i (range 105)]
                     (js/setTimeout #(.resize @the-map) (* i 10)))))}
     (if @expanded "collapse map" "expand map")]
    [mapbox-dom current-user]]])

(defn add-friends-to-map [friends]
  (when @the-map ; don't add friends to map if there is no map
    (doall ; force no lazy load
     (for [friend friends]
       (let [main-coords (:main-coords friend)
             name-coords (:name-coords friend)]

       ; TODO: make the styles for main vs name coords different
         (let [lng (:lng main-coords)
               lat (:lat main-coords)
               has-coord (and main-coords lng lat)]
           (when has-coord
             (add-user-marker {:lng-lat     [lng lat]
                               :location    (:main-location friend)
                               :img-url     (:profile_image_url_large friend)
                               :user-name   (:name friend)
                               :screen-name (:screen-name friend)
                               :info        "Twitter location"})))

         (let [lng (:lng name-coords)
               lat (:lat name-coords)
               has-coord (and name-coords lng lat)]
           (when has-coord
             (add-user-marker {:lng-lat     [lng lat]
                               :location    (:name-location friend)
                               :img-url     (:profile_image_url_large friend)
                               :user-name   (:name friend)
                               :screen-name (:screen-name friend)
                               :info        "parsed from Twitter display name"}))))))
    (when-not (nil? @the-map)
      (.on @the-map "loaded" #((println "upating markers size")
                               (update-markers-size))))))