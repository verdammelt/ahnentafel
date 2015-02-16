(ns ahnentafel.main
  (:require [compojure.core :refer :all])
  (:require [ring.util.response :as response])
  (:require [ring.middleware.defaults :refer [wrap-defaults site-defaults]])
  (:require [net.cgrand.enlive-html :as html]))

;; move these views into a new mainspace (to encapsulate enlive)
(html/defsnippet home-page "site/templates/home.html" [:div] [])
(html/deftemplate main-template "site/templates/index.html" [contents data]
  [:#content] (html/substitute (contents))
  [:#version] (html/content (:version data)))

(def app-context {:version (System/getProperty "ahnentafel.version")})

(defroutes main-handler
  (GET "/" [] (main-template home-page app-context))
  (ANY "*" request (->  (str (:uri request) " not found.")
                        response/response
                        (response/status 404)
                        (response/header "Content-Type" "text/html"))))

(def app
  (wrap-defaults main-handler
                 (assoc-in site-defaults [:static :resources] "site")))
