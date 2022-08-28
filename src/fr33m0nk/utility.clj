(ns fr33m0nk.utility
  (:refer-clojure :exclude [update-vals])
  (:import
    (java.util Optional)
    (java.util.function Function Predicate)))

(defn- instance-of-optional?
  [value]
  (instance? Optional value))

(defn ^Function fn->predicate
  "Takes a clojure predicate fn and returns a java.util.function.Predicate instance
  Multi arity versions also take an exception handler. This can be a logger as well.
  If logger is a macro e.g. clojure.tools.logging, create logger-fn using utils/macro->fn and supply that as argument
  e.g. (def logger-fn (macro->fn clojure.tools.logging/info))
  The reason is Clojure functions can't take macros as argument"
  [f exception-handler]
  (reify Predicate
    (test
      [_this arg]
      (try
        (f arg)
        (catch Exception ex
          (some-> exception-handler (apply (vector ex)))
          false)))))

(defn ^Function fn->function
  "takes a clojure fn for mapping and returns a java.util.function.Function instance
  Multi arity versions also take an exception handler. This can be a logger as well.
  If logger is a macro e.g. clojure.tools.logging, create logger-fn using utils/macro->fn and supply that as argument
  e.g. (def logger-fn (macro->fn clojure.tools.logging/info))
  The reason is Clojure functions can't take macros as argument"
  ([f exception-handler]
   (fn->function f exception-handler false))
  ([f exception-handler flat-map?]
   (reify Function
     (apply [_this arg]
       (try
         (let [result (f arg)]
           (if (and
                 flat-map?
                 (not (instance-of-optional? result)))
             (Optional/of result)
             result))
         (catch Exception ex
           (let [handled-val (some-> exception-handler (#(% ex)))]
             (if-not flat-map?
               handled-val
               (cond
                 (and handled-val (instance-of-optional? handled-val)) handled-val
                 (and handled-val (not (instance-of-optional? handled-val))) (Optional/of handled-val)
                 :else (Optional/empty))))))))))
