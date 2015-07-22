(defproject ahnentafel "0.1.0-SNAPSHOT"
  :description "Application to allow browsing of genealogical data."
  :url "http://ahnentafel.herokuapps.com"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [compojure "1.3.4"]
                 [ring "1.3.2"]
                 [ring/ring-defaults "0.1.5"]
                 [environ "1.0.0"]
                 [enlive "1.1.5"]
                 [clj-time "0.9.0"]
                 [amazonica "0.3.24"]]

  :plugins [[lein-ring "0.9.4"]]

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
    :plugins [[lein-ancient "0.6.7"]
              [lein-bikeshed "0.2.0"]
              [lein-kibit "0.1.2"]]
    :dependencies [[ring/ring-mock "0.2.0"]
                   [org.clojure/tools.namespace "0.2.10"]
                   [org.clojure/java.classpath "0.2.2"]
                   [ring/ring-jetty-adapter "1.3.2"]]
    :repl-options {:init (load "reloaded")}
    :aliases {"check-update" ^{:doc "Check for upgrades to dependencies and plugins."}
              ["ancient" ":all"]
              "lint" ^{:doc "Check code for linting and style errors."}
              ["do" ["bikeshed" "-v"] ["kibit"]]
              "package" ^{:doc "Make a stand alone package."}
              ["ring" "uberjar"]
              "build" ^{:doc "Full clean build with tests and linting."}
              ["do" "clean" ["test"] ["lint"]]}}})
