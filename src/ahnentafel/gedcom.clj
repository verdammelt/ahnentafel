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
