(ns ahnentafel.main
  (:require [ahnentafel.pages.pages :as pages])
  (:require [ahnentafel.middleware.logging :refer [simple-logging]])

  (:require [environ.core :refer [env]])
  (:require [compojure.core :refer :all])
  (:require [ring.util.response :as response])
  (:require [ring.middleware.defaults :refer [wrap-defaults site-defaults]]))

(defn make-handler [app-data]
  (simple-logging
   (wrap-defaults
    (routes
     (GET "/" [] (pages/home-page app-data))
     (ANY "*" request (-> (pages/page-not-found (merge request app-data))
                          response/response
                          (response/status 404)
                          (response/header "Content-Type" "text/html"))))
    (assoc-in site-defaults [:static :resources] "site"))))

(defn system []
  (let [app-data {:version (env :ahnentafel-version)}
        handler (make-handler app-data)]
    {:app-data app-data :handler handler}))

(def ring-handler nil)
(defn ring-init []
  (alter-var-root #'ring-handler (fn [_] (:handler (system)))))
