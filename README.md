## optional [![Clojars Project](https://img.shields.io/clojars/v/net.clojars.fr33m0nk/optional.svg)](https://clojars.org/net.clojars.fr33m0nk/optional)

`optional` is a simple library that wraps over `java.util.Optional` and offers convenience methods for easy implementation in Clojure code.

### How to use the library
All functions are available through the `fr33m0nk.optional` and `fr33m0nk.utility` namespaces. Add the following to your project dependencies:
- CLI/deps.edn dependency information:
```clojure
net.clojars.fr33m0nk/optional {:mvn/version "0.1.2"}
```
- Leningen/Boot
```clojure
[net.clojars.fr33m0nk/optional "0.1.2"]
```
- Maven:
```xml
<dependency>
    <groupId>net.clojars.fr33m0nk</groupId>
    <artifactId>optional</artifactId>
    <version>0.1.2</version>
</dependency>
```
Require at the REPL with:

```clojure
(require 
  '[fr33m0nk.optional :as optional]
  '[fr33m0nk.utility :as optional-util])
```

Or in your namespace as:

```clojure
(ns mynamespace
  (:require [fr33m0nk.optional :as optional]
    [fr33m0nk.utility :as optional-util]))
```

## Functions and macros:

| Name                                                                    | Description                                                                                                                                                                    |
|-------------------------------------------------------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| [`fr33m0nk.optional/option-of`](#option-of)                             | Returns Optional of supplied value                                                                                                                                             |
| [`fr33m0nk.optional/warp-return-in-optional`](#warp-return-in-optional) | Wraps Clojure fn `try catch` block to return Optional values                                                                                                                   |
| [`fr33m0nk.optional/has-value?`](#has-value?)                           | Returns boolean based on whether Optional has value or is empty                                                                                                                |
| [`fr33m0nk.optional/=`](#=)                                             | Like `core/=` for Optionals                                                                                                                                                    |
| [`fr33m0nk.optional/get`](#get)                                         | Retrieves the value boxed in Optional                                                                                                                                          |
| [`fr33m0nk.optional/filter`](#filter)                                   | Returns Optional value satisfying predicate                                                                                                                                    |
| [`fr33m0nk.optional/map`](#map)                                         | Transforms value boxed in Optional by applying supplied mapping function and returns `Optional<T>`                                                                             |
| [`fr33m0nk.optional/flat-map`](#flat-map)                               | Transforms value boxed in Optional by applying supplied mapping function that themselves returns `Optional<T>`.<br/>Un-nests `Optional<Optional<T>>` and returns `Optional<T>` |
| [`fr33m0nk.optional/optional->sequence`](#optional->sequence)           | Returns Clojure sequence of unboxed value from Optional<T>                                                                                                                     |
| [`fr33m0nk.utility/macro->fn`](#macro->fn)                              | Wraps a macro and returns a Clojure function.<br/>This allows to pass macro to functions                                                                                       |
| [`fr33m0nk.utility/exception->map`](#exception->map)                    | Returns exception map from supplied Exception object                                                                                                                           |
| [`fr33m0nk.utility/fn->predicate`](#fn->predicate)                      | Takes a Clojure predicate function and returns a `java.util.function.Predicate` instance                                                                                       |
| [`fr33m0nk.utility/fn->function`](#fn->function)                        | Takes a Clojure function for mapping and returns a `java.util.function.Function` instance                                                                                      |

## API Docs
- Detailed usages of below functions can be found in [tests](https://github.com/fr33m0nk/optional/blob/master/test/fr33m0nk/optional_test.clj)
- **Collections should not be wrapped in an optional as any collection by itself could represent the absence of data by being empty.** 
- Read [this](https://stackoverflow.com/questions/26327957/should-java-8-getters-return-optional-type/26328555#26328555) and [this](https://stackoverflow.com/a/73097167/19613290) for right practices of using Optional.
- ### namespace
```clojure 
(ns fr33m0nk.optional)
```

### `option-of`
- Returns an Optional 
- Zero arity version returns an empty Optional 
- Single arity version returns an Optional of value
- If `nil` is supplied as value, returns an empty Optional
#### Usage:
```clojure
(require '[fr33m0nk.optional :as optional])

(optional/option-of 10)
;;=> #object[java.util.Optional 0x21bf3689 "Optional[10]"]

(optional/option-of)
;;=> #object[java.util.Optional 0x1341289b "Optional.empty"]
```

### `warp-return-in-optional`
- Take a Clojure Fn `f` and returns a Clojure Fn that is wrapped in try catch block 
- Returned fn when executed returns Optional of value if application was successful or empty Optional when execution of supplied function throws an Exception
- Multi arity versions also take a logger
- If logger is a macro e.g. clojure.tools.logging, create logger-fn using utils/macro->fn and supply that as argument
- e.g.
```clojure
(def logger-fn (fr33m0nk.utility/macro->fn clojure.tools.logging/info))
```
- The reason is Clojure functions can't take macros as argument
#### Usage:
```clojure
(require '[fr33m0nk.optional :as optional]
         '[fr33m0nk.utility :as optional-util]
         '[clojure.tools.logging :as log])

(inc "10")
;;Execution error (ClassCastException) at user/eval1972 (form-init6918060955066684574.clj:1).
;;class java.lang.String cannot be cast to class java.lang.Number (java.lang.String and java.lang.Number are in module java.base of loader 'bootstrap')

;; Example without logger
(let [wrapped-inc (optional/warp-return-in-optional inc)]
  (wrapped-inc 1))
;;=> #object[java.util.Optional 0x57226b51 "Optional[2]"]

;; Example with logger
(let [log-fn (optional-util/macro->fn clojure.tools.logging/info)
      wrapped-inc (optional/warp-return-in-optional inc log-fn)]
  (wrapped-inc "1"))
;; Aug 15, 2022 12:32:04 PM clojure.tools.logging$eval2267$fn__2270 invoke
;; INFO: {:msg class java.lang.String cannot be cast to class java.lang.Number (java.lang.String and java.lang.Number are in module java.base of loader 'bootstrap'), :type java.lang.ClassCastException, :exception {:via [{:type java.lang.ClassCastException, :message "class java.lang.String cannot be cast to class java.lang.Number (java.lang.String and java.lang.Number are in module java.base of loader 'bootstrap')", :at [clojure.lang.Numbers inc "Numbers.java" 139]}], :trace [[clojure.lang.Numbers inc "Numbers.java" 139] [clojure.core$inc invokeStatic "core.clj" 929] [clojure.core$inc invoke "core.clj" 924] [clojure.lang.AFn applyToHelper "AFn.java" 154] [clojure.lang.AFn applyTo "AFn.java" 144] [clojure.core$apply invokeStatic "core.clj" 667] [clojure.core$apply invoke "core.clj" 662] [fr33m0nk.optional$warp_return_in_optional$fn__1959 doInvoke "optional.clj" 114] [clojure.lang.RestFn invoke "RestFn.java" 408] [user$eval2280 invokeStatic "form-init16753772140548043809.clj" 3] [user$eval2280 invoke "form-init16753772140548043809.clj" 1] [clojure.lang.Compiler eval "Compiler.java" 7194] [clojure.lang.Compiler eval "Compiler.java" 7149] [clojure.core$eval invokeStatic "core.clj" 3215] [clojure.core$eval invoke "core.clj" 3211] [nrepl.middleware.interruptible_eval$evaluate$fn__968$fn__969 invoke "interruptible_eval.clj" 87] [clojure.lang.AFn applyToHelper "AFn.java" 152] [clojure.lang.AFn applyTo "AFn.java" 144] [clojure.core$apply invokeStatic "core.clj" 667] [clojure.core$with_bindings_STAR_ invokeStatic "core.clj" 1990] [clojure.core$with_bindings_STAR_ doInvoke "core.clj" 1990] [clojure.lang.RestFn invoke "RestFn.java" 425] [nrepl.middleware.interruptible_eval$evaluate$fn__968 invoke "interruptible_eval.clj" 87] [clojure.main$repl$read_eval_print__9206$fn__9209 invoke "main.clj" 437] [clojure.main$repl$read_eval_print__9206 invoke "main.clj" 437] [clojure.main$repl$fn__9215 invoke "main.clj" 458] [clojure.main$repl invokeStatic "main.clj" 458] [clojure.main$repl doInvoke "main.clj" 368] [clojure.lang.RestFn invoke "RestFn.java" 1523] [nrepl.middleware.interruptible_eval$evaluate invokeStatic "interruptible_eval.clj" 84] [nrepl.middleware.interruptible_eval$evaluate invoke "interruptible_eval.clj" 56] [nrepl.middleware.interruptible_eval$interruptible_eval$fn__999$fn__1003 invoke "interruptible_eval.clj" 152] [clojure.lang.AFn run "AFn.java" 22] [nrepl.middleware.session$session_exec$main_loop__1067$fn__1071 invoke "session.clj" 202] [nrepl.middleware.session$session_exec$main_loop__1067 invoke "session.clj" 201] [clojure.lang.AFn run "AFn.java" 22] [java.lang.Thread run "Thread.java" 1534]], :cause class java.lang.String cannot be cast to class java.lang.Number (java.lang.String and java.lang.Number are in module java.base of loader 'bootstrap')}}
;;=> #object[java.util.Optional 0x200c7eac "Optional.empty"]
```

### `has-value?`
- Returns a boolean
- Checks if supplied Optional has a value or is empty
#### Usage:
```clojure
(require '[fr33m0nk.optional :as optional])

(optional/has-value? (optional/option-of 10))
;;=> true

(optional/has-value? (optional/option-of))
;;=> false
```

### `=`
- Returns a boolean
- Checks equality for Optionals
#### Usage:
```clojure
(require '[fr33m0nk.optional :as optional])

(optional/= (optional/option-of 10) (optional/option-of 10) (optional/option-of 10))
;;=> true

(optional/= (optional/option-of 10) (optional/option-of))
;;=> false
```

### `get`
- Return `nil` for empty Optional
- Returns value contained in Optional
- Two arity version also takes another value that would be returned if Optional is empty
#### Usage:
```clojure
(require '[fr33m0nk.optional :as optional])

(optional/get (optional/option-of 10))
;;=> 10

(optional/get (optional/option-of))
;;=> nil
```

### `filter`
- Returns empty Optional if predicate evaluates to false or predicate throws Exception 
- Returns Optional with value if predicate evaluates to true 
- Multi arity versions also take a logger 
- If logger is a macro e.g. clojure.tools.logging, create logger-fn using utils/macro->fn and supply that as argument 
- e.g.
```clojure
(def logger-fn (fr33m0nk.utility/macro->fn clojure.tools.logging/info))
```
- The reason is Clojure functions can't take macros as argument
#### Usage:
```clojure
(require '[fr33m0nk.optional :as optional]
         '[fr33m0nk.utility :as optional-util]
         '[clojure.tools.logging :as log])

(optional/filter (partial < 9) (optional/option-of 10))
;;=> #object[java.util.Optional 0x21e87e90 "Optional[10]"]

(optional/filter (partial > 9) (optional/option-of 10))
;;=> #object[java.util.Optional 0x200c7eac "Optional.empty"]

;; Example with logger and exception message
(let [log-fn (optional-util/macro->fn log/info)]
  (optional/filter (partial > 9) (optional/option-of "10") log-fn "Uh-Oh!!"))
;;Aug 15, 2022 12:48:14 PM clojure.tools.logging$eval2267$fn__2270 invoke
;;INFO: Uh-Oh!! {:msg class java.lang.String cannot be cast to class java.lang.Number (java.lang.String and java.lang.Number are in module java.base of loader 'bootstrap'), :type java.lang.ClassCastException, :exception {:via [{:type java.lang.ClassCastException, :message "class java.lang.String cannot be cast to class java.lang.Number (java.lang.String and java.lang.Number are in module java.base of loader 'bootstrap')", :at [clojure.lang.Numbers gt "Numbers.java" 261]}], :trace [[clojure.lang.Numbers gt "Numbers.java" 261] [clojure.core$_GT_ invokeStatic "core.clj" 1079] [clojure.core$_GT_ invoke "core.clj" 1072] [clojure.core$partial$fn__5908 invoke "core.clj" 2641] [fr33m0nk.utility$fn$reify__1939 test "utility.clj" 42] [java.util.Optional filter "Optional.java" 218] [fr33m0nk.optional$filter invokeStatic "optional.clj" 59] [fr33m0nk.optional$filter invoke "optional.clj" 46] [user$eval2420 invokeStatic "form-init16753772140548043809.clj" 2] [user$eval2420 invoke "form-init16753772140548043809.clj" 1] [clojure.lang.Compiler eval "Compiler.java" 7194] [clojure.lang.Compiler eval "Compiler.java" 7149] [clojure.core$eval invokeStatic "core.clj" 3215] [clojure.core$eval invoke "core.clj" 3211] [nrepl.middleware.interruptible_eval$evaluate$fn__968$fn__969 invoke "interruptible_eval.clj" 87] [clojure.lang.AFn applyToHelper "AFn.java" 152] [clojure.lang.AFn applyTo "AFn.java" 144] [clojure.core$apply invokeStatic "core.clj" 667] [clojure.core$with_bindings_STAR_ invokeStatic "core.clj" 1990] [clojure.core$with_bindings_STAR_ doInvoke "core.clj" 1990] [clojure.lang.RestFn invoke "RestFn.java" 425] [nrepl.middleware.interruptible_eval$evaluate$fn__968 invoke "interruptible_eval.clj" 87] [clojure.main$repl$read_eval_print__9206$fn__9209 invoke "main.clj" 437] [clojure.main$repl$read_eval_print__9206 invoke "main.clj" 437] [clojure.main$repl$fn__9215 invoke "main.clj" 458] [clojure.main$repl invokeStatic "main.clj" 458] [clojure.main$repl doInvoke "main.clj" 368] [clojure.lang.RestFn invoke "RestFn.java" 1523] [nrepl.middleware.interruptible_eval$evaluate invokeStatic "interruptible_eval.clj" 84] [nrepl.middleware.interruptible_eval$evaluate invoke "interruptible_eval.clj" 56] [nrepl.middleware.interruptible_eval$interruptible_eval$fn__999$fn__1003 invoke "interruptible_eval.clj" 152] [clojure.lang.AFn run "AFn.java" 22] [nrepl.middleware.session$session_exec$main_loop__1067$fn__1071 invoke "session.clj" 202] [nrepl.middleware.session$session_exec$main_loop__1067 invoke "session.clj" 201] [clojure.lang.AFn run "AFn.java" 22] [java.lang.Thread run "Thread.java" 1534]], :cause class java.lang.String cannot be cast to class java.lang.Number (java.lang.String and java.lang.Number are in module java.base of loader 'bootstrap')}}
;;=> #object[java.util.Optional 0x200c7eac "Optional.empty"]
```

### `map`
- Returns empty Optional if `f` throws Exception 
- Returns Optional with transformed value if `f` was applied successfully
- Multi arity versions also take a logger
- If logger is a macro e.g. clojure.tools.logging, create logger-fn using utils/macro->fn and supply that as argument
- e.g.
```clojure
(def logger-fn (fr33m0nk.utility/macro->fn clojure.tools.logging/info))
```
- The reason is Clojure functions can't take macros as argument
#### Usage:
```clojure
(require '[fr33m0nk.optional :as optional]
         '[fr33m0nk.utility :as optional-util]
         '[clojure.tools.logging :as log])

(->> 9
     optional/option-of
     (optional/map inc))
;;=> #object[java.util.Optional 0x6af3fba3 "Optional[10]"]

(->> "9"
     optional/option-of
     (optional/map inc))
;;=> #object[java.util.Optional 0x200c7eac "Optional.empty"]

;; Example with logger and exception message
(let [log-fn (optional-util/macro->fn log/info)]
  (->> "9"
       optional/option-of
       (#(optional/map inc % log-fn "Uh-Oh!!"))))
;;Aug 15, 2022 12:51:46 PM clojure.tools.logging$eval2267$fn__2270 invoke
;;INFO: Uh-Oh!! {:msg class java.lang.String cannot be cast to class java.lang.Number (java.lang.String and java.lang.Number are in module java.base of loader 'bootstrap'), :type java.lang.ClassCastException, :exception {:via [{:type java.lang.ClassCastException, :message "class java.lang.String cannot be cast to class java.lang.Number (java.lang.String and java.lang.Number are in module java.base of loader 'bootstrap')", :at [clojure.lang.Numbers inc "Numbers.java" 139]}], :trace [[clojure.lang.Numbers inc "Numbers.java" 139] [clojure.core$inc invokeStatic "core.clj" 929] [clojure.core$inc invoke "core.clj" 924] [fr33m0nk.utility$fn$reify__1942 apply "utility.clj" 61] [java.util.Optional map "Optional.java" 260] [fr33m0nk.optional$map invokeStatic "optional.clj" 73] [fr33m0nk.optional$map invoke "optional.clj" 61] [user$eval2442$fn__2445 invoke "form-init16753772140548043809.clj" 4] [user$eval2442 invokeStatic "form-init16753772140548043809.clj" 4] [user$eval2442 invoke "form-init16753772140548043809.clj" 1] [clojure.lang.Compiler eval "Compiler.java" 7194] [clojure.lang.Compiler eval "Compiler.java" 7149] [clojure.core$eval invokeStatic "core.clj" 3215] [clojure.core$eval invoke "core.clj" 3211] [nrepl.middleware.interruptible_eval$evaluate$fn__968$fn__969 invoke "interruptible_eval.clj" 87] [clojure.lang.AFn applyToHelper "AFn.java" 152] [clojure.lang.AFn applyTo "AFn.java" 144] [clojure.core$apply invokeStatic "core.clj" 667] [clojure.core$with_bindings_STAR_ invokeStatic "core.clj" 1990] [clojure.core$with_bindings_STAR_ doInvoke "core.clj" 1990] [clojure.lang.RestFn invoke "RestFn.java" 425] [nrepl.middleware.interruptible_eval$evaluate$fn__968 invoke "interruptible_eval.clj" 87] [clojure.main$repl$read_eval_print__9206$fn__9209 invoke "main.clj" 437] [clojure.main$repl$read_eval_print__9206 invoke "main.clj" 437] [clojure.main$repl$fn__9215 invoke "main.clj" 458] [clojure.main$repl invokeStatic "main.clj" 458] [clojure.main$repl doInvoke "main.clj" 368] [clojure.lang.RestFn invoke "RestFn.java" 1523] [nrepl.middleware.interruptible_eval$evaluate invokeStatic "interruptible_eval.clj" 84] [nrepl.middleware.interruptible_eval$evaluate invoke "interruptible_eval.clj" 56] [nrepl.middleware.interruptible_eval$interruptible_eval$fn__999$fn__1003 invoke "interruptible_eval.clj" 152] [clojure.lang.AFn run "AFn.java" 22] [nrepl.middleware.session$session_exec$main_loop__1067$fn__1071 invoke "session.clj" 202] [nrepl.middleware.session$session_exec$main_loop__1067 invoke "session.clj" 201] [clojure.lang.AFn run "AFn.java" 22] [java.lang.Thread run "Thread.java" 1534]], :cause class java.lang.String cannot be cast to class java.lang.Number (java.lang.String and java.lang.Number are in module java.base of loader 'bootstrap')}}
;;=> #object[java.util.Optional 0x200c7eac "Optional.empty"]
```

### `flat-map`
- Returns empty Optional if `f` throws Exception 
- Returns Optional with transformed value if `f` transforms value 
- Un-nests Optionals and returns a single Optional e.g. `Optional<Optional<T>> -> Optional<T>`
- This is useful when working with fn that return Optional values
- Multi arity versions also take a logger
- If logger is a macro e.g. clojure.tools.logging, create logger-fn using utils/macro->fn and supply that as argument
- e.g.
```clojure
(def logger-fn (fr33m0nk.utility/macro->fn clojure.tools.logging/info))
```
- The reason is Clojure functions can't take macros as argument
#### Usage:
```clojure
(require '[fr33m0nk.optional :as optional]
         '[fr33m0nk.utility :as optional-util]
         '[clojure.tools.logging :as log])

;; optional/map returns Optional<Optional<T>> when the mapping fn return Optional<T>
(->> 9
     optional/option-of
     (optional/map (optional/warp-return-in-optional inc)))
;;=> #object[java.util.Optional 0x38629e44 "Optional[Optional[10]]"]

;; optional/flat-map returns Optional<T> even when the mapping fn return Optional<T>
(->> 9
     optional/option-of
     (optional/flat-map (optional/warp-return-in-optional inc)))
;;=> #object[java.util.Optional 0x244313a4 "Optional[10]"]

;; optional/map returns Optional<Optional<T>> when the mapping fn return Optional<T>
(->> "9"
     optional/option-of
     (optional/map (optional/warp-return-in-optional inc)))
;;=> #object[java.util.Optional 0x1278dd1b "Optional[Optional.empty]"]

;; optional/flat-map returns Optional<T> even when the mapping fn return Optional<T>
(->> "9"
     optional/option-of
     (optional/flat-map (optional/warp-return-in-optional inc)))
;;=> #object[java.util.Optional 0x200c7eac "Optional.empty"]

;; Example with logger and exception message when mapping function is returns unboxed value
(let [log-fn (optional-util/macro->fn log/info)]
  (->> "9"
       optional/option-of
       (#(optional/flat-map inc % log-fn "Uh-Oh!!"))))
;;Aug 15, 2022 12:59:06 PM clojure.tools.logging$eval2267$fn__2270 invoke
;;INFO: Uh-Oh!! {:msg class java.lang.String cannot be cast to class java.lang.Number (java.lang.String and java.lang.Number are in module java.base of loader 'bootstrap'), :type java.lang.ClassCastException, :exception {:via [{:type java.lang.ClassCastException, :message "class java.lang.String cannot be cast to class java.lang.Number (java.lang.String and java.lang.Number are in module java.base of loader 'bootstrap')", :at [clojure.lang.Numbers inc "Numbers.java" 139]}], :trace [[clojure.lang.Numbers inc "Numbers.java" 139] [clojure.core$inc invokeStatic "core.clj" 929] [clojure.core$inc invoke "core.clj" 924] [fr33m0nk.utility$fn$reify__1942 apply "utility.clj" 61] [java.util.Optional flatMap "Optional.java" 289] [fr33m0nk.optional$flat_map invokeStatic "optional.clj" 89] [fr33m0nk.optional$flat_map invoke "optional.clj" 75] [user$eval2488$fn__2491 invoke "form-init16753772140548043809.clj" 4] [user$eval2488 invokeStatic "form-init16753772140548043809.clj" 4] [user$eval2488 invoke "form-init16753772140548043809.clj" 1] [clojure.lang.Compiler eval "Compiler.java" 7194] [clojure.lang.Compiler eval "Compiler.java" 7149] [clojure.core$eval invokeStatic "core.clj" 3215] [clojure.core$eval invoke "core.clj" 3211] [nrepl.middleware.interruptible_eval$evaluate$fn__968$fn__969 invoke "interruptible_eval.clj" 87] [clojure.lang.AFn applyToHelper "AFn.java" 152] [clojure.lang.AFn applyTo "AFn.java" 144] [clojure.core$apply invokeStatic "core.clj" 667] [clojure.core$with_bindings_STAR_ invokeStatic "core.clj" 1990] [clojure.core$with_bindings_STAR_ doInvoke "core.clj" 1990] [clojure.lang.RestFn invoke "RestFn.java" 425] [nrepl.middleware.interruptible_eval$evaluate$fn__968 invoke "interruptible_eval.clj" 87] [clojure.main$repl$read_eval_print__9206$fn__9209 invoke "main.clj" 437] [clojure.main$repl$read_eval_print__9206 invoke "main.clj" 437] [clojure.main$repl$fn__9215 invoke "main.clj" 458] [clojure.main$repl invokeStatic "main.clj" 458] [clojure.main$repl doInvoke "main.clj" 368] [clojure.lang.RestFn invoke "RestFn.java" 1523] [nrepl.middleware.interruptible_eval$evaluate invokeStatic "interruptible_eval.clj" 84] [nrepl.middleware.interruptible_eval$evaluate invoke "interruptible_eval.clj" 56] [nrepl.middleware.interruptible_eval$interruptible_eval$fn__999$fn__1003 invoke "interruptible_eval.clj" 152] [clojure.lang.AFn run "AFn.java" 22] [nrepl.middleware.session$session_exec$main_loop__1067$fn__1071 invoke "session.clj" 202] [nrepl.middleware.session$session_exec$main_loop__1067 invoke "session.clj" 201] [clojure.lang.AFn run "AFn.java" 22] [java.lang.Thread run "Thread.java" 1534]], :cause class java.lang.String cannot be cast to class java.lang.Number (java.lang.String and java.lang.Number are in module java.base of loader 'bootstrap')}}
;;=> #object[java.util.Optional 0x200c7eac "Optional.empty"]

;; Example with logger and exception message when mapping function is returns Optional<T>
(let [log-fn (optional-util/macro->fn log/info)
      mapping-fn (optional/warp-return-in-optional inc log-fn "Uh-Oh!!")]
  (->> "9"
       optional/option-of
       (optional/flat-map mapping-fn)))
;;Aug 15, 2022 12:57:43 PM clojure.tools.logging$eval2267$fn__2270 invoke
;;INFO: Uh-Oh!! {:msg class java.lang.String cannot be cast to class java.lang.Number (java.lang.String and java.lang.Number are in module java.base of loader 'bootstrap'), :type java.lang.ClassCastException, :exception {:via [{:type java.lang.ClassCastException, :message "class java.lang.String cannot be cast to class java.lang.Number (java.lang.String and java.lang.Number are in module java.base of loader 'bootstrap')", :at [clojure.lang.Numbers inc "Numbers.java" 139]}], :trace [[clojure.lang.Numbers inc "Numbers.java" 139] [clojure.core$inc invokeStatic "core.clj" 929] [clojure.core$inc invoke "core.clj" 924] [clojure.lang.AFn applyToHelper "AFn.java" 154] [clojure.lang.AFn applyTo "AFn.java" 144] [clojure.core$apply invokeStatic "core.clj" 667] [clojure.core$apply invoke "core.clj" 662] [fr33m0nk.optional$warp_return_in_optional$fn__1959 doInvoke "optional.clj" 114] [clojure.lang.RestFn invoke "RestFn.java" 408] [fr33m0nk.utility$fn$reify__1942 apply "utility.clj" 61] [java.util.Optional flatMap "Optional.java" 289] [fr33m0nk.optional$flat_map invokeStatic "optional.clj" 89] [fr33m0nk.optional$flat_map invoke "optional.clj" 75] [fr33m0nk.optional$flat_map invokeStatic "optional.clj" 84] [fr33m0nk.optional$flat_map invoke "optional.clj" 75] [user$eval2478 invokeStatic "form-init16753772140548043809.clj" 5] [user$eval2478 invoke "form-init16753772140548043809.clj" 1] [clojure.lang.Compiler eval "Compiler.java" 7194] [clojure.lang.Compiler eval "Compiler.java" 7149] [clojure.core$eval invokeStatic "core.clj" 3215] [clojure.core$eval invoke "core.clj" 3211] [nrepl.middleware.interruptible_eval$evaluate$fn__968$fn__969 invoke "interruptible_eval.clj" 87] [clojure.lang.AFn applyToHelper "AFn.java" 152] [clojure.lang.AFn applyTo "AFn.java" 144] [clojure.core$apply invokeStatic "core.clj" 667] [clojure.core$with_bindings_STAR_ invokeStatic "core.clj" 1990] [clojure.core$with_bindings_STAR_ doInvoke "core.clj" 1990] [clojure.lang.RestFn invoke "RestFn.java" 425] [nrepl.middleware.interruptible_eval$evaluate$fn__968 invoke "interruptible_eval.clj" 87] [clojure.main$repl$read_eval_print__9206$fn__9209 invoke "main.clj" 437] [clojure.main$repl$read_eval_print__9206 invoke "main.clj" 437] [clojure.main$repl$fn__9215 invoke "main.clj" 458] [clojure.main$repl invokeStatic "main.clj" 458] [clojure.main$repl doInvoke "main.clj" 368] [clojure.lang.RestFn invoke "RestFn.java" 1523] [nrepl.middleware.interruptible_eval$evaluate invokeStatic "interruptible_eval.clj" 84] [nrepl.middleware.interruptible_eval$evaluate invoke "interruptible_eval.clj" 56] [nrepl.middleware.interruptible_eval$interruptible_eval$fn__999$fn__1003 invoke "interruptible_eval.clj" 152] [clojure.lang.AFn run "AFn.java" 22] [nrepl.middleware.session$session_exec$main_loop__1067$fn__1071 invoke "session.clj" 202] [nrepl.middleware.session$session_exec$main_loop__1067 invoke "session.clj" 201] [clojure.lang.AFn run "AFn.java" 22] [java.lang.Thread run "Thread.java" 1534]], :cause class java.lang.String cannot be cast to class java.lang.Number (java.lang.String and java.lang.Number are in module java.base of loader 'bootstrap')}}
;;=> #object[java.util.Optional 0x200c7eac "Optional.empty"]
```

### `optional->sequence`
- Returns Clojure sequence from `Optional<T>`
#### Usage:
```clojure
(->> 10
     optional/option-of
     (optional/map inc)
     (optional/optional->sequence))
;;=> (11)
```
- ### namespace
```clojure 
(ns fr33m0nk.utility)
```

### `macro->fn`
- Wraps a macro and returns a Clojure function
- This allows passing macro to fn as argument
#### Usage:
```clojure
(require 
  '[fr33m0nk.utility :as optional-util]
  '[clojure.tools.logging :as log])

(apply or [true false])
;;Syntax error compiling at (/private/var/folders/wl/f90mcmts1gb8zd5nz6pp_f_w0000gn/T/form-init16753772140548043809.clj:1:1).
;;Can't take value of a macro: #'clojure.core/or

;;By wrapping macro in a function `or` works with apply
(def or-fn (optional-util/macro->fn or))
(apply or-fn [false true])
;;=> true

(def log-fn (optional-util/macro->fn log/info))
```
### `exception->map`
- Returns exception map from supplied Exception object
- Structure is following:
```clojure
{:msg (.getMessage ex) :type (class ex) :exception (-> ex Throwable->map (update-vals str))}
```

### `fn->predicate`
- Takes a clojure predicate fn and returns a `java.util.function.Predicate` instance which is used with `optional/filter`
- Multi arity versions also take a logger
- If logger is a macro e.g. clojure.tools.logging, create logger-fn using utils/macro->fn and supply that as argument
- e.g.
```clojure
(def logger-fn (fr33m0nk.utility/macro->fn clojure.tools.logging/info))
```
- The reason is Clojure functions can't take macros as argument
#### Usage:
```clojure
(require 
  '[fr33m0nk.utility :as optional-util]
  '[clojure.tools.logging :as log])

(def logger-fn (optional-util/macro->fn log/info))

(optional-util/fn->predicate inc logger-fn "Uh-oh")
;;=> #object[fr33m0nk.utility$fn$reify__1939 0x72489d41 "fr33m0nk.utility$fn$reify__1939@72489d41"]
```

### `fn->function`
- Takes a clojure fn for mapping and returns a `java.util.function.Function` instance which is used with `option/map` and `option/flat-map`
- Multi arity versions also take a logger
- If logger is a macro e.g. clojure.tools.logging, create logger-fn using utils/macro->fn and supply that as argument
- e.g.
```clojure
(def logger-fn (fr33m0nk.utility/macro->fn clojure.tools.logging/info))
```
- The reason is Clojure functions can't take macros as argument
#### Usage:
```clojure
(require 
  '[fr33m0nk.utility :as optional-util]
  '[clojure.tools.logging :as log])

;; For map operations
(optional-util/fn->function inc logger-fn "Uh-oh" false)

;; For flat-map operations
(optional-util/fn->function inc logger-fn "Uh-oh" true)
;;=> #object[fr33m0nk.utility$fn$reify__1942 0x37917038 "fr33m0nk.utility$fn$reify__1942@37917038"]
```

## License

Copyright © 2022 Prashant Sinha

Distributed under the BSD 3-Clause License
