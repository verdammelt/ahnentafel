(ns ahnentafel.gedcom.query)

(defn- find-items [tree tag]
  (filter #(= tag (:tag %)) (:subordinate-lines tree)))

(defn- find-item [tree tag]
  (first (find-items tree tag)))

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

(defn find-record [tree query]
  (let [record (find-xref tree (:xref query))
        type-of (fn [r] (get {"INDI" :individual
                             "SUBM" :submitter}
                            (:tag r)
                            :unknown))
        add-event-info (fn [m k e]
                         (if e
                           (assoc m k
                                  {:date (find-item-value e "DATE")
                                   :place (find-item-value e "PLAC")})
                           m))]
    (-> {:type (type-of record)
         :name (map :value (find-items record "NAME"))
         :sex (find-item-value record "SEX")
         :family-as-child (find-item-value record "FAMC")
         :family-as-spouse (find-item-value record "FAMS")}
        (add-event-info :birth (find-item record "BIRT"))
        (add-event-info :death (find-item record "DEAT"))
        (add-event-info :burial (find-item record "BURI")))))
