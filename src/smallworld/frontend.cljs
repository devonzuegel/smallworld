(ns smallworld.frontend
  (:require [reagent.core           :as r]
            [smallworld.session     :as session]
            [smallworld.mapbox      :as mapbox]
            [smallworld.decorations :as decorations]
            [smallworld.util        :as util]
            [clojure.pprint         :as pp]
            [clojure.string         :as str]
            [cljsjs.mapbox]
            [goog.dom]))

(defonce friends (r/atom :loading))

(def debug? false)

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
           (mapbox/add-user-marker {:lng-lat     [lng lat]
                                    :location    (:location friend)
                                    :img-url     (:profile_image_url_large friend)
                                    :user-name   (:name friend)
                                    :screen-name (:screen-name friend)
                                    :classname   "main-coords"})))

       (let [lng (:lng name-coords)
             lat (:lat name-coords)
             has-coord (and name-coords lng lat)]
         (when has-coord
           (mapbox/add-user-marker {:lng-lat     [lng lat]
                                    :location    (:location friend)
                                    :img-url     (:profile_image_url_large friend)
                                    :user-name   (:name friend)
                                    :screen-name (:screen-name friend)
                                    :classname   "name-coords"}))))))
  (when-not (nil? @mapbox/the-map)
    (.on @mapbox/the-map "loaded" #((println "upating markers size")
                                    (mapbox/update-markers-size)))))

(defn nav []
  [:div.nav
   [:a#logo-animation.logo {:href "/"}
    (decorations/animated-globe)

    [:div.logo-text "small world"]]
   [:span.fill-nav-space]
   [:a {:href "/about"} "about"]
   [:span.links-spacer "·"]
   [:a {:href "/logout"}
    "log out" [:b.screen-name " @" (:screen-name @session/store*)]]])

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

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

(defn render-friends-list [-friends friends-list-key verb-gerund location-name]
  (let [friends-list      (if (= :loading -friends)
                            []
                            (get-close-friends friends-list-key 100))
        list-count        (count friends-list)
        friend-pluralized (if (= list-count 1) "friend" "friends")
        say-pluralized    (if (= list-count 1) "says" "say")]

    [:div.friends-list
     (if (= :loading -friends)
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

; TODO: only do this on first load of logged-in-screen, not on every re-render
; and not for all the other pages
(fetch "/friends" (fn [result]
                    (reset! friends result)
                    ; TODO: only run this on the main page, otherwise you'll get errors
                    ; wait for the map to load – this is a hack & may be a source of errors ;)
                    (js/setTimeout #(add-friends-to-map @friends) 2000)))

; fetch current-user once & then again every 30 seconds
(fetch "/session" session/update!)
(js/setInterval #(fetch "/session" session/update!) (* 30 1000))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn loading-screen []
  [:div.center-vh (decorations/simple-loading-animation)])

(defn logged-out-screen []
  [:div.welcome.center-vh
   [:h1 "welcome to" [:br] "small world"]
   [:div#logo-animation.logo (decorations/animated-globe)]
   [:h2
    [:a#login-btn {:href "login"} "sign in"]
    [:br] "to connect with friends"]])

(defn logged-in-screen []
  [:<>
   (nav)
   (let [main-location (:main-location @session/store*)
         name-location (:name-location @session/store*)]
     [:div.container.nav-spacer
      [:div.current-user (render-user nil @session/store*)]

      [:a.btn {:href "#"
               :on-click (fn []
                           (fetch "/friends/refresh"
                                  (fn [result]
                                    (doall (map (mapbox/remove-friend-marker (:screen-name @session/store*))
                                                @mapbox/markers))
                                    (reset! friends result)
                                    (add-friends-to-map @friends))))}
       "refresh friends – takes several seconds to run!!!"]

      [:<>
       (when-not (empty? main-location)
         [:div.category
          [:span.current-user-location main-location]
            ;; [:div.location-info.current [:p "you are based in: " [:span.location main-location]]]
          (render-friends-list @friends :main-main "living"   main-location)
          (render-friends-list @friends :main-name "visiting" main-location)])

       (when-not (empty? name-location)
         [:div.category
          [:span.current-user-location name-location]
            ;; [:div.location-info.current [:p "your current location: " [:span.location name-location]]]
          (render-friends-list @friends :name-name "living"   name-location)
          (render-friends-list @friends :name-main "visiting" name-location)])

       (let [main-coords (:main-coords @session/store*)
             name-coords (:name-coords @session/store*)]
         (mapbox/mapbox
          {:lng-lat (or (when name-coords [(:lng name-coords) (:lat name-coords)])
                        (when main-coords [(:lng main-coords) (:lat main-coords)]))
           :location (or name-location
                         main-location)
           :user-img (:profile_image_url_large @session/store*)
           :user-name (:name @session/store*)
           :screen-name (:screen-name @session/store*)}))

       (when debug?
         [:<>
          [:br]
          [:pre "@current-user:\n\n"  (util/preify @session/store*)]
          [:br]
          (if (= @friends :loading)
            [:pre "@friends is still :loading"]
            [:pre "@friends (" (count @friends) "):\n\n" (util/preify @friends)])])]])])

(defn about-screen []
  [:<>
   (nav)
   [:div.about-container.nav-spacer
    [:div.splash
     [:h2 "welcome to small world,"]
     [:h4 "a tiny tool to stay in touch with friends"]
     [:p.serif
      "small world is a Marauder's Map" [:br]
      "for the people you know on Twitter."]
     [:br]
     [:a.btn {:href "/"} "connect with friends"]]

    [:hr]

    [:p [:b "Q: how does small world work?"]]
    [:p "small world checks to see if the people you follow on Twitter have updated their location.  it looks at two places:"]
    [:ul
     [:li "their display name, which small world interprets as they're traveling to that location"]
     [:li "their location, which small world interprets as they're living in that location"]]

    [:hr]

    [:p [:b "Q: why isn't my friend showing up in the list or on the map?"]]
    [:p "they may not have their location set on Twitter (either in their name or in the location field), or small world may not be able to parse the location yet."]
    [:p "if they have their location set but it's just not showing up in the app, please " [:a {:href "https://github.com/devonzuegel/smallworld"} "open a GitHub issue"] " and share more so I can improve the city parser."]
    [:<>]]])

(defn not-found-404-screen []
  [:h1 "404 – not found :("])

(defn app-container []
  (condp = (.-pathname (.-location js/window))
    "/about" (about-screen)
    "/" (condp = @session/store*
          :loading (loading-screen)
          session/blank (logged-out-screen)
          (logged-in-screen))
    (not-found-404-screen)))

(r/render-component [app-container] (goog.dom/getElement "app"))

