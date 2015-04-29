(ns ahnentafel.gedcom.data)

(defn- find-items [tree tag]
  (filter #(= tag (:tag %)) (:subordinate-lines tree)))

(defn- find-item [tree tag]
  (first (find-items tree tag)))

(defn- find-xref [tree xref]
  (first (filter #(= xref (:xref %)) (:subordinate-lines tree))))

(defn header [tree]
  (let [header (find-item tree "HEAD")
        add-submitter (fn [m]
                        (if-let [xref (:value (find-item header "SUBM"))]
                          (assoc m :submitter {:name (:value (find-item (find-xref tree xref) "NAME"))
                                               :xref xref})
                          m))
        add-source (fn [m]
                     (let [source (find-item header "SOUR")]
                       (assoc m :source (:value (or (find-item source "NAME")
                                                    source)))))]
    (-> {:number-of-records (count (:subordinate-lines tree))
         :destination (:value (find-item header "DEST"))
         :file (:value (find-item header "FILE"))
         :file-time (:value (find-item header "DATE"))
         :gedcom {:version (:value (find-item (find-item header "GEDC") "VERS"))
                  :type (:value (find-item (find-item header "GEDC") "FORM"))}
         :encoding (:value (find-item header "CHAR"))}
        (add-source)
        (add-submitter))))

(defn find-record [tree query]
  (let [record (find-xref tree (:xref query))
        add-event-info (fn [m k e]
                         (if e
                           (assoc m k
                                  {:date (:value (find-item e "DATE"))
                                   :place (:value (find-item e "PLAC"))})
                           m))
        type-of (fn [r] (get {"INDI" :individual
                             "SUBM" :submitter}
                            (:tag r)
                            :unknown))]
    (-> {:type (type-of record)
         :name (map :value (find-items record "NAME"))
         :sex (:value (find-item record "SEX"))
         :family-as-child (:value (find-item record "FAMC"))
         :family-as-spouse (:value (find-item record "FAMS"))}
        (add-event-info :birth (find-item record "BIRT"))
        (add-event-info :death (find-item record "DEAT"))
        (add-event-info :burial (find-item record "BURI")))))
