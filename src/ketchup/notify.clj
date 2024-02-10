(ns ketchup.notify
  (:require [ketchup.db :as db]
            [ketchup.env :as env]
            ;; [clojure.pprint :as pp]
            [sdk.expo :as expo]))

(defn status-change!! [user-id sender-new-status]
  (when (= sender-new-status "online") ; for now, only notify when user goes online
    (let [sender-new-status (case sender-new-status
                              "online" "online 🟢"
                              "offline" "offline 🔵"
                              sender-new-status)
          expo-push-token (env/get-env-var "EXPO_PUSH_TOKEN")
          sender (db/user-by-id user-id)
          _ (assert sender)
          recipients (->> (db/get-all-users)
                          (filter #(and (:push_token %)
                                        (not= user-id (:id %))))
                          (mapv (fn [recipient]
                                  (println)
                                  (println "recipient" (:id recipient) "·" (:push_token recipient))
                                  {:to (:push_token recipient)
                                   :title   (format "%s just went %s" (:screen_name sender) sender-new-status)
                                   :body (if (= sender-new-status "online 🟢")
                                           "free to ketchup?"
                                           "don't worry, you can ketchup later!")})))]
      (println "🐿️  " user-id "changed to" sender-new-status)
    ;; (pp/pprint recipients)
      (expo/push-many! expo-push-token recipients))))


