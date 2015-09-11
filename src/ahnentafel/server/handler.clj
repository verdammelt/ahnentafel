(ns ahnentafel.server.handler
  (:require [ahnentafel.server.pages.contact :refer [contact]])
  (:require [ahnentafel.server.pages.home :refer [home]])
  (:require [ahnentafel.server.pages.search :refer [search]])
  (:require [ahnentafel.server.pages.record :refer [record]])
  (:require [ahnentafel.server.pages.not-found :refer [not-found]])
  (:require [ahnentafel.server.pages.about :refer [about]])
  (:require [ahnentafel.server.middleware.logging :refer [simple-logging]])

  (:require [compojure.core :refer :all])
  (:require [ring.util.response :as response])
  (:require [ring.middleware.defaults :refer [wrap-defaults site-defaults]])
  (:require [ring.middleware.stacktrace :refer [wrap-stacktrace]]))

(defn make-routes [app-data]
  (routes
       (GET "/" [] (home app-data))
       (GET "/about" [] (about app-data))
       (GET "/contact" [] (contact app-data))
       (GET "/records/:xref" [xref] (record app-data xref))
       (GET "/search" {params :query-params} (search app-data (get params "name")))
       (ANY "*" request (-> (not-found app-data request)
                            response/response
                            (response/status 404)
                            (response/header "Content-Type" "text/html")))))

(defn make-handler [app-data]
  (-> app-data
      make-routes
      (wrap-defaults (assoc-in site-defaults [:static :resources] "site"))
      wrap-stacktrace
      simple-logging))
