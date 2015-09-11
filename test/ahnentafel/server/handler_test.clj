(ns ahnentafel.server.handler-test
  (:require [ahnentafel.server.handler :refer [make-handler]]
            [ahnentafel.server.pages
             [home :refer [home]]
             [record :refer [record]]
             [search :refer [search]]]
            [clojure.test :refer :all]
            [ring.mock.request :as mock]))

(deftest handlers
  (let [app-data {:version "x.x.x"}
        handler (make-handler app-data)
        get-page (fn [url] (handler (mock/request :get url)))]

   (testing "home page"
     (with-redefs [home (fn [data] (str "homepage: " data))]
       (let [response (get-page "/")]
         (is (= (:status response) 200))
         (is (= (:body response)
                (str "homepage: " app-data))))))

   (testing "404 response"
     (let [response (get-page "/unknown")]
       (is (= (:status response) 404))
       (is (.contains (:body response) "/unknown not found."))))

   (testing "record page"
     (with-redefs [record (fn [data & xref]
                                  (str "record page: " data " " xref))]
       (let [response (get-page "/records/@I23@")]
         (is (= (:status response) 200))
         (is  (= (:body response)
                 (str "record page: " app-data " " '("@I23@")))))))

   (testing "search page"
     (with-redefs [search (fn [data & query]
                            (str "search page: " data " " query))]
       (let [response (get-page "/search?name=Smith")]
         (is (= (:status response) 200))
         (is (= (:body response)
                (str "search page: " app-data " " '("Smith")))))))))
