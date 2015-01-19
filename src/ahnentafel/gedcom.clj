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

(defn- records-subordinate-to [record]
  (fn [r] (> (:level r) (:level record))))

(defn group-records
  ([records] (group-records (first records)
                            (split-with (records-subordinate-to (first records)) (rest records))
                            []))
  ([current-record [subordinate-records unprocessed-records] processed-records]
   (println [current-record subordinate-records unprocessed-records processed-records])
   (cond (and (empty? unprocessed-records) (empty? subordinate-records)) (conj processed-records current-record)
         (empty? unprocessed-records) (group-records (assoc current-record :subordinate-lines (group-records subordinate-records))
                                                     (list nil unprocessed-records)
                                                     processed-records)
         (empty? subordinate-records) (group-records (first unprocessed-records)
                                                     (split-with (records-subordinate-to (first unprocessed-records)) (rest unprocessed-records))
                                                     (conj processed-records current-record))
         :else (group-records (first unprocessed-records)
                              (split-with (records-subordinate-to (first unprocessed-records)) (rest unprocessed-records))
                              (conj processed-records current-record)))
   )
)
