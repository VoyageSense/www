(ns net.sailvision.www.page
  (:require
   [clojure.string :as str]
   [environ.core :refer [env]]
   [garden.core :as g]
   [garden.stylesheet :as s]
   [hiccup.page :as h]))

(def headers {"Content-Type" "text/html"})

(defn pretty-print []
  {:pretty-print? (or (env :pretty-print) false)})

(def light-foreground "50, 50, 50")
(def light-background "245, 245, 245")
(def dark-foreground  "200, 200, 200")
(def dark-background  "30, 30, 30")

(def base-css
  [[:body {:margin 0}]
   [:form
    [:label {:padding-right "10px"}]]
   [:html { :color-scheme "light dark" }]
   (s/at-media {:prefers-color-scheme :light}
               [":root" {:--foreground light-foreground
                         :--background light-background}])
   (s/at-media {:prefers-color-scheme :dark}
               [":root" {:--foreground dark-foreground
                         :--background dark-background}])
   [:body {:color      "rgb(var(--foreground))"
           :background "rgb(var(--background))"}]])

(defn head [& {:keys [title extra-css noscript extras]}]
  [:head
   [:title (str/join " - " (keep identity ["SailVision" title]))]
   [:link {:rel "icon" :type "image/png" :href "/favicon.svg"}]
   [:meta {:name "viewport" :content "width=device-width, initial-scale=1"}]
   [:style
    (g/css (pretty-print) base-css)
    extra-css]
   (if noscript
     [:noscript noscript]
     nil)
   (if extras
     (map identity extras)
     nil)])

(defn from-components [title components]
  {:headers headers
   :body
   (h/html5
    [:head
     [:title (str/join " - " (keep identity ["SailVision" title]))]
     [:link {:rel "icon" :type "image/png" :href "/favicon.svg"}]
     [:meta {:name "viewport" :content "width=device-width, initial-scale=1"}]
     [:style (g/css
              (pretty-print)
              base-css
              (mapcat :css components))]
     [:script (mapcat :script components)]
     [:noscript (g/css
                 (pretty-print)
                 (mapcat :noscript components))]]
    [:body (mapcat :body components)])})
