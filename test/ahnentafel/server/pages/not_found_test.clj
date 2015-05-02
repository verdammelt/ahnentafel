(ns ahnentafel.server.pages.not-found-test
  (:require [ahnentafel.server.pages.not-found :refer :all])

  (:require [ahnentafel.server.pages.test-utils :refer :all])
  (:require [clojure.test :refer :all]))

(deftest page-not-found-test
  (let [page (get-page not-found {:uri "/unknown"})]
    (are-on-page
     "<title>Ahnentafel</title>"
     "/unknown not found."
     "<span id=\"version\">x.x.x</span>")))
