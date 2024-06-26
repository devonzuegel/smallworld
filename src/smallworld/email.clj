(ns smallworld.email (:require [clj-http.client :as http]
                               [clojure.pprint :as pp]
                               [smallworld.util :as util]))

(def debug? true)
(def FROM_EMAIL "hello@smallworld.kiwi")
(def FROM_NAME  "Small World")
(def TEMPLATES {:welcome "d-4cb1507efaaa4a2eab8a9f18b0dabbc5"
                :friends-on-the-move "d-75f5a6ca89484938b92b5f01d883de1b"})

(defn log-event [name data]
  (util/log (str name ": " data)))

(defn- send-with-content [{to-email :to
                           from-name :from-name
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
                         :name  (or from-name FROM_NAME)}
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
                  :from {:email FROM_EMAIL
                         :name FROM_NAME}}}))

(defn send-email [options]
  (let [old-to-email (:to options)
        env          (util/get-env-var "ENVIRONMENT")
        options      (if (= env (:prod util/ENVIRONMENTS))
                       options
                       (assoc options :to "hello@smallworld.kiwi"))]

    (println)
    (println "preparing to send email with the following config: ===========================================")
    (pp/pprint (assoc options :body "[REDACTED]"))

    (when (and (not= env (:prod util/ENVIRONMENTS))
               (not= old-to-email "avery.sara.james@gmail.com"))
      (println)
      (println "NOTE: Sending email to" (:to options) "instead of" old-to-email ", because we're not in prod and we don't want to spam our users :)")
      (println))

    (try (if (:template options)
           (send-with-template options)
           (send-with-content  options))
         (catch Throwable e
           (util/log "failed to send email (error below), continuing...")
           (log-event "send-email-failed" {:error e})
           (util/log e)))

    (println "==============================================================================================")
    (println)))

(comment
  (send-email {:to      "devonzuegel@gmail.com"
               :subject "test from CLI"
               :body    "test from CLI"
               :type    "text/plain"}))
