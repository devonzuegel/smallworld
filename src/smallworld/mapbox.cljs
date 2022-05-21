(ns smallworld.mapbox
  (:require [reagent.core           :as r]
            [clojure.pprint         :as pp]
            [clojure.string         :as str]
            [smallworld.util        :as util]
            [smallworld.decorations :as decorations]
            [reagent.dom.server]
            [cljsjs.mapbox]
            [goog.dom]))

; not defonce because we want to reset it to closed upon refresh
(def expanded (r/atom false))
(def the-map (r/atom nil)) ; can't name it `map` since that's taken by the standard library
(defonce markers (r/atom []))

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

(defn with-offset [lng-lat]
  [(+ (random-offset) (first lng-lat))
   (+ (random-offset) (second lng-lat))])

(defn toggle-expanded [elem-id]
  #(.toggle (.-classList (goog.dom/getElement elem-id))
            "expanded"))

(defn round              [num] (js/parseFloat (pp/cl-format nil "~,0f" num)))
(defn round-two-decimals [num] (js/parseFloat (pp/cl-format nil "~,2f" num)))

(defn Popup-Content [{location    :location
                      info        :info
                      lng-lat     :lng-lat
                      user-name   :user-name
                      screen-name :screen-name}]
  [:<>
   [:div.top-row
    [:b.user-name user-name]
    [:a.screen-name {:href (str "http://twitter.com/" screen-name)} "@" screen-name]]
   ; TODO: display latest Tweet too (this requires some backend work)
   [:div.bottom-row {:title info}
    (decorations/location-icon)
    (when-not (clojure.string/blank? location) [:span.location location])]])

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
                                                  :closeButton false
                                                  :anchor "left"})]
          ; set position
          (.setLngLat marker (clj->js lng-lat))

          ; add to map + store in atom + render
          (.addTo marker @the-map)
          (swap! markers conj marker)
          (set! (.-id element) screen-name)
          (set! (.-style element) (str "z-index: " (if (= classname "current-user")
                                                     11 ; put current user's avatar on top
                                                     (.ceil js/Math (rand 10)))))

          (r/render-component [User-Marker {:lng-lat     lng-lat
                                            :location    location
                                            :img-url     img-url
                                            :user-name   user-name
                                            :screen-name screen-name
                                            :info        info
                                            :classname   classname}]
                              (goog.dom/getElement screen-name))

          ; add popup to the marker
          (.setPopup marker popup)
          (.setHTML popup (reagent.dom.server/render-to-string
                           (Popup-Content {:location    location
                                           :user-name   user-name
                                           :lng-lat     lng-lat
                                           :info        info
                                           :screen-name screen-name}))))
        (catch js/Error e (js/console.error e))))

; only remove marker if it's not the current user
(defn remove-friend-marker []
  (fn [marker]
    (let [friend-screen-name (.-id (.getElement marker))]
      (.remove marker))))


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
                   :maxZoom 9
                   :minZoom 0}))

  (js/setTimeout #(.resize @the-map) 500) ; make sure the map is properly sized + the markers are placed

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
  [:<>
   [:div#mapbox-container {:class (if @expanded "expanded" "not-expanded")}
    [:a.expand-me
     {:on-click (fn []
                  (reset! expanded (not @expanded))
                  (doall
                   (for [i (range 105)]
                     (js/setTimeout #(.resize @the-map) (* i 10)))))}
     (if @expanded (decorations/minimize-icon) (decorations/fullscreen-icon))]
    [mapbox-dom current-user]]])

(defn add-friends-to-map [friends curr-user]
  (when @the-map ; don't add friends to map if there is no map
    (doseq [friend (conj friends curr-user)]
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
      (let [markers (sort-by #(if (= "current-user" (:classname %))
                                1 (rand 2)) ; put the current-user in roughly the middle of the honeycomb cluster
                             markers)
            diameter (.sqrt js/Math (count markers)) ; # of avatars to show in each row/column
            avg-lng (util/average (map #(first  (:lng-lat %)) markers))
            avg-lat (util/average (map #(second (:lng-lat %)) markers))
            [lng lat] group-key]
        (doall (map-indexed
                (fn [i marker-data]
                  (let  [row (.ceil js/Math (mod i diameter))
                         col (.floor js/Math (/ i diameter))
                         col (if (even? row) ; to make the honeycomb effect
                               (+ col .5)
                               col)
                         new-lat-lng [(+ avg-lng (* 0.070 col) (* -0.5 0.070 diameter))
                                      (+ avg-lat (* 0.055 row) (* -0.5 0.055 diameter))]]
                    (add-user-marker (merge marker-data {:lng-lat new-lat-lng}))))
                markers))))

    (when-not (nil? @the-map)
      (.on @the-map "loaded" #((println "upating markers size")
                               (update-markers-size))))))