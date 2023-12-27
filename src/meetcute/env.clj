(ns meetcute.env)

(defn get-env-var [key]
  (let [value (System/getenv key)]
    (assert (some? value)
            (str "Environment variable not set: " key))
    value))

