(ns ahnentafel.gedcom.reading.reader-test
  (:require [ahnentafel.gedcom.reading.reader :refer [read-file]]
            [clojure.test :refer :all]))

(deftest reader
  (testing "usual behavior"
    (let [records (read-file (clojure.java.io/resource "simple.ged"))]
      (is (= 7 (count records)))

      (is (= "HEAD" (:tag (first records))))
      (is (= "CHAR" (:tag (first (:subordinate-lines (first records))))))
      (is (= "SUBM" (:tag (second records))))
      (is (= "TRLR" (:tag (first (reverse records)))))
      ;; (is (= "Submitters address\naddress continued here"
      ;;        (get-in [:subordinate-lines :tag] (second records))))
      ))

  (testing "the big file"
    (let [records (read-file (clojure.java.io/resource "allged.ged"))]
      (is (= 18 (count records)))))

  (testing "error conditions"
    (is (nil? (read-file "does-not-exist.ged")))))
