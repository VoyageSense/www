(ns com.popaithesailor.www.store.configuration
  (:require
   [clojure.java.io :as io]
   [clojure.string :as str]
   [com.popaithesailor.www.about :as about]
   [com.popaithesailor.www.db :as db]
   [com.popaithesailor.www.page :as page :refer [external-link]]
   [com.popaithesailor.www.store.request :as request]
   [com.popaithesailor.www.store.route :as route]
   [com.popaithesailor.www.store.target :as target]
   [com.popaithesailor.www.util :refer [raw-html style]]
   [ring.util.response :as resp]))

(defn almanac-request [code]
  {:body [[:details (style {:margin-top "3em"})
           [:summary "Don&rsquo;t see your destination or boat?"]
           [:p "Let us know where you&rsquo;re going and what you&rsquo;ll be sailing so we can start teaching Popai. We&rsquo;ll follow up once it&rsquo;s ready."]
           [:form.sku-request (merge {:action (route/with-code route/request-almanac code)
                                      :method :post}
                                     (style {:display               :grid
                                             :grid-template-columns "auto 1fr"
                                             :gap                   "0.6em"
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
                            (style {:grid-column  "span 2"
                                    :justify-self :center
                                    :margin-top   "1em"
                                    :padding      "0.3em 1em"})) "Request Almanac"]]]]})

(defn configuration [code]
  (let [config    (code target/configs)
        boats     (:boats     config)
        locations (:locations config)
        price     (:price     config)

        check     [:td (style {:color :green}) "✔︎"]
        uncheck   [:td]]
    {:css  [[:form
             [:label {:align-content :center}]]
            [:table {:background      "color-mix(in srgb, rgb(var(--background)), rgb(var(--foreground)) 3%)"
                     :border          "thin rgb(var(--foreground)) solid"
                     :border-collapse :collapse
                     :text-align      :center
                     :margin          "2em auto"}
             [:th {:background "color-mix(in srgb, rgb(var(--background)), rgb(var(--foreground)) 10%)"}]
             [:th :td {:vertical-align :middle
                       :border         "thin rgb(var(--foreground)) solid"
                       :padding        "0.8em"}]]
            [:h1 {:margin-top "2em"}]
            [:details.faq {:border        "thin color-mix(in srgb, rgb(var(--background)), rgb(var(--foreground)) 40%) solid"
                           :border-radius "1em"
                           :background    "color-mix(in srgb, rgb(var(--background)), rgb(var(--accent)) 10%)"
                           :margin-bottom "0.5em"
                           :padding       "1em"
                           :cursor        :pointer}
             [:p {:margin-bottom 0}]]]
     :body [[:main.body-width
             [:p "Popai runs on your existing mobile device, and connects to the systems already aboard your boat."]
             (raw-html (->> (slurp (io/resource "diagram.svg"))
                            (str/split-lines)
                            (drop 2) ;; TODO - this hack removes the XML declaration
                            (str/join)))
             [:h1 "Device Requirements"]
             [:p "In order for Popai to run properly on your mobile device, certain hardware features must be present. The following is a list of devices that are known to work, as well as supported features. If you don't see your device, send us a note and we'll figure out how well Popai will work on it."]
             [:table
              [:tr
               [:th "Mobile Device"]
               [:th "Supported"]
               [:th "Available Offline"]]
              [:tr
               [:td "iPhone 15 and iPhone 16"] check check]
              [:tr
               [:td "iPad 11th gen."] check check]
              [:tr
               [:td "iPhone 12, 13 and 14"] check uncheck]
              [:tr
               [:td "iPad (8th, 9th and 10th gen.)"] check uncheck]
              [:tr
               [:td "Samsung Galaxy S25 Ultra"] check uncheck]
              [:tr
               [:td "Google Pixel 9 Pro"] check uncheck]
              [:tr
               [:td "Google Pixel Tablet"] check uncheck]]
             [:img (merge {:src "/popai-indoor.webp"}
                          (style {:display       :block
                                  :width         "calc(min(100%, 600px))"
                                  :aspect-ratio  1
                                  :border-radius "1em"
                                  :margin        "5em auto"}))]
             [:h1 "Audio Interface Compatibility"]
             [:p "Since Popai runs as an app on your device and is controlled with your voice, you can use the hands-free device &mdash; or speakerphone &mdash; that you already have."]
             [:p "In addition, Popai integrates with the most popular assistant ecosystems on the market: " (external-link "https://assistant.google.com/" "Google Assistant") ", " (external-link "https://www.apple.com/siri/" "Apple Siri") " and " (external-link "https://www.amazon.com/dp/B0DCCNHWV5" "Amazon Alexa") ". As a result, most existing hardware solutions work out of the box, like Google Home (Next Mini and Nest Audio), Apple’s HomePod, HomePod Mini and Apple TV, and all existing " (external-link "https://www.amazon.com/smart-home-devices/b?ie=UTF8&node=9818047011" "Amazon Echo hardware products") "."]
             [:p "To reduce battery drain on your mobile device, we recommend using a low-power smart speaker such as the " (external-link "https://www.apple.com/homepod-mini/" "Apple HomePod Mini") ", " (external-link "https://www.amazon.com/Amazon-vibrant-helpful-routines-Charcoal/dp/B09B8V1LZ3/" "Amazon Echo Dot 5th Gen") ", or " (external-link "https://store.google.com/config/google_nest_mini?hl=en-US&selections=eyJwcm9kdWN0RmFtaWx5IjoiWjI5dloyeGxYMjVsYzNSZmJXbHVhUT09In0%3D" "Google Home Mini 2") " as an onboard speaker and microphone solution."]
             [:table
              [:tr
               [:th "Audio Device"]
               [:th "Listening Power Usage (Idle/Standby Mode)"]
               [:th "Max Power Usage (Max Volume)"]]
              [:tr
               [:td (external-link "https://www.apple.com/homepod-mini/" "Apple HomePod Mini")]
               [:td "0.8 W"]
               [:td "4 W"]]
              [:tr
               [:td (external-link "https://www.amazon.com/Amazon-vibrant-helpful-routines-Charcoal/dp/B09B8V1LZ3/" "Amazon Echo Dot 5th Gen")]
               [:td "1.3 W"]
               [:td "3.9 W"]]
              [:tr
               [:td (external-link "https://store.google.com/config/google_nest_mini?hl=en-US&selections=eyJwcm9kdWN0RmFtaWx5IjoiWjI5dloyeGxYMjVsYzNSZmJXbHVhUT09In0%3D" "Google Home Mini 2")]
               [:td "1.4 W"]
               [:td "2.7 W"]]]
             [:h1 "Chartplotter Compatibility"]
             [:p "Popai is compatible with the most popular, modern chartplotters on the market. Popai uses these devices' Wi-Fi network for two-way communication, allowing it to read your boat’s sensors and control the systems aboard. For older chartplotters or chartplotters that do not have Wi-Fi support, you can install something like the NMEA gateways from " (external-link "https://www.yachtd.com/products" "Yacht Devices") " (" (external-link "https://www.yachtd.com/products/wifi_0183_gateway.html" "YDWN-02") " or " (external-link "https://www.yachtd.com/products/wifi_gateway.html" "YDWG-02") ")."]
             [:table
              [:tr
               [:th "Chartplotter"]
               [:th "Control Systems and Devices"]
               [:th "Read Instrument Data"]]
              [:tr
               [:td "Garmin GPSMAP and  ECHOMAP"] check check]
              [:tr
               [:td "Simrad NSX, NSS, NSO and Go Series"] check check]
              [:tr
               [:td "Raymarine Axiom Series"] check check]
              [:tr
               [:td "B&G Vulcan Series"] check check]
              [:tr
               [:td "Yacht Devices NMEA Wi-Fi Gateway YDWG-02 or YDWN-02"] check check]
              [:tr
               [:td "Chartplotters built pre 2020 with Wi-Fi"] uncheck check]]
             [:img (merge {:src "/popai-outdoor.webp"}
                          (style {:display       :block
                                  :width         "calc(min(100%, 600px))"
                                  :aspect-ratio  1
                                  :border-radius "1em"
                                  :margin        "5em auto"}))]
             [:h1 "Frequently Asked Questions"]
             [:details.faq
              [:summary "AI assistants are unreliable. How is Popai different?"]
              [:p "Popai is not an AI assistant."]
              [:p "While it uses certain AI techniques &mdash; like the use of natural language processing to make the interface more natural &mdash; Popai's decision-making logic is based on established maritime rules and equations, making it determinstic and predictable."]]
             [:details.faq
              [:summary "Can I create custom checklists?"]
              [:p "Popai can remember custom checklists for your boat or the area, allowing you to create and modify them over time. You can add tasks to your checklists and mark them as completed, as well as set reminders for specific tasks."]]
             [:details.faq
              [:summary "Can I configure Popai’s behavior?"]
              [:p "Every sailor has their preferences and tolerance for risk, and Popai does its best to adapt. Things like anchor rode scope, fuel-consumption safety factor, and sail load margins have reasonable defaults but can be tweaked as desired. Just tell Popai."]]
             [:details.faq
              [:summary "What type of questions can Popai answer?"]
              [:p "Popai's knowledge consists of the combination of a cruising almanac, engine operational manual, and sailing textbook. Anything you can find in those books, you can ask Popai. Plus, Popai is tapped into the systems aboard your boat, so it can read all of the instruments."]]
             [:details.faq
              [:summary "Does Popai have a visual interface?"]
              [:p "If desired, textual input can be used to interact with Popai. This interface also provides a history of the spoken and written interactions."]]
             [:details.faq
              [:summary "Does Popai consider my experience level?"]
              [:p "Popai learns through its interactions which topics and in which situations you prefer verbose explanations or directions, versus a minimal hint or nothing at all."]]
             [:div.soft-outline (style {:display        :flex
                                        :flex-direction :column
                                        :flex           "1 1 50%"
                                        :height         :min-content
                                        :width          "calc(min(100%, 50ch))"
                                        :margin         "5em auto"})
              [:h1 (style {:font-size "1.5em"
                           :margin    "0em auto 1em"}) "Purchase Popai"]
              [:form.sku-selection (merge {:action (route/with-code route/checkout code)
                                           :method :post}
                                          (style {:display               :grid
                                                  :grid-template-columns "auto 1fr"
                                                  :gap                   "0.6em"
                                                  :margin                "auto"}))
               [:input {:type  :hidden
                        :name  :product
                        :value :popai}]
               [:label           {:for  :location} "Location:"]
               [:select#location (merge {:name :location}
                                        (style {:min-width 0}))
                [:option {:value ""} "-- Select One --"]
                (map (fn [[area locations]]
                       [:optgroup {:label area}
                        (map (fn [[k v]] [:option {:value k} v])(sort-by val locations))])
                     locations)]
               [:label       {:for  :boat} "Boat:"]
               [:select#boat (merge {:name :boat}
                                    (style {:min-width 0}))
                [:option {:value ""} "-- Select One --"]
                (map (fn [[k v]] [:option {:value k} v])(sort-by val boats))]
               [:p.total (style {:grid-column "1 / -1"
                                 :margin      "1em 0 0.5em"}) "Subtotal: $" price]
               [:button (merge {:type :submit}
                               (style {:grid-column  "span 2"
                                       :justify-self :center
                                       :padding      "0.3em 1em"})) "Checkout"]]
              (first (:body (almanac-request code)))]]]
     :script [(slurp (io/resource "almanac-checkout.js"))
              (slurp (io/resource "faq.js"))]}))

(defn page [request]
  (if-let [[code _config] (request/validate request)]
    (page/from-components "Configure Popai" [page/base
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
