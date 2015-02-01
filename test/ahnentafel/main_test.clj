(ns ahnentafel.main-test
  (:require [ahnentafel.main :refer :all]
            [clojure.test :refer :all]
            [ring.mock.request :as mock]))

(deftest home-page-handler
  (is (= (app (mock/request :get "/"))
         {:status 302
          :headers {"Location" "index.html"}
          :body ""}))
  (is (= (app (mock/request :get "/unknown"))
         {:status 404
          :headers {"Content-Type" "text/html"}
          :body "/unknown not found."}
         ))
  )
