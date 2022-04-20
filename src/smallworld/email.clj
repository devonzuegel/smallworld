(ns smallworld.email (:require [clj-http.client :as http]
                               [smallworld.util :as util]))

(def FROM_EMAIL "avery.sara.james@gmail.com") ; TODO: update this to hello@smallworld.kiwi once it's set up

(defn send-email [{to-email :email
                   subject :subject
                   body :body}]
  (http/post
   "https://api.sendgrid.com/v3/mail/send"
   {:headers {:authorization (str "Bearer " (util/get-env-var "SENDGRID_API_KEY"))}
    :content-type :json
    :form-params
    {:personalizations [{:to [{:email to-email}]
                         :subject subject}]
     :from {:email FROM_EMAIL}
     :content [{:type  "text/plain"
                :value body}]}}))

(send-email {:email   "devonzuegel@gmail.com"
             :subject "Test email 3"
             :body    "Hello, world! This is another test email"})
