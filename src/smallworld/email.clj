(ns smallworld.email (:require [clj-http.client :as http]
                               [smallworld.util :as util]))

(def debug? false)
(def FROM_EMAIL "hello@smallworld.kiwi")
(def FROM_NAME  "Small World")
(def TEMPLATES {:welcome "d-4cb1507efaaa4a2eab8a9f18b0dabbc5"})

(defn- send-with-content [{to-email :to
                           subject :subject
                           type :type
                           body :body}]
  (http/post
   "https://api.sendgrid.com/v3/mail/send"
   {:headers {:authorization (str "Bearer " (util/get-env-var "SENDGRID_API_KEY"))}
    :content-type :json
    :form-params {:personalizations [{:to [{:email to-email}]
                                      :subject subject}]
                  :from {:email FROM_EMAIL
                         :name  FROM_NAME}
                  :content [{:type (or type "text/html") :value body}]}}))

(defn- send-with-template [{to-email :to
                            template-id :template
                            dynamic-template-data :dynamic_template_data}]
  (when debug? (println)
        (println "template-id: " template-id)
        (println "to-email:    " to-email)
        (println))
  (http/post
   "https://api.sendgrid.com/v3/mail/send"
   {:headers {:authorization (str "Bearer " (util/get-env-var "SENDGRID_API_KEY"))}
    :content-type :json
    :form-params {:template_id template-id
                  :personalizations [{:to [{:email to-email}]
                                      :dynamic_template_data dynamic-template-data}]
                  :from {:email FROM_EMAIL}}}))

(defn send [options]
  (println "sending email: " options)
  (try (if (:template options)
         (send-with-template options)
         (send-with-content  options))
       (catch Throwable e
         (util/log "failed to send email")
         (util/log e))))

(comment
  (send {:to      "devonzuegel@gmail.com"
         :subject "test from CLI"
         :body    "test from CLI"
         :type    "text/plain"}))