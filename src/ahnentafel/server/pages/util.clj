(ns ahnentafel.server.pages.util
  (:require [net.cgrand.enlive-html :as html]))

(defn xref-link [r] (str "/records/" (:xref r)))

(defn format-names [name & akas]
  (str name
       (if akas
         (str " (a.k.a. " (clojure.string/join ", " akas) ")"))))
