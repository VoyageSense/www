(ns net.sailvision.www.store.request
  (:require
   [net.sailvision.www.store.target :as target]))

(defn validate [request]
  (let [code   (keyword (:code request))
        config (when code
                 (code target/configs))]
    (when (and code config)
      [code config])))
