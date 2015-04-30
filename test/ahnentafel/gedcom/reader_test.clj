(ns ahnentafel.gedcom.reader-test
  (:require [ahnentafel.gedcom.reader :refer [read-file]]
            [clojure.test :refer :all]))

(deftest reader
  (testing "usual behavior"
    (let [root (read-file (clojure.java.io/resource "simple.ged"))]
      (is (= "__ROOT__" (:tag root)))
      (let [records (:subordinate-lines root)]
        (is (= 7 (count records)))
        (is (= "HEAD" (:tag (first records))))
        (is (= "CHAR" (:tag (first (:subordinate-lines (first records))))))
        (is (= "SUBM" (:tag (second records))))
        (is (= "TRLR" (:tag (first (reverse records))))))
      ;; (is (= "Submitters address\naddress continued here"
      ;;        (get-in [:subordinate-lines :tag] (second records))))
      ))

  (testing "the big file"
    (let [root (read-file (clojure.java.io/resource "allged.ged"))]
      (is (= 18 (count (:subordinate-lines root))))))

  (testing "error conditions"
    (is (= (read-file "does-not-exist.ged")
           {:tag "__ROOT__" :level -1}))))
