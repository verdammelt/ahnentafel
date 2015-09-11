(ns ahnentafel.gedcom.query
  (:require [clojure.string :as str]))

(defn- find-items [tag tree]
  (filter #(= tag (:tag %)) (:subordinate-lines tree)))

(defn- find-item [tag tree]
  (first (find-items tag tree)))

(defn- find-xref [tree xref]
  (first (filter #(= xref (:xref %)) (:subordinate-lines tree))))

(defn- find-item-value [tag tree]
  (:value (find-item tag tree)))

(defn header
  ([tree] (header tree nil))
  ([tree start-record]
   (let [header (find-item "HEAD" tree)
         header-value (fn [tag] (find-item-value tag header))
         add-submitter (fn [m]
                         (if-let [xref (header-value "SUBM")]
                           (assoc m :submitter {:name (find-item-value "NAME" (find-xref tree xref))
                                                :xref xref})
                           m))
         add-source (fn [m]
                      (let [source (find-item "SOUR" header)]
                        (assoc m :source (:value (or (find-item "NAME" source)
                                                     source)))))
         add-start-record (fn [m xref]
                            (if-let [record
                                     (or (and xref (find-xref tree xref))
                                         (find-item "INDI" tree))]
                              (assoc m
                                     :start-record
                                     {:name (find-item-value "NAME" record)
                                      :xref (:xref record)})
                              m))]
     (-> {:number-of-records (count (:subordinate-lines tree))
          :destination (header-value "DEST")
          :file (header-value "FILE")
          :file-time (header-value "DATE")
          :gedcom {:version (find-item-value "VERS" (find-item "GEDC" header))
                   :type (find-item-value "FORM" (find-item "GEDC" header))}
          :encoding (header-value "CHAR")}
         (add-source)
         (add-submitter)
         (add-start-record start-record)))))

(defn- type-of [record]
  (get {"INDI" :individual
        "SUBM" :submitter
        "FAM" :family}
       (:tag record)
       :unknown))

(defn- add-value [m k v] (if (not (empty? v)) (assoc m k v) m))

(defn- add-event-info [m k e]
  (if e
    (assoc m k
           {:date (find-item-value "DATE" e)
            :place (find-item-value "PLAC" e)})
    m))

(defn- person-info [r]
  {:xref (:xref r) :name (find-item-value "NAME" r)})

(defn- spouse-info [tree i fams]
  (let [xref (:value fams)
        family (find-xref tree xref)
        spouses (map #(find-item-value % family)
                     '("HUSB" "WIFE"))
        spouse-xref (first (filter #(not (= % (:xref i))) spouses))
        other-person (find-xref tree spouse-xref)]
    {:xref xref
     :spouse {:xref (:xref other-person)
              :name (find-item-value "NAME" other-person)}}))

(defn- make-record [tree raw-record]
  (-> {:type (type-of raw-record)
       :xref (:xref raw-record)}

      (add-value :name (map :value (find-items "NAME" raw-record)))
      (add-value :sex (find-item-value "SEX" raw-record))
      (add-value :family-as-child (find-item-value "FAMC" raw-record))
      (add-value :family-as-spouse (map #(spouse-info tree raw-record %) (find-items "FAMS" raw-record)))
      (add-event-info :birth (find-item "BIRT" raw-record))
      (add-event-info :death (find-item "DEAT" raw-record))
      (add-event-info :burial (find-item "BURI" raw-record))

      (add-value :spouses
                 [(person-info (find-xref tree (find-item-value "HUSB" raw-record)))
                  (person-info (find-xref tree (find-item-value "WIFE" raw-record)))])
      (add-event-info :marriage (find-item "MARR" raw-record))
      (add-value :children
                 (map #(person-info (find-xref tree (:value %)))
                      (find-items "CHIL" raw-record))))  )

(defn find-record [tree query]
  (make-record tree (find-xref tree (:xref query))))

(defn search [tree query]
  (letfn [(fix-string [s]
            (-> s
                (str/replace "/" "")
                str/lower-case))]
    (->> tree
         (find-items "INDI")
         (filter #(.contains
                   (fix-string (apply str (map :value (find-items "NAME" %))))
                   (fix-string query)))
         (map #(make-record tree %)))))
