(ns ahnentafel.gedcom.reader)

(defn read-resource [resource-name]
  (when-let [file (clojure.java.io/resource resource-name)]
    (with-open [rdr (clojure.java.io/reader file)]
      (doall (line-seq rdr)))))

(defn parse-line [line]
  (when (re-find #"^0\d+" line) (throw (ahnentafel.gedcom.ParseError. line)))

  (if-let [[_ level xref tag value]
           (re-find #"^(\d+) (@\S+@)?\s?(\S+)\s?(.+)?" line)]
    {:level (Integer. level)
     :xref xref
     :tag tag
     :value value}
    (throw (ahnentafel.gedcom.ParseError. line))))

(defn- split-out-subordinate-records [records]
  (letfn [(subordinate-to-first [r] (> (:level r) (:level (first records))))]
    (split-with subordinate-to-first (rest records))))

(defn group-records
  ([records] (if (seq records)
               (group-records (first records)
                              (split-out-subordinate-records records)
                              [])))
  ([current-record [subordinate-records unprocessed-records] processed-records]
   (letfn [(assoc-if [map key value] (if value (assoc map key value) map))
           (conj-current-with-subordinates []
             (conj processed-records
                   (assoc-if current-record
                             :subordinate-lines
                             (group-records subordinate-records))))]

     (if (empty? unprocessed-records)
       (conj-current-with-subordinates)
       (group-records (first unprocessed-records)
                      (split-out-subordinate-records unprocessed-records)
                      (conj-current-with-subordinates))))))
