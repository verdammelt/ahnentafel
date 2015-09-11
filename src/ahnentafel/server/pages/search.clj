(ns ahnentafel.server.pages.search
  (:require [ahnentafel.gedcom.query :as query]
            [ahnentafel.server.pages.layout :refer [def-layout-template]]
            [net.cgrand.enlive-html :as html]))

(html/defsnippet search-snippet
  "site/templates/search.html"
  [:div#results]
  [query results]

  [:span#query] (html/content (str "\"" query "\"")))

(def-layout-template search
  [:#content]
  (let [query (first args)
        results (query/search ((:get-data data)) query)]
    (html/substitute (search-snippet query results))))
