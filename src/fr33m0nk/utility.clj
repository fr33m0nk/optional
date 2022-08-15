(ns fr33m0nk.utility
  (:refer-clojure :exclude [update-vals])
  (:import (clojure.lang IEditableCollection)
           (java.util Optional)
           (java.util.function Function Predicate)))

(defmacro macro->fn [m]
  "wraps a macro and returns a fn
  this allows passing macro to fn as argument"
  `(fn [& args#]
     (eval
       (cons '~m args#))))

(defn- update-vals
  "Applies f to the values in map m and returns updated map"
  [m f]
  (with-meta
    (persistent!
      (reduce-kv (fn [acc k v] (assoc! acc k (f v)))
                 (if (instance? IEditableCollection m)
                   (transient m)
                   (transient {}))
                 m))
    (meta m)))

(defn exception->map
  "returns exception map from supplied Exception object"
  [^Exception ex]
  {:msg (.getMessage ex) :type (class ex) :exception (-> ex Throwable->map (update-vals str))})

(defn ^Function fn->predicate
  "Takes a clojure predicate fn and returns a java.util.function.Predicate instance
  Multi arity versions also take a logger
  If logger is a macro e.g. clojure.tools.logging, create logger-fn using utils/macro->fn and supply that as argument
  e.g. (def logger-fn (macro->fn clojure.tools.logging/info))
  The reason is Clojure functions can't take macros as argument"
  [f logger log-exception-message]
  (reify Predicate
    (test
      [_this arg]
      (try
        (f arg)
        (catch Exception ex
          (when logger
            (let [logging-args (filterv some? [log-exception-message (exception->map ex)])]
              (apply logger logging-args)))
          false)))))

(defn ^Function fn->function
  "takes a clojure fn for mapping and returns a java.util.function.Function instance
  Multi arity versions also take a logger
  If logger is a macro e.g. clojure.tools.logging, create logger-fn using utils/macro->fn and supply that as argument
  e.g. (def logger-fn (macro->fn clojure.tools.logging/info))
  The reason is Clojure functions can't take macros as argument"
  ([f logger log-exception-message]
   (fn->function f logger log-exception-message false))
  ([f logger log-exception-message flat-map?]
   (reify Function
     (apply [_this arg]
       (try
         (let [result (f arg)]
           (if (and flat-map?
                    (not (instance? Optional result)))
             (Optional/of result)
             result))
         (catch Exception ex
           (when logger
             (let [logging-args (filterv some? [log-exception-message (exception->map ex)])]
               (apply logger logging-args)
               (when flat-map?
                 (Optional/empty))))))))))
