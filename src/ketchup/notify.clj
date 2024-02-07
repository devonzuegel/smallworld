(ns ketchup.notify
  (:require [sdk.expo :as expo]
            [ketchup.db :as db]
            [ketchup.env :as env]))

(defn status-change! [user-id status]
  (let [expo-push-token (env/get-env-var "EXPO_PUSH_TOKEN")
        user (db/user-by-id user-id)
        _ (assert user)
        users (db/get-all-users)
        notifications (->> users
                           ;; TODO: remove the user who is changing status
                           (filter :push_token)
                           (mapv (fn [{:keys [screen_name push_token]}]
                                   {:to push_token
                                    :title "Status Change"
                                    :body (format "Your friend %s is now %s"
                                                  screen_name
                                                  status)})))]
    (expo/push-many! expo-push-token
                     notifications)))