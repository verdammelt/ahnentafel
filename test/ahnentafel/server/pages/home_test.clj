(ns ahnentafel.server.pages.home-test
  (:require [ahnentafel.server.pages.home :refer :all])

  (:require [ahnentafel.server.pages.test-utils :refer :all])
  (:require [ahnentafel.gedcom.query :as query])
  (:require [clojure.test :refer :all]))

(deftest home-page-test
  (with-redefs [query/header (constantly {:number-of-records 23
                                          :source "Author"
                                          :destination "Recipient"
                                          :file "test.ged"
                                          :file-time "13 SEP 2000"
                                          :gedcom {:version "5.5.1"
                                                   :type "LINEAGE-LINKED"}
                                          :encoding "ANSEL"
                                          :submitter {:xref "@I31@"
                                                      :name "Fred Smith"}
                                          :start-record {:xref "@I13@"
                                                         :name "Joe Smith"}})]
    (let [page (get-page home)]
      (are-on-page
       "<title>Ahnentafel</title>"
       "23 records in test.ged."
       "id=\"home-contents\""
       "created by Author"
       "for Recipient"
       "on 13 SEP 2000"
       "GEDCOM version 5.5.1 (LINEAGE-LINKED ANSEL)"
       "Submitted by <a href=\"/records/@I31@\">Fred Smith</a>"
       "Start by looking at <a href=\"/records/@I13@\">Joe Smith</a>"
       "<span id=\"version\">x.x.x</span>"))))
