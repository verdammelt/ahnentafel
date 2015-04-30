(ns ahnentafel.server.handler-test
  (:require [ahnentafel.server.handler :refer [make-handler]]
            [clojure.test :refer :all]
            [ring.mock.request :as mock])
  (:require [ahnentafel.server.pages.home :refer [home]]
            [ahnentafel.server.pages.record :refer [record]]
            [ahnentafel.gedcom.query :as query]))

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
       (is (.contains (:body response) "/unknown not found."))
       (is (= (get-in response [:headers  "Content-Type"]) "text/html"))))

   (testing "record page"
     (with-redefs [record (fn [data & xref]
                                  (str "record page: " data " " xref))]
       (let [response (get-page "/records/@I23@")]
         (is (= (:status response) 200))
         (is  (= (:body response)
                 (str "record page: " app-data " " '("@I23@")))))))))
