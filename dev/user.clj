(ns user
  (:require [clojure.java.io :as io]
            [clojure.string :as str]
            [clojure.pprint :refer (pprint)]
            [clojure.repl :refer :all]
            [clojure.test :as test]
            [clojure.tools.namespace.repl :refer (refresh refresh-all)]
            [ring.adapter.jetty :refer (run-jetty)])
  (:require [ahnentafel.system :as system]))

(alter-var-root #'*out* (constantly *out*))

(defn all-tests []
  "Utility function for running all tests in the project namespace."
  (clojure.test/run-all-tests #"ahnentafel.*"))

(def system
  "Var to hold instance of the system"
  nil)

(defn- stop-server [system]
  (when-let [server (:server system)] (.stop server)))

(defn- start-server [system port]
  (let [server (run-jetty (:handler system) {:port port :join? false})]
    (assoc system :server server)))

(defn stop []
  "Shut down the system, if running."
  (println "Shutting down")
  (alter-var-root #'system (fn [s] (when s (stop-server s) nil))))

(defn start
  "Start up the system."
  ([] (start 3000))
  ([port]
   (println "Starting up on port" port)
   (alter-var-root #'system (fn [_] (start-server (system/system) port)))))

(defn reset []
  "Stop the running system (if any), refresh namespaces and start the
system up."
  (stop)
  (refresh :after 'user/start))
