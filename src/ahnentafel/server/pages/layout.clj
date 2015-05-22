(ns ahnentafel.server.pages.layout
  (:require [net.cgrand.enlive-html :as html]))


(defmacro def-layout-template [name & forms]
  "Anaphoric macro for defining templates in the 'standard' layout.
Defines two parmaters: DATA (the data passed down from the handler) and
ARGS (rest argument)."
  `(html/deftemplate ~name "site/templates/index.html" [~'data & ~'args]
     ~@forms
     [:#version] (html/content (:version ~'data))))
