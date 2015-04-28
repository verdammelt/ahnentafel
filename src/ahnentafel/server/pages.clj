(ns ahnentafel.server.pages
  (:require [net.cgrand.enlive-html :as html])
  (:require [ahnentafel.gedcom.data :as data]))

(html/defsnippet home-page-snippet "site/templates/home.html" [:div]
  [header-data]
  [:#home-contents]
  (html/transform-content
   (html/replace-vars {:number-of-records (str (:number-of-records header-data))
                       :file (:file header-data)
                       :source-name (:source header-data)
                       :destination-name (:destination header-data)
                       :file-time (:file-time header-data)
                       :gedcom-version (get-in header-data [:gedcom :version])
                       :gedcom-type (get-in header-data [:gedcom :type])
                       :encoding (:encoding header-data)}))

  [:#home-contents :a#submitter]
  (html/do->
   (html/content (get-in header-data [:submitter :name]))
   (html/set-attr "href" (get-in header-data [:submitter :link]))))

(defmacro def-layout-template [name & forms]
  `(html/deftemplate ~name "site/templates/index.html" [~'data]
     ~@forms
     [:#version] (html/content (:version ~'data))))

(def-layout-template home-page
  [:#content] (html/substitute (home-page-snippet (data/header ((:get-data data))))))

(def-layout-template page-not-found
  [:#content] (html/content (str (:uri data) " not found.")))

(def-layout-template record-page
  [:#content] (html/content "This space left unintentionally blank"))
