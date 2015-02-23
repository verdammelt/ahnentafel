(ns ahnentafel.server.handler-test
  (:require [ahnentafel.server.handler :refer [make-handler]]
            [clojure.test :refer :all]
            [ring.mock.request :as mock]))

(deftest home-page-handler
  (let [handler (make-handler {:version "x.x.x"})]
   (testing "home page"
     (let [response (handler (mock/request :get "/"))]
       (is (= (:status response) 200))
       (is (.contains (:body response) "Ahnentafel"))
       (is (.contains (:body response) "home-contents"))
       (is (.contains (:body response) "x.x.x"))))

   (testing "404 response"
     (let [response (handler (mock/request :get "/unknown"))]
       (is (= (:status response) 404))
       (is (.contains (:body response) "/unknown not found."))
       (is (= (get-in response [:headers  "Content-Type"])
             "text/html")))))
