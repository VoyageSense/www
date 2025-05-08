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
                           :margin-bottom "0.5em"
                           :padding       "1em"}
             [:p {:margin-bottom 0}]]]
     :body [[:main.body-width
             [:p "Popai runs on your existing mobile device, and connects to the systems already aboard your boat."]
             [:img (merge {:src "/diagram.svg"}
                          (style {:width "100%"}))]
             [:h1 "Device Requirements"]
             [:p "Popai is a powerful software product that enables you to talk to and control your boat and also has detailed knowledge on maritime rules and regulations. Unlike general purpose products like ChatGPT, Google’s Gemini and Apple Intelligence, Popai is custom built for your boat and for the purpose of assisting you on the water."]
             [:table
              [:tr
               [:th "Mobile Device"]
               [:th "Full Offline Support *"]
               [:th "Requires Internet Connectivity **"]]
              [:tr
               [:td "iPhone 12, 13 and 14"]
               [:td ""]
               [:td "Yes"]]
              [:tr
               [:td "iPhone 15 and iPhone 16"]
               [:td "Yes"]
               [:td "Yes"]]
              [:tr
               [:td "iPad (8th, 9th and 10th generations)"]
               [:td ""]
               [:td "Yes"]]
              [:tr
               [:td "iPad 11th gen"]
               [:td "Yes"]
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
             [:p "Popai installs on your mobile device and supports a wide variety of voice interfaces. Any hands free device, speaker with a microphone or a headset that connects to your Popai device will work."]
             [:p "In addition we heard our customers and integrated Popai with the top three most popular assistant ecosystems on the market " (external-link "https://assistant.google.com/" "Google Assistant") ", " (external-link "https://www.apple.com/apple-intelligence/" "Apple Intelligence") " (" (external-link "https://www.apple.com/siri/" "Apple Siri") "), and " (external-link "https://www.amazon.com/dp/B0DCCNHWV5" "Amazon Alexa") ". This means we support all existing hardware solutions like Google Home (Next Mini and Nest Audio), Apple’s HomePod, HomePod Mini and Apple TV, and all existing " (external-link "https://www.amazon.com/smart-home-devices/b?ie=UTF8&node=9818047011" "Amazon Echo hardware products") ". We also support any device like TVs, Roku, smart speakers, etc., that implement the " (external-link "https://developers.google.com/assistant" "Google Assistant API") ", " (external-link "https://developer.amazon.com/en-US/alexa/alexa-skills-kit/get-deeper/dev-tools-skill-management-api" "Alexa Skills") " or Apple Siri (" (external-link "https://developer.apple.com/documentation/sirikit/" "SiriKit") " and " (external-link "https://developer.apple.com/documentation/appintents" "App Intents") "). Our goal is to make using Popai so easy and familiar to you that you take it to all your chartering and boating adventures around the world."]
             [:p "To reduce power consumption on your boat, we recommend using a low-power smart speaker such as the " (external-link "https://www.apple.com/homepod-mini/" "Apple HomePod Mini")", " (external-link "https://www.amazon.com/Amazon-vibrant-helpful-routines-Charcoal/dp/B09B8V1LZ3/" "Amazon Echo Dot 5th Gen") ", or " (external-link "https://store.google.com/config/google_nest_mini?hl=en-US&selections=eyJwcm9kdWN0RmFtaWx5IjoiWjI5dloyeGxYMjVsYzNSZmJXbHVhUT09In0%3D" "Google Home Mini 2") " as an onboard speaker and microphone solution."]
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
             [:p "Popai is compatible with the most popular modern chartplotter on the market built since 2020 that have built in Wi-Fi connection. Popai knows how to talk to those deivces in a two-way communication to read your boat’s data and control systems and devices on the network. For older chartplotters or chartplotters that do not have Wi-Fi support you can install a " (external-link "https://www.yachtd.com/products" "Yacht Devices Wi-Fi NMEA Data Gateway") " (" (external-link "https://www.yachtd.com/products/wifi_0183_gateway.html" "YDWN-02") " or " (external-link "https://www.yachtd.com/products/wifi_gateway.html" "YDWG-02") ") third party device that enables Popai to read and control your NMEA data network (NMEA 2000 and NMEA 0183 compatible)."]
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
              [:summary "How is Popai different than ChatGPT or any other generative LLM based AI?"]
              [:p "Unlike general purpose products like ChatGPT, Google’s Gemini and Apple Intelligence, Popai is custom built for your boat and boating area for the purpose of assisting you on the water. Even the most advanced generative AI will give you generic answers that you will not trust with the safety of your boat and guests."]
              [:p "The secret sauce of Popai is the structured data that it uses and how it processes real-time information from your boat. Popai knows how to talk to your chartplotter and all sensors on the boat. It knows your boat’s exact dimensions, capacities and manufacturer’s configuration. Popai combines this knowledge with structured local knowledge, marine navigation data, and maritime rules and regulations to give you concrete actionable answers. You can see exactly how each answer was generated for you and build trust in Popai."]
              [:p "Lastly, Popai is built using state of the art techniques like machine learning and natural language processing but no single technique singularly describes how Popai works. Popai’s final performance is measured with the satisfaction of our customers and the repeat usage of the product."]]
             [:details.faq
              [:summary "What things can I configure?"]
              [:p "Popai is designed to seamlessly help with your boating experience. It will use the most common formulas to define answers for you, however you can see those formulas and configure them in the app to your liking. For example, when calculating how much anchor chain is needed for a specific location Popai will assume a desired scope ratio of 7:1. You can edit the scope ratio by updating it in the app or verbally telling Popai."]]
             [:details.faq
              [:summary "Can I set custom checklists?"]
              [:p "Yes, Popai allows you to create custom checklists for your boat and name them. You can add items to the checklist and mark them as completed. You can also set reminders for specific tasks."]]
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
     :script [(slurp (io/resource "almanac-checkout.js"))]}))

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
