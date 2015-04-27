(ns ahnentafel.gedcom.data-test
  (:require [ahnentafel.gedcom.data :as data]
            [ahnentafel.gedcom.reading.reader :refer [read-file]]
            [clojure.java.io :refer [resource]]
            [clojure.test :refer :all]))

(def test-tree (read-file (resource "sample.ged")))

(deftest header-data
  (let [header (data/header test-tree)]
    (is (= (:number-of-records header) 45))
    (is (= (:source header) "Ancestral Quest"))
    (is (= (:destination header) "Ancestral Quest"))
    (is (= (:file header) "sample.ged"))
    (is (= (:file-time header) "13 SEP 2000"))
    (is (= (:gedcom header) {:version "5.5" :type "LINEAGE-LINKED"}))
    (is (= (:encoding header) "ANSEL"))
    (is (= (:submitter header) "@SUB1@"))))
