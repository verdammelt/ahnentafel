(ns ahnentafel.gedcom.reader
  (:require [ahnentafel.gedcom.reading.lexer :refer [read-file-lines]])
  (:require [ahnentafel.gedcom.reading.parser :refer [parse-lines]])
  (:require [ahnentafel.gedcom.reading.semantic :refer [process-records]]))

(defn read-file [file]
  "Read a GEDCOM file into a sequence of records.

  This does minimal parsing by parsing the lines into records and then
  grouping subordinate-records into parent records."
  (-> file
      read-file-lines
      parse-lines
      process-records))
