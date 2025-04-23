(ns net.sailvision.www.db
  (:require
   [clojure.java.io :as io]
   [datomic.client.api :as d]
   [environ.core :refer [env]]))

(def requested-almanacs-schema
  [{:db/ident       :requested-almanacs/destination
    :db/valueType   :db.type/string
    :db/cardinality :db.cardinality/one}
   {:db/ident       :requested-almanacs/boat-model
    :db/valueType   :db.type/string
    :db/cardinality :db.cardinality/one}
   {:db/ident       :requested-almanacs/email-address
    :db/valueType   :db.type/string
    :db/cardinality :db.cardinality/one}])

(def survey-responses-schema
  [{:db/ident       :survey-responses/blob
    :db/valueType   :db.type/string
    :db/cardinality :db.cardinality/one}])

(def discount-signups-schema
  [{:db/ident       :discount-signups/store-code
    :db/valueType   :db.type/string
    :db/cardinality :db.cardinality/one}
   {:db/ident       :discount-signups/email-address
    :db/valueType   :db.type/string
    :db/cardinality :db.cardinality/one}])

(defn storage []
  (let [storage (env :db-storage)
        [start] storage]
    (case start
      \: (keyword (subs storage 1))
      \/ storage
      nil (throw (IllegalArgumentException.
                  "no storage directory specified for DB"))
      (str (io/file (System/getProperty "user.dir") storage)))))

(defn connect [storage database]
  (let [client (d/client {:server-type :datomic-local
                          :system "www"
                          :storage-dir storage})
        [db-name schema] (case database
                           :requested-almanacs ["requested-almanacs" requested-almanacs-schema]
                           :discount-signups   ["discount-signups"   discount-signups-schema]
                           :survey-responses   ["survey-responses"   survey-responses-schema])]
    (d/create-database client {:db-name db-name})
    (let [conn (d/connect client {:db-name db-name})]
      (d/transact conn {:tx-data schema})
      conn)))

(defn insert-requested-almanac
  [{:keys [conn destination boat emailAddress]}]
  (d/transact conn {:tx-data [{:requested-almanacs/destination   destination
                               :requested-almanacs/boat-model    boat
                               :requested-almanacs/email-address emailAddress}]}))

(defn list-requested-almanacs
  [{:keys [conn]}]
  (let [db  (d/db conn)
        ids (d/q '[:find ?e
                   :where
                   [?e :requested-almanacs/destination]
                   [?e :requested-almanacs/boat-model]
                   [?e :requested-almanacs/email-address]]
                 db)]
    (map (fn [id]
           (let [entity (d/pull db '[*] (first id))]
             {:destination   (:requested-almanacs/destination   entity)
              :boat          (:requested-almanacs/boat-model    entity)
              :email-address (:requested-almanacs/email-address entity)}))
         ids)))

(defn insert-discount-signup
  [{:keys [conn store-code email-address]}]
  (d/transact conn {:tx-data [{:discount-signups/store-code    store-code
                               :discount-signups/email-address email-address}]}))

(defn list-discount-signups
  [{:keys [conn]}]
  (let [db  (d/db conn)
        ids (d/q '[:find ?e
                   :where
                   [?e :discount-signups/store-code]
                   [?e :discount-signups/email-address]]
                 db)]
    (map (fn [id]
           (let [entity (d/pull db '[*] (first id))]
             {:store-code    (:discount-signups/store-code    entity)
              :email-address (:discount-signups/email-address entity)}))
         ids)))

(defn insert-survey-response
  [{:keys [conn blob]}]
  (d/transact conn {:tx-data [{:survey-responses/blob blob}]}))

(defn list-survey-responses
  [{:keys [conn]}]
  (let [db (d/db conn)]
    (map first (d/q '[:find ?blob
                      :where [_ :survey-responses/blob ?blob]]
                    db))))
