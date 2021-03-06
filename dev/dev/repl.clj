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
  (println (str "\n\nš starting the small world server (backend): http://localhost:" PORT " š\n"))
  (backend/-main)

  (println "\n\nšØ starting the Figwheel server (frontend hot-reloading) šØ\n")
  (repl-api/start-figwheel!)

  (println (str "\n\nāļø  (" 'restart-server ") ā run this every time you update server-side code āļø"))
  (println "\n"))

(initialize-repl)