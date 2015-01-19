(ns ahnentafel.main)

(defn app [request]
  {:status 200
   :headers {"Content-Type" "text/html"}
   :body "Hello World."})
