(ns ketchup.routes
  (:require [compojure.core :as compo :refer [defroutes GET POST]]
            [cheshire.core :as json :refer [generate-string]]
            [ketchup.auth :as auth]
            [ketchup.db :as db]
            [ketchup.env :as env]
            [ring.util.request]
            [ring.util.response :as resp]))

(defn login-v3 [{:keys [query-params] :as _req}]
  (let [phone (get-in query-params ["phone"])
        smsCode (get-in query-params ["smsCode"])
        user (db/find-or-insert-user! {:phone phone})]
    (assert user)
    (if (= smsCode (env/get-env-var "SMS_CODE"))
      (generate-string {:success true
                        :message "Login success!"
                        :authToken (auth/create-auth-token (:id user))})
      (generate-string {:success false
                        :message "Oops, looks like you don't have the right code!"}))))

(defroutes open-routes
  (POST "/api/v2/login" req (login-v3 req)))

(defn ping [{:keys [params auth/parsed-jwt] :as req}]
  (let [user-id (:user-id parsed-jwt)
        status (:status params)]
    (cond
      (nil? status)                   (resp/response (generate-string {:success false :message "status not provided"}))
      (not (or (= status "online")
               (= status "offline"))) (resp/response (generate-string {:success false :message "status must be either 'online' or 'offline'"}))
      :else (try
              (let [result (db/update-user-last-ping! user-id status)]
                (println "just pinged by" user-id " · " (str (java.time.Instant/now)))
                (println "updated" (count result) "users \n")
                (resp/response
                 (generate-string
                  {:success true
                   :status status
                   :message "Ping received"})))
              (catch Exception e
                (println "caught exception when pinging:" e)
                (resp/response {:success false :message "Unknown error"}))))))

(defn protected-endpoint [{:keys [auth/parsed-jwt] :as _req}]
  (if parsed-jwt
    (generate-string {:success true
                      :message "Success!"
                      :user (:user-id parsed-jwt)})
    (generate-string {:success false :message "Auth token invalid or expired"})))

(defn select-user-fields [user]
  (select-keys user [:created_at
                     :name
                     :screen_name
                     :email_address
                     :phone
                     :email_notifications
                     :last_ping
                     :status
                     :email_address
                     :updated_at]))

(defn get-all-users []
  (mapv select-user-fields (db/select-all db/users-table)))

;; Routes under this can only be accessed by authenticated clients
(defroutes authenticated-routes
  (GET "/api/v2/protected" req (protected-endpoint req))
  (GET "/api/v2/users" _ (generate-string (get-all-users)))
  (POST "/api/v2/ping" req (ping req)))

(defn wrap-body-string [handler]
  (fn [request]
    (let [body-str (ring.util.request/body-string request)]
      (handler (assoc request :body (java.io.StringReader. body-str))))))

(defn parse-body-params [body]
  (json/parse-string body true))

(defn wrap-json-params [handler]
  (fn [request]
    (if-let [params (parse-body-params (slurp (:body request)))]
      (handler (update request :params merge params))
      (handler request))))

(def app
  (-> (compo/routes open-routes
                    (auth/wrap-authenticated authenticated-routes))
      (wrap-json-params)
      (wrap-body-string)))