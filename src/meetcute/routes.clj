(ns meetcute.routes
  (:require [compojure.core :as compo :refer [defroutes GET POST ANY]]
            [clojure.java.io :as io]
            [cheshire.core :as json]
            [smallworld.matchmaking :as matchmaking]
            [meetcute.auth :as mc.auth]
            [meetcute.util :as mc.util]
            [ring.util.request]
            [ring.util.response :as resp]))

(defn parse-body-params [body]
  (json/parse-string body true))

(defroutes open-routes
  (ANY "/" [] (io/resource "public/meetcute.html"))
  (GET "/signin" req (mc.auth/signin-route req))
  (POST "/signin" req (mc.auth/start-signin-route req))
  (POST "/verify" req (mc.auth/verify-route req))
  (POST "/logout" req (mc.auth/logout-route req)))

;; Routes under this can only be accessed by authenticated clients
(defroutes authenticated-routes
  (GET "/api/matchmaking/bios" _ (json/generate-string (matchmaking/get-all-bios)))
  (POST "/api/matchmaking/profile" req (matchmaking/update-profile req))
  (ANY "/api/echo" req (resp/response (pr-str req)))
  (POST "/api/matchmaking/me" req
    (let [phone (some-> (mc.auth/req->parsed-jwt req)
                        :auth/phone
                        mc.util/clean-phone)]
      (assert phone)
      (matchmaking/my-profile phone))))

(defn wrap-body-string [handler]
  (fn [request]
    (let [body-str (ring.util.request/body-string request)]
      (handler (assoc request :body (java.io.StringReader. body-str))))))

(defn wrap-json-params [handler]
  (fn [request]
    (if-let [params (parse-body-params (slurp (:body request)))]
      (handler (update request :params merge params))
      (handler request))))

(def app
  (-> (compo/routes
       open-routes
       (mc.auth/wrap-authenticated authenticated-routes))
      (wrap-json-params)
      (wrap-body-string)))

