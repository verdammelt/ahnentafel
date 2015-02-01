(ns ahnentafel.main-test
  (:require [ahnentafel.main :refer :all]
            [clojure.test :refer :all]
            [ring.mock.request :as mock]))

(deftest home-page-handler
  (is (= (app (mock/request :get "/"))
         {:status 200
          :headers {"Content-Type" "text/html"}
          :body "Hello World."})))
