(ns ahnentafel.system
  (:require [ahnentafel.server.handler :refer [make-handler]])
  (:require [ahnentafel.gedcom.data :refer [load-data]])

  (:require [environ.core :refer [env]])
  (:require [clojure.java.io :refer [resource]]))

(defn system []
  (let [file (resource "sample.ged")
        app-data {:version (env :ahnentafel-version)
                  :file file
                  :family-tree (load-data file)}
        handler (make-handler app-data)]
    {:app-data app-data
     :handler handler}))

(defn start [system] system)
(defn stop [system] system)

(def ring-handler nil)
(defn ring-init []
  (alter-var-root #'ring-handler (fn [_] (-> (system) start :handler))))
