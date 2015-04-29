(ns ahnentafel.server.pages-test
  (:require [ahnentafel.server.pages :refer :all])
  (:require [ahnentafel.gedcom.data :as data])
  (:require [clojure.test :refer :all]))

(deftest home-page-test
  (with-redefs [data/header (constantly {:number-of-records 23
                                         :source "Author"
                                         :destination "Recipient"
                                         :file "test.ged"
                                         :file-time "13 SEP 2000"
                                         :gedcom {:version "5.5.1"
                                                  :type "LINEAGE-LINKED"}
                                         :encoding "ANSEL"
                                         :submitter {:xref "@I31@"
                                                     :name "Fred Smith"}})]
    (let [page (apply str (home-page {:version "x.x.x"
                                      :get-data (fn [] 'fake-data)
                                      :file "/path/to/test.ged"}))]
      (is (.contains page "<title>Ahnentafel</title>"))
      (is (.contains page "23 records in test.ged."))
      (is (.contains page "id=\"home-contents\""))
      (is (.contains page "created by Author"))
      (is (.contains page "for Recipient"))
      (is (.contains page "on 13 SEP 2000"))
      (is (.contains page "GEDCOM version 5.5.1 (LINEAGE-LINKED ANSEL)"))
      (is (.contains page "Submitted by <a href=\"/records/@I31@\" id=\"submitter\">Fred Smith</a>"))
      (is (.contains page "<span id=\"version\">x.x.x</span>")))))

(deftest page-not-found-test
  (let [page (apply str (page-not-found {:uri "/unknown" :version "x.x.x"}))]
    (is (.contains page "<title>Ahnentafel</title>"))
    (is (.contains page "/unknown not found."))
    (is (.contains page "<span id=\"version\">x.x.x</span>"))))

(deftest record-page-test
  (testing "individual record - maximum data"
    (with-local-vars [trapped-query nil]
      (with-redefs [data/find-record
                    (fn [data query]
                      (var-set trapped-query query)
                      {:type :individual
                       :name ["Bob Smith" "Robert Smith", "The Guy from The Cure"]
                       :birth {:date "1 JAN 1970 00:00:00"
                               :place "near his mother"}
                       :death {:date "1 JAN 2000 00:00:00"
                               :place "graveside"}
                       :burial {:date "2 JAN 2000 00:00:00"
                               :place "6 feet under"}
                       :sex "M"
                       :family-as-child "@FAM1@"
                       :family-as-spouse "@FAM2@"
                       })]
        (let [page (apply str (record-page {:xref "@I23@" :get-data (fn [] 'fake-data)}))]
          (is (= @trapped-query {:xref "@I23@"}))
          (is (.contains page "INDIVIDUAL"))
          (is (.contains page "Bob Smith (a.k.a. Robert Smith, The Guy from The Cure)"))
          (is (.contains page "Sex: M"))
          (is (.contains page "Born: 1 JAN 1970 00:00:00 near his mother"))
          (is (.contains page "Died: 1 JAN 2000 00:00:00 graveside"))
          (is (.contains page "Buried: 2 JAN 2000 00:00:00 6 feet under"))
          (is (.contains page "<a href=\"/records/@FAM1@\">Go To Family (where this person was a child)</a>"))
          (is (.contains page "<a href=\"/records/@FAM2@\">Go To Family (where this person was a spouse)</a>") page)))))

  (testing "individual record - with parts missing"
    (with-redefs [data/find-record
                  (constantly {:type :individual})]
      (let [page (apply str (record-page {:xref "@I23@" :get-data (constantly 'fake-data)}))]
        (doseq [x '("Name" "Sex" "Born" "Died" "Buried" "Go To Family")]
          (is (not (.contains page x))
              (str "Should not find '" x "' on page.")))))))
