(ns smallworld.session #?(:cljs (:require [reagent.core :as r])))

(def blank {})

#?(:cljs
   (do
     (defonce *store (r/atom :loading)) ; TODO: add (^:private) later
     (defn update! [new-session-data] (reset! *store new-session-data))))