(ns ahnentafel.gedcom-test
  (:require [ahnentafel.gedcom :refer :all]
            [clojure.test :refer :all]))

(deftest reader
  (testing "usual behavior"
    (let [lines (read-resource "simple.ged")]
      (is (= '("0 HEAD" "1 CHAR ASCII") (take 2 lines)))
      (is (= "0 TRLR" (first (reverse lines))))))

  (testing "error conditions"
    (is (nil? (read-resource "does-not-exist.ged")))))

(deftest parsing-level
  (is (= 0 (parse-level "0 HEAD")))
  (is (= 2 (parse-level "2 HEAD")))
  (is (= 10 (parse-level "10 HEAD")))
  (is (= 2 (parse-level "2 DATE 29 FEB 2000")))
  (is (thrown? ahnentafel.ParseError (parse-level "abc def")))
  (is (thrown? ahnentafel.ParseError (parse-level "01 CHAR ASCII")))
  )
