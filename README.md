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

### `has-value?`
- Returns a boolean
- Checks if supplied Optional has a value or is empty

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
