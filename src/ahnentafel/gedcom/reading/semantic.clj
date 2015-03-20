(ns ahnentafel.gedcom.reading.semantic
  (:require [clojure.zip :as zip]))

(defn gedcom-zipper [root]
  (let [branch? (constantly true)
        children (fn [node]
                   (if (map? node) (:subordinate-lines node) seq))
        make-node (fn [node children]
                    (if (map? node) (assoc node :subordinate-lines (vec children))
                        (vec children)))]
    (zip/zipper branch? children make-node root)))

(defn subordinate-records [records]
  (letfn [(rewind-to-level [loc level]
            (if (= (:level (zip/node loc)) level) loc
                (rewind-to-level (zip/up loc) level)))]
    (zip/root (reduce (fn [loc rec]
                        (let [loc-level (:level (zip/node loc))
                              rec-level (:level rec)]
                          (if (< loc-level rec-level)
                            (-> loc
                                (zip/append-child rec)
                                zip/down)
                            (-> loc
                                (rewind-to-level rec-level)
                                (zip/insert-right rec)
                                zip/right))))
                      (gedcom-zipper {:level -1 :tag "__ROOT__"})
                      records))))

(defn process-records [records]
  (-> records
      subordinate-records
      ))
