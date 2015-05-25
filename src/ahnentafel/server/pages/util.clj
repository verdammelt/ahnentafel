(ns ahnentafel.server.pages.util
  (:require [net.cgrand.enlive-html :as html]))

(defn xref-link [r] (str "/records/" (:xref r)))
