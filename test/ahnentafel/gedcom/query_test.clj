(ns ahnentafel.gedcom.query-test
  (:require [ahnentafel.gedcom.query :as query]
            [ahnentafel.gedcom.reader :refer [read-file]]
            [ahnentafel.gedcom.util.zipper :refer [gedcom-zipper]]
            [clojure.zip :as zip]
            [clojure.test :refer :all]))

(def test-tree (read-file "resource:sample.ged"))

(deftest header-data
  (let [header (query/header test-tree "@I2695@")]
    (is (= (:number-of-records header) 45))
    (is (= (:source header) "Ancestral Quest"))
    (is (= (:destination header) "Ancestral Quest"))
    (is (= (:file header) "sample.ged"))
    (is (= (:file-time header) "13 SEP 2000 10:23:03"))
    (is (= (:gedcom header) {:version "5.5" :type "LINEAGE-LINKED"}))
    (is (= (:encoding header) "ANSEL"))
    (is (= (:submitter header) {:name "John Doe" :xref "@SUB1@"}))
    (is (= (:start-record header) {:name "Emerald /Dearden/" :xref "@I2695@"})))

  (testing "without start-record uses first INDI"
    (is (= (:start-record (query/header test-tree "@I9999999"))
           {:name "William Russell /Hartley/"
            :xref "@I52@"}))
    (is (= (:start-record (query/header test-tree nil))
           {:name "William Russell /Hartley/"
            :xref "@I52@"})))

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
      (is (= (:name record) nil))))

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
      (is (= (:family-as-spouse record) [{:xref "@F661@"
                                          :spouse {:xref "@I2695@" :name "Emerald /Dearden/"}}]))))

  (testing "submitter"
    (let [record (query/find-record test-tree {:xref "@SUB1@"})]
      (is (= (:type record) :submitter))
      (is (= (:name record) ["John Doe"]))
      (doseq [x [:sex :family-as-child :family-as-spouse]]
        (is (= nil (get record x))
            (str "Value of key " x " should be nil")))
      (doseq [x [:birth :death :burial]]
        (is (not (contains? record x))
            (str "Record should not contain key " x)))))

  (testing "family"
    (let [record (query/find-record test-tree {:xref "@F661@"})]
      (is (= (:type record) :family))
      (is (= (:spouses record)
             [{:xref "@I2694@" :name "William /Hartley/"}
              {:xref "@I2695@" :name "Emerald /Dearden/"}]))
      (is (= (:marriage record)
             {:date "8 MAY 1885" :place "Logan,Cache,Utah"}))
      (is (= (:children record)
             [{:xref "@I52@" :name "William Russell /Hartley/"}])))))

(deftest search
  (testing "no result"
    (is (= (query/search test-tree "name not found") [])))
  (testing "one result"
    (let [results (query/search test-tree "William Russell /Hartley/")
          record (first results)]
      (is (= (count results) 1))

      (is (= (:xref record) "@I52@"))
      (is (= (:type record) :individual))
      (is (= (:name record) '("William Russell /Hartley/")))
      (is (= (:sex record) "M"))
      (is (= (:birth record) {:date "27 NOV 1892" :place "Pleasant Green,Salt Lake,Utah"}))
      (is (= (:death record) {:date "29 JAN 1977" :place "Lethbridge,Alberta,Canada"}))
      (is (= (:burial record) {:date "2 FEB 1977" :place "Stirling,Alberta,Canada"}))
      (is (= (:family-as-child record) "@F661@"))))

  (testing "many results"
    (let [results (query/search test-tree "Hartley")]
      (is (= (count results) 4))

      ;; spot check that we are getting different records
      (is (= (map :name results)
             '[("William Russell /Hartley/")
               ("William /Hartley/")
               ("David /Hartley/")
               ("William /Hartley/")]))
      (is (= (map #(get-in % '(:birth :date)) results)
             '("27 NOV 1892"
               "16 JAN 1864"
               "Abt 1807"
               "1 NOV 1833")))))

  (testing "case insensitive"
    (let [results (query/search test-tree "hartley")]
      (is (= (count results) 4))))

  (testing "ignore surname slashes"
    (let [results (query/search test-tree "david hartley")]
      (is (= (count results) 1))))

  (testing "finding in all names"
    (let [test-tree (read-file "resource:allged.ged")
          results (query/search test-tree "another name /surname/")]
      (is (= (count results) 1))))
  )
