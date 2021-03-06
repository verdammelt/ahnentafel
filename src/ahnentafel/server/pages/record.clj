(ns ahnentafel.server.pages.record
  (:require [net.cgrand.enlive-html :as html]
            [ahnentafel.server.pages.layout :refer [def-layout-template]]
            [ahnentafel.server.pages.util :refer :all])
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
  (if-let [family (:family-as-child record)]
    (fn [node] (html/at node
                       [:#parent-link]
                       (html/set-attr :href (xref-link {:xref family})))))

  [:#family-as-spouse]
  (if-let [families (:family-as-spouse record)]
    (html/clone-for [fams families]
                    [:#spouse-link] (html/set-attr :href (xref-link fams))
                    [:#spouse-link :#spouse-name] (html/content (:name (:spouse fams))))))

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
                  [:#person-info] (html/content (:name child))))

(def-layout-template record
  [:#content]
  (let [dispatch {:individual individual-page-snippet
                  :family family-page-snippet}
        xref (first args)
        record (query/find-record ((:get-data data)) {:xref xref})]
    (html/substitute (((:type record) dispatch) record))))
