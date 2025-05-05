(ns net.sailvision.www.store.configuration
  (:require
   [clojure.java.io :as io]
   [net.sailvision.www.about :as about]
   [net.sailvision.www.db :as db]
   [net.sailvision.www.page :as page]
   [net.sailvision.www.store.request :as request]
   [net.sailvision.www.store.route :as route]
   [net.sailvision.www.store.target :as target]
   [ring.util.response :as resp]))

(defn almanac-request [code]
  {:css  [[:details {:margin-top "3em"}
           [:button {:margin-top "1em"}]]]
   :body [[:details
           [:summary "Don&rsquo;t see your destination or boat?"]
           [:p "Let us know where you&rsquo;re going and what you&rsquo;ll be sailing so we can start working on the almanac. We&rsquo;ll follow up once it&rsquo;s ready."]
           [:form.sku-request {:action (route/with-code route/request-almanac code)
                               :method :post}
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
            [:button {:type :submit} "Request Almanac"]]]]})

(defn configuration [code]
  (let [config    (code target/configs)
        boats     (:boats     config)
        locations (:locations config)
        price     (:price     config)]
    {:css  [[:#forms {:display        :flex
                      :flex-direction :column
                      :margin         "auto"
                      :width          :min-content}
             [:h1 {:font-size "1.5em"
                   :margin    "1em auto 0.5em"}]
             [:form
              {:display               :grid
               :grid-template-columns "auto 1fr"
               :gap                   "0.3em"
               :width                 :fit-content
               :margin                "auto"}
              [:label {:align-content :center}]
              [:button {:grid-column  "span 2"
                        :justify-self :center
                        :padding      "0.3em 1em"}]]
             [:.total {:grid-column "1 / -1"
                       :font-size   "1.1em"
                       :margin      "1em 0 0.5em"}]]
            (first (:css (almanac-request code)))]
     :body [[:main.body-width
             [:div#forms.soft-outline
              [:h1 "PopAI Digital Almanac"]
              [:form.sku-selection {:action (route/with-code route/checkout code)
                                    :method :post}
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
               [:p.total "Subtotal: $" price]
               [:button {:type :submit} "Checkout"]]
              (first (:body (almanac-request code)))]]]
     :script [(slurp (io/resource "almanac-checkout.js"))]}))

(defn page [request]
  (if-let [[code _config] (request/validate request)]
    (page/from-components "Configure PopAI" [page/base
                                             page/header
                                             page/header-spacer
                                             (configuration code)
                                             about/footer])
    (resp/redirect "/")))

;;
;; Form Handlers
;;

(defn request-almanac [request]
  (if-let [[_code _config] (request/validate request)]
    (let [params  (:params request)
          storage (db/storage)
          conn    (db/connect storage :requested-almanacs)]
      (db/insert-requested-almanac (into {:conn conn} (map (fn [[k v]] [(keyword k) v]) params)))
      (page/from-components
       "Requested Almanac"
       [page/base
        page/header
        page/header-spacer
        {:body [[:main.body-width
                 [:p "Thank you for your submission. We'll let you know when we can support that configuration."]]]}
        about/footer]))
    (resp/redirect "/")))
