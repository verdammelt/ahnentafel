(ns ahnentafel.gedcom.reader-test
  (:require [ahnentafel.gedcom.reader :refer [read-file]]
            [environ.core :refer [env]]
            [clojure.test :refer :all]))

(deftest reader
  (testing "usual behavior"
    (let [root (read-file "resource:simple.ged")]
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
    (let [root (read-file "resource:allged.ged")]
      (is (= 18 (count (:subordinate-lines root))))))

  (testing "error conditions"
    (is (= (read-file "resource:does-not-exist.ged")
           {:tag "__ROOT__" :level -1})))

  (when (not (:travis env))
   (testing "s3 support"
     (let [local (read-file "resource:simple.ged")
           remote (read-file "s3://ahnentafel/simple.ged")]
       (is (= local remote))))))
