(ns ahnentafel.gedcom.util.url
  (:require [amazonica.aws.s3 :as s3])
  (:require [clj-time.core :as time-core])
  (:require [clj-time.coerce :as time-coerce])

  (:import (java.net URI)))

(defn to-url [file]
  (let [uri (URI. file)]
    (case (.getScheme uri)
      nil file
      "resource" (clojure.java.io/resource (.getSchemeSpecificPart uri))
      "s3" (s3/generate-presigned-url (.getAuthority uri)
                                      (subs (.getPath uri) 1)
                                      (time-core/plus
                                       (time-core/now)
                                       (time-core/minutes 5))))))
