(ns ahnentafel.gedcom.util.zipper
  (:require [clojure.zip :as zip]))

(defn gedcom-zipper [root]
  (let [branch? (constantly true)
        children (fn [node] (:subordinate-lines node))
        make-node (fn [node children]
                    (if children
                      (assoc node :subordinate-lines children)
                      (dissoc node :subordinate-lines)))]
    (zip/zipper branch? children make-node root)))
