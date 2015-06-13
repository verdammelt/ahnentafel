(ns ahnentafel.gedcom.reader
  (:require [ahnentafel.gedcom.reading.lexer :refer [read-file-lines]])
  (:require [ahnentafel.gedcom.reading.parser :refer [parse-lines]])
  (:require [ahnentafel.gedcom.reading.semantic :refer [process-records]])
  (:require [ahnentafel.gedcom.util.url :refer [to-url]]))

(defn read-file [file]
  "Read a GEDCOM file into a sequence of records.

  This does minimal parsing by parsing the lines into records and then
  grouping subordinate-records into parent records."
  (-> file
      to-url
      read-file-lines
      parse-lines
      process-records))
