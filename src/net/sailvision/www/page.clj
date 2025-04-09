(ns net.sailvision.www.page
  (:require
   [clojure.string :as str]
   [environ.core :refer [env]]
   [garden.core :as g]
   [garden.stylesheet :as s]))

(def headers {"Content-Type" "text/html"})

(defn pretty-print []
  {:pretty-print? (or (env :pretty-print) false)})

(def base-css
  (g/css
   (pretty-print)
   [:body {:margin 0}]
   [:form
    [:label {:padding-right "10px"}]]
   [:html { :color-scheme "light dark" }]
   (s/at-media {:prefers-color-scheme :dark}
               [:body {:background (s/rgb 30 30 30)
                       :color      (s/rgb 200 200 200)}])
   (s/at-media {:prefers-color-scheme :light}
               [:body {:background (s/rgb 245 245 245)
                       :color      (s/rgb 50 50 50)}])))

(defn head [& {:keys [title extra-css noscript]}]
  [:head
   [:title (str/join " - " (keep identity ["SailVision" title]))]
   [:link {:rel "icon" :type "image/png" :href "/favicon.svg"}]
   [:meta {:name "viewport" :content "width=device-width, initial-scale=1"}]
   (if extra-css
     [:style base-css extra-css]
     [:style base-css])
   (if noscript
     [:noscript noscript]
     nil)])
