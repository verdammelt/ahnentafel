(ns ahnentafel.server.pages.record
  (:require [net.cgrand.enlive-html :as html]
            [ahnentafel.server.pages.layout :refer [def-layout-template]]
            [ahnentafel.server.pages.util :refer [full-record-link]])
  (:require [ahnentafel.gedcom.query :as query]))


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

(defn- format-names [name & akas]
  (str name
       (if akas
         (str " (a.k.a. " (clojure.string/join ", " akas) ")"))))

(defn- xref-link [r] (str "/records/" (:xref r)))

(html/defsnippet individual-page-snippet "site/templates/record.html" [:div#individual-record]
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
                 #(full-record-link % "Go To Family (where this person was a spouse)")))

(html/defsnippet family-page-snippet "site/templates/record.html" [:div#family-record]
  [record]

  [:#type]
  (html/content (clojure.string/upper-case (name (:type record))))

  [:#spouse]
  (html/clone-for [spouse (:spouses record)]
                  [:#person-info] (html/set-attr :href (xref-link spouse))
                  [:#person-info] (html/content (:name spouse)))

  [:#married :#event-info]
  (html/content (let [marriage (:marriage record)]
                  (str (:date marriage) " " (:place marriage))))

  [:#child]
  (html/clone-for [child (:children record)]
                  [:#person-info] (html/set-attr :href (xref-link child))
                  [:#person-info] (html/content (:name child)))
  )

(def-layout-template record
  [:#content]
  (let [dispatch {:individual individual-page-snippet
                  :family family-page-snippet}
        xref (first args)
        record (query/find-record ((:get-data data)) {:xref xref})]
    (html/substitute (((:type record) dispatch) record))))
