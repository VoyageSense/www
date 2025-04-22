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
(def link-visited  "128, 128, 128")

(def base-css
  [[:body {:margin 0}
    :body.full-width {:grid-template-rows "auto 1fr"
                      :min-height         "100vh"}]
   [:form
    [:label {:padding-right "10px"}]]
   [:html { :color-scheme "light dark" }]
   [":root" {:--link-visited   link-visited}]
   (s/at-media {:prefers-color-scheme :light}
               [":root" {:--foreground     light-foreground
                         :--background     light-background
                         :--link-unvisited light-foreground}])
   (s/at-media {:prefers-color-scheme :dark}
               [":root" {:--foreground     dark-foreground
                         :--background     dark-background
                         :--link-unvisited dark-foreground}])
   [:body {:color      "rgb(var(--foreground))"
           :background "rgb(var(--background))"}]])

(def base
  (let [posthog-script (slurp (io/resource "posthog.js"))
        max-body-width "150ch"]
    {:css    [[":root" {:--max-body-width max-body-width}]
              [:* {:box-sizing :border-box}]
              [:html {:scrollbar-gutter :stable}]
              [:body {:font-family "Arial,sans-serif"}]
              [:.full-width {:display               :grid
                             :grid-column           "1 / -1"
                             :grid-template-columns "1fr 1em min(calc(100% - 2em), var(--max-body-width)) 1em 1fr"}]
              [:.body-width {:grid-column 3}]
              [:.body-width-no-edge {:grid-column "2 / -2"}]
              [:main {:margin "3em 0"}]
              (s/at-media {:pointer :coarse}
                          [:form
                           [:select {:font-size "1em"}]
                           [:button {:font-size "1.2em"}]])
              (s/at-media {:prefers-color-scheme :dark}
                          [":root" {:--bold-foreground  "240 240 240"
                                    :--bold-background  "1 1 1"
                                    :--light-visibility "hidden"
                                    :--dark-visibility  "visible"}])
              (s/at-media {:prefers-color-scheme :light}
                          [":root" {:--bold-foreground  "20 20 20"
                                    :--bold-background  "255 255 255"
                                    :--light-visibility "visible"
                                    :--dark-visibility  "hidden"}])
              [:details [:summary {:cursor :pointer}]]
              [:.soft-outline {:padding       "1em"
                               :width         :fit-content
                               :border        "thin rgba(var(--foreground), 0.5) solid"
                               :border-radius "1em"}]]
     :script (if (env :posthog)
               [posthog-script]
               nil)}))

(def header
  {:css  [[:body
           [:header {:margin-top  "1em"
                     :display     :flex
                     :overflow    :hidden
                     :gap         "0.5em"
                     :align-items "last baseline"
                     :color       "rgb(var(--bold-foreground))"
                     :text-shadow "0.05em 0.1em 0.5em rgb(var(--bold-background))"}
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
                        [:h3 {:display :none}]]])]
   :body [[:header.body-width
           [:h1 "PopAI"]
           [:h2 "The ultimate boating companion"]
           [:div]
           [:h3
            [:span "Keep your hands on the helm and eyes on the water"]
            [:span "Use the power of your voice to manage your boating experience"]]]]})

(defn head [& {:keys [title extra-css noscript extras]}]
  [:head
   [:title (str/join " - " (keep identity ["PopAI" title]))]
   [:link {:rel   "icon"
           :type  "image/svg+xml"
           :sizes :any
           :href  "/favicon.svg"}]
   [:meta {:name    "viewport"
           :content "width=device-width, initial-scale=1"}]
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
    [:body.full-width (mapcat :body components)])})
