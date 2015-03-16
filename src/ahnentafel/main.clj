(ns ahnentafel.main
  (:require [ahnentafel.pages.pages :as pages])
  (:require [ahnentafel.middleware.logging :refer [simple-logging]])

  (:require [environ.core :refer [env]])
  (:require [compojure.core :refer :all])
  (:require [ring.util.response :as response])
  (:require [ring.middleware.defaults :refer [wrap-defaults site-defaults]]))

(def app-context {:version (env :ahnentafel-version)})

(defroutes main-handler
  (GET "/" [] (pages/home-page app-context))
  (ANY "*" request (-> (pages/page-not-found (merge request app-context))
                       response/response
                       (response/status 404)
                       (response/header "Content-Type" "text/html"))))

(defn make-app []
  (simple-logging
   (wrap-defaults main-handler
                  (assoc-in site-defaults [:static :resources] "site"))))

(def the-handler nil)
