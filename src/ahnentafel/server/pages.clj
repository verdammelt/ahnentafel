(ns ahnentafel.server.pages
  (:require [net.cgrand.enlive-html :as html])
  (:require [ahnentafel.gedcom.data :as data]))

(html/defsnippet home-page-snippet "site/templates/home.html" [:div] [data]
  (let [tree ((:get-data data))
        header (data/header tree)]
    [:#home-contents]
    (html/transform-content
     (html/replace-vars {:number-of-records (str (:number-of-records header))
                         :file (:file header)
                         :source-name (:source header)
                         :destination-name (:destination header)
                         :file-time (:file-time header)
                         :gedcom-version (get-in header [:gedcom :version])
                         :gedcom-type (get-in header [:gedcom :type])
                         :encoding (:encoding header)
                         :submitter (:submitter header)}))

    ))


(html/deftemplate home-page "site/templates/index.html" [data]
  [:#content] (html/substitute (home-page-snippet data))
  [:#version] (html/content (:version data)))

(html/deftemplate page-not-found "site/templates/index.html" [data]
  [:#content] (html/content (str (:uri data) " not found."))
  [:#version] (html/content (:version data)))
