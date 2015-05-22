(ns ahnentafel.server.pages.record-test
  (:require [ahnentafel.server.pages.record :refer :all])

  (:require [ahnentafel.server.pages.test-utils :refer :all])
  (:require [ahnentafel.gedcom.query :as query])
  (:require [clojure.test :refer :all]))


(deftest individual-record-test
  (testing "maximum data"
    (with-local-vars [trapped-query nil]
      (with-redefs [query/find-record
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
        (let [page (get-page record "@I23@")]
          (is (= @trapped-query {:xref "@I23@"}))
          (are-on-page
           "INDIVIDUAL"
           "Bob Smith (a.k.a. Robert Smith, The Guy from The Cure)"
           "Sex: M"
           "Born: 1 JAN 1970 00:00:00 near his mother"
           "Died: 1 JAN 2000 00:00:00 graveside"
           "Buried: 2 JAN 2000 00:00:00 6 feet under"
           "<a href=\"/records/@FAM1@\">Go To Family (where this person was a child)</a>"
           "<a href=\"/records/@FAM2@\">Go To Family (where this person was a spouse)</a>")))))

  (testing "with parts missing"
    (with-redefs [query/find-record
                  (constantly {:type :individual})]
      (let [page (get-page record "@I23@")]
        (are [text] (page-not-contains? text)
             "Name" "Sex" "Born" "Died" "Buried" "Go To Family")))))

(deftest family-record-test
  (testing "maximum data"
    (with-local-vars [trapped-query nil]
      (with-redefs [query/find-record
                    (fn [data query]
                      (var-set trapped-query query)
                      {:type :family
                       :spouses [{:xref "@I1@" :name "Ted"}
                                 {:xref "@I2@" :name "Carol"}]
                       :marriage {:date "1 JAN 2000 00:00:00"
                                  :place "church"}
                       :children [{:xref "@I3@" :name "Bob"}
                                  {:xref "@I4@" :name "Alice"}]})]
        (let [page (get-page record "@FAM1@")]
          (is (= @trapped-query {:xref "@FAM1@"}))
          (are-on-page
           "FAMILY"
           "Spouse: <a id=\"person-info\" href=\"/records/@I1@\">Ted</a>"
           "Spouse: <a id=\"person-info\" href=\"/records/@I2@\">Carol</a>"
           "Married: <span id=\"event-info\">1 JAN 2000 00:00:00 church</span>"
           "Child: <a id=\"person-info\" href=\"/records/@I3@\">Bob</a>"
           "Child: <a id=\"person-info\" href=\"/records/@I4@\">Alice</a>"))))))
