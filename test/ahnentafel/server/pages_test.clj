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
  (testing "individual record"
    ;; TODO test cases of missing data (especially family)

    (with-local-vars [trapped-query nil]
      (with-redefs [data/find-record
                    (fn [query data]
                      (var-set trapped-query query)
                      {:name ["Bob Smith" "Robert Smith", "The Guy from The Cure"]
                       :birth {:date "1 JAN 1970 00:00:00"
                               :place "near his mother"}
                       :death {:date "1 JAN 2000 00:00:00"
                               :place "graveside"}
                       :sex "M"
                       :family "@FAM1@"})]
        (let [page (apply str (record-page {:xref "@I23@" :get-data (fn [] 'fake-data)}))]
          (is (= @trapped-query {:xref "@I23@"}))
          (is (.contains page "Bob Smith (a.k.a. Robert Smith, The Guy from The Cure)"))
          (is (.contains page "Sex: M"))
          (is (.contains page "Born: 1 JAN 1970 00:00:00 near his mother"))
          (is (.contains page "Died: 1 JAN 2000 00:00:00 graveside"))
          (is (.contains page "<a href=\"/records/@FAM1@\">Go To Family</a>")))))))
