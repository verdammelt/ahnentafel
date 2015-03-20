(ns ahnentafel.gedcom.reading.semantic)

(defn- split-out-subordinate-records [records]
  "Returns a sequence of two sequences. The first contains the records
  which are subordinate to the first record, the second contains the
  records which are not subordinate.

  Subordinate records are those following the current record whose
  level is higher than the current record."
  (letfn [(subordinate-to-first [r] (> (:level r) (:level (first records))))]
    (split-with subordinate-to-first (rest records))))

(defn- process-subordinate-records [records]
  "Groups the sequence of records into a sequence of records with
  their subordinate records added with a :subordinate-lines key."
  (if (seq records)
    (loop [current-record (first records)
           [subordinate-records unprocessed-records] (split-out-subordinate-records records)
           processed-records []]
      (letfn [(assoc-if [map key value] (if value (assoc map key value) map))
              (conj-current-with-subordinates []
                (conj processed-records
                      (assoc-if current-record
                                :subordinate-lines
                                (process-subordinate-records subordinate-records))))]

        (if (empty? unprocessed-records)
          (conj-current-with-subordinates)
          (recur (first unprocessed-records)
                 (split-out-subordinate-records unprocessed-records)
                 (conj-current-with-subordinates)))))))

(defn process-records [records]
  (-> records
      process-subordinate-records
      ))
