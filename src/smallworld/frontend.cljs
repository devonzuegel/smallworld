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
(defonce welcome-flow-complete? (r/atom false))
; TODO: store this on the session so it doesn't get reset on refresh
; TODO: maybe this should only be show when they first sign up, not every time they log in

(def debug? false)

(defn fetch [route callback]
  (-> (.fetch js/window route)
      (.then #(.json %))
      (.then #(js->clj % :keywordize-keys true))
      (.then (fn [result]
               (when debug?
                 (println route ":")
                 (pp/pprint result))
               (callback result)))
      (.catch (fn [error]
                (println (str "error fetching " route ":"))
                (js/console.log error)
                (println (str "retrying..."))
                (fetch route callback)))))

(defn add-friends-to-map []
  (when @mapbox/the-map ; don't add friends to map if there is no map
    (doall ; force no lazy load
     (for [friend @friends]
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
                                      (mapbox/update-markers-size))))))

(defn nav []
  [:div.nav
   [:a#logo-animation.logo {:href "/"}
    (decorations/animated-globe)

    [:div.logo-text "small world"]]
   [:span.fill-nav-space]
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

(defn render-friends-list [friends-list-key verb-gerund location-name]
  (let [friends-list      (if (= :loading @friends)
                            []
                            (get-close-friends friends-list-key 100))
        list-count        (count friends-list)
        friend-pluralized (if (= list-count 1) "friend" "friends")
        say-pluralized    (if (= list-count 1) "says" "say")]

    [:div.friends-list
     (if (= :loading @friends)
       [:div.loading
        (decorations/simple-loading-animation)
        "the first time takes a while to load"]

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
                    (js/setTimeout add-friends-to-map 2000)))

; fetch current-user once & then again every 30 seconds
(fetch "/session" session/update!)
(js/setInterval #(fetch "/session" session/update!) (* 30 1000))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn loading-screen []
  [:div.center-vh (decorations/simple-loading-animation)])

(defn logged-out-screen []
  [:div.welcome
   [:div.hero
    [:h1 "welcome to" [:br] "Small World"]
    [:div#logo-animation.logo (decorations/animated-globe)]
    [:h2
     [:a#login-btn {:href "login"} (decorations/twitter-icon) "log in with Twitter"]]]
   [:div.steps
    [:p [:b "step 1:"] " log in with Twitter"]
    [:p [:b "step 2:"] " update what city you're in"]
    [:p [:b "step 3:"] " see a map of who's nearby"]]
   [:div.info
    [:p "Small World uses the location from your" [:br]
     [:a {:href "https://twitter.com/settings/profile"} "Twitter profile"] " to find nearby friends"]]
   #_[:div.faq
      [:div.question
       [:p [:b "Q: how does small world work?"]]
       [:p "small world checks to see if the people you follow on Twitter have updated their location.  it looks at two places:"]
       [:ul
        [:li "their display name, which small world interprets as they're traveling to that location"]
        [:li "their location, which small world interprets as they're living in that location"]]]

      [:hr]

      [:div.question
       [:p [:b "Q: why isn't my friend showing up"]]
       [:p "they may not have their location set on Twitter (either in their name or in the location field), or small world may not be able to parse the location yet."]
       [:p "if they have their location set but it's just not showing up in the app, please " [:a {:href "https://github.com/devonzuegel/smallworld"} "open a GitHub issue"] " and share more so I can improve the city parser."]]]])

(defn logged-in-screen []
  [:<>
   (nav)
   (let [main-location (:main-location @session/store*)
         name-location (:name-location @session/store*)]
     [:<>
      [:div.container
       [:div.current-user (render-user nil @session/store*)]

       [:<>
        (when (and (empty? main-location)
                   (empty? name-location))
          [:div.no-locations-info
           ; TODO: improve the design of this state
           [:p "you haven't shared a location on Twitter, so we can't show you friends who are close by"]
           [:br]
           [:p "we pull from two places to find your location:"]
           [:ol
            [:li "your Twitter profile location"]
            [:li "your Twitter display name"]]
           [:br]
           [:p "update these fields in your " [:a {:href "https://twitter.com/settings/location"} "Twitter settings"]]
           [:br]
           [:p "if you don't want to update your Twitter settings, you can still explore the map below"]])

        (when-not (empty? main-location)
          [:div.category
           [:span.current-user-location main-location]
           (render-friends-list :main-main "living"   main-location)
           (render-friends-list :main-name "visiting" main-location)])

        (when-not (empty? name-location)
          [:div.category
           [:span.current-user-location name-location]
           (render-friends-list :name-name "living"   name-location)
           (render-friends-list :name-main "visiting" name-location)])

        (when (= :loading @friends)
          [:pre {:style {:margin "24px auto" :max-width "360px"}}
           "🚧  I realize this is taking ages to load, apologies!  I'm working on "
           "pagination, so it should be faster soon.  thanks for being Small World's first user!"])

        [:div.refresh-friends {:style {:margin-top "64px" :text-align "center"}}
         [:div {:style {:margin-bottom "12px" :font-size "0.9em"}}
          "does your friends data look out of date?"]
         [:a.btn {:href "#"
                  :on-click (fn []
                              (fetch "/friends/refresh"
                                     (fn [result]
                                       (doall (map (mapbox/remove-friend-marker (:screen-name @session/store*))
                                                   @mapbox/markers))
                                       (reset! friends result)
                                       (add-friends-to-map))))}
          "refresh friends"]
         [:div {:style {:margin-top "12px" :font-size "0.8em" :opacity "0.6" :font-family "Inria Serif, serif" :font-style "italic"}}
          "note: this takes several seconds to run"]]

        (when debug?
          [:<>
           [:br]
           [:pre "@current-user:\n\n"  (util/preify @session/store*)]
           [:br]
           (if (= @friends :loading)
             [:pre "@friends is still :loading"]
             [:pre "@friends (" (count @friends) "):\n\n" (util/preify @friends)])])]]

      (let [main-coords (:main-coords @session/store*)
            name-coords (:name-coords @session/store*)]
        (mapbox/mapbox
         {:lng-lat (or (when name-coords [(:lng name-coords) (:lat name-coords)])
                       (when main-coords [(:lng main-coords) (:lat main-coords)]))
          :location (or name-location
                        main-location)
          :user-img (:profile_image_url_large @session/store*)
          :user-name (:name @session/store*)
          :screen-name (:screen-name @session/store*)}))])])

(defonce locations (r/atom :loading))

(defn location-field [{id          :id
                       label       :label
                       placeholder :placeholder
                       value       :value
                       update!     :update!}]
  [:div.location-field {:id id}
   [:label label] [:br]
   [:input.location-input
    {:type "text"
     :id (str id "-input")
     :value value
     :auto-complete "off"
     :on-change #(let [input-elem (.-target %)
                       new-value  (.-value input-elem)]
                   (update! new-value))
     :placeholder placeholder}]
   (decorations/edit-icon)])

(defn welcome-flow-screen []
  [:div.welcome-flow

   [:div.you-signed-in-as
    [:p "you signed in as:"]
    (render-user nil @session/store*)
    (when debug? [:pre (util/preify @session/store*)])]

   [:hr]

   (let [main-location (or (:main-location @session/store*) "")
         name-location (or (:name-location @session/store*) "")]

     (when (= :loading @locations) ; TODO: clean this up, it's kinda hacky
       (reset! locations {:main main-location
                          :name name-location}))

     [:div.location-fields
      (location-field {:id "main-location"
                       :label (if (str/blank? main-location)
                                [:span ; TODO: improve the UX of this state
                                 "you haven't set a location on Twitter.  you can add one in your "
                                 [:a {:href "https://twitter.com/settings/profile"} "Twitter settings"]
                                 " and then refresh, or you can add a location just to Small World:"]
                                "based on your profile location, you’re in...")
                       :placeholder "what city do you live in?"
                       :value (or (:main @locations) "")
                       :update! #(swap! locations assoc :main %)})
      [:br]
      (location-field {:id "name-location"
                       :label (if (str/blank? name-location)
                                (str ; TODO: improve the UX of this state
                                 "based on your display name, it looks like "
                                 "you’re not traveling right now.  add a destination "
                                 "to see who’s close by:")
                                "based on your profile location, you’re in...")
                       :placeholder "any plans to travel?"
                       :value (or (:name @locations) "")
                       :update! #(swap! locations assoc :name %)})

      (when debug? [:pre "@locations: " (util/preify @locations)])])
   [:hr]
   [:div.email-options
    [:pre
     "🚧  TODO: add these fields  🚧" [:br] [:br] [:br]
     "would you like email notifications when your friends are nearby?" [:br] [:br]
     "- yes, send me daily digests" [:br]
     "- yes, send me weekly digests" [:br]
     "- no, please don’t email me" [:br] [:br] [:br]
     "what email should we send them to?" [:br] [:br]
     "  [_________________]"]]
   [:hr]
   ; TODO: add a nice animation for this transition
   [:a.btn {:href "#" :on-click #(reset! welcome-flow-complete? true)}
    "time for an adventure "]
   [:hr]
   [:div.heads-up
    [:pre "🚧  heads up  🚧" [:br] [:br]
     "I'm currently working on this sign in flow, so you might see some squirrely stuff.  "
     "in particular, if you updated your location data on this page, it won't saved.  " [:br] [:br]
     "any other data entry you do on future screens will be saved as you'd expect "
     "because those features are complete." [:br] [:br] "thanks for being an early user!"]]
   [:br] [:br]])

(defn not-found-404-screen []
  [:h1 "404 – not found :("])

(defn app-container []
  (condp = (.-pathname (.-location js/window))
    "/" (condp = @session/store*
          :loading (loading-screen)
          session/blank (logged-out-screen)
          (if @welcome-flow-complete?
            (logged-in-screen)
            (welcome-flow-screen)))
    (not-found-404-screen)))

(r/render-component [app-container] (goog.dom/getElement "app"))

