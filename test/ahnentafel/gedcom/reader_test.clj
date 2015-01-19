(ns ahnentafel.gedcom.reader-test
  (:require [ahnentafel.gedcom.reader :refer [read-file]]
            [clojure.test :refer :all]))

(deftest reader
  (testing "usual behavior"
    (let [records (read-file (clojure.java.io/resource "simple.ged"))]
      (is (= 7 (count records)))

      (is (= "HEAD" (:tag (first records))))
      (is (= "CHAR" (:tag (first (:subordinate-lines (first records))))))
      (is (= "SUBM" (:tag (second records))))
      (is (= "TRLR" (:tag (first (reverse records)))))))

  (testing "error conditions"
    (is (nil? (read-file "does-not-exist.ged")))))

(def parse-line
  "Accessing unexported symbol PARSE-LINE."
  #'ahnentafel.gedcom.reader/parse-line)
(deftest parsing-lines
  (testing "valid lines"
    (are [line expected] (= expected (parse-line line))
         "0 HEAD"
         {:level 0 :tag "HEAD" :value nil :xref nil}

         "2 HEAD"
         {:level 2 :tag "HEAD" :value nil :xref nil}

         "10 HEAD"
         {:level 10 :tag "HEAD" :value nil :xref nil}

         "2 DATE 29 FEB 2000"
         {:level 2 :tag "DATE" :value "29 FEB 2000" :xref nil}

         "2 DATE 29 FEB 2000"
         {:level 2 :tag "DATE" :value "29 FEB 2000" :xref nil}

         "0 @FATHER@ INDI"
         {:level 0 :tag "INDI" :value nil :xref "@FATHER@"}))

  (testing "error cases"
    (is (thrown? ahnentafel.gedcom.ParseError (parse-line "abc def")))
    (is (thrown? ahnentafel.gedcom.ParseError (parse-line "01 CHAR ASCII")))))

(def group-records
  "Accessing unexported symbol GROUP-RECORDS."
  #'ahnentafel.gedcom.reader/group-records)
(deftest grouping-records
  (is (= (group-records '({:level 0 :tag "HEAD"}
                          {:level 0 :tag "INDI"}))
         '[{:level 0 :tag "HEAD"}
           {:level 0 :tag "INDI"}]))

  (is (= (group-records '({:level 0 :tag "HEAD"} {:level 1 :tag "CHAR"}))
         [{:level 0
           :tag "HEAD"
           :subordinate-lines [{:level 1 :tag "CHAR"}]}]))

  (is (= (group-records '({:level 0 :tag "HEAD"}
                          {:level 1 :tag "GEDC"}
                          {:level 2 :tag "VERS"}))
         [{:level 0
           :tag "HEAD"
           :subordinate-lines [{:level 1
                                :tag "GEDC"
                                :subordinate-lines [{:level 2
                                                     :tag "VERS"}]}]}]))

  (is (= (group-records '({:level 0 :tag "HEAD"}
                          {:level 1 :tag "CHAR"}
                          {:level 0 :tag "INDI"}))
         [{:level 0
           :tag "HEAD"
           :subordinate-lines [{:level 1 :tag "CHAR"}]}
          {:level 0
           :tag "INDI"}]))

  (is (= (group-records '({:level 0 :tag "HEAD"}
                          {:level 1 :tag "CHAR"}
                          {:level 0 :tag "INDI"}
                          {:level 1 :tag "NAME"}))
         [{:level 0
           :tag "HEAD"
           :subordinate-lines [{:level 1 :tag "CHAR"}]}
          {:level 0
           :tag "INDI"
           :subordinate-lines [{:level 1 :tag "NAME"}]}]))
  )
