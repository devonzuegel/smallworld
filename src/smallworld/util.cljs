(ns smallworld.util (:require [clojure.pprint :as pp]))

; Mapbox GL docs: https://docs.mapbox.com/mapbox-gl-js/guides/install 
(defn load-stylesheet [href & [callback]]
  (let [head (aget (.getElementsByTagName js/document "head") 0)
        link (.createElement js/document "link")]
    (set! (.-rel link) "stylesheet")
    (set! (.-type link) "text/css")
    (set! (.-href link) href)
    (.appendChild head link)
    (when callback (callback))))

(defn preify [obj] (with-out-str (pp/pprint obj)))
