(ns smallworld.frontend
  (:require [reagent.core :as r]
            [clj-fuzzy.metrics :as fuzzy]
            [clojure.pprint :as pp]
            [clojure.string :as str]
            [goog.dom]
            [goog.dom.classlist :as gc]))


(defonce friends (r/atom :loading))
(defonce current-user (r/atom {:name nil ;; TODO: handle loading state
                               :screen-name nil
                               :location nil
                               :profile_image_url_large nil
                               :name-location {:location nil :coordinates nil :distance nil}
                               :main-location {:location nil :coordinates nil :distance nil}}))

(defn fetch [route callback]
  (-> (.fetch js/window route)
      (.then #(.json %))
      (.then #(js->clj % :keywordize-keys true))
      (.then (fn [result]
               #_(println route ":")
               #_(println result)
               (callback result)))))


(fetch "/friends" #(reset! friends %))
(fetch "/current-user" #(reset! current-user %))

(defn animated-globe []
  (js/setTimeout
   (fn [] (let [elem (goog.dom/getElement "logo-animation")
                rm-animation-class #(gc/toggle elem "classToBeAdded")]
            (.addEventListener elem "webkitAnimationIteration" rm-animation-class) ;; for Chrome
            (.addEventListener elem "animationiteration" rm-animation-class) ;; for Firefox
            (.addEventListener elem "MSAnimationIteration" rm-animation-class) ;; for IE
            (.addEventListener elem "animationiteration" rm-animation-class)))
   1000) ;; give time to load the animation

  [:div#logo-animation
   [:div {:class "globe-loader fas fa-globe-americas"} [:i.fas.fa-plane]]])

(defn nav []

  [:div.nav
   [:div.logo
    (animated-globe)

    [:div.logo-text "small world"]]
   [:div.links
    [:a "about"]
    [:span.links-spacer "·"]
    [:a "log out " [:b "@" (:screen-name @current-user)]]]])

(defn music []
  [:iframe {:src "https://open.spotify.com/embed/track/3fWTQXs897m4H1zsai8SOk?utm_source=generator&theme=0"
            :width "100%"
            :height "80"
            :frameBorder "0"
            :allowFullScreen ""
            :allow "autoplay; clipboard-write; encrypted-media; fullscreen; picture-in-picture"}])

(def friend-row-headers ["" "name" "handle" "location" "coordinates" "distance" "profile_image_url_large"])

(defn location-name-similarity [friend]
  (fuzzy/jaro-winkler (.toLowerCase (:location friend))
                      (.toLowerCase (:location @current-user))))

(defn friend-row [i friend]
  (let [twitter-handle (:screen-name friend)
        twitter-link   (str "http://twitter.com/" twitter-handle)]
    [:tr {:key i}
     [:td i]
     [:td (:name friend)]
     [:td [:a {:href twitter-link} (str "@" twitter-handle)]]
     [:td (:location friend)]
     [:td (pr-str (:coordinates friend))]
     [:td (:distance friend)]
     [:td (:profile_image_url_large friend)]
     [:td [:pre (prn-str)]]
     #_[:td (location-name-similarity friend)]]))

(def table-header (map-indexed (fn [i header] [:th {:key i} header])
                               friend-row-headers))

(defn get-smallest-distance [friend]
  (let [x (apply min (remove nil? [9999999999999999 ; if distance couldn't be calculated, treat as very distant
                                   (get-in friend [:distance :name-name])
                                   (get-in friend [:distance :name-main])
                                   (get-in friend [:distance :main-name])
                                   (get-in friend [:distance :main-main])]))]
    (println "distance: " x)
    x))

(defn closer-than [max-distance dist-key]
  (fn [friend]
    (let [smallest-distance (get-in friend [:distance dist-key])]
      (and (< smallest-distance max-distance)
           (not (nil? smallest-distance))))))

(def round-two-decimals #(pp/cl-format nil "~,2f" %))

(defn preify [obj] (with-out-str (pp/pprint obj)))

(defn Friend [k friend]
  (let [twitter-pic    (:profile_image_url_large friend)
        twitter-name   (:name friend)
        twitter-handle (:screen-name friend)
        twitter-link   (str "http://twitter.com/" twitter-handle)
        location       (:main-location friend)
        twitter-href   {:href twitter-link :target "_blank"}
        lat            (round-two-decimals (:lat (:main-coords friend)))
        lng            (round-two-decimals (:lng (:main-coords friend)))]
    [:div.friend {:key twitter-name}
     [:a twitter-href
      [:div.twitter-pic [:img {:src twitter-pic :key k}]]]
     [:div.right-section
      [:a.top twitter-href
       [:span.name twitter-name]
       [:span.handle "@" twitter-handle]]
      ;; [:span.name " dist: " (round-two-decimals (get-smallest-distance friend))]
      [:div.bottom
       [:a {:href (str "https://www.google.com/maps/search/" lat "%20" lng "?hl=en&source=opensearch")
            :target "_blank"}
        [:span.location location]
        [:span.coordinates [:span.coord lat] " " [:span.coord lng]]]
       #_[:pre (preify friend)]]]]))

(defn get-close-friends [distance-key max-distance]
  (->> @friends
       (sort-by #(get-in % [:distance distance-key]))
       (filter (closer-than max-distance distance-key))))

(defn render-friends-list [friends-list-key]
  (let [friends-list (get-close-friends friends-list-key 100)
        list-count   (count friends-list)]
    [:<>
     [:p.location-info friends-list-key ": " list-count]
     [:hr]
     (if (> list-count 0)
       [:div.friends (map-indexed Friend friends-list)]
       [:div.no-friends-found "no friends found"])
     [:br] [:br] [:br]]))

(defn app-container []
  (let [main-location (:main-location @current-user)
        name-location (:name-location @current-user)]
    [:div
     (nav)
     [:div.container
      ;; [:pre "(prn-str @current-user):"] [:pre (prn-str @current-user)] [:br] [:br] 
      (Friend nil @current-user)

      [:div.location-info
       [:p "you are based in: "      [:span.location main-location]]
       (when name-location
         [:p "your current location: " [:span.location name-location]])]

      [:hr] [:br]
      (if (= :loading @friends)
        [:div.loading "loading..."]
        [:<>
      ;; [:p.location-info "main-main – friends based near " [:span.location main-location] ":"]
         (when-not (empty? main-location)
           [:<>
            (render-friends-list :main-main)
            (render-friends-list :main-name)])

         (when-not (empty? name-location)
           [:<>
            (render-friends-list :name-name)
            (render-friends-list :name-main)])])

      #_[:div.sticky-footer (music)]

      ;; [:p.location-info "friends who may be near " [:span.location main-location] " right now:"]
      ;; [:hr]
      ;; [:table
      ;;  [:tbody
      ;;   [:tr table-header]
      ;;   (map-indexed friend-row friends-main-main)]]

      ;; [:p.location-info "all of your friends with their locations:"]
      ;; [:hr]
      ;; [:table
      ;;  [:tbody
      ;;   [:tr table-header]
      ;;   (map-indexed friend-row friends-sorted-by-distance)]]
      ]]))

(r/render-component [app-container] (goog.dom/getElement "app"))

