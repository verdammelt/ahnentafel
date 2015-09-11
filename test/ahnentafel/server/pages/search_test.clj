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
        "No records found"
        "\"Sir Not-Appearing-In-This-Film\"")))))
