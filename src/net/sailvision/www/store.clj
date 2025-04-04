(ns net.sailvision.www.store
  (:require
   [hiccup.page :as h]
   [net.sailvision.www.db :as db]
   [net.sailvision.www.page :as page]
   [garden.core :as g]
   [ring.util.codec :as codec]))

(def route-checkout "/store/checkout")
(def route-request-almanac "/store/request-almanac")

(def boats {:sun-odyssey-410 "Jeanneau Sun Odyssey 410"
            :oceanis-42.3    "Beneteau Oceanis 42.3"
            :dufour-41       "Dufour 41"
            :dufour-44       "Dufour 44"
            :oceanis-46.1    "Beneteau Oceanis 46.1"})

(def locations {"Carribean"
                {:usvi-bvi        "Virgin Islands (British and United States)"
                 :leeward-islands "Leeward Islands"
                 :turks-caicos    "Turks and Caicos Islands"}
                "South Pacific"
                {:tahiti          "Tahiti"}})

(def time-frames [[2025 2], [2025 3], [2025 4],
                  [2026 1], [2026 2], [2026 3], [2026 4],
                  [2027 1], [2027 2], [2027 3], [2027 4]])

(def popai-description
  [:main
   [:h1 "Say hello to PopAI"]
   [:p "Set sail with your ultimate crusing companion, PopAI (pronounced \"Popeye\")."]
   [:p "PopAI is a voice assistant created by sailors and powered by detailed knowledge sets derived from crusing
     guides, government notices, local knowledge, as well as a host of other sources. This curated bank of information
     is invaluable for sailors of all skill levels."]
   [:h3
    [:q.prompt "PopAI, where can I anchor this evening?"]]
   [:p "With access to all of this knowledge through PopAI's seamless voice interface, your trip will be one of the most
     memorable and relaxing in years. Forget about the stress of finding safe harbor during an unexpected squal,
     diagnosing an engine failure as you're motoring in a busy anchorage, or even just finding a great place to kick
     back and have a drink."]
   [:h3
    [:q.prompt "PopAI, what is the depth?"]]
   [:p "Whether you're new to the world of sailing or a seasoned pro lamenting the loss of paper charts, PopAI is the
     perfect tool to augment your skill and ability, providing a second set of eyes to help you do what you do best."]
   [:h3
    [:q.prompt "PopAI, how do I get started?"]]
   [:p "PopAI has digital almanacs available for select destinations and boat models, with more on the way. Choose your
     combination below before proceeding to checkout."]])

(def checkout-form
  [:form.sku-selection {:action route-checkout}
   [:input {:type  :hidden
            :name  :product
            :value :popai}]
   [:label           {:for :location} "Location:"]
   [:select#location {:name :location}
    (map (fn [[area locations]]
           [:optgroup {:label area}
            (map (fn [[k v]] [:option {:value k} v]) locations)])
         locations)]
   [:label       {:for :boat} "Boat:"]
   [:select#boat {:name :boat}
    (map (fn [[k v]] [:option {:value k} v]) boats)]
   [:button {:type :submit} "Checkout"]])

(def almanac-request
  [:details
   [:summary "Don't see your destination or boat?"]
   [:p "Let us know where you're going, what you'll be sailing, and when so we can start working on the almanac. We'll
      let you know if they'll be ready in time for your trip and follow up once they are."]
   [:form.sku-request {:action route-request-almanac}
    [:input {:type  :hidden
             :name  :product
             :value :popai}]
    [:label             {:for  :destination} "Destination:"]
    [:input#destination {:name :destination}]
    [:label      {:for  :boat} "Boat:"]
    [:input#boat {:name :boat}]
    [:label            {:for  :timeFrame} "Time Frame:"]
    [:select#timeFrame {:name :timeFrame}
     (map (fn [[year quarter]]
            (let [id (str year "q" quarter)
                  months (case quarter
                           1 "January - March"
                           2 "April - June"
                           3 "July - September"
                           4 "October - December")]
              [:option {:value id} (str months ", " year)]))
          time-frames)]
    [:label              {:for  :emailAddress} "Email Address:"]
    [:input#emailAddress {:name :emailAddress}]
    [:button {:type :submit} "Request Almanac"]]])

(def base-css
  (g/css
   {:pretty-print? false}
   [:body
    {:padding "0 1em 2em"}]
   [:.prompt
    {:font-style :italic}]
   [:details
    {:margin-top "1em"}
    [:summary
     {:cursor :pointer}]]
   [:form
    {:display               :grid
     :grid-template-columns "auto 1fr"
     :gap   "0.3em"
     :width :fit-content}
    [:button
     {:grid-column  "span 2"
      :justify-self :center
      :padding      "0.3em 1em"}]]))

(defn popai []
  {:headers page/headers
   :body
   (h/html5
    (page/head {:title "PopAI" :extra-css base-css})
    [:body
     popai-description
     checkout-form
     almanac-request])})

(def form-validation-css
  (g/css
   {:pretty-print? false}
   ["input:not([type=\"submit\"])"
    {:box-sizing :border-box}
    {:border "medium solid transparent"}]
   ["input:valid:not(:focus):not(:placeholder-shown)"
    {:border "medium solid #00FF0060"}]
   ["input:invalid:not(:focus):not(:placeholder-shown)"
    {:border "medium solid #FF000090"}]
   [:dialog
    {:border     0
     :background :transparent}]
   ["dialog::backdrop"
    {:backdrop-filter "blur(3px)"}]
   [:#dialog-content
    {:display         :flex
     :width           "100%"
     :height          "100%"
     :align-items     :center
     :justify-content :center}]))

(def credit-card-cardholder
  [:input {:type         :text
           :name         :card-holder
           :autocomplete :cc-name
           :placeholder  "Name on Card"
           :required     true
           :style        (g/style {:width "25em"})}])

(def credit-card-number
  [:input {:type         :text
           :name         :card-number
           :inputmode    :numeric
           :autocomplete :cc-number
           :placeholder  "1234 5678 9012 3456"
           :pattern      "\\d{13,19}"
           :required     true
           :style        (g/style {:width "15em"})}])

(def credit-card-expiry
  [:input {:type         :month
           :name         :card-expiry
           :inputmode    :numeric
           :autocomplete :cc-exp
           :placeholder "MM/YY"
           :pattern "\\d{2}/\\d{2}"
           :required true
           :style        (g/style {:width "5em"})}])

(def credit-card-cvc
  [:input {:type         :text
           :name         :card-cvc
           :inputmode    :numeric
           :autocomplete :cc-csc
           :placeholder  "123"
           :pattern      "\\d{3,4}"
           :required     true
           :style        (g/style {:width "5em"})}])

(def credit-card-form
  [:form
   [:input {:type  :hidden
            :name  :product
            :value :popai}]
   credit-card-cardholder
   [:br]
   credit-card-number
   credit-card-expiry
   credit-card-cvc
   [:br]
   [:button {:type                :button
             :popovertarget       :modal
             :popovertargetaction :show}
    "Complete Purchase"]])

(def checkout-modal
  [:div#dialog-content
   [:p "Sorry to mislead, but this isn't a real product (yet!)"]])

(defn checkout [request]
  (let [params (codec/form-decode (:query-string request))
        location (get (reduce-kv (fn [acc k v] (merge acc v)) {} locations)
                      (keyword (get params "location")))
        boat (get boats (keyword (get params "boat")))]
    (if (and location boat)
      {:headers page/headers
       :body
       (h/html5
        (page/head {:title "Checkout" :extra-css form-validation-css})
        [:body
         [:p (str "Purchasing almanac for " location " aboard a " boat ".")]
         credit-card-form
         [:dialog#modal {:popover true}
          checkout-modal]])}
      {:status 401
       :headers page/headers
       :body "invalid product configuration"})))

(defn request-almanac [request]
  (let [params  (codec/form-decode (:query-string request))
        storage (db/storage)
        conn    (db/connect storage :requested-almanacs)]
    (db/insert-requested-almanac (into {:conn conn} (map (fn [[k v]] [(keyword k) v]) params)))))
