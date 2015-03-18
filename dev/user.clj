(ns user
  (:require [clojure.java.io :as io]
            [clojure.string :as str]
            [clojure.pprint :refer (pprint)]
            [clojure.repl :refer :all]
            [clojure.test :as test]
            [clojure.tools.namespace.repl :refer (refresh refresh-all)]
            [ring.adapter.jetty :refer (run-jetty)]
            [environ.core :refer (env)])
  (:require [ahnentafel.system :as system]))

(alter-var-root #'*out* (constantly *out*))

(defn all-tests
  "Utility function for running all tests in the project namespace."
  ([] (all-tests ""))
  ([sub-ns]
   (clojure.test/run-all-tests
    (re-pattern (str "ahnentafel" sub-ns ".*")))))

(defn- set-port [port]
  (alter-var-root #'env assoc :port port))

(def system "The System." nil)

(defn init
  "Constructs the current development system."
  []
  (when-not (:port env) (set-port 3000))
  (alter-var-root #'system
    (constantly (system/system))))

(defn start
  "Starts the current development system."
  []
  (letfn [(start-jetty [s]
            (let [{:keys [handler port]} s]
              (assoc s :server
                     (run-jetty handler
                                {:port port :join? false}))))]
    (alter-var-root #'system (fn [s] (-> s system/start start-jetty)))))

(defn stop
  "Shuts down and destroys the current development system."
  []
  (letfn [(stop-jetty [s]
            (if-let [server (:server s)]
              (do (.stop server) (dissoc s :server))
              s))]
   (alter-var-root #'system (fn [s] (-> s stop-jetty system/stop)))))

(defn go
  "Initializes the current development system and starts it running."
  []
  (init)
  (start))

(defn restart []
  (stop)
  (refresh :after 'user/go))
