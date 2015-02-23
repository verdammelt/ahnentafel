(ns ahnentafel.system
  (:require [ahnentafel.server.handler :refer [make-handler]])
  (:require [environ.core :refer [env]]))

(defn system []
  (let [app-data {:version (env :ahnentafel-version)}
        handler (make-handler app-data)]
    {:app-data app-data :handler handler}))

(defn start [system] system)
(defn stop [system] system)

(def ring-handler nil)
(defn ring-init []
  (alter-var-root #'ring-handler (fn [_] (-> (system) start :handler))))