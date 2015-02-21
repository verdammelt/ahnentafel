(ns ahnentafel.middleware.logging
  )
(defn- log [msg & vals]
  (let [line (apply format msg vals)]
    (locking System/out (println line))))

(defn simple-logging [handler]
  (fn [{:keys [request-method uri] :as req}]
    (if (= uri "/__source_changed") (handler req)
      (let [start  (System/currentTimeMillis)
            resp   (handler req)
            finish (System/currentTimeMillis)
            total  (- finish start)]
        (log "request %s %s (%dms)" request-method uri total)
        resp))))
