(ns fr33m0nk.optional-test
  (:require
    [clojure.test :refer [deftest testing is]]
    [fr33m0nk.optional :as op]
    [fr33m0nk.utility :as util])
  (:import (java.util Optional)))

(deftest optional-of-test
  (testing "returns an instance of Optional of value"
    (is (.equals (Optional/of 10) (op/optional-of 10)))
    (is (.equals (Optional/of [10 11]) (op/optional-of [10 11])))
    (is (.equals (Optional/of #{10 11}) (op/optional-of #{10 11})))
    (is (.equals (Optional/of {:a 10 :b 11}) (op/optional-of {:a 10 :b 11}))))
  (testing "returns an instance of empty Optional"
    (is (.equals (Optional/empty) (op/optional-of)))))

(deftest has-value?-test
  (testing "returns true if Optional has value"
    (is (true? (op/has-value? (op/optional-of 10)))))
  (testing "returns false if Optional is empty"
    (is (false? (op/has-value? (op/optional-of))))))

(deftest =-test
  (testing "returns true if Optionals contain same value"
    (is (op/= (op/optional-of 10) (op/optional-of 10) (op/optional-of 10) (op/optional-of 10))))
  (testing "returns false if Optionals do not contain same value"
    (is (false? (op/= (op/optional-of 10) (op/optional-of 10) (op/optional-of 10) (op/optional-of 11))))
    (is (false? (op/= (op/optional-of 10) (op/optional-of))))))

(deftest get-test
  (testing "returns non-nil value held inside Optional"
    (is (= 10 (op/get (op/optional-of 10))))
    (is (= [10 11] (op/get (op/optional-of [10 11]))))
    (is (= #{10 11} (op/get (op/optional-of #{10 11}))))
    (is (= {:a 10 :b 11} (op/get (op/optional-of {:a 10 :b 11})))))
  (testing "returns nil value for empty Optional"
    (is (nil? (op/get (op/optional-of)))))
  (testing "returns default value if supplied for empty Optional"
    (is (= 2 (op/get (op/optional-of) 2)))))

(deftest filter-test
  (testing "returns Optional of value if predicate is satisfied"
    (is (op/= (op/optional-of 10) (op/filter #(< 9 %) (op/optional-of 10)))))
  (testing "returns empty Optional if predicate is satisfied"
    (is (op/= (op/optional-of) (op/filter #(> 9 %) (op/optional-of 10))))))

(deftest map-test
  (testing "returns Optional of value after application of fn"
    (is (op/= (op/optional-of 10)
              (->> 9
                   op/optional-of
                   (op/map inc)))
        "Transforms value held in option and returns Optional of resultant value")

    (is (op/= ((comp op/optional-of
                     op/optional-of) 10)
              (->> 9
                   op/optional-of
                   (op/map (op/warp-return-in-optional inc))))
        "Transforms value held in option and returns Optional of the Optional returned by transforming fn.
        If boxing of value in nested Optionals is not desired, use flat-map instead.")

    (is (= (range 1 6)
           (->> (range 5)
                (map op/optional-of)
                (map (partial op/map inc))
                (map op/get)))
        "Transforms collection of Optionals and safely executes inc on values contained in Optionals")

    (is (= [2 3 nil]
           (->> [1 2 "3"]
                (map op/optional-of)
                (map #(op/map inc % (util/macro->fn clojure.tools.logging/info) "Exception occurred"))
                (map op/get)))
        "Transforms collection of Optionals and safely executes inc on values contained in Optionals.
        If transforming fn results in exception, empty Optional is returned which is mapped to nil by op/get.
        Also uses logger from clojure.tools.logging and wraps the macro in a function as macros can't be passed to a function")

    ;; COLLECTIONS SHOULD NOT BE WRAPPED IN AN OPTIONAL AS ANY COLLECTION BY ITSELF COULD REPRESENT THE ABSENCE OF DATA BY BEING EMPTY.
    ;; Read following for right practices of using Optional
    ;; https://stackoverflow.com/questions/26327957/should-java-8-getters-return-optional-type/26328555#26328555
    ;; https://stackoverflow.com/a/73097167/19613290
    ;; However, following tests show how to work with fns returning Optional of Collections

    (is (op/= (op/optional-of [1 2 3 4 5])
              (->> (range 5)
                   op/optional-of
                   (op/map (partial map inc))))
        "Transforms Optional of collection of Numbers and returns Optional of same data structure")

    (is (op/= (op/optional-of {:a 2 :b 3})
              (->> {:a 1 :b 2}
                   op/optional-of
                   (op/map #(#'util/update-vals % inc))))
        "Transforms Options of Map and returns same data structure")

    (is (op/= (op/optional-of [{:a 2 :b 3} {:c 2 :d 3}])
              (->> [{:a 1 :b 2} {:c 1 :d 2}]
                   op/optional-of
                   (op/map (partial mapv #(#'util/update-vals % inc)))))
        "Transform Optional of collection of Maps using map and returns Optional of same data structure")

    (is (op/= (->> (range 10) (map (comp op/optional-of inc)) op/optional-of)
              (->> (range 10)
                   (map op/optional-of)
                   op/optional-of
                   (op/map (partial map (partial op/map inc)))))
        "Transforms Optional of a collection of Optionals using map and returns Optional of same data structure")

    (is (op/= (->> (range 6 10) op/optional-of)
              (->> (range 10)
                   op/optional-of
                   (op/map (partial filter (partial < 5)))))
        "Transforms Optional of a collection of Optionals using filter and returns Optional of same data structure"))

  (testing "returns empty Optional if any exception is happens in mapping fn"
    (is (false?
          (->> "9"
               op/optional-of
               (op/map inc)
               op/has-value?)))
    (is (false?
          (->> {:a "1" :b "2"}
               op/optional-of
               (op/map #(#'util/update-vals % inc))
               op/has-value?)))))

(deftest flat-map-test
  (is (op/= (op/optional-of 10)
            (->> 9
                 op/optional-of
                 (op/flat-map (op/warp-return-in-optional inc))))
      "Transforms value held in option and returns Optional of resultant value.
      Boxes transformed value in a single Optional instead of nested Optionals unlike map.
      This is useful when fn returns Optional of values and prevents unnecessary nesting of Optionals")

  (let [expected-collection (->> (range 1 6) (map (partial * 2)) (map op/optional-of))
        test-collection (->> (range 5)
                             (map op/optional-of)
                             (map (partial op/flat-map (op/warp-return-in-optional inc)))
                             (map (partial op/flat-map (op/warp-return-in-optional (partial * 2)))))]
    (is (every? true? (map op/= expected-collection test-collection))
        "Transforms a Collection of optionals using mapping fn returning Optional<T> and returns Optional<T>
        op/map would have returned nested Optionals e.g. Optional<Optional<T>>"))

  (is (op/= (op/optional-of 10)
            (->> 9
                 op/optional-of
                 (op/map inc)))
      "Transforms value held in option and returns Optional of resultant value like map")

  ;; COLLECTIONS SHOULD NOT BE WRAPPED IN AN OPTIONAL AS ANY COLLECTION BY ITSELF COULD REPRESENT THE ABSENCE OF DATA BY BEING EMPTY.
  ;; Read following for right practices of using Optional
  ;; https://stackoverflow.com/questions/26327957/should-java-8-getters-return-optional-type/26328555#26328555
  ;; https://stackoverflow.com/a/73097167/19613290
  ;; However, following tests show how to work with fns returning Optional of Collections
  (is (op/= (->> (range 10) (map (comp op/optional-of inc)) op/optional-of)
            (->> (range 10)
                 (map op/optional-of)
                 op/optional-of
                 (op/flat-map (op/warp-return-in-optional (partial map (partial op/map inc))))))
      "Transforms Optional of a collection of Optionals using map and returns Optional of same data structure"))

(deftest optional->sequence-test
  (testing "returns sequence of value contained in Optional"
    (is (= [11]
           (->> 10
                op/optional-of
                (op/map inc)
                (op/optional->sequence)))))

  (testing "returns sequence of values contained in a Collection of Optionals"
    (is (= (range 2 7)
           (->> (range 1 6)
                (map op/optional-of)
                (map (partial op/map inc))
                (mapcat op/optional->sequence)))))


  ;; COLLECTIONS SHOULD NOT BE WRAPPED IN AN OPTIONAL AS ANY COLLECTION BY ITSELF COULD REPRESENT THE ABSENCE OF DATA BY BEING EMPTY.
  ;; Read following for right practices of using Optional
  ;; https://stackoverflow.com/questions/26327957/should-java-8-getters-return-optional-type/26328555#26328555
  ;; https://stackoverflow.com/a/73097167/19613290
  ;; However, following tests show how to work with fns returning Optional of Collections

  (testing "returns sequence of values contained in Optional of collection"
    (is (= [(range 1 6)]
           (->> (range 1 6)
                op/optional-of
                op/optional->sequence)))))

(deftest warp-return-in-optional-test
  (testing "wraps a unsafe function and returns Optional of value or empty Optional"
    (let [log-fn (util/macro->fn clojure.tools.logging/info)
          wrapped-inc (op/warp-return-in-optional inc log-fn)]
      (is (op/= (op/optional-of 10) (wrapped-inc 9))
          "Returns Optional of value if fn execution is successful")

      (is (op/= (op/optional-of) (wrapped-inc "9"))
          "Returns empty Optional if fn execution is successful"))))
