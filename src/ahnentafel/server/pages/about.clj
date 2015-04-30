(ns ahnentafel.server.pages.about
  (:require [net.cgrand.enlive-html :as html]
            [ahnentafel.server.pages.layout :refer [def-layout-template]]))

(def-layout-template about
  [:#content] (html/content (html/html-resource "site/templates/about.html")))
