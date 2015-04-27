(ns ahnentafel.gedcom.data)

(defn- find-items [tree tag]
  (filter #(= tag (:tag %)) (:subordinate-lines tree)))

(defn- find-item [tree tag]
  (first (find-items tree tag)))

(defn header [tree]
  (let [header (find-item tree "HEAD")]
    {:number-of-records (count (:subordinate-lines tree))
     :source (:value (find-item (find-item header "SOUR") "NAME"))
     :destination (:value (find-item header "DEST"))
     :file (:value (find-item header "FILE"))
     :file-time (:value (find-item header "DATE"))
     :gedcom {:version (:value (find-item (find-item header "GEDC") "VERS"))
              :type (:value (find-item (find-item header "GEDC") "FORM"))}
     :encoding (:value (find-item header "CHAR"))
     :submitter (:value (find-item header "SUBM"))}))
