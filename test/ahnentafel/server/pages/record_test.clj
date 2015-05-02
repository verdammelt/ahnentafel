(ns ahnentafel.server.pages.record-test
  (:require [ahnentafel.server.pages.record :refer :all])

  (:require [ahnentafel.server.pages.test-utils :refer :all])
  (:require [ahnentafel.gedcom.query :as query])
  (:require [clojure.test :refer :all]))


(deftest record-page-test
  (testing "individual record - maximum data"
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

  (testing "individual record - with parts missing"
    (with-redefs [query/find-record
                  (constantly {:type :individual})]
      (let [page (get-page record "@I23@")]
        (are [text] (page-not-contains? text)
             "Name" "Sex" "Born" "Died" "Buried" "Go To Family")))))
