(ns ahnentafel.server.pages
  (:require [net.cgrand.enlive-html :as html])
  (:require [ahnentafel.gedcom.data :as data]))

(defn- record-link [xref] (str "/records/" xref))
(defn- full-record-link [xref text]
  (html/html-snippet "<a href=\"" (record-link xref) "\">" text "</a>"))

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

(defn- format-names [name & akas]
  (str name
       (if akas
         (str " (a.k.a. " (clojure.string/join ", " akas) ")"))))

(defn- maybe-content
  "If (get RECORD KEY) is non-nil then replace content of node with (F (get RECORD KEY))."
  ([record key] (maybe-content record key identity))

  ([record key f]
   (if-let [value (get record key)]
     (html/content (f value))
     identity)))

(defn- event-info [label event]
  (clojure.string/join " "
   (remove nil? [label (:date event) (:place event)])))

(html/defsnippet record-page-snippet "site/templates/record.html" [:div#record-contents]
  [record]
  [:#type]
  (html/content (clojure.string/upper-case (name (:type record))))

  [:#names]
  (maybe-content record :name #(apply format-names %))

  [:#sex]
  (maybe-content record :sex #(str "Sex: " %))

  [:#birth]
  (maybe-content record :birth #(event-info "Born:" %))

  [:#death]
  (maybe-content record :death #(event-info "Died:" %))

  [:#burial]
  (maybe-content record :burial #(event-info "Buried:" %))

  [:#family-as-child]
  (maybe-content record :family-as-child
                 #(full-record-link % "Go To Family (where this person was a child)"))

  [:#family-as-spouse]
  (maybe-content record :family-as-spouse
                 #(full-record-link % "Go To Family (where this person was a spouse)"))

)

(defmacro def-layout-template [name & forms]
  `(html/deftemplate ~name "site/templates/index.html" [~'data]
     ~@forms
     [:#version] (html/content (:version ~'data))))

(def-layout-template home-page
  [:#content] (html/substitute (home-page-snippet
                                (assoc (data/header ((:get-data data)))
                                       :filename (:file data)) )))

(def-layout-template page-not-found
  [:#content] (html/content (str (:uri data) " not found.")))

(def-layout-template record-page
  [:#content] (html/substitute (record-page-snippet (data/find-record
                                                     ((:get-data data))
                                                     (select-keys data [:xref])))))

(def-layout-template about-page
  [:#content] (html/content (html/html-resource "site/templates/about.html")))
