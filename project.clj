(defproject ahnentafel "0.1.0-SNAPSHOT"
  :description "Appication to allow browsing of genealogical data."
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [ring/ring-core "1.3.2"]
                 [ring/ring-jetty-adapter "1.3.2"]]

  :plugins [[lein-ring "0.9.1"]]

  :aot [ahnentafel.gedcom.ParseError]
  :ring {:handler ahnentafel.main/app}

  :profiles
  {:dev
   {:resource-paths ["test-resources"]
    :dependencies [[lein-ancient "0.6.0"]
                   [lein-bikeshed "0.2.0"]
                   [lein-kibit "0.0.8"]]
    :aliases {"check-update" ^{:doc "Check for upgrades to dependencies and plugins."}
              ["ancient" ":all"]
              "lint" ^{:doc "Check code for linting and style errors."}
              ["do" ["bikeshed" "-v"] ["kibit"]]
              "package" ^{:doc "Make a stand alone package."}
              ["ring" "uberjar"]
              "build" ^{:doc "Full clean build with tests and linting."}
              ["do" "clean" ["test"] ["lint"]]}}})
