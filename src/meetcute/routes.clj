(ns meetcute.routes
  (:require [compojure.core :as compo :refer [defroutes GET POST ANY]]
            [clojure.java.io :as io]
            [cheshire.core :as json]
            [smallworld.matchmaking :as matchmaking]
            [meetcute.auth :as mc.auth]
            [ring.util.request]
            [ring.util.response :as resp]))

(defn parse-body-params [body]
  (json/parse-string body true))

(defn signin-route [req]
  (let [params (:params req)
        phone (:phone params)]
    ;; redirect to the home page with the cookie set
    ;; this session is now authenticated
    (-> (resp/redirect "/meetcute")
        (assoc :session {:auth/jwt (mc.auth/create-auth-token {:phone phone})}))))

(defn logout-route [_req]
  (-> (resp/redirect "/meetcute")
      (assoc :session {:auth/jwt nil})))

(defroutes open-routes
  (ANY  "/"                []  (io/resource "public/meetcute.html"))
  (POST "/api/auth/signin" req (signin-route req))
  (POST "/api/auth/logout" req (logout-route req)))

;; Routes under this can only be accessed by authenticated clients
(defroutes authenticated-routes
  (GET  "/api/matchmaking/bios"    ___ (json/generate-string (matchmaking/get-all-bios)))
  (POST "/api/matchmaking/profile" req (matchmaking/update-profile req))
  (ANY  "/api/echo"                req (resp/response (pr-str req)))
  (POST "/api/matchmaking/me"      req (matchmaking/my-profile (:auth/phone (mc.auth/req->parsed-jwt req)))))

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

