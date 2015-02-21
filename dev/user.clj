(ns user
  (:require [clojure.java.io :as io]
            [clojure.string :as str]
            [clojure.pprint :refer (pprint)]
            [clojure.repl :refer :all]
            [clojure.test :as test]
            [clojure.tools.namespace.repl :refer (refresh refresh-all)]
            [ring.adapter.jetty :refer (run-jetty)])
  (:require [ahnentafel.main :refer (app)]))

(def system nil)

(defn all-tests [] (clojure.test/run-all-tests #"ahnentafel.*"))

(defn stop []
  "Shut down the system, if running."
  (println "Shutting down")
  (alter-var-root #'system
                  (fn [s] (when s (.stop s)))))

(defn start
  "Start up the system."
  ([] (start 3000))
  ([port]
   (println "Starting up on port" port)
   (alter-var-root #'system
                   (constantly
                    (run-jetty app {:port port :join? false})))))

(defn reset []
  "Stop the running system (if any), refresh namespaces and start the
system up."
  (stop)
  (refresh :after 'user/start))
