(ns smallworld.frontend
  (:require [reagent.core :as r]
            [smallworld.current-user :as cu]
            [smallworld.session-storage :as session-storage]
            [clj-fuzzy.metrics :as fuzzy]
            [clojure.pprint :as pp]
            [clojure.string :as str]
            [goog.dom]
            [goog.dom.classlist :as gc]))

(defonce storage session-storage/local-storage)
(defonce current-user (r/atom :loading))
(defonce friends (r/atom :loading))

(defn fetch [route callback]
  (-> (.fetch js/window route)
      (.then #(.json %))
      (.then #(js->clj % :keywordize-keys true))
      (.then (fn [result]
               (println route ":")
               (pp/pprint result)
               (callback result)))))

;; TODO: only fetch friends if current-user is set
(fetch "/friends" #(reset! friends %))
(fetch "/session" #(reset! current-user %))

(defn animated-globe []
  (let [handle-hover (fn [] (let [elem (goog.dom/getElement "logo-animation")
                                  start-animation #(gc/remove elem "no-animation")
                                  stop-animation #(gc/add elem "no-animation")
                                  stop-after-iteration #((.addEventListener elem "webkitAnimationIteration" stop-animation) ;; for Chrome
                                                         (.addEventListener elem "animationiteration" stop-animation) ;; for Firefox
                                                         (.addEventListener elem "MSAnimationIteration" stop-animation) ;; for IE
                                                         (.addEventListener elem "animationiteration" stop-animation))]
                              (.addEventListener elem "mouseover" start-animation)
                              (.addEventListener elem "mouseout" stop-after-iteration)))]
    ;; give time to load the animation
    (js/setTimeout handle-hover 1000))
  [:div {:class "globe-loader fas fa-globe-americas"} [:i.fas.fa-plane]])

(defn simple-loading-animation []
  [:svg.loader
   [:path {:fill "#fff"
           :d "M73,50c0-12.7-10.3-23-23-23S27,37.3,27,50 M30.9,50c0-10.5,8.5-19.1,19.1-19.1S69.1,39.5,69.1,50"}]])

(defn logout []
  (fetch "/logout" #(reset! current-user {})))

(defn nav []

  [:div.nav
   [:div#logo-animation.logo
    (animated-globe)

    [:div.logo-text "small world"]]
   [:div.links
    [:a "about"]
    [:span.links-spacer "·"]
    (if (nil? (:screen-name @current-user))
      [:a#login {:href "/login"} "sign in"]
      [:a {:href "/" :on-click logout} "log out " [:b "@" (:screen-name @current-user)]])]])

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
     #_[:td (location-name-similarity friend)]]))

(def table-header (map-indexed (fn [i header] [:th {:key i} header])
                               friend-row-headers))

(defn get-smallest-distance [friend]
  (let [x (apply min (remove nil? [9999999999999999 ; if distance couldn't be calculated, treat as very distant
                                   (get-in friend [:distance :name-name])
                                   (get-in friend [:distance :name-main])
                                   (get-in friend [:distance :main-name])
                                   (get-in friend [:distance :main-main])]))]
    ;; (println "distance: " x)
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
       (sort-by #(do #_(println (:screen-name %) "– distance:" (get-in % [:distance distance-key]))
                     (get-in % [:distance distance-key])))
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
    ;;  [:br] [:br] [:br]
     ]))

(defn loading-screen []
  [:div.center-vh (simple-loading-animation)])

(defn logged-out-screen []
  [:div.welcome.center-vh
   [:h1 "welcome to Small World"]
   [:div#logo-animation.logo (animated-globe)]
   [:h2
    [:a#login-btn {:href "login"} "sign in " [:span.arrow "→"]]
    [:br] "to connect with friends"]])

(defn logged-in-screen []
  [:<>
   (nav)
   (let [main-location (:main-location @current-user)
         name-location (:name-location @current-user)]
     [:div.container
      [:pre "@current-user:\n" (preify @current-user)] [:br] [:br]

      (Friend nil @current-user)
      [:div.location-info
       [:p "you are based in: "      [:span.location main-location]]
       (when name-location
         [:p "your current location: " [:span.location name-location]])]

      [:hr] [:br]

      (if (= :loading @friends)
        (simple-loading-animation) ;; TODO: replace this with list of empty Friends to make the transition less jarring
        [:<>
         (when-not (empty? main-location)
           [:<>
            (render-friends-list :main-main)
            (render-friends-list :main-name)])

         (when-not (empty? name-location)
           [:<>
            (render-friends-list :name-name)
            (render-friends-list :name-main)])

         ;; for debugging:
         (when (seq? @friends)
           [:pre "count @friends:\n" (count @friends)])])])])

(defn app-container []
  (condp = @current-user
    :loading (loading-screen)
    cu/default-state (logged-out-screen)
    (logged-in-screen)))

(r/render-component [app-container] (goog.dom/getElement "app"))

