(ns meetcute.routes
  (:require [compojure.core :as compo :refer [defroutes GET POST ANY]]
            [clojure.java.io :as io]
            [cheshire.core :as json]
            [smallworld.matchmaking :as matchmaking]
            [meetcute.auth :as mc.auth]
            [ring.util.request]
            [ring.middleware.params :as ring.params]))

#_(defn stream? [x]
    (instance? java.io.InputStream x))

(defn reader? [x]
  (instance? java.io.Reader x))



(defn parse-body-params [body]
  (json/parse-string body true)
  #_(cond
      (string? body)
    ;; (stream? body) (json/parse-stream body :key-fn keyword)
    ;; :else body
      ))

(defn signin-route [req]
  (let [params (:params req)
        phone (:phone params)]
    {:status 200
     :body (json/generate-string {:jwt (mc.auth/create-auth-token {:phone phone})
                                  :params params})}))

(defroutes open-routes
  (ANY "/" [] (io/resource "public/meetcute.html"))
  (POST "/api/auth/signin" req (signin-route req)))

(defroutes authenticated-routes
  (GET "/api/matchmaking/bios" _ (json/generate-string (matchmaking/get-all-bios)))
  (POST "/api/matchmaking/profile" req (matchmaking/update-profile req)))

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

