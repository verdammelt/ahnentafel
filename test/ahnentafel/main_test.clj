(ns ahnentafel.main-test
  (:require [ahnentafel.main :refer :all]
            [clojure.test :refer :all]
            [ring.mock.request :as mock]))

(deftest home-page-handler
  (testing "static file redirect"
    (let [response (app (mock/request :get "/"))]
      (is (= (:status response) 200))
      (is (.contains (:body response) "Ahnentafel"))
      (is (.contains (:body response) (System/getProperty "ahnentafel.version")))))

  (testing "404 response"
    (let [response (app (mock/request :get "/unknown"))]
      (is (= (:status response) 404))
      (is (= (:body response) "/unknown not found."))
      (is (= (get-in response [:headers  "Content-Type"]) "text/html")))))
