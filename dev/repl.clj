(ns dev.repl
  (:require [figwheel-sidecar.repl-api :as repl-api]))

(defn figwheel-up []
  (repl-api/start-figwheel!)
  (repl-api/cljs-repl))
