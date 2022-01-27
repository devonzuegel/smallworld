(ns smallworld.mapbox
  (:require [reagent.core :as r]
            [cljsjs.mapbox]
            [goog.dom]))

; not defonce because we want to reset it to closed upon refresh
(def expanded (r/atom true #_false))
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

(defn add-friend-marker [{lng-lat :lng-lat
                          img-url :img-url
                          classname :classname}]
  (try (do
         (assert-long-lat lng-lat)
         (let [element (.createElement js/document "div")
               img     (.createElement js/document "img")
               marker  (new js/mapboxgl.Marker element)]

          ;;  (set! (.-src img) img-url)
          ;;  (.appendChild element img)

           (set! (.. element -style -backgroundImage) img-url)
           (set! (.-className element) (str "marker " classname))

           (.setLngLat marker (clj->js lng-lat))
           (.addTo marker @the-map)
           (swap! markers conj marker)))
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
             (let [current-transform (.-transform (.-style marker))
                   new-transform     (str "transform: " current-transform " scale(" scale ");")]
               (set! (.-style marker) new-transform))))))

(defn render-map [& [current-user-coordinates]]
  (r/create-class
   {:component-did-mount (fn []
                           (reset! the-map
                                   (new js/mapboxgl.Map
                                        #js{:container "mapbox"
                                            :key    (get-in mapbox-config [mapbox-style :access-token])
                                            :style  (get-in mapbox-config [mapbox-style :style])
                                            :center (clj->js (or current-user-coordinates middle-of-USA))
                                            :attributionControl false ; removes the Mapbox copyright symbol
                                            :zoom 4}))

                           (when current-user-coordinates (add-friend-marker {:lng-lat current-user-coordinates
                                                                              :classname "current-user"}))

                           ; calibrate markers' size on various rendering events
                           (update-markers-size) ; initial calibration
                           (.on @the-map "zoomend" #(js/setTimeout update-markers-size 1)) ; the timeout is a hack
                           (.on @the-map "zoom"       update-markers-size)

      ; TODO: clean this up once I've successfully debugged it
                           (.on @the-map "drag"      (fn [] (js/setTimeout #(update-markers-size "update-markers-size | drag")      1)))
                           (.on @the-map "dragstart" (fn [] (js/setTimeout #(update-markers-size "update-markers-size | dragstart") 1)))
                           (.on @the-map "dragend"   (fn [] (js/setTimeout #(update-markers-size "update-markers-size | dragend")   1))))

    :reagent-render (fn [] [:div#mapbox])}))

(defn mapbox [map-center]
  [:<>
   [:div#mapbox-container {:class (if @expanded "expanded" "not-expanded")}
    [:a.expand-me
     {:on-click (fn []
                  (reset! expanded (not @expanded))
                  (doall
                   (for [i (range 20)]
                     (js/setTimeout #(.resize @the-map) (* i 10)))))}
     (if @expanded "collapse map" "expand map")]
    [render-map map-center]]
   [:div#mapbox-spacer]])
