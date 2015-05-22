(ns-unalias *ns* 'system)
(require '[ahnentafel.system :as system])

(defn- set-port [port]
  (alter-var-root #'env assoc :port port))

(defn- set-data-file [file]
  (alter-var-root #'env assoc :gedcom-file file))

(def the-system "The System." nil)

(defn init
  "Constructs the current development system."
  []
  (when-not (:port env) (set-port 3000))
  (alter-var-root #'the-system
    (constantly (system/system))))

(defn start
  "Starts the current development system."
  []
  (letfn [(start-jetty [s]
            (let [{:keys [handler port]} s]
              (assoc s :server
                     (run-jetty handler
                                {:port port :join? false}))))]
    (alter-var-root #'the-system (fn [s] (-> s system/start start-jetty)))))

(defn stop
  "Shuts down and destroys the current development system."
  []
  (letfn [(stop-jetty [s]
            (if-let [server (:server s)]
              (do (.stop server) (dissoc s :server))
              s))]
   (alter-var-root #'the-system (fn [s] (-> s stop-jetty system/stop)))))

(defn go
  "Initializes the current development system and starts it running."
  []
  (init)
  (start))

(defn restart []
  (stop)
  (refresh :after 'user/load-and-go))
