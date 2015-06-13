(ns ahnentafel.gedcom.reader
  (:require [ahnentafel.gedcom.reading.lexer :refer [read-file-lines]])
  (:require [ahnentafel.gedcom.reading.parser :refer [parse-lines]])
  (:require [ahnentafel.gedcom.reading.semantic :refer [process-records]])

  (:require [amazonica.aws.s3 :as s3])
  (:require [clj-time.core :as time-core])
  (:require [clj-time.coerce :as time-coerce])

  (:import (java.net URI)))

(defn- to-url [file]
  (let [uri (URI. file)]
    (case (.getScheme uri)
      nil file
      "resource" (clojure.java.io/resource (.getSchemeSpecificPart uri))
      "aws" (s3/generate-presigned-url (.getSchemeSpecificPart uri)
                                       (.getFragment uri)
                                       (time-coerce/to-long
                                        (time-core/plus
                                         (time-core/now)
                                         (time-core/minutes 5)))))))

(defn read-file [file]
  "Read a GEDCOM file into a sequence of records.

  This does minimal parsing by parsing the lines into records and then
  grouping subordinate-records into parent records."
  (-> file
      to-url
      read-file-lines
      parse-lines
      process-records))
