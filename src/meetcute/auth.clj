(ns meetcute.auth
  (:require [buddy.sign.jwt :as jwt]
            [clojure.string :as str]
            [meetcute.env :as env]))

;; Adding authentication to some of the pages
;; She wants everything to be stateless if possible

(defn- jwt-secret []
  (env/get-env-var "JWT_SECRET_KEY"))

(defn create-auth-token [user-id]
  (jwt/sign {:auth/user-id user-id} (jwt-secret)))

(defn verify-auth-token [auth-token]
  (try
    (jwt/unsign auth-token (jwt-secret))
    (catch Exception _e
      nil)))

(defn req->auth-token [req]
  (some-> (get-in req [:headers "authorization"])
          (str/split #"\s+")
          (second)))

(defn wrap-authenticated [handler]
  (fn [request]
    (if-let [auth-token (req->auth-token request)]
      (if-let [jwt (verify-auth-token auth-token)]
        (handler (assoc request :auth/jwt jwt))
        {:status 401
         :body "Unauthorized!"})
      {:status 401
       :body "Unauthorized!"})))