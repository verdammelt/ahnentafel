(ns ahnentafel.main-test
  (:require [ahnentafel.main :refer :all]
            [clojure.test :refer :all]
            [ring.mock.request :as mock]))

(deftest home-page-handler
  (testing "redirect to index"
    (let [response (app (mock/request :get "/"))]
      (is (= (:status response) 302))
      (is (= (get-in response [:headers "Location"]) "http://localhost/index.html"))))

  (testing "static file redirect"
    (let [response (app (mock/request :get "/index.html"))]
      (is (= (:status response) 200))
      (is (= (.getName (:body response)) "index.html"))
      (is (re-matches #".*/site/index.html$" (.getPath (:body response))))))

  (testing "404 response"
    (let [response (app (mock/request :get "/unknown"))]
      (is (= (:status response) 404))
      (is (= (:body response) "/unknown not found."))
      (is (= (get-in response [:headers  "Content-Type"]) "text/html")))))
