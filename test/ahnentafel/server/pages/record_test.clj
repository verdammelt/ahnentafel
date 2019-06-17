(ns ahnentafel.server.pages.record-test
  (:require [ahnentafel.server.pages.record :refer :all]
            [net.cgrand.enlive-html :as html])

  (:require [ahnentafel.server.pages.test-utils :refer :all])
  (:require [ahnentafel.gedcom.query :as query])
  (:require [clojure.test :refer :all])
  (:import java.io.StringReader))

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
                       :family-as-spouse '({:xref "@FAM2@"
                                            :spouse {:xref "@I13@" :name "Mary /Jones"}}
                                           {:xref "@FAM3@"
                                            :spouse {:xref "@I23@" :name "Jane /Smith"}})
                       })]
        (let [page (html/html-resource (StringReader. (get-page record "@I23@")))]
          (is (= @trapped-query {:xref "@I23@"}))
          (let [type (contents-of-element page [:div#type])]
            (is (= '("INDIVIDUAL") (map first type))))
          (let [names (contents-of-element page [:div#names])]
            (is (= '("Bob Smith (a.k.a. Robert Smith, The Guy from The Cure)") (map first names))))
          (let [gender (contents-of-element page [:div#sex])]
            (is (= '("Sex: M") (first gender))))
          (let [birth (contents-of-element page [:div#birth])]
            (is (= '("Born: 1 JAN 1970 00:00:00 near his mother") (map first birth))))
          (let [death (contents-of-element page [:div#death])]
            (is (= '("Died: 1 JAN 2000 00:00:00 graveside") (first death))))
          (let [burial (contents-of-element page [:div#burial])]
            (is (= '("Buried: 2 JAN 2000 00:00:00 6 feet under") (first burial))))
          (let [as-child (contents-of-element page [:div#family-as-child])]
            (is (= '("View parents") (flatten (map #(-> % first html/unwrap) as-child))))
            (is (= '("/records/@FAM1@") (flatten (map #(-> % first :attrs :href) as-child)))))
          (let [as-spouse (contents-of-element page [:div#family-as-spouse])]
            (is (= '("View family with " "View family with ")
                   (flatten (map #(-> % first html/unwrap first) as-spouse))))
            (is (= '("Mary /Jones" "Jane /Smith")
                   (flatten (map #(-> % first html/unwrap second html/unwrap) as-spouse))))
            (is (= '("/records/@FAM2@" "/records/@FAM3@")
                   (flatten (map #(-> % first :attrs :href) as-spouse)))))))))

  (testing "with parts missing"
    (with-redefs [query/find-record
                  (constantly {:type :individual})]
      (let [page (get-page record "@I23@")]
        (are [text] (page-not-contains? text)
          "Sex" "Born" "Died" "Buried" "View family with" "View parents")))))

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
        (let [page (html/html-resource (StringReader. (get-page record "@FAM1@")))]
          (is (= @trapped-query {:xref "@FAM1@"}))
          (let [type (contents-of-element page [:div#type])]
            (is (= '("FAMILY") (map first type))))
          (let [spouses (contents-of-element page [:div#spouse])]
            (is (= '("Spouse: " "Spouse: ") (map first spouses)))
            (is (= '("Ted" "Carol") (flatten (map #(-> % second html/unwrap) spouses))))
            (is (= '("/records/@I1@" "/records/@I2@") (flatten (map #(-> % second :attrs :href) spouses)))))
          (let [marriages (contents-of-element page [:div#married])]
            (is (= '("Married: ") (map first marriages)))
            (is (= '("1 JAN 2000 00:00:00 church") (flatten (map #(-> % second html/unwrap) marriages)))))
          (let [children (map html/unwrap (html/select page [:div#child]))]
            (is (= '("Child: " "Child: ") (map first children)))
            (is (= '("Bob" "Alice") (flatten (map #(-> % second html/unwrap) children))))
            (is (= '("/records/@I3@" "/records/@I4@") (flatten (map #(-> % second :attrs :href) children))))))))))
