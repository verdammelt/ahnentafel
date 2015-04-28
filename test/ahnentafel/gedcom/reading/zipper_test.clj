(ns ahnentafel.gedcom.reading.zipper-test
  (:require [ahnentafel.gedcom.reading.zipper :refer [gedcom-zipper]])
  (:require [clojure.zip :as zip])
  (:require [clojure.test :refer :all]))

(deftest node-insertion
  (let [z (gedcom-zipper {:level -1 :tag "__ROOT__"})]
    (is (= (-> z (zip/insert-child {:level 0 :tag "HEAD"}) zip/root)
           {:level -1 :tag "__ROOT__"
            :subordinate-lines [{:level 0 :tag "HEAD"}]}))

    (is (= (-> z (zip/insert-child {:level 0 :tag "HEAD"})
               zip/next
               (zip/insert-child {:level 1 :tag "CONC"})
               (zip/insert-right {:level 0 :tag "INDI"})
               zip/root)
           {:level -1 :tag "__ROOT__"
            :subordinate-lines
            [{:level 0 :tag "HEAD"
              :subordinate-lines [{:level 1 :tag "CONC"}]}
             {:level 0 :tag "INDI"}]}))))

(deftest node-deletion
  (let [z (gedcom-zipper
           (-> (gedcom-zipper {:level -1 :tag "__ROOT__"})
               (zip/insert-child {:level 0 :tag "HEAD"})
               (zip/append-child {:level 0 :tag "INDI"})
               zip/next
               (zip/insert-child {:level 1 :tag "CONC"})
               zip/root))]
    (is (= (-> z zip/next zip/next zip/remove zip/next zip/node)
           {:level 0 :tag "INDI"}))
    ))
