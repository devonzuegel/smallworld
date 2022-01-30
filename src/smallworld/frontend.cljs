(ns smallworld.frontend
  (:require [reagent.core :as r]
            [smallworld.current-user :as cu]
            [smallworld.mapbox :as mapbox]
            [smallworld.decorations :as decorations]
            [clj-fuzzy.metrics :as fuzzy]
            [clojure.pprint :as pp]
            [clojure.string :as str]
            [cljsjs.mapbox]
            [goog.dom]
            [goog.dom.classlist :as gc]))

(def debug? true)

(defonce current-user (r/atom cu/dummy-data))
(defonce friends      (r/atom :loading))

(defn fetch [route callback]
  (-> (.fetch js/window route)
      (.then #(.json %))
      (.then #(js->clj % :keywordize-keys true))
      (.then (fn [result]
               (when debug?
                 (println route ":")
                 (pp/pprint result))
               (callback result)))))

(fetch "/friends" (fn [result]
                    (let [result (if debug? cu/dummy-data-friends result)]
                      (reset! friends result)
                      (reset! mapbox/friends result))))
(fetch "/session" (fn [result]
                    (reset! current-user (if debug? cu/dummy-data result))))

(defn nav []

  [:div.nav
   [:div#logo-animation.logo
    (decorations/animated-globe)

    [:div.logo-text "small world"]]
   [:span.fill-nav-space]
   [:a {:href "#about"} "about"]
   [:span.links-spacer "·"]
   [:a {:href "/logout"}
    "log out" [:b.screen-name " @" (:screen-name @current-user)]]])

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn preify [obj] (with-out-str (pp/pprint obj)))

(defn closer-than [max-distance dist-key]
  (fn [friend]
    (let [smallest-distance (get-in friend [:distance dist-key])]
      (and (< smallest-distance max-distance)
           (not (nil? smallest-distance))))))

(defn round-to-int [num]
  (let [formatted  (pp/cl-format nil "~,0f" num)
        no-decimal (str/replace formatted #"\." "")]
    no-decimal))

(defn get-smallest-distance [friend]
  (apply min (remove nil? [9999999999999999 ; if distance couldn't be calculated, treat as very distant
                           (get-in friend [:distance :name-name])
                           (get-in friend [:distance :name-main])
                           (get-in friend [:distance :main-name])
                           (get-in friend [:distance :main-main])])))

(defn Friend [k friend]
  (let [twitter-pic    (:profile_image_url_large friend)
        twitter-name   (:name friend)
        twitter-handle (:screen-name friend)
        twitter-link   (str "http://twitter.com/" twitter-handle)
        location       (:main-location friend)
        twitter-href   {:href twitter-link :target "_blank" :title "Twitter"}
        lat            (:lat (:main-coords friend))
        lng            (:lng (:main-coords friend))
        distance       (get-smallest-distance friend)]
    [:div.friend {:key twitter-name}
     [:a twitter-href
      [:div.twitter-pic [:img {:src twitter-pic :key k}]]]
     [:div.right-section
      [:a.top twitter-href
       [:span.name twitter-name]
       [:span.handle "@" twitter-handle]]
      [:div.bottom
       [:a {:href (str "https://www.google.com/maps/search/" lat "%20" lng "?hl=en&source=opensearch")
            :title "Google Maps"
            :target "_blank"}
        [:span.location location]
        (when (< distance 1000)
          [:span.distance (round-to-int distance) " mile" (when (not= "1" (round-to-int distance)) "s") " away"])]]]]))

(defn get-close-friends [distance-key max-distance]
  (->> @friends
       (sort-by #(get-in % [:distance distance-key]))
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
       (decorations/simple-loading-animation)

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
  [:div.center-vh (decorations/simple-loading-animation)])

(defn logged-out-screen []
  [:div.welcome.center-vh
   [:h1 "welcome to" [:br] "Small World"]
   [:div#logo-animation.logo (decorations/animated-globe)]
   [:h2
    [:a#login-btn {:href "login"} "sign in"]
    [:br] "to connect with friends"]])

(defn logged-in-screen []
  [:<>
   (nav)
   (let [main-location (:main-location @current-user)
         name-location (:name-location @current-user)]
     [:div.container {:id "scrollable-content"}
      [:div.current-user (Friend nil @current-user)]

      [:<>
       (when-not (empty? main-location)
         [:div.category
          [:span.current-user-location main-location]
            ;; [:div.location-info.current [:p "you are based in: " [:span.location main-location]]]
          (render-friends-list :main-main "living"   main-location)
          (render-friends-list :main-name "visiting" main-location)])

       (when-not (empty? name-location)
         [:div.category
          [:span.current-user-location name-location]
            ;; [:div.location-info.current [:p "your current location: " [:span.location name-location]]]
          (render-friends-list :name-name "living"   name-location)
          (render-friends-list :name-main "visiting" name-location)])

       (let [main-coords (:main-coords @current-user)
             name-coords (:name-coords @current-user)]
         [mapbox/mapbox
          {:lng-lat (or (when name-coords [(:lng name-coords) (:lat name-coords)])
                        (when main-coords [(:lng main-coords) (:lat main-coords)]))
           :location (or name-location
                         main-location)
           :user-img (:profile_image_url_large @current-user)
           :user-name (:name @current-user)
           :screen-name (:screen-name @current-user)}])

         ;; for debugging:
       [:pre "@current-user:\n\n"  (preify @current-user)]
       [:br]
       [:pre "count @friends:  " (try (count @friends)
                                      (catch js/Error e (str @friends)))]
       [:br]
       [:pre "@friends:\n\n"       (preify @friends)]
        ;;
       ]])])

(defn app-container []
  (condp = @current-user
    :loading (loading-screen)
    cu/default-state (logged-out-screen)
    (logged-in-screen)))

(r/render-component [app-container] (goog.dom/getElement "app"))

