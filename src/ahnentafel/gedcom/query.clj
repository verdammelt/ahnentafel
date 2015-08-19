(ns ahnentafel.gedcom.query)

(defn- find-items [tag tree]
  (filter #(= tag (:tag %)) (:subordinate-lines tree)))

(defn- find-item [tree tag]
  (first (find-items tag tree)))

(defn- find-xref [tree xref]
  (first (filter #(= xref (:xref %)) (:subordinate-lines tree))))

(defn- find-item-value [tree tag]
  (:value (find-item tree tag)))

(defn header
  ([tree] (header tree nil))
  ([tree start-record]
   (let [header (find-item tree "HEAD")
         header-value (fn [tag] (find-item-value header tag))
         add-submitter (fn [m]
                         (if-let [xref (header-value "SUBM")]
                           (assoc m :submitter {:name (find-item-value (find-xref tree xref) "NAME")
                                                :xref xref})
                           m))
         add-source (fn [m]
                      (let [source (find-item header "SOUR")]
                        (assoc m :source (:value (or (find-item source "NAME")
                                                     source)))))
         add-start-record (fn [m xref]
                            (if-let [record
                                     (or (and xref (find-xref tree xref))
                                         (find-item tree "INDI"))]
                              (assoc m
                                     :start-record
                                     {:name (find-item-value record "NAME")
                                      :xref (:xref record)})
                              m))]
     (-> {:number-of-records (count (:subordinate-lines tree))
          :destination (header-value "DEST")
          :file (header-value "FILE")
          :file-time (header-value "DATE")
          :gedcom {:version (find-item-value (find-item header "GEDC") "VERS")
                   :type (find-item-value (find-item header "GEDC") "FORM")}
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
           {:date (find-item-value e "DATE")
            :place (find-item-value e "PLAC")})
    m))

(defn- person-info [r]
  {:xref (:xref r) :name (find-item-value r "NAME")})

(defn- spouse-info [tree i fams]
  (let [xref (:value fams)
        family (find-xref tree xref)
        spouses (map #(find-item-value family %)
                     '("HUSB" "WIFE"))
        spouse-xref (first (filter #(not (= % (:xref i))) spouses))
        other-person (find-xref tree spouse-xref)]
    {:xref xref
     :spouse {:xref (:xref other-person)
              :name (find-item-value other-person "NAME")}}))

(defn- make-record [tree raw-record]
    (-> {:type (type-of raw-record)}

        (add-value :name (map :value (find-items "NAME" raw-record)))
        (add-value :sex (find-item-value raw-record "SEX"))
        (add-value :family-as-child (find-item-value raw-record "FAMC"))
        (add-value :family-as-spouse (map #(spouse-info tree raw-record %) (find-items "FAMS" raw-record)))
        (add-event-info :birth (find-item raw-record "BIRT"))
        (add-event-info :death (find-item raw-record "DEAT"))
        (add-event-info :burial (find-item raw-record "BURI"))

        (add-value :spouses
                   [(person-info (find-xref tree (find-item-value raw-record "HUSB")))
                    (person-info (find-xref tree (find-item-value raw-record "WIFE")))])
        (add-event-info :marriage (find-item raw-record "MARR"))
        (add-value :children
                   (map #(person-info (find-xref tree (:value %)))
                        (find-items "CHIL" raw-record))))  )

(defn find-record [tree query]
  (make-record tree (find-xref tree (:xref query))))

(defn search [tree query]
  (->> tree
       (find-items "INDI")
       (filter #(.contains (find-item-value % "NAME") query))
       (map #(make-record tree %))))
