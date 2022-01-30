(ns smallworld.mapbox
  (:require [reagent.core :as r]
            [cljsjs.mapbox]
            [smallworld.util :as util]
            [goog.dom]))

; Mapbox API docs: https://docs.mapbox.com/mapbox-gl-js/api/map

(util/load-stylesheet "./css/mapbox-gl.inc.css")

; not defonce because we want to reset it to closed upon refresh
(def expanded (r/atom false))
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

(defn random-offset [] (- (rand 0.4) 0.2))

(defn with-offset [lng-lat]
  [(+ (random-offset) (first lng-lat))
   (+ (random-offset) (second lng-lat))])

(defn add-friend-marker [{lng-lat     :lng-lat
                          location    :location
                          img-url     :img-url
                          user-name   :user-name
                          screen-name :screen-name
                          classname   :classname}]
  (try (do
         (assert-long-lat lng-lat)
         (let [element  (.createElement js/document "div")
               name-div (.createElement js/document "div")
               marker   (new js/mapboxgl.Marker element)]

           (.setLngLat marker (clj->js (if (= classname "current-user")
                                         lng-lat
                                         (with-offset lng-lat))))
           (.addTo marker @the-map)
           (swap! markers conj marker)

           (.setProperty (.-style element) "background-image" (str "url(" img-url ")"))
           (set! (.-className element) (str "marker " classname))

           ; add name element to marker
           (.appendChild name-div (.createTextNode js/document user-name))
           (.appendChild element name-div)
           (set! (.-className name-div) "user-name")))
       (catch js/Error e (js/console.error e))))

(def mapbox-config {:frank-lloyd-wright {:access-token "pk.eyJ1IjoiZGV2b256dWVnZWwiLCJhIjoickpydlBfZyJ9.wEHJoAgO0E_tg4RhlMSDvA"
                                         :style "mapbox://styles/devonzuegel/ckyn7uof70x1e14ppotxarzhc"
                                        ;;  :style "./mapbox-style-frank.json"
                                         }
                    :minimo {:access-token "pk.eyJ1IjoiZGV2b256dWVnZWwiLCJhIjoickpydlBfZyJ9.wEHJoAgO0E_tg4RhlMSDvA"
                             :style "mapbox://styles/devonzuegel/ckyootmv72ci414ppwl6j34a2"}
                    :curios-bright {:access-token "pk.eyJ1IjoiZGV2b256dWVnZWwiLCJhIjoickpydlBfZyJ9.wEHJoAgO0E_tg4RhlMSDvA"
                                    :style "mapbox://styles/devonzuegel/cj8rx2ti3aw2z2rnzhwwy3bvp"}})

;; (def mapbox-style :frank-lloyd-wright)
(def mapbox-style :curios-bright)

(set! (.-accessToken js/mapboxgl) (get-in mapbox-config [mapbox-style :access-token]))

(def middle-of-USA [-90, 40])

(defn update-markers-size [& [to-print]]
  (let [scale   (+ .1 (* (.getZoom @the-map) 0.2))
        markers (array-seq (goog.dom/getElementsByClass "marker"))]
    (when (string? to-print)
      (println to-print " | scale:" scale "| (count markers):" (count markers)))
    (doall (for [marker markers]
             (let [new-diameter (str (* scale 30) "px")] ; min & max set in CSS
               (.setProperty (.-style marker) "width" new-diameter)
               (.setProperty (.-style marker) "height" new-diameter))))))

(defn toggle-expanded [& [{expanded? :expanded?}]]
  (reset! expanded (or expanded? (not @expanded)))
  (doall
   (for [i (range 20)]
     (js/setTimeout #(.resize @the-map) (* i 10)))))

(defn after-mount [current-user]
  ; create the map
  (reset! the-map
          (new js/mapboxgl.Map
               #js{:container "mapbox"
                   :key    (get-in mapbox-config [mapbox-style :access-token])
                   :style  (get-in mapbox-config [mapbox-style :style])
                   :center (clj->js (or (:lng-lat current-user) middle-of-USA))
                   :attributionControl false ; removes the Mapbox copyright symbol
                   :zoom 9
                   :maxZoom 9
                   :minZoom 1.5}))

  ; calibrate markers' size on various rendering events
  (.on @the-map "load" update-markers-size)
  (.on @the-map "zoom" update-markers-size)
  (.on @the-map "click" #(toggle-expanded {:expanded? true}))

  ; add the current user to the map
  (js/setTimeout ; the timeout is a hack to ensure the map is loaded before adding the current-user marker
   (when (:lng-lat current-user) (add-friend-marker {:lng-lat     (:lng-lat current-user)
                                                     :location    (:location current-user)
                                                     :img-url     (:user-img current-user)
                                                     :user-name   (:user-name current-user)
                                                     :screen-name (:screen-name current-user)
                                                     :classname   "current-user"}))
   10))

(defn mapbox [current-user]
  [:<>
   [:div#mapbox-container {:class (if @expanded "expanded" "not-expanded")}
    [:a.expand-me
     {:on-click toggle-expanded}
     (if @expanded "collapse map" "expand map")]


    [(r/create-class
      {:component-did-mount #(after-mount current-user)
       :reagent-render      (fn [] [:div#mapbox])})]]
   [:div#mapbox-spacer]])
