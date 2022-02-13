(ns smallworld.session #?(:cljs (:require  [reagent.core :as r])))

(def blank {})

;; (#?(:cljs
;;     (defonce data (r/atom :loading))))

;; (#?(:cljs
;;     (defn update [new-session-data] (reset! data new-session-data))))