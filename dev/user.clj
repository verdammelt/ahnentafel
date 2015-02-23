(ns user
  (:require [clojure.java.io :as io]
            [clojure.string :as str]
            [clojure.pprint :refer (pprint)]
            [clojure.repl :refer :all]
            [clojure.test :as test]
            [clojure.tools.namespace.repl :refer (refresh refresh-all)]
            [ring.adapter.jetty :refer (run-jetty)])
  (:require [ahnentafel.main :refer (app)]))

(alter-var-root #'*out* (constantly *out*))

(defn all-tests
  "Utility function for running all tests in the project namespace."
  ([] (all-tests ""))
  ([sub-ns] (clojure.test/run-all-tests (re-pattern (str "ahnentafel" sub-ns ".*")))))

(def system
  "Var to hold instance of the system"
  nil)

(defn- stop-server [system]
  (when-let [server (:server system)]
    (.stop server)
    (dissoc system :server)))

(defn- start-server [system port]
  (let [server (run-jetty (:handler system) {:port port :join? false})]
    (assoc system :server server)))

(defn init []
  "Construct the current environment"
  (alter-var-root #'system (constantly (system/system))))

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
