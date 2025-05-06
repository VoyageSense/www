(ns net.sailvision.www.store.configuration
  (:require
   [clojure.java.io :as io]
   [net.sailvision.www.about :as about]
   [net.sailvision.www.db :as db]
   [net.sailvision.www.page :as page]
   [net.sailvision.www.store.request :as request]
   [net.sailvision.www.store.route :as route]
   [net.sailvision.www.store.target :as target]
   [net.sailvision.www.util :refer [style]]
   [ring.util.response :as resp]))

(defn almanac-request [code]
  {:body [[:details (style {:margin-top "3em"})
           [:summary "Don&rsquo;t see your destination or boat?"]
           [:p "Let us know where you&rsquo;re going and what you&rsquo;ll be sailing so we can start working on the almanac. We&rsquo;ll follow up once it&rsquo;s ready."]
           [:form.sku-request (merge {:action (route/with-code route/request-almanac code)
                                      :method :post}
                                     (style {:display               :grid
                                             :grid-template-columns "auto 1fr"
                                             :gap                   "0.3em"
                                             :width                 :fit-content
                                             :margin                "auto"}))
            [:input {:type  :hidden
                     :name  :product
                     :value :popai}]
            [:label             {:for  :destination} "Destination:"]
            [:input#destination {:name :destination}]
            [:label      {:for  :boat} "Boat:"]
            [:input#boat {:name :boat}]
            [:label              {:for  :emailAddress} "Email Address:"]
            [:input#emailAddress {:type :email
                                  :name :emailAddress}]
            [:button (merge {:type :submit}
                            (style {:margin-top "1em"})) "Request Almanac"]]]]})

(defn configuration [code]
  (let [config    (code target/configs)
        boats     (:boats     config)
        locations (:locations config)
        price     (:price     config)]
    {:css  [[:form
             [:label {:align-content :center}]]]
     :body [[:main.body-width
             [:div.soft-outline (style {:display        :flex
                                        :flex-direction :column
                                        :margin         "auto"
                                        :width          :min-content})
              [:h1 (style {:font-size "1.5em"
                           :margin    "0em auto 1em"}) "PopAI Digital Almanac"]
              [:form.sku-selection (merge {:action (route/with-code route/checkout code)
                                           :method :post}
                                          (style {:display               :grid
                                                  :grid-template-columns "auto 1fr"
                                                  :gap                   "0.3em"
                                                  :width                 :fit-content
                                                  :margin                "auto"}))
               [:input {:type  :hidden
                        :name  :product
                        :value :popai}]
               [:label           {:for  :location} "Location:"]
               [:select#location {:name :location}
                [:option {:value ""} "-- Select One --"]
                (map (fn [[area locations]]
                       [:optgroup {:label area}
                        (map (fn [[k v]] [:option {:value k} v]) locations)])
                     locations)]
               [:label       {:for  :boat} "Boat:"]
               [:select#boat {:name :boat}
                [:option {:value ""} "-- Select One --"]
                (map (fn [[k v]] [:option {:value k} v]) boats)]
               [:p.total (style {:grid-column "1 / -1"
                                 :font-size   "1.1em"
                                 :margin      "1em 0 0.5em"}) "Subtotal: $" price]
               [:button (merge {:type :submit}
                               (style {:grid-column  "span 2"
                                       :justify-self :center
                                       :padding      "0.3em 1em"}))"Checkout"]]
              (first (:body (almanac-request code)))]]]
     :script [(slurp (io/resource "almanac-checkout.js"))]}))

(defn page [request]
  (if-let [[code _config] (request/validate request)]
    (page/from-components "Configure PopAI" [page/base
                                             (page/header code)
                                             page/header-spacer
                                             (configuration code)
                                             (about/footer code)])
    (resp/redirect "/")))

;;
;; Form Handlers
;;

(defn request-almanac [request]
  (if-let [[code _config] (request/validate request)]
    (let [params  (:params request)
          storage (db/storage)
          conn    (db/connect storage :requested-almanacs)]
      (db/insert-requested-almanac (into {:conn conn} (map (fn [[k v]] [(keyword k) v]) params)))
      (page/from-components
       "Requested Almanac"
       [page/base
        (page/header code)
        page/header-spacer
        {:body [[:main.body-width
                 [:p "Thank you for your submission. We'll let you know when we can support that configuration."]]]}
        (about/footer code)]))
    (resp/redirect "/")))
