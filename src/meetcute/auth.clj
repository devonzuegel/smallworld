(ns meetcute.auth
  (:require [buddy.sign.jwt :as jwt]
            [clojure.string :as str]
            [cheshire.core :as json]
            [ring.util.response :as resp]
            [hiccup2.core :as hiccup]
            [net.cgrand.enlive-html :as enlive]
            [meetcute.sms :as sms]
            [meetcute.util :as mc.util]
            [smallworld.email :as email]
            [meetcute.env :as env]
            [meetcute.screens.styles :as mc.styles]
            [meetcute.logic :as logic]
            [clojure.java.io :as io]
            [cljs.pprint :as pp]
            [smallworld.airtable :as airtable]))

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
     :body (str (hiccup/html [:p {:style {:font-family "monospace"
                                          :max-width "300px"
                                          :text-align "center"
                                          :margin "100px auto"
                                          :font-size "1.5em"
                                          :line-height "1.5em"}}
                              "Oops! You need to " [:a {:href "/meetcute/signin"} "sign in"]
                              " or " [:a {:href "/meetcute/signup"} "sign up"] " first."]))}))

(defn wrap-authenticated [handler]
  (fn [request]
    (if-let [auth-token (req->auth-token request)]
      (if-let [jwt (verify-auth-token auth-token)]
        (handler (assoc request :auth/parsed-jwt jwt))
        (unauthorized-response request))
      (unauthorized-response request))))

;; ====================================================================== 
;; (deprecated for Twilio verify) SMS verification flow

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

;; ================================================================================ 
;; HTML

(enlive/deftemplate base-index (io/resource "public/meetcute.html")
  [body]
  [:section#app] (enlive/html-content body)
  [:script#cljs] nil)

(defn html-response [hiccup-body]
  {:status 200
   :headers {"Content-Type" "text/html"}
   :body (base-index (str (hiccup/html hiccup-body)))})

(defn embed-js-script [resource]
  [:script {} (hiccup/raw (slurp resource))])

;; ====================================================================== 
;; Sign Up

(defn airtable-iframe [src]
  ;; keep id in sync with resources/public/signup.js
  [:iframe {:id "airtable-signup"
            :src src
            :style {:display "none"
                    :frameborder "0"
                    :onmousewheel ""
                    :height "100%"
                    :width "100%"}}])

#_(defn signup-screen []
    [:div {:style {:display "flex"
                   :flex-direction "column"
                   :height "100vh"
                   :font-size "1.2em"
                   :line-height "1.6em"
                   :text-align "center"
                   :overflow "hidden"
                   :padding "0 12px"
                   :vertical-align "top" ; vertically align flex items to the top, make them stick to the top even if they don't take the whole height
                   }}
     [:p {:style {:text-align "right"
                  :font-size ".8em"
                  :position "fixed"
                  :top "12px"
                  :right "48px"}}
      "Already have an account? " [:a {:href "/meetcute/signin"} "Sign in"]]
     [:div {:style {:width "100%" :flex 1}} ;; keep in sync with resources/public/signup and resources/public/css/meetcute.css
      [:div#loading-spinner.spinner {:style {:display "block"}}]
      [:script {:src "https://static.airtable.com/js/embed/embed_snippet_v1.js"}]
      (airtable-iframe "https://airtable.com/embed/appF2K8ThWvtrC6Hs/shrZJIaP3ZbuXmiW1")
      (embed-js-script (io/resource "public/signup.js"))]])

(defn signup-screen [{:keys [phone phone-input-error code-error started?]}]
  [:form {:method "post" :action (if started?
                                   "/meetcute/verify-signup"
                                   "/meetcute/signup")}
   [:div {:style {:margin-left "auto"
                  :margin-right "auto"
                  :width "90%"
                  :padding-top "48px"
                  :text-align "center"}}
    ;; [:h1 {:style {:font-size "36px" :line-height "1.4em" :margin-bottom "60px" :margin-top "12px"}} "Welcome to" [:br] "MeetCute!"]
    [:h2 {:style {:font-size "24px" :line-height "1.4em" :margin "24px"}} "Sign up"]
    (when (or phone-input-error code-error)
      [:div {:style {:color "red" :min-height "1.4em" :margin-bottom "8px"}}
       (or phone-input-error code-error)])
    [:label {:for "phone"}
     [:p {:style {:font-weight "bold"
                  :margin "24px 4px 4px 4px"
                  :text-transform "uppercase"
                  :font-style "italic"
                  :color "#bcb5af"
                  :font-size ".8em"}} "Your phone number:"]]
    [:input {:id "phone"
             :name "phone"
             :value phone
             :type "hidden"}]
    [:input {:id "display-phone"
             :type "tel"
             :name "display-phone"
             :value phone
             :style {:background "#66666620"
                     :border-radius "8px"
                     :width "13em"
                     :padding "6px 8px"
                     :margin-right "4px"
                     :padding-left "50px"}}]
    (if-not started?

      [:p {:style {:margin-top "8px"
                   :color "#9e958d"
                   :font-size ".8em"}}
       "We will text you a code via SMS"]

      [:div
       [:label {:for "code"}
        [:p {:style {:font-weight "bold"
                     :margin "24px 4px 4px 4px"
                     :text-transform "uppercase"
                     :font-style "italic"
                     :color "#bcb5af"
                     :font-size ".8em"}} "SMS code:"]]
       [:input {:type "text"
                :autocomplete "one-time-code"
                :name "code"
                :style {:background "#66666620"
                        :border-radius "8px"
                        :padding "6px 8px"
                        :margin-right "4px"}}]])
    [:div {:style {:margin-bottom "12px"}}]
    [:button {:class "btn primary"
              :type "submit"}
     "Sign up"]
    [:p {:style {:font-size ".8em"
                 :margin-top "24px"}}
     "Already have an account? " [:a {:href "/meetcute/signin"} "Sign in"]]
    (when started?
      [:div {:class "resend" :style {:margin-top "2rem"}}
       [:p "Didn't get the code?  " [:a {:href "/meetcute/signin"} "Start over"]]])
    (embed-js-script (io/resource "public/signin.js"))]])

(defn signup-route [_]
  (html-response
   (signup-screen {:phone ""
                   :started? false
                   :phone-input-error nil})))

;; ====================================================================== 
;; Sign In

(defn signin-screen [{:keys [phone phone-input-error code-error started?]}]
  [:div.oranges-wallpaper
   [:form {:method "post" :action (if started?
                                    "/meetcute/verify"
                                    "/meetcute/signin")}
    [:div.signin-form-background
    ;; [:h1 {:style {:font-size "36px" :line-height "1.4em" :margin-bottom "60px" :margin-top "12px"}} "Welcome to" [:br] "MeetCute!"]
     [:h2 {:style {:font-size "24px" :line-height "1.4em"}} "Sign in"]
     [:p {:style {:margin "28px 0 12px 0" :font-size ".88em"}} "Hello from " [:a {:href "https://twitter.com/devonzuegel"} "Devon"] " & " [:a {:href "https://twitter.com/eriktorenberg"} "Erik"] "!"]
     [:p {:style {:margin "0    0 32px 0" :font-size ".88em"}} "This is our a little experiment to introduce single friends to each other, & we're excited you're part of it"]
     (when (or phone-input-error code-error)
       [:div {:style {:color "red" :min-height "1.4em" :margin-bottom "8px"}}
        (or phone-input-error code-error)])
     #_[:label {:for "phone"}
        [:p {:style {:font-weight "bold"
                     :margin "24px 4px 4px 4px"
                     :text-transform "uppercase"
                     :font-style "italic"
                     :color "#bcb5af"
                     :font-size ".8em"}} "Your phone number:"]]
     [:input {:id "phone"
              :name "phone"
              :value phone
              :type "hidden"}]
     [:input {:id "display-phone"
              :type "tel"
              :name "display-phone"
              :value phone
              :style {:background "#66666620"
                      :border-radius "8px"
                      :width "13em"
                      :padding "6px 8px"
                      :margin-right "4px"
                      :padding-left "50px"}}]
     (if-not started?
       [:p {:style {:margin-top "8px"
                    :color "#9e958d"
                    :font-size ".8em"}}
        "We will text you a code via SMS"]
       [:div
        [:label {:for "code"}
         [:p {:style {:font-weight "bold"
                      :margin "24px 4px 4px 4px"
                      :text-transform "uppercase"
                      :font-style "italic"
                      :color "#bcb5af"
                      :font-size ".8em"}} "SMS code:"]]
        [:input {:type "text"
                 :autocomplete "one-time-code"
                 :name "code"
                 :style {:background "#66666620"
                         :border-radius "8px"
                         :padding "6px 8px"
                         :margin-right "4px"}}]])
     [:div {:style {:margin-bottom "12px"}}]
     [:button.btn.primary.green {:type "submit"}
      "Sign in →"]
     [:p {:style {:font-size ".8em"
                  :margin-top "24px"}}
      "No account yet? " [:a {:href "/meetcute/signup"} "Sign up →"]]

     (when started?
       [:div {:class "resend" :style {:margin-top "2rem"}}
        [:p "Didn't get the code?  " [:a {:href "/meetcute/signin"} "Start over"]] ; TODO: have this resend the code, instead of starting over entirely
        ])
     (embed-js-script (io/resource "public/signin.js"))]]])

(enlive/deftemplate base-index (io/resource "public/meetcute.html") #_"resources/public/meetcute.html"
  [body]
  [:section#app] (enlive/html-content body)
  [:script#cljs] nil)

;; TODO(sebas): use index.html around the content
(defn html-response [hiccup-body]
  {:status 200
   :headers {"Content-Type" "text/html"}
   :body (base-index (str (hiccup/html hiccup-body)))})

(defn signin-route [_]
  (html-response
   (signin-screen {:phone ""
                   :started? false
                   :phone-input-error nil})))

(comment
  (def TEST_SMS_CODE "123456"))

(def TEST_PHONE_NUMBER (mc.util/clean-phone "111-111-1111"))
(def TEST_VERIFICATION_ID "VE478b3f02238dee0544e9062cfc16c1ff")

(defn start-signin-route [req]
  (let [params (:params req)
        phone (some-> (:phone params) mc.util/clean-phone)]

    (println)
    (println "Attempting login with phone number: " phone)

    (if-not (mc.util/valid-phone? phone)
      (html-response
       (signin-screen {:phone (or (:phone params) "")
                       :phone-input-error "Invalid phone number"}))

      (if-not (logic/existing-phone-number? phone)
        (html-response
         (signin-screen {:phone (or (:phone params) "")
                         :phone-input-error "No account associated to this phone number. Sign up first."}))

        (let [verification-id
              (if (= TEST_PHONE_NUMBER phone)
                TEST_VERIFICATION_ID
                (try
                  (sms/start-verification! {:phone phone})
                  (catch Exception _e
                    :error)))]
          (if (= :error verification-id)
            (html-response
             (signin-screen {:phone (or (:phone params) "")
                             :phone-input-error "Error sending SMS. Try again later."}))
            (html-response
             (signin-screen {:phone (or (:phone params) "")
                             :started? true}))))))))

(defn start-signup-route [req]
  (let [params (:params req)
        phone (some-> (:phone params) mc.util/clean-phone)]

    (println)
    (println "Attempting signup with phone number: " phone)

    (if-not (mc.util/valid-phone? phone)
      (html-response
       (signup-screen {:phone (or (:phone params) "")
                       :phone-input-error "Invalid phone number"}))

      (if (logic/existing-phone-number? phone)
        (html-response
         (signup-screen {:phone (or (:phone params) "")
                         :phone-input-error "This phone number is already associated to an account. Sign in instead."}))

        (let [verification-id
              (if (= TEST_PHONE_NUMBER phone)
                TEST_VERIFICATION_ID
                (try
                  (sms/start-verification! {:phone phone})
                  (catch Exception _e
                    :error)))]
          (if (= :error verification-id)
            (html-response
             (signup-screen {:phone (or (:phone params) "")
                             :phone-input-error "Error sending SMS. Try again later."}))
            (html-response
             (signup-screen {:phone (or (:phone params) "")
                             :started? true}))))))))

(defn verify-route [req]
  (println "made it to verify-route!!!!!!!!!!!!!!!!!!!!!!!!!!!!")
  (println "params: " (:params req))
  (let [params (:params req)
        error-response (fn [msg]
                         (html-response
                          (signin-screen {:phone (:phone params)
                                          :started? true
                                          :code-error msg})))]
    (if-let [phone (some-> (:phone params) mc.util/clean-phone)]
      (if-let [code (some-> (:code params) str/trim)]
        (let [verify-r (when-not (= TEST_PHONE_NUMBER phone)
                         (try
                           (when-not (re-matches #"^\d{4}$" code)                {:error "Hmm that doesn't match the format of the code"})
                           (when-not (sms/check-code! {:phone phone :code code}) {:error "Hmm that's the wrong code..."})
                           (catch Exception _e                                   {:error "Hmm that didn't work! Please try again"})))]
          (if-let [error-msg (:error verify-r)]
            (error-response error-msg)
            ;; redirect to the home page with the cookie set
            ;; this session is now authenticated
            (-> (resp/redirect "/meetcute")
                (assoc :session {:auth/jwt (create-auth-token {:phone phone})}))))
        (error-response "Missing code"))
      (error-response "Missing phone"))))

(defn verify-signup-route [req]
  (let [params (:params req)
        error-response (fn [msg]
                         (html-response
                          (signup-screen {:phone (:phone params)
                                          :started? true
                                          :code-error msg})))]
    (if-let [phone (some-> (:phone params) mc.util/clean-phone)]
      (if-let [code (some-> (:code params) str/trim)]
        (let [verify-r (when-not (= TEST_PHONE_NUMBER phone)
                         (try
                           (when-not (re-matches #"^\d{4}$" code)                {:error "Hmm that doesn't match the format of the code"})
                           (when-not (sms/check-code! {:phone phone :code code}) {:error "Hmm that's the wrong code..."})
                           (catch Exception _e                                   {:error "Hmm that didn't work! Please try again"})))]
          (if-let [error-msg (:error verify-r)]
            (error-response error-msg)
            ;; create the new user, then
            ;; redirect to the home page with the cookie set
            ;; this session is now authenticated
            (do
              (println "🐣 Creating new user in airtable with phone: " phone)
              ; send email to admin notifying them that a new user has signed up:
              (email/send-email {:to        "hello@smallworld.kiwi"
                                 :from-name "MeetCute logs"
                                 :subject   (str "🐣 New user signed up: " phone)
                                 :body      (str "<div style='line-height: 1.6em; font-family: Roboto Mono, monospace !important; margin: 24px 0'>"
                                                 "View list of users who have not yet been reviewed: "
                                                 "<a href='https://airtable.com/appF2K8ThWvtrC6Hs/tbl0MIb6C4uOFmNAb/viwNrg3C6HulVYNMh?blocks=hide'>https://airtable.com/appF2K8ThWvtrC6Hs/tbl0MIb6C4uOFmNAb/viwNrg3C6HulVYNMh</a>."
                                                 "</div>")})
              (airtable/create-in-base logic/airtable-base
                                       [@logic/airtable-cuties-db-name]
                                       {:fields {:Phone phone}})
              (-> (resp/redirect "/meetcute/settings")
                  (assoc :session {:auth/jwt (create-auth-token {:phone phone})})))))
        (error-response "Missing code"))
      (error-response "Missing phone"))))

(defn logout-route [_req]
  (-> (resp/redirect "/meetcute/signin")
      (assoc :session {:auth/jwt nil})))