(ns ahnentafel.gedcom.reading.parser-test
  (:require [ahnentafel.gedcom.reading.parser :refer [parse-line]]
            [clojure.test :refer :all]))

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
    (is (thrown? ahnentafel.gedcom.reading.ParseError (parse-line "abc def")))
    (is (thrown? ahnentafel.gedcom.reading.ParseError (parse-line "01 CHAR ASCII")))))
