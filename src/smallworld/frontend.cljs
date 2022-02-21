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
(defonce welcome-flow-complete? (r/atom :loading))
(defonce email-address (r/atom :loading))
; TODO: store this on the session so it doesn't get reset on refresh
; TODO: maybe this should only be show when they first sign up, not every time they log in

(def debug? false)

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
(defn fetch-post [route body]
  (let [request (new js/Request
                     route
                     #js {:method "POST"
                          :body (.stringify js/JSON (clj->js body))
                          :headers (new js/Headers #js{:Content-Type "application/json"})})]
    (->
     (js/fetch request)
     (.then (fn [res] (.json res)))
     (.then (fn [res] (.log js/console res))))))

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
                             [:u verb-gerund] " nearby"]]
          [:div.friends (map-indexed render-user friends-list)]]

         [:div.friends
          [:div.no-friends-found
           "0 friends shared that they're " [:u verb-gerund]]]))]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

; TODO: only do this on first load of logged-in-screen, not on every re-render
; and not for all the other pages
(fetch "/friends" (fn [result]
                    (reset! friends result)
                    ; TODO: only run this on the main page, otherwise you'll get errors
                    ; wait for the map to load – this is a hack & may be a source of errors ;)
                    (js/setTimeout add-friends-to-map 2000))
       :retry? true)

(fetch "/settings" (fn [result]
                     (reset! welcome-flow-complete? (:welcome_flow_complete result))
                     (reset! email-address (:email result))))

; fetch current-user once & then again every 30 seconds
(fetch "/session" #((session/update! %)
                    (reset! email-address (:email %))))
(js/setInterval (fn [] (fetch "/session" #((session/update! %)
                                           (reset! email-address (:email %)))))
                (* 30 1000))

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

        (when (not= :loading @friends)
          [:div {:style {:line-height "1.2em" :padding "12px 6px"}}
           [:br]
           [:p.location-info ; TODO: this should use different classes
            "you are following " [:b (count @friends)] " people on Twitter.  "
            "if they've shared a location, they'll show up in the map below."]])

        (when (= :loading @friends)
          [:pre {:style {:margin "24px auto" :max-width "360px"}}
           "🚧  this wil take a while to load, apologies.  I'm working on "
           "making it faster.  thanks for being Small World's first user!"])

        #_[:div.refresh-friends {:style {:margin-top "64px" :text-align "center"}}
           [:div {:style {:margin-bottom "12px" :font-size "0.9em"}}
            "does the data for your friends look out of date?"]
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
                       tabindex    :tab-index
                       auto-focus  :auto-focus
                       label       :label
                       placeholder :placeholder
                       value       :value
                       update!     :update!}]
  [:div.field.location-field {:id id}
   [:label label] [:br]
   [:div
    [:input.location-input
     {:type "text"
      :tab-index tabindex
      :auto-focus auto-focus
      :id (str id "-input")
      :name (str id "-input")
      :value value
      :auto-complete "off"
      :on-change #(let [input-elem (.-target %)
                        new-value  (.-value input-elem)]
                    (update! new-value))
      :placeholder placeholder}]
    (decorations/edit-icon)]
   [:div.small-info-text "this will not update your Twitter profile"]
   [:br]
   [:iframe {:width "200px" :height "100px" :src "https://api.mapbox.com/styles/v1/devonzuegel/cj8rx2ti3aw2z2rnzhwwy3bvp.html?title=false&access_token=pk.eyJ1IjoiZGV2b256dWVnZWwiLCJhIjoickpydlBfZyJ9.wEHJoAgO0E_tg4RhlMSDvA&zoomwheel=false#3.48/36.44/-121.17" :title "Curios Bright" :style {:border "none" :background "#9dc7d9" :border-radius "40px"}}]])

(defn input-by-name [name & [other]]
  (.querySelector js/document (str "input[name= \"" name "\"]"
                                   (or other ""))))

(defn welcome-flow-screen []
  (when (nil? @email-address) (reset! email-address (:email @session/store*)))
  [:div.welcome-flow
   [:<>
    [:p.serif {:style {:font-size "1.3em" :margin-bottom "2px"}}
     "welcome to"]
    [:h1 {:style {:font-weight "bold" :font-size "2.2em"}}
     "Small World"]]

   [:hr]

   [:div.you-signed-in-as

    (render-user nil @session/store*)]

   (when debug? [:pre (util/preify @session/store*)])


   (let [main-location (or (:main-location @session/store*) "")
         name-location (or (:name-location @session/store*) "")]

     (when (= :loading @locations) ; TODO: clean this up, it's kinda hacky
       (reset! locations {:main main-location
                          :name name-location}))

     [:div.location-fields
      [:br]
      (location-field {:id "main-location"
                       :tab-index "1"
                       :auto-focus true
                       :label (if (str/blank? main-location)
                                [:<> ; TODO: improve the UX of this state
                                 "you haven't set a location on Twitter.  you can add one in your "
                                 [:a {:href "https://twitter.com/settings/profile"} "Twitter settings"]
                                 " and then refresh, or you can add a location just to Small World:"]
                                [:<>
                                 "based on your profile location, you’re in..."])
                       :placeholder "what city do you live in?"
                       :value (or (:main @locations) "")
                       :update! #(swap! locations assoc :main %)})
      [:br]
      (location-field {:id "name-location"
                       :tab-index "2"
                       :label (if (str/blank? name-location)
                                [:<> ; TODO: improve the UX of this state
                                 "based on your display name, it looks like " [:br]
                                 "you’re not traveling right now.  " [:br] [:br]
                                 "add a destination to see who’s close by:"]
                                [:<>
                                 "based on your display name, you’re in..."])
                       :placeholder "any plans to travel?"
                       :value (or (:name @locations) "")
                       :update! #(swap! locations assoc :name %)})

      (when debug? [:pre "@locations: " (util/preify @locations)])])
   [:br]
   [:div.email-options {:tab-index "3"}
    [:p "would you like email notifications" [:br] "when your friends are nearby?"]
    [:div.radio-btns
     [:div.radio-btn
      [:input {:name "email_notification" :type "radio" :value "instant" :id "instant"}]
      [:label {:for "instant"} "yes, notify me immediately"]]
     [:div.radio-btn
      [:input {:name "email_notification" :type "radio" :value "daily" :id "daily"}]
      [:label {:for "daily"} "yes, send me daily digests"]]
     [:div.radio-btn
      [:input {:name "email_notification" :type "radio" :value "weekly" :id "weekly" :default-checked true}]
      [:label {:for "weekly"} "yes, send me weekly digests"]]
     [:div.radio-btn
      [:input {:name "email_notification" :type "radio" :value "muted" :id "muted"}]
      [:label {:for "muted"} "no, don't notify me by email"]]]
    [:br]]
   [:br]
   [:div.email-options
    [:div.email-address
     [:label "what's your email address?"] [:br]
     [:div.field
      [:input {:type "text"
               :tab-index "4"
               :id "email-address-input"
               :name "email-address-input"
               :value @email-address ; TODO: this is a hack
               :auto-complete "off"
               :on-change #(let [input-elem (.-target %)
                                 new-value  (.-value input-elem)]
                             (reset! email-address new-value))
               :placeholder "email address"}]
      (decorations/edit-icon)]]]
   [:br]
   ; TODO: add a nice animation for this transition
   [:a.btn {:href "#"
            :on-click #(let [email_pref (.-id (input-by-name "email_notification" ":checked"))
                             m-location (.-value (input-by-name "main-location-input"))
                             n-location (.-value (input-by-name "name-location-input"))]
                         (reset! welcome-flow-complete? true)
                         (fetch-post "/settings/update"
                                     {:main_location_corrected m-location
                                      :name_location_corrected n-location
                                      :email_notifications     email_pref     ; TODO: add db support
                                      :email_address           @email-address ; TODO: add db support
                                      :welcome_flow_complete   true}))}
    "let's go!"]
   [:br] [:br] [:br]
   [:div.heads-up
    [:pre "🚧  heads up  🚧" [:br] [:br]
     "I'm currently working on this sign in flow, so you updated your location "
     "data on this page, it won't saved (yet!).  " [:br] [:br]
     "any other data entry you do on future screens will be saved as you'd expect "
     "because those features are complete." [:br] [:br] "thanks for being an early user!"]]
   [:br] [:br]])

(defonce admin-data (r/atom :loading))
(defn admin-screen []
  [:div.admin-screen
   (if-not (= "devonzuegel" (:screen-name @session/store*))
     [:p {:style {:margin "30vh auto 0 auto" :text-align "center" :font-size "2em"}}
      "whoops, you don't have access to this page"]
     [:<>
      [:a.btn {:href "#"
               :on-click #(fetch "/admin-data" (fn [result]
                                                 (println "successfully fetched /admin-data:")
                                                 (pp/pprint result)
                                                 (reset! admin-data result)))}
       "load admin data"]
      [:br] [:br] [:br]
      (when (not= :loading @admin-data)
        (map (fn [key] [:details {:open true} [:summary [:b key]]
                        [:pre "count: " (count (get @admin-data key))]
                        [:pre "keys: " (util/preify (map #(or (:request_key %) (:screen_name %))
                                                         (get @admin-data key)))]
                        [:pre {:id key} (util/preify (get @admin-data key))]])
             (reverse (sort (keys @admin-data)))))])])

(defn not-found-404-screen []
  [:p {:style {:margin "30vh auto 0 auto" :text-align "center" :font-size "2em"}}
   "404 not found"])

(defn app-container []
  (condp = (.-pathname (.-location js/window))
    "/" (condp = @session/store*
          :loading (loading-screen)
          session/blank (logged-out-screen)
          (condp = @welcome-flow-complete?
            :loading (loading-screen)
            true (logged-in-screen)
            false (welcome-flow-screen)))
    "/admin" (admin-screen)
    (not-found-404-screen)))

(r/render-component [app-container] (goog.dom/getElement "app"))

