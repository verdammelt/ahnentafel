(ns ahnentafel.gedcom.reading.semantic-test
  (:require [ahnentafel.gedcom.reading.semantic :refer [group-records]]
            [clojure.test :refer :all]))

(deftest grouping-records
  (is (= (group-records '({:level 0 :tag "HEAD"}
                          {:level 0 :tag "INDI"}))
         '[{:level 0 :tag "HEAD"}
           {:level 0 :tag "INDI"}]))

  (is (= (group-records '({:level 0 :tag "HEAD"} {:level 1 :tag "CHAR"}))
         [{:level 0
           :tag "HEAD"
           :subordinate-lines [{:level 1 :tag "CHAR"}]}]))

  (is (= (group-records '({:level 0 :tag "HEAD"}
                          {:level 1 :tag "GEDC"}
                          {:level 2 :tag "VERS"}))
         [{:level 0
           :tag "HEAD"
           :subordinate-lines [{:level 1
                                :tag "GEDC"
                                :subordinate-lines [{:level 2
                                                     :tag "VERS"}]}]}]))

  (is (= (group-records '({:level 0 :tag "HEAD"}
                          {:level 1 :tag "CHAR"}
                          {:level 0 :tag "INDI"}))
         [{:level 0
           :tag "HEAD"
           :subordinate-lines [{:level 1 :tag "CHAR"}]}
          {:level 0
           :tag "INDI"}]))

  (is (= (group-records '({:level 0 :tag "HEAD"}
                          {:level 1 :tag "CHAR"}
                          {:level 0 :tag "INDI"}
                          {:level 1 :tag "NAME"}))
         [{:level 0
           :tag "HEAD"
           :subordinate-lines [{:level 1 :tag "CHAR"}]}
          {:level 0
           :tag "INDI"
           :subordinate-lines [{:level 1 :tag "NAME"}]}]))
  )
