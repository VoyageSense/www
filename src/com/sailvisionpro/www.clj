(ns com.sailvisionpro.www
  (:gen-class)
  (:require [clojure.string :as str]
            [clout.core :as c]
            [garden.core :as g]
            [garden.def :refer [defstylesheet]]
            [garden.stylesheet :as s]
            [hiccup.page :as h]
            [ring.adapter.jetty :as jetty]
            [ring.middleware.params :as params]
            [ring.middleware.resource :as resource]
            [ring.util.codec :as codec]
            [ring.util.response :as resp])
  (:gen-class))

(defstylesheet css
  [:body
   {:margin 0}]
  [:form
   [:label {:padding-right "10px"}]]
  [:#slogan
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
               {:color (s/rgb 50 50 50)}]))

(defstylesheet form-validation-css
  ["input:not([type=\"submit\"])"
   {:box-sizing :border-box}
   {:border "medium solid transparent"}]
  ["input:valid:not(:focus):not(:placeholder-shown)"
   {:border "medium solid #00FF0060"}]
  ["input:invalid:not(:focus):not(:placeholder-shown)"
   {:border "medium solid #FF000090"}])

(defn head [& {:keys [title extra-css]}]
  [:head
   [:title (str/join " - " (keep identity ["SailVision" title]))]
   [:link {:rel "icon" :type "image/png" :href "/favicon.svg"}]
   (if extra-css
     [:style css
      :style extra-css]
     [:style css])])

(defn sun-odyssey [model]
  [:option (str "Jeanneau Sun Odyssey " model)])

(def route-purchase "/store/purchase")

(def boats {:sun-odyssey-410 "Jeanneau Sun Odyssey 410"
            :oceanis-42.3 "Beneteau Oceanis 42.3"
            :dufour-41 "Dufour 41"
            :dufour-44 "Dufour 44"
            :oceanis-46.1 "Beneteau Oceanis 46.1"})

(def locations {"Carribean"
                {:usvi-bvi "Virgin Islands (British and United States)"
                 :leeward-islands "Leeward Islands"
                 :turks-caicos "Turks and Caicos Islands"}
                "South Pacific"
                {:tahiti "Tahiti"}})

(defn store []
  {:headers {"Content-Type" "text/html"}
   :body (h/html5 (head {:title "PopAI"})
           [:body
            [:p "This is the product page for PopAI."]
            [:form {:action route-purchase}
             [:input {:type :hidden
                      :name :product
                      :value :popai}]
             [:label {:for :location} "Location:"]
             [:select#location {:name :location}
              (map (fn [[area locations]]
                     [:optgroup {:label area}
                      (map (fn [[k v]] [:option {:value k} v]) locations)])
                   locations)]
             [:br]
             [:label {:for :boatModel} "Boat Model:"]
             [:select#boatModel {:name :boatModel}
              (map (fn [[k v]] [:option {:value k} v]) boats)]
             [:br]
             [:input {:type :submit
                      :value "Checkout"}]]])})

(defn purchase [request]
  (let [params (codec/form-decode (:query-string request))
        location (get (reduce-kv (fn [acc k v] (merge acc v)) {} locations)
                      (keyword (get params "location")))
        boat (get boats (keyword (get params "boatModel")))]
    (if (and location boat)
      {:headers {"Content-Type" "text/html"}
       :body (h/html5 (head {:title "Checkout" :extra-css form-validation-css})
               [:body
                [:p (str "Purchasing almanac for " location " aboard a " boat ".")]
                [:form
                 [:input {:type :hidden
                          :name :product
                          :value :popai}]
                 [:input {:type :text
                          :name :card-holder
                          :autocomplete :cc-name
                          :placeholder "Name on Card"
                          :required true
                          :style (g/style {:width "25em"})}]
                 [:br]
                 [:input {:type :text
                          :name :card-number
                          :inputmode :numeric
                          :autocomplete :cc-number
                          :placeholder "1234 5678 9012 3456"
                          :pattern "\\d{13,19}"
                          :required true
                          :style (g/style {:width "15em"})}]
                 [:input {:type :month
                          :name :card-expiry
                          :inputmode :numeric
                          :autocomplete :cc-exp
                          :placeholder "MM/YY"
                          :pattern "\\d{2}/\\d{2}"
                          :required true
                          :style (g/style {:width "5em"})}]
                 [:input {:type :text
                          :name :card-cvc
                          :inputmode :numeric
                          :autocomplete :cc-csc
                          :placeholder "123"
                          :pattern "\\d{3,4}"
                          :required true
                          :style (g/style {:width "5em"})}]
                 [:br]
                 [:button {:type :button}
                  "Complete Purchase"]]])}
      {:status 401
       :headers {"Content-Type" "text/plain"}
       :body "invalid product configuration"})))

(defn home []
  {:headers {"Content-Type" "text/html"}
   :body (h/html5
          (head)
          [:body
           [:h1#slogan "The future of sailing is here"]])})

(defn robots-exclusion []
  {:headers {"Content-Type" "text/plain"}
   :body "User-agent: *\nDisallow: /"})

(defn route [request]
  (condp c/route-matches request
    (c/route-compile "/store/popai") (store)
    (c/route-compile route-purchase) ((params/wrap-params purchase) request)
    (c/route-compile "/robots.txt") (robots-exclusion)
    (c/route-compile "/") (home)
    (resp/redirect "/")))

(def handler
  (resource/wrap-resource route "public"))

(when-let [wrap-refresh (resolve 'ring.middleware.refresh/wrap-refresh)]
  (def refreshing-handler
    (wrap-refresh handler)))

(defn -main []
  (jetty/run-jetty handler {:port 8080}))
