(ns ahnentafel.gedcom.reading.lexer
  (:require [clojure.java.io :refer [as-file reader]]))

(defn read-file-lines [file]
  "Read all the lines of the file. Returns NIL if file does not exist."
  (try
    (with-open [rdr (reader file)]
      (doall (line-seq rdr)))
    (catch Exception e nil)))
