(ns sdk.expo
  (:require [clj-http.client :as http]
            [clojure.pprint :as pp]
            [clojure.data.json :as json]))

(def base-url "https://exp.host/--")

;; From https://docs.expo.dev/push-notifications/sending-notifications/

;; curl -H "Content-Type: application/json" -X POST "https://exp.host/--/api/v2/push/send" -d '{
;;   "to": "ExponentPushToken[xxxxxxxxxxxxxxxxxxxxxx]",
;;   "title":"hello",
;;   "body": "world"
;; }'

(comment
  {:to "ExponentPushToken[xxxxxxxxxxxxxxxxxxxxxx]"
   :title "hello"
   :body "world"})

(defn valid-notification? [{:keys [to title body]}]
  (and (string? to)
       (string? title)
       (string? body)))

(comment
  ;; example response
  {:data {:status "ok", :id "e9a83bbe-377a-45d7-a0d6-7b281c809e19"}})

(defn push-one!

  [expo-push-token {:keys [device-token title body] :as notification}]

  {:pre [(valid-notification? notification)]}

  (let [data {:to device-token
              :title title
              :body body}
        r (-> (str base-url "/api/v2/push/send")
              (http/post
               {:as :json
                :body (json/write-str data)
                :headers {"Content-Type" "application/json"
                          "Authorization" (str "Bearer " expo-push-token)}}))]
    (if (= 200 (:status r))
      (:data (:body r))
      (throw (ex-info "Failed to send push notification"
                      {:status (:status r)
                       :body (:body r)})))))

(def MAX_EXPO_NOTIFICATIONS 100)

(defn push-many!

  [expo-push-token notifications]

  {:pre [(string? expo-push-token)
         (< (count notifications) MAX_EXPO_NOTIFICATIONS)
         (every? valid-notification? notifications)]}

  (let [r (-> (str base-url "/api/v2/push/send")
              (http/post
               {:as :json
                :body (json/write-str notifications)
                :headers {"Content-Type" "application/json"
                          "Authorization" (str "Bearer " expo-push-token)}}))]
    (println)
    (println "push-many! ================================================")
    (println)
    (pp/pprint r)
    (println)
    (println r)
    (println)
    (println "===========================================================")
    (if (= 200 (:status r))
      (:data (:body r))
      (throw (ex-info "Failed to send push notification"
                      {:status (:status r)
                       :body (:body r)})))))