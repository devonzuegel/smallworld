(ns smallworld.mapbox
  (:require [reagent.core :as r]
            [cljsjs.mapbox]
            [goog.dom]))

; not defonce because we want to reset it to closed upon refresh
(def expanded (r/atom false))
(def the-map (r/atom nil)) ;; can't name it `map` since that's taken
(defonce markers (r/atom []))

(defn add-marker [coordinate]
  (let [element (.createElement js/document "div")
        marker (new js/mapboxgl.Marker element)]
    (.setLngLat marker (clj->js coordinate))
    (.addTo marker @the-map)
    (set! (.-className element) "marker")
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

(defn render-map []
  (r/create-class
   {:component-did-mount (fn []
                           (reset! the-map
                                   (new js/mapboxgl.Map
                                        #js{:container "mapbox"
                                            :key (get-in mapbox-config [mapbox-style :access-token])
                                            :style (get-in mapbox-config [mapbox-style :style])
                                            :attributionControl false ;; remove the Mapbox copyright symbol
                                            :center #js[74.5, 40] ;; TODO: center on user's location
                                            :zoom 1}))
                           (add-marker [74.5, 40])
                           (add-marker [174.5, 40])
                           (add-marker [-14.5, 30]))
    :reagent-render (fn [] [:div#mapbox])}))

(defn mapbox []
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
    [render-map]]
   [:div#mapbox-spacer]])

;; const marker = new mapgl.Marker(map, {
;;     coordinates: [55.31878, 25.23584],
;; });


;; (new js/mapboxgl.Map
;;      :container container-id
;;      :style "mapbox://styles/mapbox/streets-v11"
;;      :center [-74.5, 40]
;;      :zoom 9)
;; (defn MapRender
;;   []
;;   (let [ref (r/useRef nil)
;;         [map setMap] (r/useState nil)]
;;     (r/useEffect
;;      (fn []
;;        (when (and (.-current ref) (not map))
;;          (let [map
;;                (new
;;                 (.-Map mapboxgl)
;;                 #js
;;                  {:container (.-current ref)
;;                   :style "mapbox://styles/mapbox/streets-v11"
;;                   :center #js [0 0]
;;                   :zoom 1})]
;;            (setMap map))))
;;      #js [ref map])
;;     [:div {:className "map-container", :ref ref}]))

;; mapboxgl.accessToken = '<your access token here>';
;; const map = new mapboxgl.Map({
;;     container: 'map', // container ID
;;     style: 'mapbox://styles/mapbox/streets-v11', // style URL
;;     center: [-74.5, 40], // starting position [lng, lat]
;;     zoom: 9 // starting zoom
;; });

;; js/mapbox.accessToken "pk.eyJ1IjoiZGV2b256dWVnZWwiLCJhIjoickpydlBfZyJ9.wEHJoAgO0E_tg4RhlMSDvA"
  ;; (let [my-div [:div "my-div"]
  ;;       my-map (new js/mapboxgl.Map
  ;;                   :container my-div
  ;;                   :style "mapbox://styles/mapbox/streets-v11"
  ;;                   :center [-74.5, 40]
  ;;                   :zoom 9)]
  ;;   ;; (print "my-map:")
  ;;   ;; (print my-map)
  ;;   my-map)

