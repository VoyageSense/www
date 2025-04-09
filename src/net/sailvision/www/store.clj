(ns net.sailvision.www.store
  (:require
   [clojure.string :as str]
   [hiccup.page :as h]
   [net.sailvision.www.db :as db]
   [net.sailvision.www.page :as page]
   [garden.core :as g]
   [garden.stylesheet :as s]
   [ring.util.codec :as codec]
   [ring.util.response :as resp]))

(def route-home "/store/:token")
(def route-checkout (str route-home "/checkout"))
(def route-request-almanac "/store/request-almanac")

(defn boats [token]
  (case token
    :popai {:sun-odyssey-410 "Jeanneau Sun Odyssey 410"
            :oceanis-42.3    "Beneteau Oceanis 42.3"
            :dufour-41       "Dufour 41"
            :dufour-44       "Dufour 44"
            :oceanis-46.1    "Beneteau Oceanis 46.1"}
    nil))

(defn locations [token]
  (case token
    :popai {"Carribean"
            {:usvi-bvi        "Virgin Islands (British and United States)"
             :leeward-islands "Leeward Islands"
             :turks-caicos    "Turks and Caicos Islands"}
            "South Pacific"
            {:tahiti          "Tahiti"}}
    nil))

(def time-frames [[2025 2], [2025 3], [2025 4],
                  [2026 1], [2026 2], [2026 3], [2026 4],
                  [2027 1], [2027 2], [2027 3], [2027 4]])

(def header
  [:header
   [:h1 "PopAI"]
   [:h2 "The ultimate boating companion"]
   [:div]
   [:h3
    [:span "Keep your hands on the helm and eyes on the water"]
    [:span "Use the power of your voice to manage your boating experience"]]])

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

(def header-css
  [[:body
    {:padding "0 0 2em"}
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
                 [:h3 {:display :none}]]])])

(def base-css
  (g/css
   {:pretty-print? false}
   [:main
    {:margin "1em 1em"}]
   [:details :form
    {:margin "0em 1em"}]
   (s/at-media {:prefers-color-scheme :dark}
               [:body
                [:header {:color       (s/rgb 240 240 240)
                          :background  (s/rgb 0 0 0)}]])
   (s/at-media {:prefers-color-scheme :light}
               [:body
                [:header {:color      (s/rgb 20 20 20)
                          :background (s/rgb 255 255 255)}]])
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
      :padding      "0.3em 1em"}]]
   header-css))

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

(defn checkout-form [{:keys [boats locations token]}]
  [:form.sku-selection {:action (str/replace-first route-checkout ":token" (name token))}
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

(defn popai [request]
  (let [token             (keyword (:token request))
        [boats locations] [(boats     token)
                           (locations token)]]
    (if (and boats locations)
      {:headers page/headers
       :body
       (h/html5
        (page/head :extra-css base-css)
        [:body
         header])}
      (resp/redirect "/"))))

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
  (let [token    (keyword (:token request))
        params   (codec/form-decode (:query-string request))
        location (get (reduce-kv (fn [acc k v]
                                   (merge acc v)) {} (locations token))
                      (keyword (get params "location")))
        boat     (get (boats token)
                      (keyword (get params "boat")))]
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
