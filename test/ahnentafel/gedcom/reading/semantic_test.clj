(ns ahnentafel.gedcom.reading.semantic-test
  (:require [ahnentafel.gedcom.reading.semantic :refer [process-records]]
            [clojure.test :refer :all]))

(deftest grouping-subordinate-records
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
                               :subordinate-lines [{:level 1 :tag "NAME"}]}]})))

(deftest continuation-lines
  (is (= (process-records '({:level 0 :tag "STUFF" :value "Bob"}
                            {:level 1 :tag "CONT" :value "more Bob"}))
         {:level -1 :tag "__ROOT__"
          :subordinate-lines [{:level 0 :tag "STUFF" :value "Bob\nmore Bob"}]}))

  (is (= (process-records '({:level 0 :tag "STUFF" :value "Bob"}
                            {:level 1 :tag "CONC" :value " more Bob"}))
         {:level -1 :tag "__ROOT__"
          :subordinate-lines [{:level 0 :tag "STUFF" :value "Bob more Bob"}]}))

  (let [processed-records
        (:subordinate-lines
         (process-records '({:level 0 :tag "STUFF" :value "Bob"}
                            {:level 1 :tag "CONT" :value "more Bob"}
                            {:level 1 :tag "CONT" :value "even more Bob"}
                            {:level 1 :tag "OTHER" :value "stuff"}
                            {:level 2 :tag "CONC" :value " and things"}
                            {:level 0 :tag "AGAIN" :value "here we go"}
                            {:level 1 :tag "CONC" :value " again"})))]
    (is (= "Bob\nmore Bob\neven more Bob" (-> processed-records (nth 0) :value)))
    (is (= "stuff and things"
           (-> processed-records
               (nth 0)
               :subordinate-lines
               (nth 0)
               :value)))
    (is (= "here we go again"
           (-> processed-records
               (nth 1)
               :value)))))

(deftest postprocess-datetimes
  (is (= (process-records [{:level 0 :tag "DATE" :value "12 FEB 2000"}
                           {:level 1 :tag "TIME" :value "12:13:14"}])
         {:level -1 :tag "__ROOT__"
          :subordinate-lines
          [{:level 0 :tag "DATE" :value "12 FEB 2000 12:13:14"}]})))
