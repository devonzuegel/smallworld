(ns meetcute.routes
  (:require [compojure.core :as compo :refer [defroutes GET POST ANY]]
            [compojure.route :as route]
            [clojure.java.io :as io]
            [cheshire.core :as json]
            [smallworld.matchmaking :as matchmaking]
            [meetcute.auth :as mc.auth]
            [meetcute.util :as mc.util]
            [ring.util.mime-type :as mime]
            [ring.util.request]
            [ring.util.response :as resp]
            [cheshire.core :refer [generate-string]]
            [clojure.string :as str]))

(defn parse-body-params [body]
  (json/parse-string body true))

(defn- add-wildcard [^String path]
  (str path (if (.endsWith path "/") "*" "/*")))

(defn- add-mime-type [response path options]
  (if-let [mime-type (mime/ext-mime-type path (:mime-types options {}))]
    (resp/content-type response mime-type)
    response))

(defn resources
  ([path]
   (resources path {}))
  ([path options]
   (GET (add-wildcard path) {{resource-path :*} :route-params}
     (let [resource-path (str/replace resource-path (re-pattern "^/meetcute/") ; only the first occurrence, anchored to the beginning of the string
                                      "")
           root "public"]
       (some-> (resp/resource-response (str root "/" resource-path))
               (add-mime-type resource-path options))))))

(defroutes open-routes
  (ANY  "/" []        (io/resource "public/meetcute.html"))
  (ANY  "/admin" []   (io/resource "public/meetcute.html"))
  (GET  "/signup" req (mc.auth/signup-route req))
  (GET  "/signin" req (mc.auth/signin-route req))
  (POST "/signin" req (mc.auth/start-signin-route req))
  (POST "/verify" req (mc.auth/verify-route req))
  (POST "/logout" req (mc.auth/logout-route req)))

;; Routes under this can only be accessed by authenticated clients
(defroutes authenticated-routes
  (GET  "/api/matchmaking/bios"    _   (json/generate-string (matchmaking/get-all-bios)))
  (POST "/api/matchmaking/profile" req (matchmaking/update-profile req))
  (ANY  "/api/echo"                req (resp/response (pr-str req)))
  (POST "/api/matchmaking/me"      req (let [phone (some-> (mc.auth/req->parsed-jwt req)
                                                           :auth/phone
                                                           mc.util/clean-phone)]
                                         (assert phone)
                                         (generate-string {:fields (matchmaking/my-profile phone)})))
  (POST "/api/refresh-todays-cutie" req (matchmaking/refresh-todays-cutie-route req)))

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
       (resources "/")
       (mc.auth/wrap-authenticated authenticated-routes))
      (wrap-json-params)
      (wrap-body-string)))

