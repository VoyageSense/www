(ns net.sailvision.www.page
  (:require
   [clojure.string :as str]
   [garden.core :as g]
   [garden.stylesheet :as s]))

(def headers {"Content-Type" "text/html"})

(def base-css
  (g/css
   {:pretty-print? false}
   [:body
    {:margin 0}]
   [:form
    [:label {:padding-right "10px"}]]
   [:#banner
    {:display :flex}
    {:align-items :center}
    {:justify-content :center}
    {:height "100vh"}
    {:margin 0}
    {:font-family "Arial, sans-serif"}]
   (s/at-media {:prefers-color-scheme :dark}
               [:body
                {:background (s/rgb 30 30 30)}
                {:color (s/rgb 200 200 200)}]
               [:html
                {:color-scheme "dark !important"}])
   (s/at-media {:prefers-color-scheme :light}
               [:body
                {:background (s/rgb 245 245 245)}
                {:color (s/rgb 50 50 50)}])))

(defn head [& {:keys [title extra-css]}]
  [:head
   [:title (str/join " - " (keep identity ["SailVision" title]))]
   [:link {:rel "icon" :type "image/png" :href "/favicon.svg"}]
   [:meta {:name "viewport" :content "width=device-width, initial-scale=1"}]
   (if extra-css
     [:style base-css extra-css]
     [:style base-css])])
