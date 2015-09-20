(ns ahnentafel.server.pages.search-test
  (:require [ahnentafel.gedcom.query :as query]
            [ahnentafel.server.pages.search :refer :all]
            [ahnentafel.server.pages.test-utils :refer :all]
            [clojure.string :as str]
            [clojure.test :refer :all]
            [net.cgrand.enlive-html :as html])
  (:import java.io.StringReader))

(deftest no-results
  (with-local-vars [trapped-query nil]
   (with-redefs [query/search
                 (fn [data query]
                   (var-set trapped-query query)
                   '())]
     (let [page (html/html-resource (StringReader. (get-page search "Sir Not-Appearing-In-This-Film")))]
       (is (= @trapped-query "Sir Not-Appearing-In-This-Film"))
       (let [description (contents-of-element page [:#description])]
         (is (= '("No records found for \"Sir Not-Appearing-In-This-Film\"") (map first description))))))))

(deftest one-result
  (with-redefs [query/search
                (fn [data query]
                  [{:xref "@I1@"
                    :name ["John /Doe"]
                    :birth {:date "1 Jan 1970"}
                    :death {:date "1 Jan 2000"}}])]
    (let [page (html/html-resource (StringReader. (get-page search "/Doe")))]
      (let [description (contents-of-element page [:#description])]
        (is (= '("1 record found for \"/Doe\"") (map first description))))
      (let [result-list (contents-of-element page [:#result-list :li])
            the-result (first result-list)]
        (is (= 1 (count result-list)))
        (let [the-link (first the-result)
              the-dates (str/trim (second the-result))]
          (is (= "/records/@I1@" (:href (:attrs the-link))))
          (is (= '("John /Doe") (html/unwrap the-link)))
          (is (= "(1 Jan 1970 - 1 Jan 2000)" the-dates)))))))

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
    (let [page (html/html-resource (StringReader. (get-page search "/Doe")))]
      (let [description (contents-of-element page [:#description])]
        (is (= '("2 records found for \"/Doe\"") (map first description))))
      (let [result-list (contents-of-element page [:#result-list :li])]
        (is (= 2 (count result-list)))
        (let [the-links (map first result-list)
              the-dates (map #(-> % second str/trim) result-list)]
          (is (= '("/records/@I1@" "/records/@I2@") (map #(-> % :attrs :href) the-links)))
          (is (= '(("John /Doe") ("Jane /Doe")) (map html/unwrap the-links)))
          (is (= '("(1 Jan 1970 - 1 Jan 2000)" "(2 Jan 1970 - 2 Jan 2000)") the-dates)))))))

(deftest result-with-multiple-names
  (with-redefs [query/search
                (fn [data query]
                  [{:xref "@I1@"
                    :name ["John /Doe" "John J. /Doe"]
                    :birth {:date "yesterday"}
                    :death {:date "today"}}])]
    (let [page (html/html-resource (StringReader. (get-page search "/Doe")))
          names (html/unwrap (first (html/select page [:#result-list :a])))]
      (is (= '("John /Doe (a.k.a. John J. /Doe)") names)))))

(deftest with-missing-dates
  (with-redefs [query/search
                (fn [data query]
                  [{:xref "@I1@"
                    :name ["John /Doe"]}])]
    (let [page (html/html-resource (StringReader. (get-page search "/Doe")))
          dates (str/trim (second (html/unwrap (first (html/select page [:#result-list :li])))))]
      (is (= "( - )" dates)))))
