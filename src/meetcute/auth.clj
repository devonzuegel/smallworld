(ns meetcute.auth
  (:require [buddy.sign.jwt :as jwt]
            [clojure.string :as str]
            [cheshire.core :as json]
            [ring.util.response :as resp]
            [hiccup2.core :as hiccup]
            [net.cgrand.enlive-html :as enlive]
            [meetcute.sms :as sms]
            [meetcute.util :as mc.util]
            [meetcute.env :as env]
            [meetcute.screens.styles :as mc.styles]
            [clojure.java.io :as io]))

;; Adding authentication to some of the pages
;; She wants everything to be stateless if possible

(defn- jwt-secret []
  (env/get-env-var "JWT_SECRET_KEY"))

(defn create-auth-token [phone]
  (jwt/sign {:auth/phone phone} (jwt-secret)))

(defn verify-auth-token [auth-token]
  (try
    (jwt/unsign auth-token (jwt-secret))
    (catch Exception _e
      nil)))

(defn req->auth-token [req]
  (or
   (some-> (get-in req [:headers "authorization"])
           (str/split #"\s+")
           (second)))
  (get-in req [:session :auth/jwt]))

(defn req->parsed-jwt [req]
  (:auth/parsed-jwt req))

(defn json-req? [req]
  (= "application/json"
     (some-> (or (get-in req [:headers "content-type"])
                 (get-in req [:headers "Content-Type"]))
             (str/trim)
             (str/lower-case))))

(defn unauthorized-response [req]
  (if (json-req? req)
    {:status 401
     :headers {"Content-Type" "application/json"}
     :body (json/generate-string {:error "unauthorized"})}
    {:status 401
     :headers {"Content-Type" "text/html"}
     :body "Unauthorized"}))

(defn wrap-authenticated [handler]
  (fn [request]
    (if-let [auth-token (req->auth-token request)]
      (if-let [jwt (verify-auth-token auth-token)]
        (handler (assoc request :auth/parsed-jwt jwt))
        (unauthorized-response request))
      (unauthorized-response request))))

;; ====================================================================== 
;; SMS verification flow

(comment
  {"phone" {:code "123456"
            :attempts [{:time 123 :code "1234" :result :success}]}})

(defonce sms-sessions (atom {}))

(defn reset-sms-sessions! []
  (reset! sms-sessions {}))

(defn random-code []
  (str (+ 100000 (rand-int 900000))))

(defn add-new-code [sms-sessions phone code]
  {:pre [(string? phone) (string? code)]}
  (if (get sms-sessions phone)
    (update sms-sessions phone assoc :code code)
    (assoc sms-sessions phone {:code code
                               :attempts []})))

(defn now [] (java.util.Date.))

(def MAX_ATTEMPTS_PER_HOUR 6)

(defn in-last-hour? [now time]
  (< (- (.getTime now) (.getTime time)) (* 60 60 1000)))

(defn new-attempt [sms-sessions phone attempted-code]
  (let [{:keys [attempts code]} (get sms-sessions phone)
        last-hour-attempts (->> attempts
                                (map :time)
                                (filter (partial in-last-hour? (now)))
                                count)
        r (cond
            (nil? attempted-code) :error
            (< MAX_ATTEMPTS_PER_HOUR last-hour-attempts) :error
            (= code attempted-code) :success
            :else :error)
        attempt {:code attempted-code
                 :time (now)
                 :result r}]
    (update-in sms-sessions [phone :attempts] conj attempt)))

(defn last-attempt [attempts]
  (->> attempts
       (sort-by (fn [{:keys [time]}]
                  (- (.getTime time))))
       first))

;; ====================================================================== 
;; Pages

(defn signin-screen [{:keys [phone phone-input-error started? code-error]}]
  [:form {:method "post" :action (if started?
                                   "/meetcute/verify"
                                   "/meetcute/signin")}
   [:div {:style {:margin-left "auto"
                  :margin-right "auto"
                  :width "90%"
                  :padding-top "48px"
                  :text-align "center"}}
    [:h1 {:style {:font-size 48 :line-height "1.6em" :margin-bottom "18px"}} "Sign in"]
    (when phone-input-error
      [:div {:style {:color "red" :min-height "1.4em" :margin-bottom "8px"}}
       phone-input-error])
    [:label {:for "phone"}
     [:p {:style {:line-height "2.5em"}} "Your phone number:"]]
    [:input {:type "text"
             :name "phone"
             :value phone
             :style {:background "#66666620" :border-radius "8px" :padding "6px 8px" :margin-right "4px"}}]
    (when started?
      [:div
       (when code-error
         [:div {:style {:color "red" :min-height "1.4em" :margin-bottom "8px"}}
          code-error])
       [:label {:for "code"}
        [:p {:style {:line-height "2.5em"}} "Code:"]]
       [:input {:type "text"
                :autocomplete "one-time-code"
                :name "code"
                :style {:background "#66666620" :border-radius "8px" :padding "6px 8px" :margin-right "4px"}}]])
    [:div {:style {:margin-bottom "12px"}}]
    [:button {:style mc.styles/btn
              :type "submit"}
     "Sign in"]
    [:a {:style {:margin-left "12px" :margin-right "12px"}
         :href "/meetcute/signup"}
     "Sign up"]]])

(enlive/deftemplate base-index (io/resource "public/meetcute.html")
  [body]
  [:section#app] (enlive/html-content body)
  [:script#cljs] nil)

(defn html-response [hiccup-body]
  {:status 200
   :headers {"Content-Type" "text/html"}
   :body (base-index (str (hiccup/html hiccup-body)))})

(defn signin-route [_]
  (html-response
   (signin-screen {:phone ""
                   :phone-input-error nil
                   :started? false})))

(defn start-signin-route [req]
  (let [params (:params req)]
    (if-let [phone (some-> (:phone params) mc.util/clean-phone)]
      (let [new-code (random-code)]
        (sms/send! {:to phone
                    :message (sms/code-template new-code)})
        (swap! sms-sessions add-new-code phone new-code)
        (html-response
         (signin-screen {:phone (:phone params)
                         :started? true})))
      (html-response
       (signin-screen {:phone (:phone params)
                       :phone-input-error "Invalid phone number"})))))

(defn verify-route [req]
  (let [params (:params req)
        error-response (delay
                         (html-response
                          (signin-screen {:phone (:phone params)
                                          :started? true
                                          :code-error "Invalid code"})))]
    (if-let [phone (some-> (:phone params) mc.util/clean-phone)]
      (if-let [code (some-> (:code params) str/trim)]
        (let [sms-sessions' (swap! sms-sessions new-attempt phone code)
              {:keys [attempts]} (get sms-sessions' phone)
              attempt (last-attempt attempts)]
          (case (:result attempt)
            ;; redirect to the home page with the cookie set
            ;; this session is now authenticated
            :success (-> (resp/redirect "/meetcute")
                         (assoc :session {:auth/jwt (create-auth-token {:phone phone})}))
            @error-response))
        @error-response)
      @error-response)))

(defn logout-route [_req]
  (-> (resp/redirect "/meetcute/signin")
      (assoc :session {:auth/jwt nil})))