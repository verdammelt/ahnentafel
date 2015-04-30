(ns ahnentafel.server.pages-test
  (:require [ahnentafel.server.pages.home :refer :all])
  (:require [ahnentafel.server.pages.record :refer :all])
  (:require [ahnentafel.server.pages.not-found :refer :all])

  (:require [ahnentafel.gedcom.query :as query])
  (:require [clojure.test :refer :all]))

(defn get-page [page & args]
  (apply str (apply page
                    {:version "x.x.x"
                    :gedcom-file "/path/to/test.ged"
                     :get-data (fn [] 'fake-data)}
                    args)))

(defmethod assert-expr 'page-contains? [msg form]
  (let [text (nth form 1)
        msg (or msg (str "Expected to find '" text "' on the page."))]
    `(let [result# (.contains ~'page ~text)]
       (do-report {:message ~msg :expected '~form :actual ~'page
                   :type (if result# :pass :fail)}))))

(defmethod assert-expr 'page-not-contains? [msg form]
  (let [text (nth form 1)
        msg (or msg (str "Expected not to find '" text "' on the page."))]
    `(let [result# (.contains ~'page ~text)]
       (do-report {:message ~msg :expected '~form :actual ~'page
                   :type (if result# :fail :pass)}))))

(defmacro are-on-page [& texts]
  `(are [text] (~'page-contains? text) ~@texts))

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
                                                     :name "Fred Smith"}})]
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
       "<span id=\"version\">x.x.x</span>"))))

(deftest page-not-found-test
  (let [page (get-page not-found {:uri "/unknown"})]
    (are-on-page
     "<title>Ahnentafel</title>"
     "/unknown not found."
     "<span id=\"version\">x.x.x</span>")))

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
