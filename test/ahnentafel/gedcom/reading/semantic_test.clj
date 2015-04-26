(ns ahnentafel.gedcom.reading.semantic-test
  (:require [ahnentafel.gedcom.reading.semantic :refer [process-records]]
            [clojure.test :refer :all]))

(deftest grouping-records
  (is (= (process-records '({:level 0 :tag "HEAD"}
                            {:level 0 :tag "INDI"}))
         {:level -1 :tag "__ROOT__"
          :subordinate-lines[{:level 0 :tag "HEAD"}
                             {:level 0 :tag "INDI"}]}))

  (is (= (process-records '({:level 0 :tag "HEAD"}
                            {:level 1 :tag "CHAR"}))
         {:level -1 :tag "__ROOT__"
          :subordinate-lines [{:level 0
                               :tag "HEAD"
                               :subordinate-lines [{:level 1 :tag "CHAR"}]}]}))

  (is (= (process-records '({:level 0 :tag "HEAD"}
                    {:level 1 :tag "GEDC"}
                    {:level 2 :tag "VERS"}))
         {:level -1 :tag "__ROOT__"
          :subordinate-lines [{:level 0
                               :tag "HEAD"
                               :subordinate-lines [{:level 1
                                                    :tag "GEDC"
                                                    :subordinate-lines [{:level 2
                                                                         :tag "VERS"}]}]}]}))

  (is (= (process-records '({:level 0 :tag "HEAD"}
                    {:level 1 :tag "CHAR"}
                    {:level 0 :tag "INDI"}))
         {:level -1 :tag "__ROOT__"
          :subordinate-lines [{:level 0
                               :tag "HEAD"
                               :subordinate-lines [{:level 1 :tag "CHAR"}]}
                              {:level 0 :tag "INDI"}]}))

  (is (= (process-records '({:level 0 :tag "HEAD"}
                    {:level 1 :tag "CHAR"}
                    {:level 0 :tag "INDI"}
                    {:level 1 :tag "NAME"}))
         {:level -1 :tag "__ROOT__"
          :subordinate-lines [{:level 0
                               :tag "HEAD"
                               :subordinate-lines [{:level 1 :tag "CHAR"}]}
                              {:level 0
                               :tag "INDI"
                               :subordinate-lines [{:level 1 :tag "NAME"}]}]}))

  ;; (is (= (process-records '({:level 0 :tag "STUFF" :value "Bob"}
  ;;                           {:level 1 :tag "CONT" :value "more Bob"}))
  ;;        {:level -1 :tag "__ROOT__"
  ;;         :subordinate-lines [{:level 0 :tag "STUFF" :value "Bob\nmore Bob"}]}))
  )
