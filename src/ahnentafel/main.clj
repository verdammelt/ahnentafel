(ns ahnentafel.main
  (:require [ahnentafel.pages.pages :as pages])
  (:require [compojure.core :refer :all])
  (:require [ring.util.response :as response])
  (:require [ring.middleware.defaults :refer [wrap-defaults site-defaults]]))

(def app-context {:version (System/getProperty "ahnentafel.version")})

(defroutes main-handler
  (GET "/" [] (pages/home-page app-context))
  (ANY "*" request (-> (pages/page-not-found (merge request app-context))
                       response/response
                       (response/status 404)
                       (response/header "Content-Type" "text/html"))))

(def app
  (wrap-defaults main-handler
                 (assoc-in site-defaults [:static :resources] "site")))
