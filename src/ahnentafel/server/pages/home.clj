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

(defn- link-or-blank [record label]
  (html/substitute
   (if-let [{:keys [xref name]} record]
     (conj (full-record-link xref name)
           (html/html-snippet label))
     "")))

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
  (link-or-blank (:submitter header-data) "Submitted by ")

  [:#start-record]
  (link-or-blank (:start-record header-data) "Start by looking at "))

(def-layout-template home
  [:#content] (html/substitute (home-page-snippet
                                (assoc (query/header ((:get-data data))
                                                     (:start-record data))
                                       :filename (:gedcom-file data)) )))
