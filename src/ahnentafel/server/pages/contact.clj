(ns ahnentafel.server.pages.contact
  (:require [ahnentafel.gedcom.query :as query]
            [ahnentafel.server.pages.layout :refer [def-layout-template]]
            [net.cgrand.enlive-html :as html]))

(html/defsnippet contact-snippet
  "site/templates/contact.html"
  [:div#contact]
  [email]

  [:a]
  (html/do->
   (html/set-attr :href (str "mailto:" email))
   (html/content email)))

(def-layout-template contact
  [:#content]
  (html/substitute (contact-snippet (:email data))))
