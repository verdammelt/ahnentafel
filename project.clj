(defproject ahnentafel "0.1.0-SNAPSHOT"
  :description "Application to allow browsing of genealogical data."
  :url "http://ahnentafel.herokuapps.com"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [compojure "1.4.0"]
                 [ring "1.4.0"]
                 [ring/ring-defaults "0.1.5"]
                 [environ "1.0.1"]
                 [enlive "1.1.6"]
                 [clj-time "0.11.0"]
                 [amazonica "0.3.33"]]

  :plugins [[lein-ring "0.9.6"]]

  :aot [ahnentafel.gedcom.reading.ParseError]

  :ring {:init ahnentafel.system/ring-init
         :handler ahnentafel.system/the-handler}

  :resource-paths ["resources" "resources/data"]

  :profiles
  {:production
   {:prep-tasks [["compile" "ahnentafel.gedcom.reading.ParseError"]]}

   :dev
   {:source-paths ["dev"]
    :resource-paths ["test-resources"]
    :plugins [[lein-ancient "0.6.7"]]
    :dependencies [[ring/ring-mock "0.3.0"]
                   [org.clojure/tools.namespace "0.2.10"]
                   [org.clojure/java.classpath "0.2.2"]
                   [ring/ring-jetty-adapter "1.4.0-beta1"]]
    :repl-options {:init (load "reloaded")}
    :aliases {"check-update" ^{:doc "Check for upgrades to dependencies and plugins."}
              ["ancient" ":all"]
              "package" ^{:doc "Make a stand alone package."}
              ["ring" "uberjar"]
              "build" ^{:doc "Full clean build with tests and linting."}
              ["do" "clean" ["test"]]}}})
