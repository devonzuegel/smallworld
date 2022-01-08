;; this is nested under dev/dev/ to avoid namespace collisions.  I know
;; it looks messy, but there's a reson for it!

(ns dev.repl
  (:require [figwheel-sidecar.repl-api :as repl-api]
            [smallworld.web :as backend]))

(def PORT 3001)

(defn restart-server []
  (use 'smallworld.web :reload)
  (backend/stop!)
  (backend/start! PORT))


(defn initialize-repl []
  (println (str "\n\n🌎 starting the Small World server (backend): http://localhost:" PORT " 🌍\n"))
  (backend/start! 3001)

  (println "\n\n🎨 starting the Figwheel server (frontend hot-reloading) 🎨\n")
  (repl-api/start-figwheel!)

  (println (str "\n\n⚙️  (" 'restart-server ") – run this every time you update server-side code ⚙️"))
  (println "\n"))

(initialize-repl)