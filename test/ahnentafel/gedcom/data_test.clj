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
    (is (= (:file-time header) "13 SEP 2000 10:23:03"))
    (is (= (:gedcom header) {:version "5.5" :type "LINEAGE-LINKED"}))
    (is (= (:encoding header) "ANSEL"))
    (is (= (:submitter header) {:name "John Doe" :xref "@SUB1@"}))))

(deftest record-data
  (testing "individual"
    (let [record (data/find-record test-tree {:xref "@I52@"})]
      (is (= (:type record) :individual))
      (is (= (:name record) '("William Russell /Hartley/")))
      (is (= (:sex record) "M"))
      (is (= (:birth record) {:date "27 NOV 1892" :place "Pleasant Green,Salt Lake,Utah"}))
      (is (= (:death record) {:date "29 JAN 1977" :place "Lethbridge,Alberta,Canada"}))
      (is (= (:burial record) {:date "2 FEB 1977" :place "Stirling,Alberta,Canada"}))
      (is (= (:family-as-child record) "@F661@"))
      )
    ;; TODO test case of multiple names
    ;; TODO test family-as-parent
    ))
