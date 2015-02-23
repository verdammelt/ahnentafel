(ns ahnentafel.main
  (:require [ahnentafel.server.handler :refer [make-handler]])
  (:require [environ.core :refer [env]]))

(def the-handler nil)
