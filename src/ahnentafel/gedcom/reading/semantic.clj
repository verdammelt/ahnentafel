(ns ahnentafel.gedcom.reading.semantic
  (:require [ahnentafel.gedcom.reading.zipper :refer [gedcom-zipper]])
  (:require [clojure.zip :as zip]))

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

(defn- combine-locs
  "Concatenate the value of LOC2 to that of LOC1 separated by SEPARATOR.
Removes LOC2. Returns updated location."
  [loc1 loc2 separator]
  (let [node1 (zip/node loc1)
        node2 (zip/node loc2)]
    (-> loc1
        (zip/replace (assoc node1
                            :value (str (:value node1)
                                        separator
                                        (:value node2))))
        zip/next
        zip/remove)))

(defn continuation-lines [tree]
  (letfn [(loc-tag [loc] (:tag (zip/node loc)))
          (continuation? [loc]
            (or (= (loc-tag loc) "CONC") (= (loc-tag loc) "CONT")))
          (continuation-connector [loc]
            (if (= (loc-tag loc) "CONT") "\n" ""))]
    (let [zip (gedcom-zipper tree)]
      (zip/root
       (loop [z zip]
         (cond (zip/end? z) z

               (continuation? (zip/next z))
               (recur (combine-locs z (zip/next z)
                                    (continuation-connector (zip/next z))))

               :otherwise
               (recur (zip/next z))))))))



(defn datetimes [tree]
  (letfn [(loc-tag [loc] (:tag (zip/node loc)))]
    (loop [z (gedcom-zipper tree)]
      (cond (zip/end? z) (zip/root z)

            (and (= (loc-tag z) "DATE")
                 (= (loc-tag (zip/next z)) "TIME"))
            (recur (combine-locs z (zip/next z) " "))

            :otherwise
            (recur (zip/next z))))))

(defn process-records [records]
  (-> records
      subordinate-records
      continuation-lines
      datetimes
      ))
