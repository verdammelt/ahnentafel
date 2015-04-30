(ns ahnentafel.server.pages.home
  (:require [net.cgrand.enlive-html :as html])
  (:require [ahnentafel.server.pages.layout :refer [def-layout-template]])
  (:require [ahnentafel.server.pages.util :refer [full-record-link]])
  (:require [ahnentafel.gedcom.query :as query]))

(defn- maybe-substitute
  [m k f]
  (if-let [value (get m k)]
    (html/substitute (f value))
    identity))

(html/defsnippet home-page-snippet "site/templates/home.html" [:div#home-contents]
  [header-data]
  [:#home-contents]
  (letfn [(ensure-strings [m]
            (zipmap (keys m) (map str (vals m))))]
    (html/transform-content
     (html/replace-vars
      (ensure-strings {:number-of-records (:number-of-records header-data)
                       :file (or (:file header-data)
                                 (:filename header-data))
                       :source-name (:source header-data)
                       :gedcom-version (get-in header-data [:gedcom :version])
                       :gedcom-type (get-in header-data [:gedcom :type])
                       :encoding (:encoding header-data)}))))

  [:#destination]
  (maybe-substitute header-data :destination #(str "for " %))

  [:#file-time]
  (maybe-substitute header-data :file-time #(str "on " %))

  [:#submitter]
  (html/substitute
   (if-let [submitter (:submitter header-data)]
     (conj (full-record-link (:xref submitter) (:name submitter))
           (html/html-snippet "Submitted by "))
     "")))

(def-layout-template home
  [:#content] (html/substitute (home-page-snippet
                                (assoc (query/header ((:get-data data)))
                                       :filename (:gedcom-file data)) )))
