(ns ahnentafel.gedcom)

(defn read-resource [resource-name]
  (if-let [file (clojure.java.io/resource resource-name)]
   (with-open [rdr (clojure.java.io/reader file)]
     (doall (line-seq rdr)))))
