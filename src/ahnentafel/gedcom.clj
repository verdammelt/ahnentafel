(ns ahnentafel.gedcom)

(defn read-resource [resource-name]
  (when-let [file (clojure.java.io/resource resource-name)]
   (with-open [rdr (clojure.java.io/reader file)]
     (doall (line-seq rdr)))))

(defn parse-level [line]
  (when (re-find #"^0\d+" line) (throw (ahnentafel.ParseError. line)))
  (if-let [level (re-find #"^\d+" line)]
    (Integer. level)
    (throw (ahnentafel.ParseError. line))))
