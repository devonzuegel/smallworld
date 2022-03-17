(ns smallworld.admin #?(:clj (:require
                              [ring.util.response :as response]
                              [smallworld.db :as db]
                              [cheshire.core :as cheshire])))

(def screen-name "devon_dos")

(defn is-admin [user]
  (assert (not (nil? (:screen-name user))))
  (= screen-name (:screen-name user)))

(is-admin {:screen-name ""})

#?(:clj
   (do
     (defn summary-data [get-current-user req]
       (let [current-user (get-current-user req)
             result (if-not (is-admin current-user)
                      (response/bad-request {:message "you don't have access to this page"})
                      {:profiles    (db/select-all db/profiles-table)
                       :settings    (db/select-all db/settings-table)
                       :friends     (map #(-> %
                                              (assoc :friends-count (count (get-in % [:data "friends"])))
                                              (dissoc :data))
                                         (db/select-all db/friends-table))
                       :coordinates (db/select-all db/coordinates-table)})]
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