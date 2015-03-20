(ns ahnentafel.server.pages-test
  (:require [ahnentafel.server.pages :refer :all])
  (:require [clojure.test :refer :all]))

(deftest home-page-test
  (let [page (apply str (home-page {:version "x.x.x"
                                    :get-data (fn [] (list 1 2 3))
                                    :file "test.ged"}))]
    (is (.contains page "<title>Ahnentafel</title>"))
    (is (.contains page "id=\"home-contents\""))
    (is (.contains page "<span id=\"version\">x.x.x</span>"))
    (is (.contains page "3 records in test.ged."))))

(deftest page-not-found-test
  (let [page (apply str (page-not-found {:uri "/unknown" :version "x.x.x"}))]
    (is (.contains page "<title>Ahnentafel</title>"))
    (is (.contains page "/unknown not found."))
    (is (.contains page "<span id=\"version\">x.x.x</span>"))))
