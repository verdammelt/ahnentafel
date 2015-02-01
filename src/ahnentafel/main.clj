(ns ahnentafel.main
  (:require [compojure.core :refer :all])
  (:require [ring.util.response :as response]))

(defroutes main-handler
  (GET "/" [] (response/redirect "index.html"))
  (ANY "*" request (->  (str (:uri request) " not found.")
                        response/response
                        (response/status 404)
                        (response/header "Content-Type" "text/html"))))

(def app main-handler)
