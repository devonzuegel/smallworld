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

(defonce current-user (r/atom :loading))
(defonce friends      (r/atom :loading))

(def debug? true)

(defn fetch [route callback]
  (-> (.fetch js/window route)
      (.then #(.json %))
      (.then #(js->clj % :keywordize-keys true))
      (.then (fn [result]
               (when debug?
                 (println route ":")
                 (pp/pprint result))
               (callback result)))))

(defn add-friends-to-map [-friends]
  (doall ; force no lazy load
   (for [friend -friends]
     (let [main-coords (:main-coords friend)
           name-coords (:name-coords friend)]

       ; TODO: make the styles for main vs name coords different
       (let [lng (:lng main-coords)
             lat (:lat main-coords)
             has-coord (and main-coords lng lat)]
         (when has-coord
           (mapbox/add-friend-marker {:lng-lat     [lng lat]
                                      :location    (:location friend)
                                      :img-url     (:profile_image_url_large friend)
                                      :user-name   (:name friend)
                                      :screen-name (:screen_name friend)
                                      :classname   "main-coords"})))

       (let [lng (:lng name-coords)
             lat (:lat name-coords)
             has-coord (and name-coords lng lat)]
         (when has-coord
           (mapbox/add-friend-marker {:lng-lat     [lng lat]
                                      :location    (:location friend)
                                      :img-url     (:profile_image_url_large friend)
                                      :user-name   (:name friend)
                                      :screen-name (:screen_name friend)
                                      :classname   "name-coords"})))))))

(fetch "/friends" (fn [result]
                    (reset! friends result)
                    ; wait for the map to load – this is a hack & may be a source of errors ;)
                    (js/setTimeout #(add-friends-to-map @friends) 500)))
(fetch "/session" #(reset! current-user %))

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

(defn render-user [k user]
  (let [twitter-pic    (:profile_image_url_large user)
        twitter-name   (:name user)
        twitter-handle (:screen-name user)
        twitter-link   (str "http://twitter.com/" twitter-handle)
        location       (:main-location user)
        twitter-href   {:href twitter-link :target "_blank" :title "Twitter"}
        lat            (:lat (:main-coords user))
        lng            (:lng (:main-coords user))
        distance       (get-smallest-distance user)]
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
          [:div.friends (map-indexed render-user friends-list)]]

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
     [:div.container
      [:div.current-user (render-user nil @current-user)]

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


       (when debug?
         [:<>
          [:br]
          [:pre "@current-user:\n\n"  (preify @current-user)]
          [:br]
          [:pre "@friends (take 20):\n\n"       (preify (take 20 @friends))]])]])])

(defn app-container []
  (condp = @current-user
    :loading (loading-screen)
    cu/empty-session (logged-out-screen)
    (logged-in-screen)))

(r/render-component [app-container] (goog.dom/getElement "app"))

