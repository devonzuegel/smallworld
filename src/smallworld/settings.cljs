(ns smallworld.settings
  (:require [reagent.core           :as r]
            [smallworld.session     :as session]
            [smallworld.decorations :as decorations]
            [smallworld.util        :as util]
            [clojure.pprint         :as pp]
            [clojure.string         :as str]
            [smallworld.user-data   :as user-data]))

(def debug? false)
(defonce *locations     (r/atom :loading))
(defonce *settings      (r/atom :loading))
(defonce *email-address (r/atom :loading))

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
  (when (nil? @*email-address) (reset! *email-address (:email @session/store*)))
  [:div.welcome-flow
   [:<>
    [:p.serif {:style {:font-size "1.3em" :margin-bottom "2px"}}
     "welcome to"]
    [:h1 {:style {:font-weight "bold" :font-size "2.2em"}}
     "Small World"]]

   [:hr]

   [:div.you-signed-in-as

    (user-data/render-user nil @session/store*)]

   (when debug? [:pre (util/preify @session/store*)])

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
                       :update! #(swap! *locations assoc :name %)})

      (when debug? [:pre "@*locations: " (util/preify @*locations)])])
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
               :value @*email-address ; TODO: this is a hack - do it the same way as (location-input) instead, i.e. remove the atom
               :auto-complete "off"
               :on-change #(let [input-elem (.-target %)
                                 new-value  (.-value input-elem)]
                             (reset! *email-address new-value))
               :placeholder "email address"}]
      (decorations/edit-icon)]]]
   [:br]
   [:a.btn {:href "#"
            :on-click #(let [new-settings {:main_location_corrected (.-value (input-by-name "main-location-input"))
                                           :name_location_corrected (.-value (input-by-name "name-location-input"))
                                           :email_address           (.-value (input-by-name "email-address-input"))
                                           :email_notifications     (.-id (input-by-name "email_notification" ":checked"))
                                           :welcome_flow_complete   true}]
                         (reset! *settings new-settings)
                         ; TODO: shouldn't have to refresh the data from Twitter at this stage (which is
                         ; what's happening right now) - we just need to recalculate the distances based
                         ; on the new location provided in the welcome flow
                         (util/fetch-post "/settings/update" new-settings user-data/recompute-friends))}
    "let's go!"]
   [:br] [:br] [:br] [:br] [:br]])