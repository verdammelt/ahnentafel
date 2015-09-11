(ns ahnentafel.server.pages.search
  (:require [ahnentafel.gedcom.query :as query]
            [ahnentafel.server.pages.layout :refer [def-layout-template]]
            [ahnentafel.server.pages.util :refer :all]
            [net.cgrand.enlive-html :as html]))

(html/defsnippet search-snippet
  "site/templates/search.html"
  [:div#results]
  [query results]

  [:span#description]
  (html/content
   (clojure.pprint/cl-format nil "~[No~:;~:*~D~] record~:P found for \"~A\""
                             (count results) query))

  [:ul#result-list :li]
  (html/clone-for [result results]
                  [:#record-link]
                  (html/do->
                   (html/set-attr :href (xref-link result))
                   (html/content (apply format-names (:name result))))
                  [:#birth]
                  (html/substitute (:date (:birth result)))
                  [:#death]
                  (html/substitute (:date (:death result)))))

(def-layout-template search
  [:#content]
  (let [query (first args)
        results (query/search ((:get-data data)) query)]
    (html/substitute (search-snippet query results))))
