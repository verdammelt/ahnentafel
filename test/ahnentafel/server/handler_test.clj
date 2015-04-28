(ns ahnentafel.server.handler-test
  (:require [ahnentafel.server.handler :refer [make-handler]]
            [clojure.test :refer :all]
            [ring.mock.request :as mock])
  (:require [ahnentafel.server.pages :as pages]))

(deftest home-page-handler
  (let [app-data {:version "x.x.x"}
        handler (make-handler app-data)]
   (testing "home page"
     (with-redefs [pages/home-page (fn [data] (str "homepage: " data))]
       (let [response (handler (mock/request :get "/"))]
         (is (= (:status response) 200))
         (is (= (:body response)
                (str "homepage: " app-data))))))

   (testing "404 response"
     (let [response (handler (mock/request :get "/unknown"))]
       (is (= (:status response) 404))
       (is (.contains (:body response) "/unknown not found."))
       (is (= (get-in response [:headers  "Content-Type"])
             "text/html"))))))
