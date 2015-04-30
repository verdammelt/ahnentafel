(ns ahnentafel.server.pages.util
  (:require [net.cgrand.enlive-html :as html]))

(defn full-record-link [xref text]
  (html/html-snippet "<a href=\"/records/" xref "\">" text "</a>"))
