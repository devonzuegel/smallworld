(ns smallworld.session-storage)

;; adapted from https://github.com/netpyoung/cljs-storage
;; TODO: consider using https://github.com/alandipert/storage-atom instead

(defprotocol IClearable
  (-clear! [this]))


(deftype Storage [type js-storage]

  IClearable
  (-clear! [this]
    (.clear js-storage)
    this)

  IReset
  (-reset! [this new-value]
    (when-not (map? new-value)
      (throw (js/Error. (str new-value " is not Dictionary"))))

    (-clear! this)
    (doseq [[k v] new-value]
      (-assoc! this k v))
    this)

  ICounted
  (-count [_]
    (.-length js-storage))

  ITransientAssociative
  (-assoc! [this key val]
    (.setItem js-storage key val)
    this)

  ITransientMap
  (-dissoc! [this key]
    (.removeItem js-storage key)
    this)

  ILookup
  (-lookup [coll k]
    (-lookup coll k nil))
  (-lookup [coll k not-found]
    (if-let [ret (.getItem js-storage k)]
      ret
      not-found))

  ISeqable
  (-seq [this]
    (when (pos? (-count this))
      (map (fn [k] [k (.getItem js-storage k)])
           (js-keys js-storage))))

  IPrintWithWriter
  (-pr-writer [this writer opts]
    (-write writer (str "#<" (name type)))
    (print-map (-seq this) pr-writer writer opts)
    (-write writer ">"))

  ;; http://stackoverflow.com/questions/5370784/localstorage-eventlistener-is-not-called
  ;; IWatchable
  ;; (-notify-watches [this oldval newval]
  ;;   (doseq [[key f] watches]
  ;;     (f key this oldval newval)))
  ;; (-add-watch [this key f]
  ;;   (set! (.-watches this) (assoc watches key f))
  ;;   this)
  ;; (-remove-watch [this key]
  ;;   (set! (.-watches this) (dissoc watches key)))
  )


(defn local-storage
  []
  (Storage. :localStorage js/localStorage))


(defn session-storage
  []
  (Storage. :sessionStorage js/sessionStorage))


(defn clear!
  [storage]

  (-clear! storage))