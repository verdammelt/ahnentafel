(ns ahnentafel.server.handler
  (:require [ahnentafel.server.pages :as pages])
  (:require [ahnentafel.server.middleware.logging :refer [simple-logging]])

  (:require [compojure.core :refer :all])
  (:require [ring.util.response :as response])
  (:require [ring.middleware.defaults :refer [wrap-defaults site-defaults]])
  (:require [ring.middleware.stacktrace :refer [wrap-stacktrace]]))

(defn make-handler [app-data]
  (-> (routes
       (GET "/" [] (pages/home-page app-data))
       (GET "/records/:xref" [xref] (pages/record-page (merge app-data {:xref xref})))
       (ANY "*" request (-> (pages/page-not-found (merge request app-data))
                            response/response
                            (response/status 404)
                            (response/header "Content-Type" "text/html"))))
      (wrap-defaults (assoc-in site-defaults [:static :resources] "site"))
      wrap-stacktrace
      simple-logging))
