(ns ahnentafel.server.pages.not-found
  (:require [net.cgrand.enlive-html :as html]
            [ahnentafel.server.pages.layout :refer [def-layout-template]]))

(def-layout-template not-found
  [:#content] (let [request (first args)]
                (html/content (str (:uri request) " not found."))))
