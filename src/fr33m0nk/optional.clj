(ns fr33m0nk.optional
  (:require
    [fr33m0nk.utility :as util])
  (:import (java.util Optional))
  (:refer-clojure :exclude [= get filter map]))

(defn ^Optional option-of
  "returns an Optional
  Zero arity version returns an empty Optional
  Single arity version returns an Optional of value"
  ([]
   (option-of nil))
  ([value]
   (if value
     (Optional/of value)
     (Optional/empty))))

(defn has-value?
  "returns a boolean.
  checks if supplied Optional has any value"
  [^Optional optional]
  (.isPresent optional))

(defn =
  "returns a boolean
  checks equality for Optionals"
  ([_optional] true)
  ([^Optional optional ^Optional another-optional]
   (.equals optional another-optional))
  ([^Optional optional ^Optional another-optional & more-optionals]
   (if (= optional another-optional)
     (if-let [tail (next more-optionals)]
       (recur another-optional (first more-optionals) tail)
       (.equals another-optional (first more-optionals)))
     false)))

(defn get
  "return nil for empty Optional
  returns value contained in Optional
  Two arity version also takes another value that would be returned if Optional is empty"
  ([^Optional optional]
   (get optional nil))
  ([^Optional optional other-value]
   (.orElse optional other-value)))

(defn filter
  "returns empty Optional if predicate evaluates to false or predicate throws Exception or
  returns Optional with value if predicate evaluates to true
  Multi arity versions also take a logger
  If logger is a macro e.g. clojure.tools.logging, create logger-fn using utils/macro->fn and supply that as argument
  e.g. (def logger-fn (utils/macro->fn clojure.tools.logging/info))
  The reason is Clojure functions can't take macros as argument"
  ([predicate optional]
   (filter predicate optional nil nil))
  ([predicate optional logger-fn]
   (filter predicate optional logger-fn nil))
  ([predicate ^Optional optional logger-fn log-exception-message]
   (->> (util/fn->predicate predicate logger-fn log-exception-message)
        (.filter optional))))

(defn map
  "returns empty Optional if f throws Exception or returns Optional with transformed value if f was applied successfully
  Multi arity versions also take a logger
  If logger is a macro e.g. clojure.tools.logging, create logger-fn using utils/macro->fn and supply that as argument
  e.g. (def logger-fn (utils/macro->fn clojure.tools.logging/info))
  The reason is Clojure functions can't take macros as argument"
  ([f optional]
   (map f optional nil nil))
  ([f optional logger-fn]
   (map f optional logger-fn nil))
  ([f ^Optional optional logger-fn log-exception-message]
   (->> (util/fn->function f logger-fn log-exception-message)
        (.map optional))))

(defn flat-map
  "returns empty Optional if f throws Exception or returns Optional with transformed value if f transforms value
  Un-nests Optionals and returns a single Optional e.g. Optional<Optional<T>> -> Optional<T>
  This is useful when working with fn that return Optional values
  Multi arity versions also take a logger
  If logger is a macro e.g. clojure.tools.logging, create logger-fn using utils/macro->fn and supply that as argument
  e.g. (def logger-fn (utils/macro->fn clojure.tools.logging/info))
  The reason is Clojure functions can't take macros as argument"
  ([f optional]
   (flat-map f optional nil nil))
  ([f optional logger-fn]
   (flat-map f optional logger-fn nil))
  ([f ^Optional optional logger-fn log-exception-message]
   (->> (util/fn->function f logger-fn log-exception-message true)
        (.flatMap optional))))

(defn optional->sequence
  "returns Clojure sequence from Optional<T>"
  [^Optional optional]
  (-> optional
      .stream
      .iterator
      iterator-seq))

(defn warp-return-in-optional
  "takes a Clojure Fn and returns a Clojure Fn that is wrapped in try catch block
  returned fn when executed returns Optional of value if application was successful
  or empty Optional when execution of supplied function throws an Exception
  Multi arity versions also take a logger
  If logger is a macro e.g. clojure.tools.logging, create logger-fn using utils/macro->fn and supply that as argument
  e.g. (def logger-fn (utils/macro->fn clojure.tools.logging/info))
  The reason is Clojure functions can't take macros as argument"
  ([f]
   (warp-return-in-optional f nil nil))
  ([f logger]
   (warp-return-in-optional f logger nil))
  ([f logger log-exception-message]
   (fn [& args]
     (try
       (option-of (apply f args))
       (catch Exception ex
         (when logger
           (let [logging-args (filterv some? [log-exception-message (util/exception->map ex)])]
             (apply logger logging-args)))
         (option-of))))))
