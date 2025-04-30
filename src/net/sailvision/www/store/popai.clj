(ns net.sailvision.www.store.popai
  (:require
   [clojure.java.io :as io]
   [net.sailvision.www.about :as about]
   [net.sailvision.www.page :as page]
   [net.sailvision.www.store.icon :as icon]
   [net.sailvision.www.store.request :as request]
   [net.sailvision.www.store.route :as route]
   [net.sailvision.www.util :refer [long-str]]
   [garden.core :as g]
   [hiccup.core :as h]
   [ring.util.response :as resp]))

(defn style [content]
  {:style (g/style content)})

(def background-image
  {:css  [[:body
           [:header {:color       "#f8f8f8"
                     :text-shadow "0.05em 0.1em 0.5em #404040"}]]
          [:.background {:position   :absolute
                         :z-index    -2
                         :background "rgb(var(--background))"}
           [:img {:position       :fixed
                  :width          "100vw"
                  :height         "100vh"
                  :object-fit     :cover
                  :transition     "opacity 0.8s linear, transform 0.8s ease"
                  :mask-image     "linear-gradient(to bottom,black 70%,transparent)"
                  :mask-composite :intersect
                  :mask-size      "100% 100%"}]
           [:img.dark  {:visibility "var(--dark-visibility)"}]
           [:img.light {:visibility "var(--light-visibility)"}]]
          [:html.js
           [:.background
            [:img {:opacity   0
                   :transform "translateY(20px)"}]
            [:img.visible {:opacity   1
                           :transform :none}]]]]
   :body [[:div.background
           [:img.light {:src    "/popai-hero-background-light.jpg"
                        :onload "this.classList.add('visible')"}]
           [:img.dark  {:src    "/popai-hero-background-dark.jpg"
                        :onload "this.classList.add('visible')"}]]]})

(defn flyout
  ([pairs]
   (flyout pairs {}))
  ([pairs style]
   (flyout pairs style 0))
  ([pairs style delay]
   (let [duration 1]
     {
      :css    [[:.flyouts {:display :grid
                           :row-gap "1em"}
                [:.body-width {:padding  "4em 0"}]
                [:.flyout-pair {:display    :flex
                                :padding    "0 5vw"
                                :font-size  "2em"
                                :font-style :italic}
                 [:div {:flex-grow 1}]
                 [:q {:quotes :none
                      :transition (str "transform " duration "s, opacity " duration "s")}]
                 [:q.right {:margin "0.75em"}]]]
               [:html.js
                [:.flyout-pair
                 [:q       {:opacity   0}]
                 [:q.left  {:transform "translateX(-1em)"}]
                 [:q.right {:transform "translateX(1em)"}]]
                [:.flyout-pair.visible
                 [:q {:opacity   1
                      :transform :none}]]]]
      :body   [[:div.flyouts.full-width
                [:div.body-width {:style (g/style style)}
                 (map-indexed (fn [i {:keys [left right]}]
                                (let [row-delay   (+ delay (* 600 i))
                                      left-delay  (str row-delay "ms")
                                      right-delay (str (+ 200 row-delay) "ms")
                                      row-indent  (str (float (/ i 2)) "em")]
                                  [:div.flyout-pair.body-width
                                   (when left
                                     [:q.left {:style (g/style {:transition-delay left-delay
                                                                :padding-left     row-indent})} left])
                                   [:div]
                                   (when right
                                     [:q.right {:style (g/style {:transition-delay right-delay
                                                                 :padding-right    row-indent})} right])]))
                              pairs)]]]
      :script [(slurp (io/resource "flyout.js"))]})))

(defn topic-backdrop [body]
  (let [datauri #(str "url('data:image/svg+xml;utf8," % "')")
        wave-base {:xmlns               "http://www.w3.org/2000/svg"
                   :viewBox             "0 0 200 100"
                   :width               200
                   :height              100
                   :preserveAspectRatio :none}
        head-wave (h/html [:svg wave-base
                           [:path {:d (long-str "M 0 50"
                                                "Q 25 90, 50 50"
                                                "T 100 50"
                                                "T 150 50"
                                                "T 200 50"
                                                "L 200 100"
                                                "L 0 100"
                                                "Z")
                                   :fill "rgb(var(--background))"}]])
        tail-wave (h/html [:svg wave-base
                           [:path {:d (long-str "M -25 50"
                                                "Q 0 10, 25 50"
                                                "T 75 50"
                                                "T 125 50"
                                                "T 175 50"
                                                "T 225 50"
                                                "L 225 0"
                                                "L -25 0"
                                                "Z")
                                   :fill "rgb(var(--background))"}]])]
    (let [height  "1.4em"
          padding "3em"
          wave    {:height      height
                   :grid-column "1 / -1"
                   :mask-size   "100% 100%"
                   :mask-repeat :no-repeat
                   :background  "rgb(var(--background))"}
          offset   (str "calc(2px - " height ")")]
      [[:div.full-width.topic (style {:background "rgb(var(--background))"
                                      :margin     (str "calc(2 * " height ") 0")})
        [:div (style (merge wave {:mask-image    (datauri head-wave)
                                  :margin-top    offset
                                  :margin-bottom padding}))]
        body
        [:div.full-width.wave.tail-wave (style (merge wave {:mask-image    (datauri tail-wave)
                                                            :margin-bottom offset
                                                            :margin-top    padding}))]]])))

(def buy-button-css
  [["a.button" {:grid-column     3
                :user-select     :none
                :text-decoration :none
                :font-size       "1.2em"
                :border-radius   "1em"
                :padding         "1em"
                :cursor          :pointer
                :color           :white
                :justify-self    :center
                :background      "rgb(var(--accent))"
                :box-shadow      "3px 3px 0px color-mix(in srgb, rgb(var(--accent)), rgb(var(--foreground)) 70%)"}]
   ["a.button:hover"        {:background "color-mix(in srgb, rgb(var(--accent)), white 15%)"}]
   ["a.button:hover:active" {:background "color-mix(in srgb, rgb(var(--accent)), black 15%)"
                             :box-shadow :none
                             :transform  "translate(3px, 3px)"}]])

(defn topic
  ([body] (topic nil body))
  ([code body]
   {:body (topic-backdrop
           (concat
            body
            (when code
              [[:a.button {:href (route/with-code route/configure code)
                           :style (g/style {:margin "5em 0 3em"})}
                "Configure and Buy"]])))}))

(defn elaboration
  ([chunks] (elaboration nil chunks))
  ([code chunks]
   (topic
    code
    (let [[accent & points] chunks
          [head body]       accent

          gap "3em"]
      [[:div.body-width (style {:display :flex
                                :flex-flow "column nowrap"})
        [:div (style {:display   :flex
                      :flex-flow "row nowrap"})
         [:div (style {:flex-grow 1
                       :min-width "30vw"})]
         [:div (style {:color "rgb(var(--accent))"})
          [:h1 (style {:display :block}) head]
          [:p body]]]
        (map-indexed (fn [i point]
                       (let [[head body] point]
                         (if (= 0 (mod i 2))
                           [:div (style {:margin-top gap})
                            [:h1 head]
                            [:p body]]
                           [:div (style {:margin-top gap
                                         :display    :flex
                                         :flex-flow  "row nowrap"})
                            [:div (style {:flex-grow 1
                                          :min-width "10vw"})]
                            [:div
                             [:h1 head]
                             [:p body]]])))
                     points)]]))))

(defn page [request]
  (if-let [[code config] (request/validate request)]
    (page/from-components
     nil
     [page/base
      page/header
      {:css [buy-button-css]}
      background-image
      (flyout[{:left  "PopAI, are we ready to go?"
               :right "Yes! All instruments are on. AIS is transmitting. The water tanks are 90% full. We have 50 gallons of fuel and the batteries are fully charged."}]
             {:color       "white"
              :text-shadow "0.1em 0.2em 0.6em black"
              :height      "50vh"}
             700)
      (elaboration [["Ready to Cast Off"
                     "After months of preparation you are finally on a new charter boat with all your family and friends. The adventure begins …"]
                    ["Pre-Cruise Checklists"
                     "PopAI turns your boat into a powerful trusty deckhand who knows their way around the boat and automates going over tedious checklists for you. Save time and have confidence that you will never miss a critical task."]
                    ["Simple and reliable voice interface"
                     "Simply ask PopAI for any instrument data and get an instant update in your ear. Operating an unfamiliar vessel is stressful enough. No need to fight screen glare or brightness just to have to find where is the depth or boat speed."]
                    ["Expert handling charter companies"
                     "It’s easy to overlook things when checking out a charter boat—you’re relying on the charter company to have everything set up correctly. PopAI helps you take control with smart checklists: what gear should be onboard, what to test, what to ask about, and how to operate key systems. It also provides clear return instructions to ensure a smooth handover."]])
      (flyout [{:left "PopAI, notify me when if the depth goes below 15 feet."}
               {:right "I set an alarm for 15 feet minimum depth."}
               {:left "PopAI, what time are we set to arrive?"
                :right "We’ll arrive after dark, around 9:30."}]
              {:color       "white"
               :text-shadow "0.1em 0.2em 0.6em black"})
      (elaboration [["Cruising"
                     "It is a glorious day and everybody onboard is having a blast!"]
                    ["PopAI has your back"
                     "PopAI will read you your boat instruments' data when prompted. Setting an alarm is as easy as saying it out loud."]
                    ["Control your boat from anywhere"
                     "Control yourt boat seamlessly just with your voice. From yout MFD brightness and night mode to turning on and off lights, pumps and other systems."]
                    ["Activity oriented answers"
                     "From picturesque anchorages to the hottest local dive spot. PopAI is your instant reference."]])
      (flyout [{:left  "PopAI, why did the engine suddenly stop?"
                :right "It could be a fuel, electrical, overheating or a mechanical issue. Would you like me to provide you with troubleshooting steps?"}
               {:left "PopAI, where is the fuel pump’s fuse?"}]
              {:color       "white"
               :text-shadow "0.1em 0.2em 0.6em black"})
      (elaboration [["Breakdown"
                     "You’re alone at last—open sea, no signal, no distractions. Then—silence. The engine dies."]
                    ["Instant access to your boat’s manuals"
                     "Your Data Almanac is built from your boat’s manufacturer manuals. PopAI uses it to deliver instant, accurate answers for your specific systems."]
                    ["Troubleshooting"
                     "PopAI guides you through step-by-step troubleshooting. In a stressful moment, a clear, methodical approach can mean the difference between getting back underway—or drifting for hours in frustration."]
                    ["Repair instructions for your skill level"
                     "With a little know-how, you can handle 90% of boat repairs yourself."]])
      (flyout [{:left  "PopAI, how do I hail the Simpson Bay Bridge on VHF?"
                :right "Use channel 12 and say: “Simpson Bay Bridge, Simpson Bay Bridge, Simpson Bay Bridge, this is sailing vessel Kayo, over.”"}]
              {:color       "white"
               :text-shadow "0.1em 0.2em 0.6em black"})
      (elaboration [["Land Ahoy"
                     "You’ve reached your final destination—now it’s time to navigate the last challenge: docking in a new, unfamiliar marina on a foggy night."]
                    ["VHF instructions"
                     "Your Data Almanac is built from your boat’s manufacturer manuals. PopAI uses it to deliver instant, accurate answers tailored to your specific systems."]
                    ["COLREGS"
                     "When you need to change your plans for whatever reason, PopAI is there to help you find the best destination. Just like a cruising guide PopAI has a detailed local knowledge you can query with your voice."]
                    ["Pilot Plan"
                     "When you need to change your plans for whatever reason, PopAI is there to help you find the best destination. Just like a cruising guide PopAI has a detailed local knowledge you can query with your voice."]])
      (flyout [{:left  "PopAI, how do I hail the Simpson Bay Bridge on VHF?"
                :right "Use channel 12 and say: “Simpson Bay Bridge, Simpson Bay Bridge, Simpson Bay Bridge, this is sailing vessel Kayo, over.”"}]
              {:color       "white"
               :text-shadow "0.1em 0.2em 0.6em black"})
      (elaboration code
                   [["At the dock!"
                     "What an adventure! You’re safely docked, the crew is happily worn out from a day on the water—and now, it’s time to kick back and unwind."]
                    ["Instant access to your boat’s manuals"
                     "Your Data Almanac is built from your boat’s manufacturer manuals. PopAI uses it to deliver instant, accurate answers tailored to your specific systems."]
                    ["Local guide knowledge when you need it"
                     "When you need to change your plans for whatever reason, PopAI is there to help you find the best destination. Just like a cruising guide PopAI has a detailed local knowledge you can query with your voice."]])
      (flyout [{:left  "What should we ask here?"}]
              {:color       "white"
               :text-shadow "0.1em 0.2em 0.6em black"})
      (topic code
             [[:div.body-width (style {:display   :flex
                                       :flex-flow "column nowrap"
                                       :gap       "4em"})
               [:h1 (style {:margin 0}) "Instant, Quality Answers"]
               [:div (style {:display               :grid
                             :grid-template-columns "auto 1fr 5vw"
                             :grid-template-rows    "auto auto"
                             :gap                   "1em"
                             :text-align            :left})
                icon/run
                [:h2 (style {:margin      0
                             :grid-column 2}) "Instant reference"]
                [:p (style {:margin      0
                            :grid-column 2}) "Access live data from the boat’s instruments, including information about other vessels through AIS."]]
               [:div (style {:display               :grid
                             :grid-template-columns "5vw 1fr auto"
                             :grid-template-rows    "auto auto"
                             :gap                   "1em"
                             :text-align            :right})
                icon/boat-connect
                [:h2 (style {:margin      0
                             :grid-column 2}) "Concrete, exact answers"]
                [:p (style {:margin       0
                            :grid-column 2}) "Answers from the boat and engine operational manuals, maintenance guides, and other manufacturer documentation."]]
               [:div (style {:display               :grid
                             :grid-template-columns "auto 1fr 5vw"
                             :grid-template-rows    "auto auto"
                             :gap                   "1em"
                             :text-align            :left})
                icon/encyclopedia
                [:h2 (style {:margin      0
                             :grid-column 2}) "Encyclopedic detail"]
                [:p (style {:margin      0
                            :grid-column 2}) "Look up applicable navigation rules and maritime regulations for your cruising area. From COLREGS to Local Notice to Mariners, PopAI has your back."]]]])
      (flyout [])
      (flyout [])
      (flyout [])
      about/footer])
    (resp/redirect "/")))
