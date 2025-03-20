(ns com.sailvisionpro.www
  (:gen-class)
  (:require [clojure.string :as str]
            [clout.core :as c]
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
               {:color (s/rgb 200 200 200)}])
  (s/at-media {:prefers-color-scheme :light}
              [:body
               {:background (s/rgb 245 245 245)}
               {:color (s/rgb 50 50 50)}]))

(defn head [& title]
  [:head
   [:title (str/join " - " (keep identity (cons "SailVision" title)))]
   [:link {:rel "icon" :type "image/png" :href "/favicon.svg"}]
   [:style css]])

(defn sunOdyssey [model]
  [:option (str "Jeanneau Sun Odyssey " model)])

(def routePurchase "/store/purchase")

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
   :body (h/html5 (head "PopAI")
           [:body
            [:p "This is the product page for PopAI."]
            [:form {:action routePurchase}
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
       :body (h/html5 (head "Checkout")
               [:body
                [:p (str "Purchasing almanac for " location " aboard a " boat ".")]])}
      {:status 401
       :headers {"Content-Type" "text/plain"}
       :body "invalid product configuration"})))

(defn home []
  {:headers {"Content-Type" "text/html"}
   :body (h/html5
          (head)
          [:body
           [:h1#slogan "The future of sailing is here"]])})

(defn robotsExclusion []
  {:headers {"Content-Type" "text/plain"}
   :body "User-agent: *\nDisallow: /"})

(defn route [request]
  (condp c/route-matches request
    (c/route-compile "/store/popai") (store)
    (c/route-compile routePurchase) ((params/wrap-params purchase) request)
    (c/route-compile "/robots.txt") (robotsExclusion)
    (c/route-compile "/") (home)
    (resp/redirect "/")))

(def handler
  (resource/wrap-resource route "public"))

(when-let [wrap-refresh (resolve 'ring.middleware.refresh/wrap-refresh)]
  (def refreshingHandler
    (wrap-refresh handler)))

(defn -main []
  (jetty/run-jetty handler {:port 8080}))
