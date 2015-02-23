(ns ahnentafel.gedcom.reading.reader)

(defn- read-file-lines [file]
  "Read all the lines of the file. Returns NIL if file does not exist."
  (when (.exists (clojure.java.io/as-file file))
    (with-open [rdr (clojure.java.io/reader file)]
      (doall (line-seq rdr)))))

(defn- parse-line [line]
  "Parses a single GEDCOM line into a map.

  Throws ahnentafel.gedcom.ParseError if line is not well formatted."
  (when (re-find #"^0\d+" line)
    (throw (ahnentafel.gedcom.ParseError. line)))

  (if-let [[_ level xref tag value]
           (re-find #"^(\d+) (@\S+@)?\s?(\S+)\s?(.+)?" line)]
    {:level (Integer. level)
     :xref xref
     :tag tag
     :value value}
    (throw (ahnentafel.gedcom.ParseError. line))))

(defn- split-out-subordinate-records [records]
  "Returns a sequence of two sequences. The first contains the records
  which are subordinate to the first record, the second contains the
  records which are not subordinate.

  Subordinate records are those following the current record whose
  level is higher than the current record."
  (letfn [(subordinate-to-first [r] (> (:level r) (:level (first records))))]
    (split-with subordinate-to-first (rest records))))

(defn- group-records
  "Groups the sequence of records into a sequence of records with
  their subordinate records added with a :subordinate-lines key."
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

(defn read-file [file]
  "Read a GEDCOM file into a sequence of records.

  This does minimal parsing by parsing the lines into records and then
  grouping subordinate-records into parent records."
  (group-records
   (map parse-line
        (read-file-lines file))))
