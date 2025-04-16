(ns net.sailvision.www.page
  (:require
   [clojure.java.io :as io]
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

(def base
  (let [posthog-script (slurp (io/resource "posthog.js"))
        max-body-width "150ch"]
    {:css    [[:body {:display :grid
                      :grid-template-columns (str "1fr min(100%," max-body-width ") 1fr")
                      :font-family "Arial,sans-serif"}]
              [:.full-width {:grid-column           "1 / -1"
                             :display               :grid
                             :grid-template-columns (str "1fr min(100%," max-body-width ") 1fr")}]
              [:.body-width {:grid-column 2}]
              [:main {:margin "1em 1em"}]
              [:details :form {:margin "0em 1em"}]
              (s/at-media {:prefers-color-scheme :dark}
                          [":root" {:--bold-foreground  "240 240 240"
                                    :--bold-background  "1 1 1"
                                    :--light-visibility "hidden"
                                    :--dark-visibility  "visible"}]
                          (s/at-media {:prefers-color-scheme :light}
                                      [":root" {:--bold-foreground  "20 20 20"
                                                :--bold-background  "255 255 255"
                                                :--light-visibility "visible"
                                                :--dark-visibility  "hidden"}]))
              [:details {:margin-top "1em"}
               [:summary {:cursor :pointer}]]
              [:form {:display               :grid
                      :grid-template-columns "auto 1fr"
                      :gap   "0.3em"
                      :width :fit-content}
               [:button {:grid-column  "span 2"
                         :justify-self :center
                         :padding      "0.3em 1em"}]]]
     :script (if (env :posthog)
               [posthog-script]
               nil)}))

(defn head [& {:keys [title extra-css noscript extras]}]
  [:head
   [:title (str/join " - " (keep identity ["PopAI" title]))]
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
     [:title (str/join " - " (keep identity ["PopAI" title]))]
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
