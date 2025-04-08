(ns net.sailvision.www.page
  (:require
   [clojure.string :as str]
   [garden.core :as g]
   [garden.stylesheet :as s]))

(def headers {"Content-Type" "text/html"})

(def base-css
  (g/css
   {:pretty-print? false}
   [:body {:margin 0}
    [:header {:padding     "0.8em"
              :display     :flex
              :overflow    :hidden
              :gap         "0.5em"
              :align-items "last baseline"}
     [:h1 :h2 :h3 {:margin         0
                   :font-weight    :bold
                   :font-family    "Arial, san-serif"}]
     [:h1         {:font-size      "3em"}]
     [:h2         {:font-size      "1em"}]
     [:h3         {:font-size      "0.9em"
                   :white-space    :nowrap
                   :display        :inline-flex
                   :flex-direction :column}]
     [:div        {:flex-grow      1}]]]
   (s/at-media {:max-width "60em"}
               [:body
                [:header
                 [:h3 {:display :none}]]])
   [:form
    [:label {:padding-right "10px"}]]
   (s/at-media {:prefers-color-scheme :dark}
               [:html {:color-scheme "dark !important"}]
               [:body {:background (s/rgb 30 30 30)
                       :color      (s/rgb 200 200 200)}
                [:header {:color       (s/rgb 240 240 240)
                          :background  (s/rgb 0 0 0)}]])
   (s/at-media {:prefers-color-scheme :light}
               [:body {:background (s/rgb 245 245 245)
                       :color      (s/rgb 50 50 50)}
                [:header {:color      (s/rgb 20 20 20)
                          :background (s/rgb 255 255 255)}]])))

(def header
  [:header
   [:h1 "PopAI"]
   [:h2 "The ultimate boating companion"]
   [:div]
   [:h3
    [:span "Keep your hands on the helm and eyes on the water"]
    [:span "Use the power of your voice to manage your boating experience"]]])

(defn head [& {:keys [title extra-css]}]
  [:head
   [:title (str/join " - " (keep identity ["SailVision" title]))]
   [:link {:rel "icon" :type "image/png" :href "/favicon.svg"}]
   [:meta {:name "viewport" :content "width=device-width, initial-scale=1"}]
   (if extra-css
     [:style base-css extra-css]
     [:style base-css])])
