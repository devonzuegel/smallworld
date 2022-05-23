(ns smallworld.util
  (:require [clojure.pprint :as pp]
            [reagent.core :as r])
  (:import [goog.async Debouncer]))

(def debug? false)

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

(defn average [list-of-nums]
  (/ (reduce + list-of-nums)
     (count list-of-nums)))

(defn rm-from-list [col idx]
  (filter identity (map-indexed #(when-not (= %1 idx) %2) col)))

(defn info-footer [screen-name recompute-friends]
  [:div.info-footer
   [:a {:href "https://devonzuegel.com" :target "_blank"} "made in Miami Beach"]
   [:span.dot-separator " · "]
   [:a {:href "https://github.com/devonzuegel/smallworld/issues" :target "_blank"} "report a bug"]
   (when (= screen-name "devonzuegel")
     [:<> [:span.dot-separator " · "]
      [:a {:href "#" :on-click recompute-friends} "recompute locations"]
      [:span.dot-separator " · "]
      [:a "v5"]])])

(defn error-boundary [& children]
  (let [err-state (r/atom nil)]
    (r/create-class
     {:display-name "error boundary"
      :component-did-catch (fn [err info]
                             (reset! err-state [err info]))
      :reagent-render (fn [& children]
                        (if (nil? @err-state)
                          (into [:<>] children)
                          (let [[_ info] @err-state]
                            [:pre (pr-str info)])))})))

(comment
  "------------------------------ usage example: -----------------------------"

  (defn my-happy-component [] [:b "This has no error at all, yay!"])
  (defn my-error-component [] (throw (js/Error. "Oops! 👻")))

  (defn error-boundary-example-1 [] [util/error-boundary [my-error-component]])
  (defn error-boundary-example-2 [] [util/error-boundary [my-happy-component]])

  [err-bound-example-1]
  [err-bound-example-2])

(defn exponent [base power] (.pow js/Math base power))

(defn query-dom [selector]
  (array-seq (.querySelectorAll js/document selector)))