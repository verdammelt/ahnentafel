(ns ahnentafel.server.pages
  (:require [net.cgrand.enlive-html :as html])
  (:require [ahnentafel.gedcom.data :as data]))

(html/defsnippet home-page-snippet "site/templates/home.html" [:div] [header]
  [:#home-contents]
  (html/transform-content
   (html/replace-vars {:number-of-records (str (:number-of-records header))
                       :file (:file header)
                       :source-name (:source header)
                       :destination-name (:destination header)
                       :file-time (:file-time header)
                       :gedcom-version (get-in header [:gedcom :version])
                       :gedcom-type (get-in header [:gedcom :type])
                       :encoding (:encoding header)}))

  [:#home-contents :a#submitter]
  (html/do->
   (html/content (:submitter header))
   (html/set-attr "href" (:submitter header))))

(defmacro def-layout-template [name & forms]
  `(html/deftemplate ~name "site/templates/index.html" [~'data]
     ~@forms
     [:#version] (html/content (:version ~'data))))

(def-layout-template home-page
  [:#content] (html/substitute (home-page-snippet (data/header ((:get-data data))))))

(def-layout-template page-not-found
  [:#content] (html/content (str (:uri data) " not found.")))
