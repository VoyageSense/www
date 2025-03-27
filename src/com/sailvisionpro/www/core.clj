(ns com.sailvisionpro.www.core
  (:gen-class)
  (:require [clojure.java.io :as io]
            [clojure.string :as str]
            [clout.core :as c]
            [com.github.sikt-no.clj-jwt :as clj-jwt]
            [com.sailvisionpro.www.db :as db]
            [environ.core :refer [env]]
            [garden.core :as g]
            [garden.def :refer [defstylesheet]]
            [garden.stylesheet :as s]
            [hiccup.page :as h]
            [ring.adapter.jetty :as jetty]
            [ring.middleware.params :as params]
            [ring.middleware.resource :as resource]
            [ring.util.codec :as codec]
            [ring.util.response :as resp]))

(defonce server (atom nil))

(defn stop-server []
  (when-let [srv @server]
    (.stop srv))
  (shutdown-agents))

(defn db-storage []
  (let [storage (env :db-storage)
        [start] storage]
    (case start
      \: (keyword (subs storage 1))
      \/ storage
      nil (throw (IllegalArgumentException.
                  "no storage directory specified for DB"))
      (str (io/file (System/getProperty "user.dir") storage)))))

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
   {:border "medium solid #FF000090"}]
  [:dialog
   {:border 0
    :background :transparent}]
  ["dialog::backdrop"
   {:backdrop-filter "blur(3px)"}]
  [:#dialog-content
   {:display :flex
    :width "100%"
    :height "100%"
    :align-items :center
    :justify-content :center}])

(defstylesheet store-css
  [:body
   {:padding "0 1em"}]
  [:.prompt
   {:font-style :italic}]
  [:details
   {:margin-top "1em"}
   [:summary
    {:cursor :pointer}]]
  [:form
   {:display :grid
    :grid-template-columns "auto 1fr"
    :gap "0.3em"
    :width :fit-content
    :margin-inline-start "2em"}
   [:button
    {:grid-column "span 2"
     :justify-self :center
     :padding "0.3em 1em"}]])

(defn head [& {:keys [title extra-css]}]
  [:head
   [:title (str/join " - " (keep identity ["SailVision" title]))]
   [:link {:rel "icon" :type "image/png" :href "/favicon.svg"}]
   (if extra-css
     [:style css extra-css]
     [:style css])])

(defn sun-odyssey [model]
  [:option (str "Jeanneau Sun Odyssey " model)])

(def route-purchase "/store/purchase")
(def route-request "/store/request")

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
   :body (h/html5 (head {:title "PopAI" :extra-css store-css})
           [:body
            [:h1 "Say hello to PopAI"]
            [:p "Set sail with your ultimate crusing companion, PopAI (pronounced \"Popeye\")."]
            [:p "PopAI is a voice assistant created by sailors and powered by detailed knowledge sets derived from crusing guides, government notices, local knowledge, as well as a host of other sources. This curated bank of information is invaluable for sailors of all skill levels."]
            [:h3 [:q.prompt "PopAI, where can I anchor this evening?"]]
            [:p "With access to all of this knowledge through PopAI's seamless voice interface, your trip will be one of the most memorable and relaxing in years. Forget about the stress of finding safe harbor during an unexpected squal, diagnosing an engine failure as you're motoring in a busy anchorage, or even just finding a great place to kick back and have a drink."]
            [:h3 [:q.prompt "PopAI, what is the depth?"]]
            [:p "Whether you're new to the world of sailing or a seasoned pro lamenting the loss of paper charts, PopAI is the perfect tool to augment your skill and ability, providing a second set of eyes to help you do what you do best."]
            [:h3 [:q.prompt "PopAI, how do I get started?"]]
            [:p "PopAI has digital almanacs available for select destinations and boat models, with more on the way. Choose your combination below before proceeding to checkout."]
            [:form.sku-selection {:action route-purchase}
             [:input {:type :hidden
                      :name :product
                      :value :popai}]
             [:label {:for :location} "Location:"]
             [:select#location {:name :location}
              (map (fn [[area locations]]
                     [:optgroup {:label area}
                      (map (fn [[k v]] [:option {:value k} v]) locations)])
                   locations)]
             [:label {:for :boatModel} "Boat Model:"]
             [:select#boatModel {:name :boatModel}
              (map (fn [[k v]] [:option {:value k} v]) boats)]
             [:button {:type :submit} "Checkout"]]
            [:details
             [:summary "Don't see your destination or boat?"]
             [:p "Let us know where you're going, what you'll be sailing, and when so we can start working on the almanac. We'll let you know if they'll be ready in time for your trip and follow up once they are."]
             [:form.sku-request {:action route-request}
              [:input {:type :hidden
                       :name :product
                       :value :popai}]
              [:label {:for :destination} "Destination:"]
              [:input#destination {:name :destination}]
              [:label {:for :boatModel} "Boat Model:"]
              [:input#boatModel {:name :boatModel}]
              [:label {:for :timeFrame} "Time Frame:"]
              [:select#timeFrame {:name :timeFrame}
               (map (fn [[year quarter]]
                      (let [id (str year "q" quarter)
                            months (case quarter
                                     1 "January - March"
                                     2 "April - June"
                                     3 "July - September"
                                     4 "October - December")]
                        [:option {:value id} (str months ", " year)]))
                    [[2025 2], [2025 3], [2025 4],
                     [2026 1], [2026 2], [2026 3], [2026 4],
                     [2027 1], [2027 2], [2027 3], [2027 4]])
               ]
              [:label {:for :emailAddress} "Email Address:"]
              [:input#emailAddress {:name :emailAddress}]
              [:button {:type :submit} "Request Almanac"]]]])})

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
                 [:button {:type :button
                           :popovertarget :modal
                           :popovertargetaction :show}
                  "Complete Purchase"]]]
               [:dialog#modal {:popover true}
                [:div#dialog-content
                 [:p "Sorry to mislead, but this isn't a real product (yet!)"]]])}
      {:status 401
       :headers {"Content-Type" "text/plain"}
       :body "invalid product configuration"})))

(defn request-almanac [request]
  (let [params (codec/form-decode (:query-string request))
        storage (db-storage)
        conn (db/connect storage :requested-almanacs)]
    (db/insert-requested-almanac (into {:conn conn} (map (fn [[k v]] [(keyword k) v]) params)))))

(defn home []
  {:headers {"Content-Type" "text/html"}
   :body (h/html5
          (head)
          [:body
           [:h1#slogan "The future of sailing is here."]])})

(defn robots-exclusion []
  {:headers {"Content-Type" "text/plain"}
   :body "User-agent: *\nDisallow: /"})

(defn deploy [request]
  (if-let [next-path (env :next-path)]
    (let [auth    (get (:headers request) "authorization")
          jwk-url "https://token.actions.githubusercontent.com/.well-known/jwks"
          uberjar (:body request)
          oidc    (try
                    (clj-jwt/unsign jwk-url auth)
                    (catch Throwable t
                      (println "Failed to unsign OIDC token:" (ex-message t))))
          oidc-aud  (:aud oidc)
          oidc-repo (:repository oidc)]
      (if (and (= oidc-aud  "prod-deploy")
               (= oidc-repo "SailVision/www"))
        (do
          (println "Writing uberjar to" next-path)
          (with-open [out (io/output-stream next-path)]
            (io/copy uberjar out))
          (println "Finished writing uberjar")
          (future (stop-server))
          {:status 201})
        (do
          (println "Unauthorized deployment attempted - aud:" oidc-aud " repo:" oidc-repo)
          {:status 403})))
    (do
      (println "Deployment attempted but NEXT_PATH is not set")
      {:status 400})))

(defn route [request]
  (condp c/route-matches request
    (c/route-compile "/store/popai") (store)
    (c/route-compile route-purchase) ((params/wrap-params purchase) request)
    (c/route-compile route-request) ((params/wrap-params request-almanac) request)
    (c/route-compile "/robots.txt") (robots-exclusion)
    (c/route-compile "/i/deploy") (deploy request)
    (c/route-compile "/") (home)
    (resp/redirect "/")))

(def handler
  (resource/wrap-resource route "public"))

(when-let [wrap-refresh (resolve 'ring.middleware.refresh/wrap-refresh)]
  (def refreshing-handler
    (wrap-refresh handler)))

(defn -main []
  (let [srv (jetty/run-jetty handler {:host  "localhost"
                                      :port  8080
                                      :join? false})]
    (reset! server srv)))
