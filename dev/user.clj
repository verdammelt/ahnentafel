(ns user
  (:require [clojure.java.io :as io]
            [clojure.string :as str]
            [clojure.pprint :refer (pprint)]
            [clojure.repl :refer :all]
            [clojure.test :as test]
            [clojure.tools.namespace.repl :refer (refresh refresh-all)]
            [ring.adapter.jetty :refer (run-jetty)]
            [environ.core :refer (env)]))

(alter-var-root #'*out* (constantly *out*))

(defn all-tests
  "Utility function for running all tests in the project namespace."
  ([] (all-tests ""))
  ([sub-ns]
   (clojure.test/run-all-tests
    (re-pattern (str "ahnentafel" sub-ns ".*")))))

(defn load-and-go []
  (load "reloaded")
  ((ns-resolve *ns* 'go)))
