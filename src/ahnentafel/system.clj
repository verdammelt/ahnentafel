(ns ahnentafel.system
  (:require [ahnentafel.server.handler :refer [make-handler]])
  (:require [ahnentafel.gedcom.reader :refer [read-file]])

  (:require [environ.core :refer [env]])
  (:require [clojure.java.io :refer [resource]]))

(defn system []
  (let [file (:gedcom-file env "resource:sample.ged")
        data (future (read-file file))
        app-data {:version (:ahnentafel-version env)
                  :analytics-id (:analytics-id env)
                  :email (:maintainer-email env "noreply@example.com")
                  :port (:port env)
                  :gedcom-file file
                  :get-data (fn [] @data)
                  :start-record (:start-record env)}
        handler (make-handler app-data)]
    (merge {:handler handler} app-data)))

(defonce the-handler nil)

(defn start
  "Start the system up."
  [system]
  (alter-var-root #'the-handler (constantly (:handler system)))
  system)

(defn stop
  "Shut the system down."
  [system]
  (alter-var-root #'the-handler (constantly nil))
  system)

(defn ring-init [] (start (system)))
