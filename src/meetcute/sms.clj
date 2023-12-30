(ns meetcute.sms
  (:require [clj-http.client :as http]
            [meetcute.env :as env]))

(defn code-template [code]
  (format "%s is your MeetCute verification code" code))

(defn send! [{:keys [phone message]}]
  {:pre [(string? phone) (string? message)]}
  (let [sid (env/get-env-var "TWILIO_SID")]
    (http/post
     (format "https://api.twilio.com/2010-04-01/Accounts/%s/Messages.json" sid)
     {:basic-auth [sid (env/get-env-var "TWILIO_AUTH_TOKEN")]
      ;; :content-type :json
      :form-params {:From (env/get-env-var "TWILIO_PHONE_NUMBER")
                    :To phone
                    :Body message}})))

(defn start-verification!
  "Starts a verification from Twilio's service.
   Returns an id for its session or an error."
  [{:keys [phone]}]
  (-> (format "https://verify.twilio.com/v2/Services/%s/Verifications"
              (env/get-env-var "TWILIO_VERIFY_SERVICE"))
      (http/post
       {:basic-auth [(env/get-env-var "TWILIO_SID")
                     (env/get-env-var "TWILIO_AUTH_TOKEN")]
        :as :json
        :form-params {:Channel "sms" :To phone}})
      :body
      :sid))

(defn check-code!
  "Checks if the code is the right one. Returns either true or false if the code is invalid"
  [{:keys [phone code]}]
  (-> (format "https://verify.twilio.com/v2/Services/%s/VerificationCheck"
              (env/get-env-var "TWILIO_VERIFY_SERVICE"))
      (http/post
       {:basic-auth [(env/get-env-var "TWILIO_SID")
                     (env/get-env-var "TWILIO_AUTH_TOKEN")]
        :as :json
        :form-params {:Code code :To phone}})
      :body
      :valid))

