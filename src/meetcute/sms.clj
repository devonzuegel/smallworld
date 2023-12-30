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
