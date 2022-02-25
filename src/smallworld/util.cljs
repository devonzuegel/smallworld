(ns smallworld.util
  (:require [clojure.pprint :as pp])
  (:import [goog.async Debouncer]))

(def debug? false)

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

(defn fetch [route callback & {:keys [retry?] :or {retry? false}}]
  (-> (.fetch js/window route)
      (.then #(.json %)) ; parse
      (.then #(js->clj % :keywordize-keys true)) ; parse
      (.then (fn [result] ; run the callback
               (when debug?
                 (println route ":")
                 (pp/pprint result))
               (callback result)))
      (.catch (fn [error] ; retry
                (println (str "error fetching " route ":"))
                (js/console.error error)
                (when retry?
                  (println (str "retrying..."))
                  (fetch route callback))))))

; TODO: combine this with the fetch function
(defn fetch-post [route body & [callback]]
  (let [request (new js/Request
                     route
                     #js {:method "POST"
                          :body (.stringify js/JSON (clj->js body))
                          :headers (new js/Headers #js{:Content-Type "application/json"})})]
    (-> (js/fetch request)
        (.then #(.json %))
        (.then #(js->clj % :keywordize-keys true)) ; parse
        (.then (fn [res]
                 (when debug? (.log js/console res))
                 (when callback (callback res)))))))

(defn debounce [f interval]
  (let [dbnc (Debouncer. f interval)]
    ;; use apply here to support functions of various arities
    (fn [& args] (.apply (.-fire dbnc) dbnc (to-array args)))))
