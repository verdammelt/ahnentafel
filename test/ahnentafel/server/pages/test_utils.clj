(ns ahnentafel.server.pages.test-utils
  (:require [clojure.test :refer :all]))

(defn get-page [page & args]
  (apply str (apply page
                    {:version "x.x.x"
                    :gedcom-file "/path/to/test.ged"
                     :get-data (fn [] 'fake-data)}
                    args)))

(defmethod assert-expr 'page-contains? [msg form]
  (let [text (nth form 1)
        msg (or msg (str "Expected to find '" text "' on the page."))]
    `(let [result# (.contains ~'page ~text)]
       (do-report {:message ~msg :expected '~form :actual ~'page
                   :type (if result# :pass :fail)}))))

(defmethod assert-expr 'page-not-contains? [msg form]
  (let [text (nth form 1)
        msg (or msg (str "Expected not to find '" text "' on the page."))]
    `(let [result# (.contains ~'page ~text)]
       (do-report {:message ~msg :expected '~form :actual ~'page
                   :type (if result# :fail :pass)}))))

(defmacro are-on-page [& texts]
  `(are [text] (~'page-contains? text) ~@texts))
