(ns ahnentafel.pages.pages
  (:require [net.cgrand.enlive-html :as html]))

(html/defsnippet home-page-snippet "site/templates/home.html" [:div] [])

(html/deftemplate home-page "site/templates/index.html" [data]
  [:#content] (html/substitute (home-page-snippet))
  [:#version] (html/content (:version data)))

(html/deftemplate page-not-found "site/templates/index.html" [data]
  [:#content] (html/content (str (:uri data) " not found."))
  [:#version] (html/content (:version data)))
