(ns ahnentafel.server.pages
  (:require [net.cgrand.enlive-html :as html]))

(html/defsnippet home-page-snippet "site/templates/home.html" [:div] [data]
  (let [file (:file data)
        num-records (count ((:get-data data)))]
    [:#home-contents]
    (html/transform-content
     (html/replace-vars {:num-records (str num-records) :file (str file)}))))


(html/deftemplate home-page "site/templates/index.html" [data]
  [:#content] (html/substitute (home-page-snippet data))
  [:#version] (html/content (:version data)))

(html/deftemplate page-not-found "site/templates/index.html" [data]
  [:#content] (html/content (str (:uri data) " not found."))
  [:#version] (html/content (:version data)))
