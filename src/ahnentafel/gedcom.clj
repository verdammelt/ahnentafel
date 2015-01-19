(ns ahnentafel.gedcom)

(defn read-resource [resource-name]
  (when-let [file (clojure.java.io/resource resource-name)]
   (with-open [rdr (clojure.java.io/reader file)]
     (doall (line-seq rdr)))))

(defn parse-line [line]
  (when (re-find #"^0\d+" line) (throw (ahnentafel.ParseError. line)))

  (if-let [[_ level xref tag value]
           (re-find #"^(\d+) (@\S+@)?\s?(\S+)\s?(.+)?" line)]
    {:level (Integer. level)
     :xref xref
     :tag tag
     :value value}
    (throw (ahnentafel.ParseError. line))))

(defn- split-out-subordinate-records [records]
  (letfn [(subordinate-to-first [r] (> (:level r) (:level (first records))))]
   (split-with subordinate-to-first (rest records))))

(defn group-records
  ([records] (group-records (first records)
                            (split-out-subordinate-records records)
                            []))
  ([current-record [subordinate-records unprocessed-records] processed-records]
   (cond (and (empty? unprocessed-records) (empty? subordinate-records))
         (conj processed-records current-record)

         (empty? unprocessed-records)
         (group-records (assoc current-record
                               :subordinate-lines
                               (group-records subordinate-records))
                        (list nil unprocessed-records)
                        processed-records)

         :else
         (group-records (first unprocessed-records)
                        (split-out-subordinate-records unprocessed-records)
                        (conj processed-records current-record)))
   )
)
