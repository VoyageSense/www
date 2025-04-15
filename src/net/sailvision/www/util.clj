(ns net.sailvision.www.util
  (:require [clojure.string :as str]))

(defn long-str [& lines]
 (str/join " " lines))
