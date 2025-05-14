(ns com.popaithesailor.www.store.request
  (:require
   [com.popaithesailor.www.store.target :as target]))

(defn validate [request]
  (let [code   (keyword (:code request))
        config (when code
                 (code target/configs))]
    (when (and code config)
      [code config])))
