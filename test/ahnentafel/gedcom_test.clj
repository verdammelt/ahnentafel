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
