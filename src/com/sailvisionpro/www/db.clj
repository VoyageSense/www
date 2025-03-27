(ns com.sailvisionpro.www.db
  ;; (:gen-class)
  (:require [datomic.client.api :as d]))

(def requested-almanacs-schema
  [{:db/ident :requested-almanacs/destination
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one}
   {:db/ident :requested-almanacs/boat-model
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one}
   {:db/ident :requested-almanacs/time-frame
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one}
   {:db/ident :requested-almanacs/email-address
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one}])

(defn connect [storage database]
  (let [client (d/client {:server-type :datomic-local
                          :system "www"
                          :storage-dir storage})
        [db-name schema] (case database
                           :requested-almanacs ["requested-almanacs" requested-almanacs-schema])]
    (d/create-database client {:db-name db-name})
    (let [conn (d/connect client {:db-name db-name})]
      (d/transact conn {:tx-data schema})
      conn)))

(defn insert-requested-almanac
  [{:keys [conn destination boatModel timeFrame emailAddress]}]
  (d/transact conn {:tx-data [{:requested-almanacs/destination destination
                               :requested-almanacs/boat-model boatModel
                               :requested-almanacs/time-frame timeFrame
                               :requested-almanacs/email-address emailAddress}]}))
