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
             [:p "PopAI runs on your existing mobile device, and connects to the systems already aboard your boat."]
             [:img {:src "/diagram.svg"}]
             [:h1 "Device Requirements"]
             [:p "PopAI is a powerful software product that enables you to talk to and control your boat and also has detailed knowledge on maritime rules and regulations. Unlike general purpose products like ChatGPT, Google’s Gemini and Apple Intelligence, PopAI is custom built for your boat and for the purpose of assisting you on the water."]
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
             [:p "PopAI installs on your mobile device and supports a wide variety of voice interfaces. Any hands free device, speaker with a microphone or a headset that connects to your PopAI device will work."]
             [:p "In addition we heard our customers and integrated PopAI with the top three most popular assistant ecosystems on the market " [:a {:href "https://assistant.google.com/"} "Google Assistant"] ", " [:a {:href "https://www.apple.com/apple-intelligence/"} "Apple Intelligence"] " (" [:a {:href "https://www.apple.com/siri/"} "Apple Siri"] "), and " [:a {:href "https://www.amazon.com/dp/B0DCCNHWV5"} "Amazon Alexa"] ". This means we support all existing hardware solutions like Google Home (Next Mini and Nest Audio), Apple’s HomePod, HomePod Mini and Apple TV, and all existing " [:a {:href "https://www.amazon.com/smart-home-devices/b?ie=UTF8&node=9818047011"} "Amazon Echo hardware products"] ". We also support any device like TVs, Roku, smart speakers, etc., that implement the " [:a {:href "https://developers.google.com/assistant"} "Google Assistant API"] ", " [:a {:href "https://developer.amazon.com/en-US/alexa/alexa-skills-kit/get-deeper/dev-tools-skill-management-api"} "Alexa Skills"] " or Apple Siri (" [:a {:href "https://developer.apple.com/documentation/sirikit/"} "SiriKit"] " and " [:a {:href "https://developer.apple.com/documentation/appintents"} "App Intents"] "). Our goal is to make using PopAI so easy and familiar to you that you take it to all your chartering and boating adventures around the world."]
             [:p "Recommended speakers based to optimize for low power usage on your boat and "]
             [:table
              [:tr
               [:th "Audio Device"]
               [:th "Max Power Usage (Max Volume)"]
               [:th "Power Usage Listening (Standby Mode)"]]
              [:tr
               [:td [:a {:href "https://www.apple.com/homepod-mini/"} "Apple HomePod Mini"]]
               [:td "4 W"]
               [:td "0.8 W"]]
              [:tr
               [:td [:a {:href "https://store.google.com/config/google_nest_mini?hl=en-US&selections=eyJwcm9kdWN0RmFtaWx5IjoiWjI5dloyeGxYMjVsYzNSZmJXbHVhUT09In0%3D"} "Google Home Mini 2"]]
               [:td "2.7 W"]
               [:td ""]]
              [:tr
               [:td [:a {:href "https://www.amazon.com/Amazon-vibrant-helpful-routines-Charcoal/dp/B09B8V1LZ3/"} "Amazon Echo Dot"]]
               [:td ""]
               [:td "1.4 W"]]]
             [:h1 "Chartplotter Compatibility"]
             [:p "PopAI is compatible with the most popular modern chartplotter on the market built since 2020 that have built in Wi-Fi connection. PopAI knows how to talk to those deivces in a two-way communication to read your boat’s data and control systems and devices on the network. For older chartplotters or chartplotters that do not have Wi-Fi support you can install a " [:a {:href "https://www.yachtd.com/products"} "Yacht Devices Wi-Fi NMEA Data Gateway"] " (" [:a {:href "https://www.yachtd.com/products/wifi_0183_gateway.html"} "YDWN-02"] " or " [:a {:href "https://www.yachtd.com/products/wifi_gateway.html"} "YDWG-02"] ") third party device that enables PopAI to read and control your NMEA data network (NMEA 2000 and NMEA 0183 compatible)."]
             [:table
              [:tr
               [:th ""]
               [:th "Garmin GPSMAP and  ECHOMAP"]
               [:th "Simrad NSX, NSS, NSO and Go Series"]
               [:th "Raymarine Axiom Series"]
               [:th "B&G Vulcan Series"]
               [:th "Yacht Devices NMEA Wi-Fi Gateway YDWG-02 or YDWN-02"]
               [:th "Chartplotters built pre 2020 with Wi-Fi"]]
              [:tr
               [:td "Control Systems and Devices"]
               [:td "Yes"]
               [:td "Yes"]
               [:td "Yes"]
               [:td "Yes"]
               [:td "Yes"]
               [:td ""]]
              [:tr
               [:td "Read Instruments  Data"]
               [:td "Yes"]
               [:td "Yes"]
               [:td "Yes"]
               [:td "Yes"]
               [:td "Yes"]
               [:td "Yes"]]]
             [:sup "* all systems must be manufactured post 2020 and have built in Wi-Fi connectivity"]
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
