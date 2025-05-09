(ns net.sailvision.www.store.configuration
  (:require
   [clojure.java.io :as io]
   [net.sailvision.www.about :as about]
   [net.sailvision.www.db :as db]
   [net.sailvision.www.page :as page :refer [external-link]]
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
                            (style {:grid-column  "span 2"
                                    :justify-self :center
                                    :margin-top   "1em"})) "Request Almanac"]]]]})

(defn configuration [code]
  (let [config    (code target/configs)
        boats     (:boats     config)
        locations (:locations config)
        price     (:price     config)]
    {:css  [[:form
             [:label {:align-content :center}]]
            [:table {:background      "color-mix(in srgb, rgb(var(--background)), rgb(var(--foreground)) 3%)"
                     :border          "thin rgb(var(--foreground)) solid"
                     :border-collapse :collapse
                     :text-align      :center
                     :margin          "0 auto"}
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
             [:img (merge {:src "/diagram.svg"}
                          (style {:width "100%"}))]
             [:h1 "Device Requirements"]
             [:p "In order for Popai to run properly on your mobile device, certain hardware features must be present. The following is a list of devices that are known to work, as well as supported features. If you don't see your device, send us a note and we'll figure out how well Popai will work on it."]
             [:table
              [:tr
               [:th "Mobile Device"]
               [:th "Full Offline Support *"]
               [:th "Requires Internet Connectivity **"]]
              [:tr
               [:td "iPhone 15 and iPhone 16"]
               [:td "Yes"]
               [:td ""]]
              [:tr
               [:td "iPad 11th gen"]
               [:td "Yes"]
               [:td ""]]
              [:tr
               [:td "iPhone 12, 13 and 14"]
               [:td ""]
               [:td "Yes"]]
              [:tr
               [:td "iPad (8th, 9th and 10th generations)"]
               [:td ""]
               [:td "Yes"]]
              [:tr
               [:td "Samsung Galaxy S25 Ultra"]
               [:td ""]
               [:td "Yes"]]
              [:tr
               [:td "Google Pixel 9 Pro"]
               [:td ""]
               [:td "Yes"]]
              [:tr
               [:td "Google Pixel Tablet"]
               [:td ""]
               [:td "Yes"]]]
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
               [:td "Garmin GPSMAP and  ECHOMAP"]
               [:td "Yes"]
               [:td "Yes"]]
              [:tr
               [:td "Simrad NSX, NSS, NSO and Go Series"]
               [:td "Yes"]
               [:td "Yes"]]
              [:tr
               [:td "Raymarine Axiom Series"]
               [:td "Yes"]
               [:td "Yes"]]
              [:tr
               [:td "B&G Vulcan Series"]
               [:td "Yes"]
               [:td "Yes"]]
              [:tr
               [:td "Yacht Devices NMEA Wi-Fi Gateway YDWG-02 or YDWN-02"]
               [:td "Yes"]
               [:td "Yes"]]
              [:tr
               [:td "Chartplotters built pre 2020 with Wi-Fi"]
               [:td ""]
               [:td "Yes"]]]
             [:h1 "Frequently Asked Questions"]
             [:details.faq
              [:summary "AI assistants are unreliable. How is Popai different?"]
              [:p "Popai is not an AI assistant."]
              [:p "Popai is a software product built around the principles of reliability, accuracy and transparency. While it uses techniques like machine learning and natural language processing, Popai is built on well understood marine industry rules and formulas."]
              [:p "The secret sauce of Popai is the structured data that it uses and how it processes real-time information from your boat. Popai knows how to talk to your chartplotter and all sensors on the boat. It knows your boat’s exact dimensions, capacities and manufacturer’s configuration. Popai combines this knowledge with structured local knowledge, marine navigation data, and maritime rules and regulations to give you concrete actionable answers. You can see exactly how each answer was generated for you and build trust in Popai."]]
             [:details.faq
              [:summary "What things can I configure?"]
              [:p "Popai uses marine industry standard formulas plus the specifics of your boat (draft, materials, size, etc.) and environment (weather, depth, tides, etc.) to define answers for you. You can see and change those formulas in the app or you can simply ask Popai to remember a new setting."]]
             [:details.faq
              [:summary "What type of questions can Popai answer?"]
              [:p "Popai reads data from your chartplotter when prompted and can control systems on your boat. Popai answers questions related to your boat, sailing area, maritime rules and regulations and can help you troubleshoot and repair breakdowns."]]
             [:details.faq
              [:summary "How does Popai know about systems on the boat?"]
              [:p "Popai connects to your chartlotter's Wi-Fi network and accesses the NMEA data bus to read sensor data and control your boat."]
              [:p "If your chartplotter does not have Wi-Fi connectivity you can install a third party device on the boat (Yacht Devices NMEA Wi-Fi Gateway) that will enable the same functionality."]]
             [:details.faq
              [:summary "How do you communicate with Popai"]
              [:p "Popai enables you to talk to your boat. You do this either by using your phone's speaker or a hands free device like a bluetooth headset or speaker and microphone. You can also configure Popai to work with a smart speaker such as Apple HomePod, Amazon Echo or Google Home."]]
             [:details.faq
              [:summary "Does Popai have a screen interface?"]
              [:p "Popai's super power is voice control which is the intended mode of usage. However Popai is an app and has a chat interface in it as well."]]
             [:details.faq
              [:summary "Does Popai consider my experience level?"]
              [:p "Yes, you can tell Popai that you want explanations and directions for novice or for experienced crew and it will tailor it’s answers and verbosity."]]
             [:details.faq
              [:summary "Can I set custom checklists?"]
              [:p "Yes, Popai allows you to create custom checklists for your boat and name them. You can add tasks to your checklists and mark them as completed. You can also set reminders for specific tasks."]]
             [:div (style {:display    :flex
                           :flex-wrap  :wrap
                           :margin-top "5em"})
              [:img (merge {:src "/person.svg"}
                           (style {:flex         "1 1 50%"
                                   :aspect-ratio 1}))]
              [:div.soft-outline (style {:display        :flex
                                         :flex-direction :column
                                         :flex           "1 1 50%"
                                         :height         :min-content})
               [:h1 (style {:font-size "1.5em"
                            :margin    "0em auto 1em"}) "Popai Digital Almanac"]
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
                         (map (fn [[k v]] [:option {:value k} v]) locations)])
                      locations)]
                [:label       {:for  :boat} "Boat:"]
                [:select#boat (merge {:name :boat}
                                     (style {:min-width 0}))
                 [:option {:value ""} "-- Select One --"]
                 (map (fn [[k v]] [:option {:value k} v]) boats)]
                [:p.total (style {:grid-column "1 / -1"
                                  :font-size   "1.1em"
                                  :margin      "1em 0 0.5em"}) "Subtotal: $" price]
                [:button (merge {:type :submit}
                                (style {:grid-column  "span 2"
                                        :justify-self :center
                                        :padding      "0.3em 1em"})) "Checkout"]]
               (first (:body (almanac-request code)))]]]]
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
