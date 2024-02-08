(ns ketchup.auth
  (:require [buddy.sign.jwt :as jwt]
            [clojure.string :as str]
            [cheshire.core :as json]
            [ketchup.env :as env]))

(defn create-auth-token [user-id]
  {:pre [(some? user-id)]}
  (jwt/sign {:user-id user-id} (env/get-env-var "JWT_SECRET_KEY")))

(defn verify-auth-token [auth-token]
  (try
    (jwt/unsign auth-token (env/get-env-var "JWT_SECRET_KEY"))
    (catch Exception _e
      nil)))

(defn get-authorization-token [req]
  (some-> (get-in req [:headers "authorization"])
          (str/split #"\s+")
          (second)))

(def unauthorized-response
  {:status 401
   :headers {"Content-Type" "application/json"}
   :body (json/generate-string {:error "unauthorized to ketchup API"})})

(defn wrap-authenticated [handler]
  (fn [request]
    (if-let [auth-token (get-authorization-token request)]
      (if-let [jwt (verify-auth-token auth-token)]
        (handler (assoc request :auth/parsed-jwt jwt))
        unauthorized-response)
      unauthorized-response)))