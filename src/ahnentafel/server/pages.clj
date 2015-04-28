(ns ahnentafel.server.pages
  (:require [net.cgrand.enlive-html :as html])
  (:require [ahnentafel.gedcom.data :as data]))

(defn- record-link [xref] (str "/records/" xref))

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
   (html/set-attr "href"
                  (record-link (get-in header-data [:submitter :xref])))))

(defn- format-names [name & akas]
  (str name
       (if akas
         (str " (a.k.a. " (clojure.string/join ", " akas) ")"))))

(html/defsnippet record-page-snippet "site/templates/record.html" [:div#record-contents]
  [record]
  [:#names]
  (html/content (apply format-names (:name record)))

  [:#sex]
  (html/content (str "Sex: " (:sex record)))

  [:#birth]
  (html/content (str "Born: "
                     (get-in record [:birth :date]) " "
                     (get-in record [:birth :place])))

  [:#death]
  (html/content (str "Died: "
                     (get-in record [:death :date]) " "
                     (get-in record [:death :place])))

  [:#family]
  (html/html-content "<a href=\"/records/" (:family record) "\">Go To Family</a>")
)

(defmacro def-layout-template [name & forms]
  `(html/deftemplate ~name "site/templates/index.html" [~'data]
     ~@forms
     [:#version] (html/content (:version ~'data))))

(def-layout-template home-page
  [:#content] (html/substitute (home-page-snippet (data/header ((:get-data data))))))

(def-layout-template page-not-found
  [:#content] (html/content (str (:uri data) " not found.")))

(def-layout-template record-page
  [:#content] (html/substitute (record-page-snippet (data/find-record
                                                     (select-keys data [:xref])
                                                     ((:get-data data))))))
