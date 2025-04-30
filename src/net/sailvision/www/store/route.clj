(ns net.sailvision.www.store.route
  (:require
   [clojure.string :as str]))

(def home "/store/popai/:code")
(def configure (str home "/configure"))
(def checkout (str home "/checkout"))
(def request-almanac (str home "/request-almanac"))
(def discount (str home "/discount"))
(def survey (str home "/survey"))

(defn with-code [route code]
  (str/replace-first route ":code" (name code)))
