(ns ahnentafel.system
  (:require [environ.core :refer [env]])
  (:require [ahnentafel.server.handler :refer [make-handler]]))

(defn system
  "Returns a new instance of the entire system."
  []
  (let [app-data {:port (:port env)
                  :version (:version env)}
        handler (make-handler app-data)]
    (merge {:handler handler} app-data)))

(def the-handler nil)

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
