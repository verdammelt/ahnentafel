(ns ahnentafel.system
  (:require [environ.core :refer [env]])
  (:require [ahnentafel.main]))

(defn system
  "Returns a new instance of the entire system."
  []
  {:port (:port env)
   :handler (ahnentafel.main/make-app)})

(defn start
  "Start the system up."
  [system]
  (alter-var-root #'ahnentafel.main/the-handler (constantly (:handler system)))
  system)

(defn stop
  "Shut the system down."
  [system]
  (alter-var-root #'ahnentafel.main/the-handler (constantly nil))
  system)

(defn ring-init [] (start (system)))
