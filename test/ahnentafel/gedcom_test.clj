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
  (testing "well formed lines"
    (is (= {:level 0 :tag "HEAD" :value nil :xref nil} (parse-line "0 HEAD")))
    (is (= {:level 2 :tag "HEAD" :value nil :xref nil} (parse-line "2 HEAD")))
    (is (= {:level 10 :tag "HEAD" :value nil :xref nil} (parse-line "10 HEAD")))
    (is (= {:level 2 :tag "DATE" :value "29 FEB 2000" :xref nil} (parse-line "2 DATE 29 FEB 2000")))
    (is (= {:level 0 :tag "INDI" :value nil :xref "@FATHER@"} (parse-line "0 @FATHER@ INDI"))))

  (testing "error cases"
    (is (thrown? ahnentafel.ParseError (parse-line "abc def")))
    (is (thrown? ahnentafel.ParseError (parse-line "01 CHAR ASCII")))))
