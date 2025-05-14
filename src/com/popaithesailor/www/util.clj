(ns com.popaithesailor.www.util
  (:require
   [clojure.string :as str]
   [garden.core :as g]))

(defn long-str [& lines]
 (str/join " " lines))

(defn inline [elements]
  (apply concat elements))

(defn style [content]
  {:style (g/style content)})
