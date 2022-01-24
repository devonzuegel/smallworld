(ns smallworld.mapbox
  (:require [reagent.core :as r]
            [cljsjs.mapbox]
            [goog.dom]))

; not defonce because we want to reset it to closed upon refresh
(def expanded (r/atom true #_false))
(def the-map (r/atom nil)) ;; can't name it `map` since that's taken
(defonce markers (r/atom []))

(defn assert-long-lat [[long lat]]
  (assert (and (number? lat)
               (number? long))
          (str "[lat long] must be numbers – [lat long] = [" lat " " long "]"))
  (assert (and (>= lat -90)
               (<= lat 90))
          (str "lat must be between -90 & 90 [" lat "]"))
  (assert (and (>= long -180)
               (<= long 180))
          (str "long must be between -180 & 180 [" lat "]")))

(defn add-marker [coordinate & [classname]]
  (assert-long-lat coordinate)
  (let [element (.createElement js/document "div")
        newContent (.createTextNode js/document (str coordinate))
        marker (new js/mapboxgl.Marker element)]

    (.appendChild element newContent)
    (set! (.-className element) (str "marker " classname))

    (.setLngLat marker (clj->js coordinate))
    (.addTo marker @the-map)
    (swap! markers conj marker)))

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

(defn render-map [& [center]]
  (let [center (or center middle-of-USA)]
    (r/create-class
     {:component-did-mount (fn []
                             (reset! the-map
                                     (new js/mapboxgl.Map
                                          #js{:container "mapbox"
                                              :key (get-in mapbox-config [mapbox-style :access-token])
                                              :style (get-in mapbox-config [mapbox-style :style])
                                              :attributionControl false ;; remove the Mapbox copyright symbol
                                              :center (clj->js center) ;; TODO: center on user's location
                                              :zoom 1}))
                             (add-marker center "center")
                             (add-marker [74.5, 40])
                             (add-marker [-90, 40])
                             (add-marker [-77, 39]) ;; Washington DC
                             (add-marker [174.5, 40])
                             (add-marker [-14.5, 30]))
      :reagent-render (fn [] [:div#mapbox])})))

(defn mapbox [map-center]
  [:<>
   [:div#mapbox-container {:class (if @expanded "expanded" "not-expanded")}
    [:a.expand-me
     {:on-click (fn []
                  (reset! expanded (not @expanded))
                  ; TODO: iterate over these timeouts so it's not so gross – need to find a non-lazy way to do it so it actually works
                  ; resize the map multiple times to account for the animation
                  (js/setTimeout #(.resize @the-map) 0)
                  (js/setTimeout #(.resize @the-map) 10)
                  (js/setTimeout #(.resize @the-map) 20)
                  (js/setTimeout #(.resize @the-map) 30)
                  (js/setTimeout #(.resize @the-map) 40)
                  (js/setTimeout #(.resize @the-map) 50)
                  (js/setTimeout #(.resize @the-map) 60)
                  (js/setTimeout #(.resize @the-map) 70)
                  (js/setTimeout #(.resize @the-map) 80)
                  (js/setTimeout #(.resize @the-map) 90)
                  (js/setTimeout #(.resize @the-map) 100)
                  (js/setTimeout #(.resize @the-map) 110)
                  (js/setTimeout #(.resize @the-map) 120)
                  (js/setTimeout #(.resize @the-map) 130)
                  (js/setTimeout #(.resize @the-map) 140)
                  (js/setTimeout #(.resize @the-map) 150)
                  (js/setTimeout #(.resize @the-map) 160)
                  (js/setTimeout #(.resize @the-map) 170)
                  (js/setTimeout #(.resize @the-map) 180)
                  (js/setTimeout #(.resize @the-map) 190)
                  (js/setTimeout #(.resize @the-map) 200))}
     (if @expanded "collapse map" "expand map")]
    [render-map map-center]]
   [:div#mapbox-spacer]])
