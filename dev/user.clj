(ns user
  (:require [clojure.java.io :as io]
            [clojure.string :as str]
            [clojure.pprint :refer (pprint)]
            [clojure.repl :refer :all]
            [clojure.test :as test]
            [clojure.tools.namespace.repl :refer (refresh refresh-all)]
            [clojure.tools.namespace.find :refer (find-namespaces-in-dir)]
            [ring.adapter.jetty :refer (run-jetty)]
            [environ.core :refer (env)]))

(alter-var-root #'*out* (constantly *out*))
(alter-var-root #'*err* (constantly *err*))

(defn all-tests
  "Utility function for running all tests in the project namespace."
  ([] (all-tests ""))
  ([sub-ns]
   (clojure.test/run-all-tests
    (re-pattern (str "ahnentafel" sub-ns ".*")))))

(defn- reload-pages []
  (map #(require (symbol %) :reload)
       (find-namespaces-in-dir
        (clojure.java.io/as-file "src/ahnentafel/server/pages"))))

(defn load-and-go []
  (load "reloaded")
  (reload-pages)
  ((ns-resolve *ns* 'go)))
