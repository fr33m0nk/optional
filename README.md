## optional

`optional` is a simple library that wraps over `java.util.Optional` and offers convenience methods for easy implementation in Clojure code.

### How to use the library
All functions are available through the `fr33m0nk.optional` and `fr33m0nk.utility` namespaces. Add the following to your project dependencies:
- CLI/deps.edn dependency information:
```clojure
fr33m0nk/optional {:mvn/version "0.1.0"}
```
- Leningen
```clojure
[fr33m0nk/optional "0.1.0"]
```
- Maven:
```xml
<dependency>
  <groupId>fr33m0nk/optional</groupId>
  <artifactId>optional</artifactId>
  <version>0.1.0</version>
</dependency>
```
Require at the REPL with:

```clojure
(require '[fr33m0nk.optional :as optional]
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

### `get`
- Return `nil` for empty Optional
- Returns value contained in Optional
- Two arity version also takes another value that would be returned if Optional is empty

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

### `optional->sequence`
- Returns Clojure sequence from `Optional<T>`

- ### namespace
```clojure 
(ns fr33m0nk.utility)
```

### `macro->fn`
- Wraps a macro and returns a Clojure function
- This allows passing macro to fn as argument

### `exception->map`
- Returns exception map from supplied Exception object
- Structure is following:
```clojure
{:msg (.getMessage ex) :type (class ex) :exception (-> ex Throwable->map (update-vals str))}
```

### `fn->predicate`
- Takes a clojure predicate fn and returns a `java.util.function.Predicate` instance
- Multi arity versions also take a logger
- If logger is a macro e.g. clojure.tools.logging, create logger-fn using utils/macro->fn and supply that as argument
- e.g.
```clojure
(def logger-fn (fr33m0nk.utility/macro->fn clojure.tools.logging/info))
```
- The reason is Clojure functions can't take macros as argument

### `fn->function`
- Takes a clojure fn for mapping and returns a `java.util.function.Function` instance
- Multi arity versions also take a logger
- If logger is a macro e.g. clojure.tools.logging, create logger-fn using utils/macro->fn and supply that as argument
- e.g.
```clojure
(def logger-fn (fr33m0nk.utility/macro->fn clojure.tools.logging/info))
```
- The reason is Clojure functions can't take macros as argument



Invoke a library API function from the command-line:

    $ clojure -X fr33m0nk.optional/foo :a 1 :b '"two"'
    {:a 1, :b "two"} "Hello, World!"

Run the project's tests (they'll fail until you edit them):

    $ clojure -T:build test

Run the project's CI pipeline and build a JAR (this will fail until you edit the tests to pass):

    $ clojure -T:build ci

This will produce an updated `pom.xml` file with synchronized dependencies inside the `META-INF`
directory inside `target/classes` and the JAR in `target`. You can update the version (and SCM tag)
information in generated `pom.xml` by updating `build.clj`.

Install it locally (requires the `ci` task be run first):

    $ clojure -T:build install

Deploy it to Clojars -- needs `CLOJARS_USERNAME` and `CLOJARS_PASSWORD` environment
variables (requires the `ci` task be run first):

    $ clojure -T:build deploy

Your library will be deployed to net.clojars.fr33m0nk/optional on clojars.org by default.

## License

Copyright © 2022 Prashant Sinha

Distributed under the BSD 3-Clause License
