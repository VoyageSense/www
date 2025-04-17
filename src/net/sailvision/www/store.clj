(ns net.sailvision.www.store
  (:require
   [clojure.string :as str]
   [net.sailvision.www.about :as about]
   [net.sailvision.www.db :as db]
   [net.sailvision.www.page :as page]
   [garden.core :as g]
   [garden.stylesheet :as s]
   [ring.util.codec :as codec]
   [ring.util.response :as resp]))

(def route-home "/store/popai/:code")
(def route-configure (str route-home "/configure"))
(def route-checkout (str route-home "/checkout"))
(def route-request-almanac "/store/request-almanac")

(defn route-with-code [route code]
  (str/replace-first route ":code" (name code)))

(def targets
  {:watermellon {:boats     {:sun-odyssey-410 "Jeanneau Sun Odyssey 410"
                             :oceanis-42.3    "Beneteau Oceanis 42.3"
                             :dufour-41       "Dufour 41"
                             :dufour-44       "Dufour 44"
                             :oceanis-46.1    "Beneteau Oceanis 46.1"}
                 :locations {"Carribean"
                             {:usvi-bvi        "Virgin Islands (British and United States)"
                              :leeward-islands "Leeward Islands"
                              :turks-caicos    "Turks and Caicos Islands"}
                             "South Pacific"
                             {:tahiti          "Tahiti"}}
                 :price     300}

   :mtklk {:boats     {:sun-odyssey-410 "Jeanneau Sun Odyssey 410"
                       :oceanis-42.3    "Beneteau Oceanis 42.3"
                       :dufour-41       "Dufour 41"
                       :dufour-44       "Dufour 44"
                       :oceanis-46.1    "Beneteau Oceanis 46.1"}
           :locations {"Carribean"
                       {:usvi-bvi        "Virgin Islands (British and United States)"
                        :leeward-islands "Leeward Islands"
                        :turks-caicos    "Turks and Caicos Islands"}
                       "South Pacific"
                       {:tahiti          "Tahiti"}}
           :price     300}
   :ulrbt {:boats     {:sun-odyssey-410 "Jeanneau Sun Odyssey 410"
                       :oceanis-42.3    "Beneteau Oceanis 42.3"
                       :dufour-41       "Dufour 41"
                       :dufour-44       "Dufour 44"
                       :oceanis-46.1    "Beneteau Oceanis 46.1"}
           :locations {"Carribean"
                       {:usvi-bvi        "Virgin Islands (British and United States)"
                        :leeward-islands "Leeward Islands"
                        :turks-caicos    "Turks and Caicos Islands"}
                       "South Pacific"
                       {:tahiti          "Tahiti"}}
           :price     300}
   :pwhom {:boats     {:sun-odyssey-410 "Jeanneau Sun Odyssey 410"
                       :oceanis-42.3    "Beneteau Oceanis 42.3"
                       :dufour-41       "Dufour 41"
                       :dufour-44       "Dufour 44"
                       :oceanis-46.1    "Beneteau Oceanis 46.1"}
           :locations {"Carribean"
                       {:usvi-bvi        "Virgin Islands (British and United States)"
                        :leeward-islands "Leeward Islands"
                        :turks-caicos    "Turks and Caicos Islands"}
                       "South Pacific"
                       {:tahiti          "Tahiti"}}
           :price     300}
   :ocbto {:boats     {:sun-odyssey-410 "Jeanneau Sun Odyssey 410"
                       :oceanis-42.3    "Beneteau Oceanis 42.3"
                       :dufour-41       "Dufour 41"
                       :dufour-44       "Dufour 44"
                       :oceanis-46.1    "Beneteau Oceanis 46.1"}
           :locations {"Carribean"
                       {:usvi-bvi        "Virgin Islands (British and United States)"
                        :leeward-islands "Leeward Islands"
                        :turks-caicos    "Turks and Caicos Islands"}
                       "South Pacific"
                       {:tahiti          "Tahiti"}}
           :price     300}
   :aocbq {:boats     {:sun-odyssey-410 "Jeanneau Sun Odyssey 410"
                       :oceanis-42.3    "Beneteau Oceanis 42.3"
                       :dufour-41       "Dufour 41"
                       :dufour-44       "Dufour 44"
                       :oceanis-46.1    "Beneteau Oceanis 46.1"}
           :locations {"Carribean"
                       {:usvi-bvi        "Virgin Islands (British and United States)"
                        :leeward-islands "Leeward Islands"
                        :turks-caicos    "Turks and Caicos Islands"}
                       "South Pacific"
                       {:tahiti          "Tahiti"}}
           :price     300}
   :bsfqn {:boats     {:sun-odyssey-410 "Jeanneau Sun Odyssey 410"
                       :oceanis-42.3    "Beneteau Oceanis 42.3"
                       :dufour-41       "Dufour 41"
                       :dufour-44       "Dufour 44"
                       :oceanis-46.1    "Beneteau Oceanis 46.1"}
           :locations {"Carribean"
                       {:usvi-bvi        "Virgin Islands (British and United States)"
                        :leeward-islands "Leeward Islands"
                        :turks-caicos    "Turks and Caicos Islands"}
                       "South Pacific"
                       {:tahiti          "Tahiti"}}
           :price     300}
   :ysquo {:boats     {:sun-odyssey-410 "Jeanneau Sun Odyssey 410"
                       :oceanis-42.3    "Beneteau Oceanis 42.3"
                       :dufour-41       "Dufour 41"
                       :dufour-44       "Dufour 44"
                       :oceanis-46.1    "Beneteau Oceanis 46.1"}
           :locations {"Carribean"
                       {:usvi-bvi        "Virgin Islands (British and United States)"
                        :leeward-islands "Leeward Islands"
                        :turks-caicos    "Turks and Caicos Islands"}
                       "South Pacific"
                       {:tahiti          "Tahiti"}}
           :price     300}
   :lhztx {:boats     {:sun-odyssey-410 "Jeanneau Sun Odyssey 410"
                       :oceanis-42.3    "Beneteau Oceanis 42.3"
                       :dufour-41       "Dufour 41"
                       :dufour-44       "Dufour 44"
                       :oceanis-46.1    "Beneteau Oceanis 46.1"}
           :locations {"Carribean"
                       {:usvi-bvi        "Virgin Islands (British and United States)"
                        :leeward-islands "Leeward Islands"
                        :turks-caicos    "Turks and Caicos Islands"}
                       "South Pacific"
                       {:tahiti          "Tahiti"}}
           :price     300}
   :xqccm {:boats     {:sun-odyssey-410 "Jeanneau Sun Odyssey 410"
                       :oceanis-42.3    "Beneteau Oceanis 42.3"
                       :dufour-41       "Dufour 41"
                       :dufour-44       "Dufour 44"
                       :oceanis-46.1    "Beneteau Oceanis 46.1"}
           :locations {"Carribean"
                       {:usvi-bvi        "Virgin Islands (British and United States)"
                        :leeward-islands "Leeward Islands"
                        :turks-caicos    "Turks and Caicos Islands"}
                       "South Pacific"
                       {:tahiti          "Tahiti"}}
           :price     300}
   :plpgm {:boats     {:sun-odyssey-410 "Jeanneau Sun Odyssey 410"
                       :oceanis-42.3    "Beneteau Oceanis 42.3"
                       :dufour-41       "Dufour 41"
                       :dufour-44       "Dufour 44"
                       :oceanis-46.1    "Beneteau Oceanis 46.1"}
           :locations {"Carribean"
                       {:usvi-bvi        "Virgin Islands (British and United States)"
                        :leeward-islands "Leeward Islands"
                        :turks-caicos    "Turks and Caicos Islands"}
                       "South Pacific"
                       {:tahiti          "Tahiti"}}
           :price     300}})

(def header
  {:css  [[:body
           [:header {:margin-top  "1em"
                     :display     :flex
                     :overflow    :hidden
                     :gap         "0.5em"
                     :align-items "last baseline"
                     :color       "rgb(var(--bold-foreground))"
                     :text-shadow "0.05em 0.1em 0.5em rgb(var(--bold-background))"}
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
                        [:h3 {:display :none}]]])]
   :body [[:header.body-width
           [:h1 "PopAI"]
           [:h2 "The ultimate boating companion"]
           [:div]
           [:h3
            [:span "Keep your hands on the helm and eyes on the water"]
            [:span "Use the power of your voice to manage your boating experience"]]]]})

(def hero
  (let [loaded      {:opacity   1
                     :transform :none}
        mask-opacity 0.85]
    {:css      [[:.hero {:width          "100%"
                         :position       :fixed
                         :opacity        0
                         :top            0
                         :z-index        -2
                         :transform      "translateY(20px)"
                         :transition     "opacity 0.8s linear, transform 0.8s ease"
                         :mask-image     "linear-gradient(to bottom,black 70%,transparent)"
                         :mask-composite :intersect
                         :mask-size      "100% 100%"}]
                [:.dark {:visibility "var(--dark-visibility)"}]
                [:.light {:visibility "var(--light-visibility)"}]
                [:.loaded loaded]
                [:.hero-mask {:height      "calc(min(70vw, 50vh))"
                              :background  (str
                                            "linear-gradient(to bottom, transparent 85%, rgba(var(--background),"
                                            mask-opacity
                                            "))")
                              :z-index     -1}]
                [:.over-hero {:background  (str "rgba(var(--background), " mask-opacity ")")}]]
     :noscript [[:.hero loaded]]
     :body     [[:img.hero.light
                 {:src    "/popai-hero-background-light.jpg"
                  :alt    "Looking over the bow of a boat sailing in the San Francisco bay, city in the background"
                  :onload "this.classList.add('loaded')"}]
                [:img.hero.dark
                 {:src    "/popai-hero-background-dark.jpg"
                  :alt    "Looking over the bow of a boat sailing in the San Francisco bay, sunset in the background"
                  :onload "this.classList.add('loaded')"}]
                [:div.hero-mask.full-width]]}))

(def get-to-know
  {:css  [[:.get-to-know {:margin "3em 0"}
           [:h1 {:margin  0
                 :display :inline}]
           [:h2 {:font-size-adjust "0.3"
                 :padding-left     "0.4em"
                 :display          :inline}]
           [:div {:display :inline}]
           [:.emphasis {:font-weight     :bold
                        :font-style      :italic
                        :text-decoration :underline}]]]
   :body [[:div.over-hero.full-width
           [:div.get-to-know.body-width
            [:div [:h1 "Get to know PopAI"]]
            [:div [:h2 "pronounced " [:q "Popeye"]]]
            [:p "PopAI is a voice-controlled boating assistant. It is part guide , part mechanic, part instructor , and part crew member."]
            [:p "PopAI is a mobile app designed to run seamlessly on your phone or tablet, and works perfectly with your favorite hands-free headset. It&rsquo;s your smart sailing companion-always ready when you need it. Whether you&rsquo;re a seasoned skipper or just starting out, PopAI gives you instant access to information tailored to your boat, your trip, and essential maritime rules and regulations."]
            [:p "No more flipping through soggy manuals, squinting at screens in the dark, or trying to remember how to calculate scope ratio while juggling conversations with your kids. PopAI takes the stress out of sailing, helping turn your trip into a memory you&rsquo;ll cherish - and maybe even the start of a beloved tradition with friends and family."]]]]})

(defn card-script [id]
  [:script
   (str "document.getElementById('" id "').addEventListener('toggle', (event) => {"
        "shownfn = event.target.matches(':popover-open')"
        "? (prompt) => prompt.classList.add('shown')"
        ": (prompt) => prompt.classList.remove('shown');"
        "event.target.querySelectorAll('.prompt').forEach(shownfn);"
        "});")])

(defn card [{:keys [title subtitle intro image details]}]
  (let [image-aspect-ratio "500/630"
        modal-id (str/join "-" (flatten
                                ["modal"
                                 (-> title
                                     (str/lower-case)
                                     (str/split #" "))]))]
    {:css      [[:.card {:display        :flex
                         :flex-direction :column
                         :gap            "1.2em"
                         :padding        "1em"
                         :border         0
                         :border-radius  "1em"
                         :background     "rgb(var(--bold-background))"
                         :color          "rgb(var(--bold-foreground))"
                         :cursor         :pointer
                         :transition     "transform 0.2s ease-out"
                         :width          "calc(var(--max-body-width)/4)"}
                 [:.topbar {:display               :grid
                            :width                 "100%"
                            :grid-template-columns "1fr auto"}]
                 [:h3 :h4 {:text-align :start
                           :margin     0}]
                 [:h3     {:font-size "1.8em"}]
                 [:h4     {:font-size "1em"}]
                 [:img    {:width         "100%"
                           :aspect-ratio  image-aspect-ratio
                           :border-radius "0.5em"}]
                 [:.space {:flex-grow 1}]]
                [:.card:hover {:transform "scale(1.03)"}]
                [:dialog.feature {:width              "100%"
                                  :height             "100%"
                                  :border             0
                                  :background         :transparent
                                  :animation-duration "0.3s"}
                 [:.content {:margin        "auto"
                             :max-width     "calc(min(80ch, 90vw))"
                             :padding       "2em"
                             :border-radius "1em"
                             :text-align    :start
                             :color         "rgb(var(--foreground))"
                             :background    "rgb(var(--background))"}
                  [:svg.close {:grid-column 2}]
                  [:.section {:border        "solid thin rgba(var(--foreground), 0.3)"
                              :border-radius "0.5em"
                              :padding       "0 1em"
                              :margin        "3em 0 0"}]
                  [:h1 {:text-align :center
                        :margin-top 0
                        :font       "italic 3em Arial, san-serif"}]
                  [:p.intro {:font-size "1.4em"
                             :margin    0}]
                  [:span.heading {:font-weight :bold
                                  :font-style  :italic}]
                  [".section > *" {:font-size "1.1em"}]
                  [:.prompt       {:display      :block
                                   :font         "italic 1.8em Arial, san-serif"
                                   :padding-top  "0.8em"
                                   :margin       "0.5em"
                                   :transition   "opacity 1s linear, transform 1s ease-out"}]
                  [:.prompt.left  {:margin-right "5ch"
                                   :text-align   :left
                                   :opacity      0.4
                                   :transform    "translateX(-0.3ch)"}]
                  [:.prompt.right {:margin-left  "5ch"
                                   :text-align   :right
                                   :opacity      0.4
                                   :transform    "translateX(0.3ch)"}]
                  [:.prompt.shown {:opacity      1
                                   :transform    :none}]]]
                ["dialog.feature::backdrop" {:backdrop-filter "blur(10px)"}]]
     :noscript [[:dialog.feature
                 [:.content
                  [:.prompt.left :.prompt.right {:opacity   1
                                                 :transform :none}]]]]
     :body     [[:button.card {:type          :button
                               :popovertarget modal-id}
                 [:div.topbar
                  [:h3 [:i title]]
                  [:svg {:width   24
                         :height  24
                         :viewBox "0 0 24 24"
                         :fill    :none
                         :xmlns   "http://www.w3.org/2000/svg"}
                   [:rect {:x            3
                           :y            3
                           :width        18
                           :height       18
                           :rx           2
                           :ry           2
                           :stroke       :currentColor
                           :stroke-width 2}]
                   [:path {:d               "M14 6H18V10"
                           :stroke          :currentColor
                           :stroke-width    2
                           :stroke-linecap  :round
                           :stroke-linejoin :round}]
                   [:path {:d               "M10 14L18 6"
                           :stroke          :currentColor
                           :stroke-width    2
                           :stroke-linecap  :round
                           :stroke-linejoin :round}]]]
                 [:h4 subtitle]
                 [:div.space]
                 [:img {:src image}]
                 [:dialog.feature {:id      modal-id
                                   :popover true}
                  [:div.content
                   [:div.topbar
                    [:svg.close {:xmlns           "http://www.w3.org/2000/svg"
                                 :width           24
                                 :height          24
                                 :viewBox         "0 0 24 24"
                                 :fill            :none
                                 :stroke          :currentColor
                                 :stroke-width    2
                                 :stroke-linecap  :round
                                 :stroke-linejoin :round}
                     [:line {:x1 18
                             :y1 6
                             :x2 6
                             :y2 18}]
                     [:line {:x1 6
                             :y1 6
                             :x2 18
                             :y2 18}]]]
                   [:h1 title]
                   [:p.intro intro]
                   (map (fn [{:keys [heading body prompts]}]
                          [:div.section
                           [:p [:span.heading heading] " " (map (fn [p] [:p.body p]) body)]
                           (map (fn [prompt, i]
                                  [:q.prompt {:class (if (= 0 (mod i 2))
                                                       "left"
                                                       "right")
                                              :style (g/style {:transition-duration (str (+ 500 (* 500 i)) "ms")})}
                                   prompt])
                                prompts (range))])
                        details)
                   (card-script modal-id)]]]]}))

(def features-cards
  (let [cards
        [{:title    "Cruising Guide"
          :subtitle "Boating almanac on the go"
          :image    "/card-boat.jpg"
          :intro    "... searching your local guide has never been easier. Simply ask!"
          :details
          [{:heading "Local Navigation"
            :body    ["Local weather patterns and seasonal considerations, local rules and regulations, fuel docks and provisioning spots, customs and immigration procedures. PopAI is there to help make your boating experience smooth and stress free."]
            :prompts ["What are the predominant winds here?"
                      "Can we anchor in Cam Bay National Park?"
                      "How do I clear customs in Tortola?"]}
           {:heading "Marinas, Anchorages and Points of Interest"
            :body    ["Planning your day has never been easier. Simply tell PopAI what activities you want to do and it will suggest areas around you where you can do those."]
            :prompts ["Where do I snorkel to see Manta rays?"
                      "Where is a child friendly beach?"
                      "Which marina has fuel, ice, and hot showers?"]}
           {:heading "Off The Water Insights"
            :body    ["From the best restaurants and bars, to where to get groceries and services in town, PopAI can help."]
            :prompts ["Where do I do laundry?"
                      "Find a children's playground?"]}]}
         {:title    "Boat Mechanic"
          :subtitle "Manuals, diagrams, schematics and troubleshooting"
          :image    "/card-engine.jpg"
          :intro    "... 90% of the time anybody can fix things on the boat with a little bit of help!"
          :details
          [{:heading "Know Your Boat Inside and Out"
            :body    ["PopAI gives you unprecedented confidence and clarity by putting everything about your charter boat at your fingertips. From the owner's manuals to professional-grade engine diagrams, you'll have instant access to:"
                      [:ul
                       [:li "Propulsion system details, fuel tank capacity, engine consumption charts"]
                       [:li "Electrical and water system schematics"]
                       [:li "Hot water configurations"]
                       [:li "AC diagrams and instructions"]
                       [:li "Black and grey water tank types, locations, capacities, and maintenance instructions"]
                       [:li "Running and standing rigging diagrams, explanations and load tables"]
                       [:li "Sails' load charts, polar diagrams, anchoring, hoisting and reefing instructions"]]
                      "Whether you're troubleshooting or just getting familiar before you cast off, PopAI makes sure you're fully prepared. Once on board you can <b>just ask about any system</b> and <b>PopAI will give you an instant reference to the boat documentation</b>."]
            :prompts ["What is the fuel capacity?"
                      "What is the cruising RPM?"]}
           {:heading "Operation Instructions"
            :body    ["PopAI will walk you through step by step instructions on how to do specific actions on the boat."]
            :prompts ["How do I empty the black water?"
                      "Should I sail with the engine in gear?"]}
           {:heading "Smart Troubleshooting"
            :body    ["PopAI helps guide you through general diagnostic steps when issues arise on board. While boat systems can vary or evolve over time, PopAI provides practical, tailored advice to help you pinpoint the problem &mdash;whether it is electrical, mechanical, or plumbing-related. Even if your system differs slightly from the manual, PopAI can assist with identifying likely causes and recommending next steps to get you back underway."]
            :prompts ["Why are the running lights off?"
                      "Why is the water pump turning on when no one is using water?"]}
           {:heading "Step-by-step Fix It Yourself"
            :body    ["PopAI will provide basic do-it-yourself instructions for simple repairs on the boat. <b>90% of the time, you can fix the problem yourself</b> with a bit of knowledge, calm thinking, and a few basic tools."]
            :prompts ["How do I clean the water strainer?"
                      "How can I fix the dinghy's flooded carburetor?"
                      "How do I change the windlass breaker?"]}]}
         {:title    "Sailing Instructor"
          :subtitle "Instant reference, rules and regulations"
          :image    "/card-textbook.jpg"
          :details
          [{:heading "Rusty Knowledge Fret No More!"
            :body    ["One of the biggest fears of charters is rusty or outdated knowledge. The majority of boat charterers go on a boat a few times per year. It is way too easy to forget all the processes and procedures one is expected to know chartering. Now you can simply ask PopAI and you will get a step-by-step reminders."]
            :prompts ["How do you <q>heave to</q>?" 
                      "How do you do a Med mooring, step by step?"]}
           {:heading "Never Forget a Boat Term"
            :body    ["From the least experienced sailor to the most experienced captain, we all sometimes need a reminder on boat terms."]
            :prompts ["What is the name of the metal plate that attaches the shrouds to the hull?"
                      "How many fathoms are in a shackle?"]}
           {:heading "Instant Reference To All Rules and Regulations"
            :body    ["Instant reference to all International Regulations for Preventing Collisions at Sea (COLREGS) and other conventions by the International Maritime Organization, US Coast Guard and other navigation regulating bodies."]
            :prompts ["What do three short horn blasts mean?"
                      "What vessel has two white lights and a yellow light?"]}
           {:heading "Local Laws"
            :body    ["There are national, state and local laws that all must be followed when operating vessels. <q>Officer, I did not know</q> will probably not avoid a $2000 fine for violating the No Discharge Zone regulations. PopAI is here to help."]
            :prompts ["Can I discharge my black water tanks here?"
                      "Can I anchor at the Rhone Marine Park?"
                      "Do I need to quarantine my dog in when visiting Jamaica?"]}]}
         {:title    "Crew Member"
          :subtitle "Interact, control and monitor"
          :image    "/card-glenn.jpg"
          :intro    "Connect PopAI to your boat's WIFI network and turn your boat into a crew member!"
          :details
          [{:heading "Access All Boat Data With Your Voice"
            :body    ["PopAI connects to your boat's Wi-Fi data network and allows you to query all data available on the network using your voice. You no longer have to make the trip to a MFD or fight with screen glare or brightness just to get the depth. You can keep your eyes on the water and focus on steering while having full access to all the instruments' data."]
            :prompts ["What is the boat speed?"
                      "What is the true wind speed?"
                      "What is the engine RPM?"]}
           {:heading "Simplify and Automate Checklists"
            :body    ["Departure and arrival checklists can feel like they take forever &mdash; there are so many steps, it's almost like you need a checklist just to manage your checklists. That's where PopAI comes in. It automates much of the process, saving you time and reducing the risk of oversight. PopAI can create, remember, and manage your checklists for you &mdash; and even handle many of the tasks itself. From checking fuel and oil levels to monitoring water tanks, battery bank status, lights, instruments, GPS, radar, AIS, and more, PopAI takes care of the details so you can focus on the journey."]
            :prompts ["Are we ready to go?"
                      "Walk me through the anchoring checklist."]}
           {:heading "Voice Control Your Boat"
            :body    ["PopAI allows you to control with your voice almost any functionality that you can control with you chart plotter display or MFD."]
            :prompts ["Turn on night mode on the instruments."
                      "Turn up the brightness."]}
           {:heading "On Watch"
            :body    ["PopAI keeps watch 24 hours a day. It constantly monitors your boat and alerts you when something unusual happens."]
            :prompts ["Notification: The water tank is empty and the water pump is running continuously."
                      "Notification: Your house batteries are down to 15% charge."]}]}]]
    {:css       [[:.cards {:overflow-x :auto}]
                 [:.cards-slide {:display :flex
                                 :gap     "2em"
                                 :padding "2em"
                                 :width   "var(--max-body-width)"}]
                 (-> cards first card :css)]
     :noscript [(-> cards first card :noscript)]
     :body     [[:div.over-hero.full-width
                 [:div.cards.body-width-no-edge
                  [:div.cards-slide
                   (apply concat (map #(-> % card :body) cards))]]]]}))

  (defn description [code]
    {:css  [[:.background {:background "rgb(var(--background))"
                           :color      "rgb(var(--foreground))"}]
            [:.description {:margin "3em 0"}]
            [:div.buy-now {:display        :flex
                           :flex-direction :column
                           :align-items    :center
                           :padding        "2em"}]
            ["div.buy-now > a" {:background      "#5050a0"
                                :font-size       "1.5em"
                                :color           :white
                                :text-decoration :none
                                :text-align      :center
                                :border          "thin #303070 solid"
                                :border-radius   "0.3em"
                                :padding         "1em"}]]
     :body [[:div.background.full-width
             [:div.description.body-width
             [:p "Are you chartering a boat and going cruising? Will you be in an area with Internet connectivity? The PopAI App is for you. The PopAI App is a lightweight app that will give you access to the latest sailing almanac for your charter destination, all manufacturer diagrams, schematics and manuals for systems on your boat. The app also acts as a sailing instructor who knows all rules and regulations, can remind you common sailing terms and can walk you through how to do most popular maneuvers, etc. It has all COLREGS, immigration rules and regulations, lights and markers, etc. With the PopAI App you will never feel unprepared for a charter again. Simply download the app on your favorite mobile device (phone or tablet and talk to it with your preferred method."]
             [:div.buy-now
              [:a {:href (route-with-code route-configure code)} "Configure PopAI"]]]]]})

(defn show-popover-on-load [id]
  {:script [(str "window.addEventListener('load', () => {"
                 "document.querySelector('#" id "').showPopover()"
                 "}, false);")]})

(defn popai [request]
  (let [code (keyword (:code request))]
    (if (and code (code targets))
      (page/from-components nil [page/base
                                 ;; (show-popover-on-load "modal-cruising-guide")
                                 header
                                 hero
                                 get-to-know
                                 features-cards
                                 (description code)
                                 about/footer])
      (resp/redirect "/"))))


(def almanac-request
  (let [time-frames [          [2025 2], [2025 3], [2025 4],
                     [2026 1], [2026 2], [2026 3], [2026 4],
                     [2027 1], [2027 2], [2027 3], [2027 4]]]
    {:css  [[:details {:margin-top "3em"}
             [:button {:margin-top "1em"}]]]
     :body [[:details
             [:summary "Don't see your destination or boat?"]
             [:p "Let us know where you're going, what you'll be sailing, and when so we can start working on the almanac. We'll let you know if they'll be ready in time for your trip and follow up once they are."]
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
              [:button {:type :submit} "Request Almanac"]]]]}))

(defn configuration [code]
  (let [config    (code targets)
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
            (first (:css almanac-request))]
     :body [[:main.body-width
             [:div#forms.soft-outline
              [:h1 "PopAI Digital Almanac"]
              [:form.sku-selection {:action (route-with-code route-checkout code)}
               [:input {:type  :hidden
                        :name  :product
                        :value :popai}]
               [:label           {:for  :location} "Location:"]
               [:select#location {:name :location}
                (map (fn [[area locations]]
                       [:optgroup {:label area}
                        (map (fn [[k v]] [:option {:value k} v]) locations)])
                     locations)]
               [:label       {:for  :boat} "Boat:"]
               [:select#boat {:name :boat}
                (map (fn [[k v]] [:option {:value k} v]) boats)]
               [:p.total "Subtotal: $" price]
               [:button {:type :submit} "Checkout"]]
             (first (:body almanac-request))]]]}))

(defn configure [request]
  (let [code (keyword (:code request))]
    (if (and code (code targets))
      (page/from-components "Configure PopAI" [page/base
                                               header
                                               (configuration code)
                                               about/footer])
      (resp/redirect "/"))))

(defn thank-you [&{:keys [location boat]}]
  (let [functions ["Cruising Guide"
                   "Boat Mechanic"
                   "Sailing Instructor"
                   "Crew Member"]
        survey [{:name     :mostIntriguing
                 :question "Of the four categories of functions, which did you find most intriguing?"
                 :answers  functions}
                {:name     :leastIntriguing
                 :question "Of the four categories of functions, which did you find least intriguing?"
                 :answers  functions}
                {:name     :offline
                 :question "Are you interested in a version of the product that can work entirely offline?"
                 :answers  ["Yes"
                            "No"
                            "This product requires an Internet connection?"]}
                {:name     :remote
                 :question "Are you interested in the capability to monitor and control your boat remotely?"
                 :answers  ["Yes"
                            "No"]}]]
    {:css  [[:form.survey {:margin                "4em auto 2em"
                           :display               :grid
                           :grid-template-columns "1fr auto 1fr"
                           :row-gap               "1em"}
             [:label.question {:grid-column "1 / -1"}]
             [:select {:grid-column   2
                       :margin-bottom "2em"
                       :width "100%"}]
             [:div.emailAddress {:display       :flex
                                 :grid-column   "1 / -1"
                                 :margin-bottom "2em"
                                 :max-width     "60ch"}
              [:input {:flex-grow 1}]]
             [:button {:grid-column  "span 3"
                       :justify-self :center
                       :padding      "0.3em 1em"}]]]
     :body [[:main.body-width
             [:p "Thank you for your interest, but unfortunately, this isn't a real product yet. We really appreciate you giving us your attention and we hope we haven't caused any disruption with our experiment."]
             [:p "As a thank-you, we'd like to offer you a coupon for 75% off. Hopefully the next time you're sailing in" location "or you're on a" (str boat "," "we'll have an almanac ready to go. Just give us an email address and we'll send you a message when it's ready to go. Use the same email address at checkout and the discount will automatically be applied.")]
             [:p "Oh, and if you wouldn't mind, we'd love a bit of feedback on the product before you go. No worries if you'd rather skip the survey though &mdash; we'll honor the coupon either way. Thanks again!"]
             [:form.survey
              (apply concat (map (fn [&{:keys [name question answers]}]
                                   [[:label.question {:for  name} question]
                                    [:select {:id   name
                                              :name name}
                                     (map (fn [v]
                                            [:option {:value v} v])
                                          (flatten ["-- Select One --"
                                                    answers]))]])
                                 survey))
              [:div.emailAddress
               [:label {:for  :emailAddress} "Email Address:"]
               [:input {:name :emailAddress
                        :type :email}]]
              [:button {:type :submit} "Submit"]]]]}))

(defn checkout [request]
  (let [code         (keyword (:code request))
        config       (when code
                       (code targets))
        locations    (:locations config)
        location-key (keyword (:location (:params request)))
        location     (when location-key
                       (location-key (reduce-kv (fn [acc k v]
                                                  (merge acc v))
                                                {}
                                                locations)))
        boats        (:boats config)
        boat-key     (keyword (:boat (:params request)))
        boat         (when boat-key
                       (boat-key boats))]
    (if (and location boat)
      (page/from-components
       "Checkout"
       [page/base
        header
        (thank-you {:location location
                    :boat     boat})
        about/footer])
      {:status 401
       :headers page/headers
       :body "invalid product configuration"})))

(defn request-almanac [request]
  (let [params  (codec/form-decode (:query-string request))
        storage (db/storage)
        conn    (db/connect storage :requested-almanacs)]
    (db/insert-requested-almanac (into {:conn conn} (map (fn [[k v]] [(keyword k) v]) params)))))
