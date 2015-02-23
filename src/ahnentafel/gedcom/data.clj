(ns ahnentafel.gedcom.data
  (:require [ahnentafel.gedcom.reading.reader :refer [read-file]]))

(defn load-data [file]
  (future (read-file file)))

(defn all-data [family-tree] @family-tree)
