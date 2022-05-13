(ns smallworld.admin
  #?(:cljs (:require [reagent.core           :as r]
                     [smallworld.session     :as session]
                     [smallworld.util        :as util]
                     [smallworld.decorations :as decorations]
                     [clojure.pprint         :as pp]))
  #?(:clj (:require [ring.util.response     :as response]
                    [smallworld.db          :as db]
                    [cheshire.core          :as cheshire])))

(def screen-name "devonzuegel")

(defn is-admin [user]
  (assert (not (nil? (:screen-name user))))
  (= screen-name (:screen-name user)))

(is-admin {:screen-name ""})


#?(:cljs
   (do
     (defonce admin-summary* (r/atom :loading))

     (defn summary-screen [] ; TODO: fetch admin data on screen load – probably needs react effects to do it properly
       [:div.admin-screen
        (if-not (= screen-name (:screen-name @session/*store))

          (if (= :loading @session/*store)
            (decorations/loading-screen)
            [:p {:style {:margin "30vh auto 0 auto" :text-align "center" :font-size "2em"}}
             "whoops, you don't have access to this page"])

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
     (defn summary-data [get-current-user req]
       (let [current-user (get-current-user req)
             result (if-not (is-admin current-user)
                      (response/bad-request {:message "you don't have access to this page"})
                      {:twitter-profiles (db/select-all db/twitter-profiles-table)
                       :settings         (db/select-all db/settings-table)
                       :friends          (map #(-> %
                                                   (assoc :friends-count (count (get-in % [:data "friends"])))
                                                   (dissoc :data))
                                              (db/select-all db/friends-table))
                       :coordinates      (db/select-all db/coordinates-table)})]
         (cheshire/generate-string result)))

     (defn friends-of-specific-user [get-current-user get-users-friends req]
       (fn [{params :params}]
         (println "params:")
         (println params)
         (let [curr-user-screen-name (:screen-name (get-current-user req))
               target-screen-name    (:screen_name params)]
           (if-not (= screen-name curr-user-screen-name)
             (response/bad-request {:message "you don't have access to this page"})
             (get-users-friends req target-screen-name)))))))