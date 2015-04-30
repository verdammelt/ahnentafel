(ns ahnentafel.server.pages.layout
  (:require [net.cgrand.enlive-html :as html]))


(defmacro def-layout-template [name & forms]
  `(html/deftemplate ~name "site/templates/index.html" [~'data & ~'args]
     ~@forms
     [:#version] (html/content (:version ~'data))))
