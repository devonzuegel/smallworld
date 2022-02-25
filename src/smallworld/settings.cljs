(ns smallworld.settings
  (:require [reagent.core           :as r]
            [smallworld.session     :as session]
            [smallworld.decorations :as decorations]
            [smallworld.util        :as util]
            [smallworld.mapbox      :as mapbox]
            [clojure.string         :as str]
            [smallworld.user-data   :as user-data]))

(def debug? false)
(defonce *locations      (r/atom :loading))
(defonce *minimaps       (r/atom {}))
(defonce *settings       (r/atom :loading))
(defonce *email-address  (r/atom :loading))
(defonce *form-errors    (r/atom {}))

(defn fetch-coordinates! [minimap-id input]
  (util/fetch-post "/coordinates" {:location-name input}
                   (fn [result]
                     (.flyTo (get @*minimaps minimap-id)
                             #js{:essential true ; this animation is essential with respect to prefers-reduced-motion
                                 :zoom 4
                                 :center #js[(:lng result) (:lat result)]}))))

(def fetch-coordinates-debounced! (util/debounce fetch-coordinates! 300))

(def email_notifications_options ["instant" "daily" "weekly" "muted"])

(defn minimap [minimap-id location-name]
  (r/create-class {:component-did-mount
                   (fn [] ; this should be called just once when the component is mounted
                     (swap! *minimaps assoc minimap-id
                            (new js/mapboxgl.Map
                                 #js{:container minimap-id
                                     :key    (get-in mapbox/config [mapbox/style :access-token])
                                     :style  (get-in mapbox/config [mapbox/style :style])
                                     :center (clj->js mapbox/Miami) ; TODO: center on location they provie to Twitter
                                     :interactive false ; makes the map not zoomable or draggable
                                     :attributionControl false ; removes the Mapbox copyright symbol
                                     :zoom 4
                                     :maxZoom 8
                                     :minZoom 0}))
                     ; zoom out if they haven't provided a location
                     (if (clojure.string/blank? location-name)
                       (.setZoom (get @*minimaps minimap-id) 0)))
                   :reagent-render (fn [] [:div {:id minimap-id}])}))

(defn location-field [{id          :id
                       tabindex    :tab-index
                       auto-focus  :auto-focus
                       label       :label
                       placeholder :placeholder
                       value       :value
                       update!     :update!}]
  (let [minimap-id (str "minimap--" id)]
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
        :autoComplete "off"
        :auto-complete "off"
        :on-change #(let [input-elem (.-target %)
                          new-value  (.-value input-elem)]
                      (fetch-coordinates-debounced! minimap-id new-value)
                      (update! new-value))
        :placeholder placeholder}]
      (decorations/edit-icon)]
     [:div.small-info-text "this will not update your Twitter profile"]
     [:br]
     [:div.mapbox-container
      [minimap minimap-id value]
      (when-not (clojure.string/blank? value)
        [:div.center-point])]]))

(defn input-by-name [name & [other]]
  (.querySelector js/document (str "input[name= \"" name "\"]"
                                   (or other ""))))

(defn add-form-error [id error-msg] (swap! *form-errors assoc id error-msg))

(defn invalid-email? [email]
  (let [regex-pattern #"(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\x01-\x08\x0b\x0c\x0e-\x1f\x21\x23-\x5b\x5d-\x7f]|\\[\x01-\x09\x0b\x0c\x0e-\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\x01-\x08\x0b\x0c\x0e-\x1f\x21-\x5a\x53-\x7f]|\\[\x01-\x09\x0b\x0c\x0e-\x7f])+)\])"
        regex-result (re-matches regex-pattern email)]
    (nil? regex-result)))

(defn valid-inputs!? [{email_address       :email_address
                       email_notifications :email_notifications}]
  (reset! *form-errors {})

  ; email_notifications
  (when (contains? email_notifications_options email_notifications)
    (add-form-error :email_notifications (str "must be one of: " (str email_notifications_options))))

  ; email_address
  (cond
    (clojure.string/blank? email_address)
    (add-form-error :email-address-input "oops, email can't be blank!")

    (invalid-email? email_address)
    (add-form-error :email-address-input "oops, that's not a valid email address!"))

  ; global
  (when (> (count (keys @*form-errors))  0)
    (add-form-error :global "there was an error in the form"))

  (= 0 (count (keys @*form-errors))))


(defn submit-welcome-form []
  (let [new-settings {:main_location_corrected (.-value (input-by-name "main-location-input"))
                      :name_location_corrected (.-value (input-by-name "name-location-input"))
                      :email_address           (.-value (input-by-name "email-address-input"))
                      :email_notifications     (.-id (input-by-name "email_notification" ":checked"))
                      :welcome_flow_complete   true}]
    (when (valid-inputs!? new-settings)
      (reset! *settings new-settings)
      (util/fetch-post "/settings/update" new-settings user-data/recompute-friends))))

(defn welcome-flow-screen []
  (when (nil? @*email-address)
    (reset! *email-address (:email @session/store*)))
  [:div.welcome-flow
   [:<>
    [:p.serif {:style {:font-size "1.3em" :margin-bottom "2px"}}
     "welcome to"]
    [:h1 {:style {:font-weight "bold" :font-size "2.2em"}}
     "Small World"]]

   [:hr]

   [:div.you-signed-in-as
    (user-data/render-user nil @session/store*)]

   (let [main-location (or (:main-location @session/store*) "")
         name-location (or (:name-location @session/store*) "")]

     (when (= :loading @*locations) ; TODO: clean this up, it's kinda hacky
       (reset! *locations {:main main-location
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
                       :value (or (:main @*locations) "")
                       :update! #(swap! *locations assoc :main %)})
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
                       :value (or (:name @*locations) "")
                       :update! #(swap! *locations assoc :name %)})])
   [:br]
   [:div.email-options {:tab-index "3"}
    [:p "would you like email notifications" [:br] "when your friends are nearby? *"]
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
   [:div.email-options {:class (when (:email-address-input @*form-errors) "error")}
    [:div.email-address
     [:label "what's your email address? *"] [:br]
     [:div.field
      [:input {:type "text"
               :tab-index "4"
               :id "email-address-input"
               :name "email-address-input"
               :value @*email-address ; TODO: this is a hack - do it the same way as (location-input) instead, i.e. remove the atom
               :autoComplete "off"
               :auto-complete "off"
               :on-change #(let [input-elem (.-target %)
                                 new-value  (.-value input-elem)]
                             (reset! *email-address new-value))
               :placeholder "email address"}]
      (decorations/edit-icon)]
     [:div.error-msg (:email-address-input @*form-errors)]]]
   [:br]
   [:button.btn {:on-click submit-welcome-form} "let's go!"]
   (when debug?
     [:br] [:br]
     [:button.btn {:on-click #(reset! *form-errors {})} "clear errors"]
     [:div {:style {:text-align "left"}}
      [:br]
      [:pre "@session/store*: \n" (util/preify @session/store*)]
      [:pre "@*locations:     \n" (util/preify @*locations)]
      [:pre "@*form-errors:   \n" (util/preify @*form-errors)]])
   [:br] [:br] [:br] [:br] [:br]])