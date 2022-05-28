;; this is nested under dev/dev/ to avoid namespace collisions.  I know
;; it looks messy, but there's a reson for it!

(ns dev.repl
  (:require [figwheel-sidecar.repl-api :as repl-api]
            [smallworld.web :as backend]))

(def PORT 3001)

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(defn restart-server []
  (backend/stop!)
  (use 'smallworld.web :reload)
  (println "\nrestarting server......\n")
  (backend/-main))

(defn initialize-repl []
  (println (str "\n\n🌎 starting the small world server (backend): http://localhost:" PORT " 🌍\n"))
  (backend/-main)

  (println "\n\n🎨 starting the Figwheel server (frontend hot-reloading) 🎨\n")
  (repl-api/start-figwheel!)

  (println (str "\n\n⚙️  (" 'restart-server ") – run this every time you update server-side code ⚙️"))
  (println "\n"))

(initialize-repl)