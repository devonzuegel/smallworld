(ns smallworld.admin
  #?(:cljs (:require [reagent.core           :as r]
                     [smallworld.session     :as session]
                     [smallworld.util        :as util]
                     [smallworld.decorations :as decorations]
                     [clojure.pprint         :as pp]))
  #?(:clj (:require [ring.util.response     :as response]
                    [smallworld.db          :as db]
                    [cheshire.core          :as cheshire])))

(def screen-names ["devonzuegel"
                   "devon_dos"
                   "meadowmaus"])

(defn is-admin [user]
  (let [screen-name (:screen-name user)]
    (and
     (not-empty screen-name)
     (not (nil? screen-name))
     (util/in? screen-name screen-names))))

(is-admin {:screen-name ""})


#?(:cljs
   (do
     (defonce admin-summary* (r/atom :loading))

     (defn screen [] ; TODO: fetch admin data on screen load – probably needs react effects to do it properly
       [:div.admin-screen
        (if-not (= screen-name (:screen-name @session/*store))

          (if (= :loading @session/*store)
            (decorations/loading-screen)
            [:p {:style {:margin "30vh auto 0 auto" :text-align "center" :font-size "2em"}}
             "not found"])

          [:<>
           [:a.btn {:on-click #(util/fetch "/api/v1/admin/refresh_all_users_friends" (fn [result]
                                                                                       (pp/pprint result)))}
            "refresh all users' friends + send emails"] [:br]
           [:p "this job runs every 24 hours; clicking this button will add an extra run"]
           [:br] [:br] [:br]
           [:a.btn {:on-click #(util/fetch "/api/v1/admin/summary" (fn [result]
                                                                     (pp/pprint result)
                                                                     (reset! admin-summary* result)))}
            "load admin data"]
           [:br] [:br] [:br]
           (when (not= :loading @admin-summary*)
             (map (fn [key] [:details {:open false} [:summary [:b key]]
                             [:pre "count: " (count (get @admin-summary* key))]
                             #_[:pre "keys: " (util/preify (map #(or (:request_key %) (:screen_name %))
                                                                (get @admin-summary* key)))]
                             [:pre {:id key} (util/preify (get @admin-summary* key))]])
                  (reverse (sort (keys @admin-summary*)))))])])))

#?(:clj
   (do
     (defn refresh-all-users-friends [current-session log-event worker]
       (if-not (is-admin current-session)
         (cheshire/generate-string (response/bad-request {:message "you don't have access to this page"}))

         (let [endpoint "/api/v1/admin/refresh_all_users_friends"
               message (str "hit endpoint: " endpoint)]
           (log-event "worker" {:message message
                                :endpoint endpoint
                                :screen-name (:screen-name current-session)})
           (worker)
           (cheshire/generate-string message))))

     (defn summary-data [get-current-session req]
       (let [current-session (get-current-session req)
             result (if-not (is-admin current-session)
                      (response/bad-request {:message "you don't have access to this page"})
                      {:twitter-profiles (db/select-all db/twitter-profiles-table)
                       :settings         (db/select-all db/settings-table)
                       :friends          (map #(-> %
                                                   (assoc :friends-count (count (get-in % [:data "friends"])))
                                                   (dissoc :data))
                                              (db/select-all db/friends-table))
                       :coordinates      (db/select-all db/coordinates-table)})]
         (cheshire/generate-string result)))))