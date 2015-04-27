(ns ahnentafel.gedcom.reading.semantic
  (:require [clojure.zip :as zip]))

(defn gedcom-zipper [root]
  (let [branch? (constantly true)
        children (fn [node]
                   (if (map? node) (:subordinate-lines node) seq))
        make-node (fn [node children]
                    (println (str "(MAKE_NODE " node " " children ")"))
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

(defn continuation-lines [tree]
  (letfn [(continuation? [loc]
            (contains? #{"CONC" "CONT"} (:tag (zip/node loc))))
          (continuation-connector [loc]
            (if (= (:tag (zip/node loc)) "CONT") "\n" ""))
          (add-continuation [loc cont-loc]
            (let [node (zip/node loc)
                  node-value (:value node)
                  cont-value (:value (zip/node cont-loc))
                  connector (continuation-connector cont-loc)]
              (zip/replace loc
                           (assoc node
                                  :value (str node-value
                                              connector
                                              cont-value)))))]
    (let [zip (gedcom-zipper tree)]
      (zip/root
       (loop [z zip]
         (cond (zip/end? z) z

               (continuation? (zip/next z))
               (let [new-loc (add-continuation z (zip/next z))]
                 (recur (-> new-loc zip/next zip/remove)))

               (not (zip/node (zip/next z))) z

               :otherwise
               (recur (zip/next z))))))))

(defn process-records [records]
  (-> records
      subordinate-records
      continuation-lines
      ))
