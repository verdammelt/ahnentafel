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

(deftest parsing-lines
  (testing "valid lines"
    (are [line expected] (= expected (parse-line line))
         "0 HEAD" {:level 0 :tag "HEAD" :value nil :xref nil}
         "2 HEAD" {:level 2 :tag "HEAD" :value nil :xref nil}
         "10 HEAD" {:level 10 :tag "HEAD" :value nil :xref nil}
         "2 DATE 29 FEB 2000" {:level 2 :tag "DATE" :value "29 FEB 2000" :xref nil}
         "2 DATE 29 FEB 2000" {:level 2 :tag "DATE" :value "29 FEB 2000" :xref nil}
         "0 @FATHER@ INDI" {:level 0 :tag "INDI" :value nil :xref "@FATHER@"}))

  (testing "error cases"
    (is (thrown? ahnentafel.ParseError (parse-line "abc def")))
    (is (thrown? ahnentafel.ParseError (parse-line "01 CHAR ASCII")))))

(deftest grouping-records
  (is (= '[{:level 0 :tag "HEAD" :subordinate-lines nil}
           {:level 0 :tag "INDI" :subordinate-lines nil}]
         (group-records '({:level 0 :tag "HEAD" :subordinate-lines nil}
                          {:level 0 :tag "INDI" :subordinate-lines nil}))))
  (is (= [{:level 0
           :tag "HEAD"
           :subordinate-lines [{:level 1 :tag "CHAR" :subordinate-lines nil}]}]
         (group-records '({:level 0 :tag "HEAD"} {:level 1 :tag "CHAR"}))))
  (is (= [{:level  0 :tag "SUBM"
           :subordinate-lines [{:level 1 :tag "NAME" :value "/Submitter/" :subordinate-lines nil}
                               {:level 1 :tag "ADDR" :value "Submitters address"
                                :subordinate-lines [{:level 2 :tag "CONT" :value "address continued here" :subordinate-lines nil}]}]}
          {:level  0 :tag "INDI"}]
         (group-records '({:level 0 :tag "SUBM"}
                          {:level 1 :tag "NAME" :value "/Submitter/"}
                          {:level 1 :tag "ADDR" :value "Submitters address"}
                          {:level 2 :tag "CONT" :value "address continued here"}
                          {:level 0 :tag "INDI"}))))

  )
