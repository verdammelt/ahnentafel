(ns ahnentafel.gedcom.reading.parser)

(defn parse-line [line]
  "Parses a single GEDCOM line into a map.

  Throws ahnentafel.gedcom.reading.ParseError if line is not well formatted."
  (when (re-find #"^0\d+" line)
    (throw (ahnentafel.gedcom.reading.ParseError. line)))

  (if-let [[_ level xref tag value]
           (re-find #"^(\d+) (@\S+@)?\s?(\S+)\s?(.+)?" line)]
    {:level (Integer. level)
     :xref xref
     :tag tag
     :value value}
    (throw (ahnentafel.gedcom.reading.ParseError. line))))

(defn parse-lines [lines] (map parse-line lines))
