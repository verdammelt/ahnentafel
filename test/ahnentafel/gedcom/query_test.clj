(ns ahnentafel.gedcom.query-test
  (:require [ahnentafel.gedcom.query :as query]
            [ahnentafel.gedcom.reader :refer [read-file]]
            [ahnentafel.gedcom.util.zipper :refer [gedcom-zipper]]
            [clojure.java.io :refer [resource]]
            [clojure.zip :as zip]
            [clojure.test :refer :all]))

(def test-tree (read-file (resource "sample.ged")))

(deftest header-data
  (let [header (query/header test-tree)]
    (is (= (:number-of-records header) 45))
    (is (= (:source header) "Ancestral Quest"))
    (is (= (:destination header) "Ancestral Quest"))
    (is (= (:file header) "sample.ged"))
    (is (= (:file-time header) "13 SEP 2000 10:23:03"))
    (is (= (:gedcom header) {:version "5.5" :type "LINEAGE-LINKED"}))
    (is (= (:encoding header) "ANSEL"))
    (is (= (:submitter header) {:name "John Doe" :xref "@SUB1@"})))

  (testing "without submitter"
    (let [new-tree (-> (gedcom-zipper test-tree)
                       zip/down zip/down
                       zip/right zip/right zip/right zip/right zip/right zip/right
                       (zip/edit assoc :value nil)
                       zip/root)]
      (let [header (query/header new-tree)]
        (is (not (nil? header)))
        (is (= (:submitter header) nil)))))

  (testing "direct source name"
    (let [new-tree (-> (gedcom-zipper test-tree)
                       zip/down zip/down
                       (zip/edit dissoc :subordinate-lines)
                       zip/root)]
      (let [header (query/header new-tree)]
        (is (= (:source header) "AncestQuest"))))))

(deftest record-data
  (testing "if record not found"
    (let [record (query/find-record test-tree {:xref "xref not to be found"})]
      (is (= (:type record) :unknown))
      (is (= (:name record) '()))))

  (testing "individual"
    (let [record (query/find-record test-tree {:xref "@I52@"})]
      (is (= (:type record) :individual))
      (is (= (:name record) '("William Russell /Hartley/")))
      (is (= (:sex record) "M"))
      (is (= (:birth record) {:date "27 NOV 1892" :place "Pleasant Green,Salt Lake,Utah"}))
      (is (= (:death record) {:date "29 JAN 1977" :place "Lethbridge,Alberta,Canada"}))
      (is (= (:burial record) {:date "2 FEB 1977" :place "Stirling,Alberta,Canada"}))
      (is (= (:family-as-child record) "@F661@")))

    (let [record (query/find-record test-tree {:xref "@I2694@"})]
      (is (= (:family-as-spouse record) "@F661@"))))

  (testing "submitter"
    (let [record (query/find-record test-tree {:xref "@SUB1@"})]
      (is (= (:type record) :submitter))
      (is (= (:name record) ["John Doe"]))
      (doseq [x [:sex :family-as-child :family-as-spouse]]
        (is (= nil (get record x))
            (str "Value of key " x " should be nil")))
      (doseq [x [:birth :death :burial]]
        (is (not (contains? record x))
            (str "Record should not contain key " x))))))
