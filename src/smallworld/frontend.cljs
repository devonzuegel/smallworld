(ns smallworld.frontend
  (:require [reagent.core :as r]
            [smallworld.current-user :as cu]
            [smallworld.mapbox]
            [clj-fuzzy.metrics :as fuzzy]
            [clojure.pprint :as pp]
            ;; [clojure.string :as str]
            [cljsjs.mapbox]
            [goog.dom]
            [goog.dom.classlist :as gc]))

(defonce current-user (r/atom :loading))
(defonce friends      (r/atom :loading))

(defn fetch [route callback]
  (-> (.fetch js/window route)
      (.then #(.json %))
      (.then #(js->clj % :keywordize-keys true))
      (.then (fn [result]
              ;;  (println route ":")
              ;;  (pp/pprint result)
               (callback result)))))

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
   [:span.fill-nav-space]
   [:a {:href "#about"} "about"]
   [:span.links-spacer "·"]
   (if (nil? (:screen-name @current-user))
     [:a#login {:href "/login"} "sign in"]
     [:a {:href "/" :on-click logout} "log out " [:b "@" (:screen-name @current-user)]])])

(defn closer-than [max-distance dist-key]
  (fn [friend]
    (let [smallest-distance (get-in friend [:distance dist-key])]
      (and (< smallest-distance max-distance)
           (not (nil? smallest-distance))))))

(def round-two-decimals #(pp/cl-format nil "~,2f" %))

(defn Friend [k friend]
  (let [twitter-pic    (:profile_image_url_large friend)
        twitter-name   (:name friend)
        twitter-handle (:screen-name friend)
        twitter-link   (str "http://twitter.com/" twitter-handle)
        location       (:main-location friend)
        twitter-href   {:href twitter-link :target "_blank" :title "Twitter"}
        lat            (round-two-decimals (:lat (:main-coords friend)))
        lng            (round-two-decimals (:lng (:main-coords friend)))]
    [:div.friend {:key twitter-name}
     [:a twitter-href
      [:div.twitter-pic [:img {:src twitter-pic :key k}]]]
     [:div.right-section
      [:a.top twitter-href
       [:a.name twitter-name]
       [:a.handle "@" twitter-handle]]
      ;; [:span.name " dist: " (round-two-decimals (get-smallest-distance friend))]
      [:div.bottom
       [:a {:href (str "https://www.google.com/maps/search/" lat "%20" lng "?hl=en&source=opensearch")
            :title "Google Maps"
            :target "_blank"}
        [:span.location location]]]]]))

(defn get-close-friends [distance-key max-distance]
  (->> @friends
       (sort-by #(do #_(println (:screen-name %) "– distance:" (get-in % [:distance distance-key]))
                     (get-in % [:distance distance-key])))
       (filter (closer-than max-distance distance-key))))

(defn render-friends-list [friends-list-key verb-gerund location-name]
  (let [friends-list      (if (= :loading @friends)
                            []
                            (get-close-friends friends-list-key 100))
        list-count        (count friends-list)
        friend-pluralized (if (= list-count 1) "friend" "friends")
        say-pluralized    (if (= list-count 1) "says" "say")]

    [:div.friends-list
     (if (= :loading @friends)
       (simple-loading-animation)

       (if (> list-count 0)
         [:<>
          [:p.location-info [:<>
                             list-count " "
                             friend-pluralized " "
                             say-pluralized " they're "
                             [:u verb-gerund] " nearby:"]]
          [:div.friends (map-indexed Friend friends-list)]]

         [:div.friends
          [:div.no-friends-found
           "0 friends have shared that they're " [:u verb-gerund] "."]]))]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

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
      [:div.current-user (Friend nil @current-user)]

      [:<>
       (when-not (empty? main-location)
         [:div.category
          [:span.current-user-location main-location]
            ;; [:div.location-info.current [:p "you are based in: " [:span.location main-location]]]
          (render-friends-list :main-main "living" main-location)
          (render-friends-list :main-name "visiting"     main-location)])

       (when-not (empty? name-location)
         [:div.category
          [:span.current-user-location name-location]
            ;; [:div.location-info.current [:p "your current location: " [:span.location name-location]]]
          (render-friends-list :name-name "living" name-location)
          (render-friends-list :name-main "visiting" name-location)])

       [smallworld.mapbox/mapbox]

        ;;  ;; for debugging:
        ;;  [:pre "@current-user:\n\n"  (preify @current-user)]
        ;;  [:br]
        ;;  [:pre "count @friends:  " (try (count @friends)
        ;;                                 (catch js/Error e (str @friends)))]
        ;;  [:br]
        ;;  [:pre "@friends:\n\n"       (preify @friends)]
        ;;
       ]])])

(defn app-container []
  (condp = @current-user
    :loading (loading-screen)
    cu/default-state (logged-out-screen)
    (logged-in-screen)))

(r/render-component [app-container] (goog.dom/getElement "app"))

