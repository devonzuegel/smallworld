(ns meetcute.routes
  (:require [compojure.core :as compo :refer [defroutes GET POST ANY]]
            [compojure.route :as route]
            [clojure.java.io :as io]
            [cheshire.core :as json]
            [meetcute.logic :as logic]
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

#_(defn tmp-upload-handler [request]
    (if-let [file (-> request :params :file)]
      (let [phone (some-> (mc.auth/req->parsed-jwt request) :auth/phone mc.util/clean-phone)
            cutie (logic/my-profile phone :force-refresh? true)]
        (println     "file: " file)
        (println "filename: " (:filename file))
        (println "cutie id: " (:id cutie))
        (println "   phone: " phone)
        (io/copy (:tempfile file)
                 (io/file (str "resources/public/tmp-img-uploads/" (:filename file))))
        (logic/update-cutie-picture (:id cutie)
                                    (str "https://7138-186-177-83-218.ngrok-free.app/tmp-img-uploads/" (:filename file)))
        (resp/redirect "/meetcute/settings"))
      (resp/response "No file provided")))

(defn tmp-upload-handler [request]
  (try
    (let [files (-> request :params :file)
            ; make sure files is a list, even if we're just given one file. make it a seq: 
          files (if (map? files) (list files) files)]
      (println "files: ")
      (println files)
      (if (seq files)
        (let [phone (some-> (mc.auth/req->parsed-jwt request) :auth/phone mc.util/clean-phone)
              cutie (logic/my-profile phone :force-refresh? true)]
          (println "cutie id: " (:id cutie))
          (println "   phone: " phone)
          (doseq [file files]
            (println "file: " file)
            (println "filename: " (:filename file))
            (println "")
            (io/copy (:tempfile file)
                     (io/file (str "resources/public/tmp-img-uploads/" (:filename file))))
            (logic/update-cutie-picture (:id cutie)
                                        (str "https://7138-186-177-83-218.ngrok-free.app/tmp-img-uploads/" (:filename file))))
          (resp/redirect "/meetcute/settings"))
        (resp/response "No file provided")))

    (catch Exception _e
      (resp/response "Error while processing file upload."))))


(defroutes open-routes
  (ANY  "/"         []  (io/resource "public/meetcute.html"))
  (ANY  "/admin"    []  (io/resource "public/meetcute.html"))
  (ANY  "/settings" []  (io/resource "public/meetcute.html"))
  (GET  "/signup"   req (mc.auth/signup-route req))
  (GET  "/signin"   req (mc.auth/signin-route req))
  (POST "/signup"   req (mc.auth/start-signup-route req))
  (POST "/signin"   req (mc.auth/start-signin-route req))
  (POST "/verify-signup" req (mc.auth/verify-signup-route req))
  (POST "/verify"   req (mc.auth/verify-route req))
  (POST "/logout"   req (mc.auth/logout-route req)))

;; Routes under this can only be accessed by authenticated clients
(defroutes authenticated-routes
  (GET  "/api/matchmaking/bios"    req (json/generate-string (logic/get-needed-bios req)))
  (POST "/api/matchmaking/profile" req (logic/update-profile req))
  (POST "/tmp-upload"              req (tmp-upload-handler req))
  (ANY  "/api/echo"                req (resp/response (pr-str req)))
  (POST "/api/matchmaking/me"      req (let [phone (some-> (mc.auth/req->parsed-jwt req)
                                                           :auth/phone
                                                           mc.util/clean-phone)]
                                         (assert phone)
                                         (generate-string {:fields (logic/my-profile phone)})))
  (GET  "/api/get-airtable-db-name"        _  (json/generate-string (logic/get-airtable-db-name)))
  (POST "/api/admin/update-airtable-db"   req (logic/update-airtable-db req))
  (POST "/api/refresh-todays-cutie"       req (let [parsed-body (:params req)
                                                    id (:id parsed-body)]
                                                (logic/refresh-todays-cutie-from-id id)))
  (POST "/api/refresh-todays-cutie/mine"  req (logic/refresh-todays-cutie-route-mine req))
  (POST "/api/refresh-todays-cutie/all"   req (logic/refresh-todays-cutie-route-all req)))

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

