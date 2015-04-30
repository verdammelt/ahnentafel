(ns ahnentafel.server.handler
  (:require [ahnentafel.server.pages :as pages])
  (:require [ahnentafel.server.middleware.logging :refer [simple-logging]])

  (:require [compojure.core :refer :all])
  (:require [ring.util.response :as response])
  (:require [ring.middleware.defaults :refer [wrap-defaults site-defaults]])
  (:require [ring.middleware.stacktrace :refer [wrap-stacktrace]]))

(defn make-routes [app-data]
  (routes
       (GET "/" [] (pages/home app-data))
       (GET "/about" [] (pages/about app-data))
       (GET "/records/:xref" [xref] (pages/record app-data xref))
       (ANY "*" request (-> (pages/not-found app-data request)
                            response/response
                            (response/status 404)
                            (response/header "Content-Type" "text/html")))))

(defn make-handler [app-data]
  (-> app-data
      make-routes
      (wrap-defaults (assoc-in site-defaults [:static :resources] "site"))
      wrap-stacktrace
      simple-logging))
