(ns ahnentafel.server.pages.layout
  (:require [net.cgrand.enlive-html :as html]))


(html/defsnippet analytics-snippet
  "site/templates/analytics.html" [:script#analytics]
  [analytics-id]
  (html/transform-content
   (html/replace-vars {:analytics-id (or analytics-id "UA-XXXXXX")})))


(defmacro def-layout-template [name & forms]
  "Anaphoric macro for defining templates in the 'standard' layout.
Defines two parmaters: DATA (the data passed down from the handler) and
ARGS (rest argument)."
  `(html/deftemplate ~name "site/templates/index.html" [~'data & ~'args]
     ~@forms
     [:#version] (html/content (:version ~'data))
     [:#analytics] (html/substitute
                     (analytics-snippet (:analytics-id ~'data)))))
