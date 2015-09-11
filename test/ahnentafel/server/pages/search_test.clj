(ns ahnentafel.server.pages.search-test
  (:require [ahnentafel.gedcom.query :as query]
            [ahnentafel.server.pages.test-utils :refer :all]
            [ahnentafel.server.pages.search :refer :all]
            [clojure.test :refer :all]))

(deftest no-results
  (with-local-vars [trapped-query nil]
   (with-redefs [query/search
                 (fn [data query]
                   (var-set trapped-query query)
                   '())]
     (let [page (get-page search "Sir Not-Appearing-In-This-Film")]
       (is (= @trapped-query "Sir Not-Appearing-In-This-Film"))
       (are-on-page
        "No records found for"
        "\"Sir Not-Appearing-In-This-Film\"")))))

(deftest one-result
  (with-redefs [query/search
                (fn [data query]
                  [{:xref "@I1@"
                    :name ["John /Doe"]
                    :birth {:date "1 Jan 1970"}
                    :death {:date "1 Jan 2000"}}])]
    (let [page (get-page search "/Doe")]
      (are-on-page
       "1 record found for"
       "\"/Doe\""
       "<a href=\"/records/@I1@\" id=\"record-link\">John /Doe</a>"
       "(1 Jan 1970 - 1 Jan 2000)"))))

(deftest multiple-results
  (with-redefs [query/search
                (fn [data query]
                  [{:xref "@I1@"
                    :name ["John /Doe"]
                    :birth {:date "1 Jan 1970"}
                    :death {:date "1 Jan 2000"}}
                   {:xref "@I2@"
                    :name ["Jane /Doe"]
                    :birth {:date "2 Jan 1970"}
                    :death {:date "2 Jan 2000"}}]
                  )]
    (let [page (get-page search "/Doe")]
      (are-on-page
       "2 records found for"
       "<a href=\"/records/@I1@\" id=\"record-link\">John /Doe</a>"
       "<a href=\"/records/@I2@\" id=\"record-link\">Jane /Doe</a>"))))

(deftest result-with-multiple-names)
(deftest with-missing-dates)
