(ns net.sailvision.www.store
  (:require
   [clojure.string :as str]
   [net.sailvision.www.about :as about]
   [net.sailvision.www.db :as db]
   [net.sailvision.www.page :as page]
   [garden.core :as g]
   [ring.util.codec :as codec]
   [ring.util.response :as resp]))

(def route-home "/store/popai/:code")
(def route-configure (str route-home "/configure"))
(def route-checkout (str route-home "/checkout"))
(def route-request-almanac (str route-home "/request-almanac"))
(def route-survey (str route-home "/survey"))

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
           :price     300}
   :mango {}
   :1ayjd {}
   :1vhji {}
   :1nzxl {}
   :1rxah {}
   :1ojpw {}
   :1uvac {}
   :1xppc {}
   :1hhey {}
   :1ruxy {}
   :1hxpd {}
   :1djii {}
   :1pdqs {}
   :1jxop {}
   :1ovwc {}
   :1eblo {}
   :1psbx {}
   :1bgpt {}
   :1xjjv {}
   :1kksd {}
   :1nbnj {}
   :1mkrj {}
   :1khbf {}
   :1fnji {}
   :1jamh {}
   :1smob {}
   :1pajn {}
   :1dqhp {}
   :1kcnz {}
   :1hqqo {}
   :1fovf {}
   :1rvxb {}
   :1lpzj {}
   :1urcs {}
   :1gupz {}
   :1terr {}
   :1ddor {}
   :1bnps {}
   :1rsrs {}
   :1sobx {}
   :1pflq {}
   :1ajyk {}
   :1zevq {}
   :1eyey {}
   :1hlis {}
   :1afqs {}
   :1emdb {}
   :1bncy {}
   :1yhvz {}
   :1donk {}
   :1wcnc {}
   :1beyc {}
   :1knjj {}
   :1wlda {}
   :1boie {}
   :1wzir {}
   :1utcg {}
   :1zexj {}
   :1kfny {}
   :1yypb {}
   :1jrto {}
   :1hwza {}
   :1yunx {}
   :1gglp {}
   :1jnog {}
   :1dopu {}
   :1smaj {}
   :1seja {}
   :1xzuy {}
   :1rdyf {}
   :1obwb {}
   :1rpsd {}
   :1uokj {}
   :1brka {}
   :1wmef {}
   :1cddg {}
   :1rzsg {}
   :1zxsc {}
   :1sphl {}
   :1lvql {}
   :1mcpd {}
   :1amcw {}
   :1dnvv {}
   :1gmvw {}
   :1tpfp {}
   :1cfjg {}
   :1fcml {}
   :1envx {}
   :1xlel {}
   :1rjza {}
   :1rbrw {}
   :1ixeo {}
   :1fqap {}
   :1aixh {}
   :1drkr {}
   :1ofoh {}
   :1uscz {}
   :1slxl {}
   :1dijp {}
   :1bdvq {}
   :1mvyh {}
   :1jbch {}
   :1wdti {}
   :1qaag {}
   :1ttxc {}
   :1wpai {}
   :1ohvg {}
   :1wabw {}
   :1jatp {}
   :1cxmm {}
   :1kbsu {}})

(defn validate [request]
  (let [code   (keyword (:code request))
        config (when code
                 (code targets))]
    (when (and code config)
      [code config])))

(def hero
  (let [loaded      {:opacity   1
                     :transform :none}
        mask-opacity 0.85]
    {:css      [[:body
                 [:header {:color       "#f8f8f8"
                           :text-shadow "0.05em 0.1em 0.5em #404040"}]]
                [:.hero {:width          "100%"
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
                [:.hero-mask {:height      "calc(min(70vw, 75vh))"
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
  {:body [[:div.over-hero.full-width
           [:div.get-to-know.body-width
            [:h1 "Get to know PopAI"]
            [:p "PopAI (pronounced " [:q "Popeye"] ") is a voice-controlled boating assistant which runs on your phone or tablet and integrates with the systems on your boat. It&rsquo;s your smart sailing companion &mdash; always ready when you need it."]
            [:p "Whether you&rsquo;re a seasoned skipper or just starting out, PopAI gives you instant access to information tailored to your boat, your trip, and essential maritime rules and regulations."]
            [:p "PopAI takes the stress out of sailing, helping turn your trip into a memory you&rsquo;ll cherish - and maybe even the start of a beloved tradition with friends and family."]
            [:br]
            [:p "Click or tap on the cards below to learn more."]]]]})

(defn card-modal-show [id]
  (str "let modal = document.getElementById('" id "');"
       "modal.showModal();"
       "modal.querySelectorAll('.prompt').forEach((prompt) => prompt.classList.add('shown'));"))

(defn card-modal-hide [id]
  (str "let modal = document.getElementById('" id "');"
       "modal.close();"
       "modal.querySelectorAll('.prompt').forEach((prompt) => prompt.classList.remove('shown'));"))

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
                 [:h3 :h4 {:text-align :start
                           :margin     0}]
                 [:h3     {:font-size "1.8em"}]
                 [:h4     {:font-size "1em"}]
                 [:img    {:width         "100%"
                           :aspect-ratio  image-aspect-ratio
                           :border-radius "0.5em"}]
                 [:.space {:flex-grow 1}]]
                [:.topbar {:display               :grid
                           :width                 "100%"
                           :grid-template-columns "1fr auto"}]
                [:.card:hover {:transform "scale(1.03)"}]
                [:.modal {:max-width          "100vw"
                          :max-height         "100vh"
                          :border             0
                          :margin             0
                          :padding            "5vh 5vw"
                          :background         :transparent
                          :animation-duration "0.3s"}
                 [:.content {:margin        "auto"
                             :max-width     "calc(min(80ch, 100%))"
                             :padding       "2em"
                             :border-radius "1em"
                             :text-align    :start
                             :color         "rgb(var(--foreground))"
                             :background    "rgb(var(--background))"}
                  [:svg.close {:grid-column 2
                               :cursor      :pointer}]
                  [:.section {:border        "solid thin rgba(var(--foreground), 0.3)"
                              :border-radius "0.5em"
                              :padding       "0 1em"
                              :margin        "3em 0 0"}]
                  [:h1 {:text-align :left
                        :margin-top 0
                        :font       "italic clamp(2em, 1em + 4vw, 3em) Arial, san-serif"}]
                  [:p.intro {:font-size "clamp(1.2em, 0.2em + 3vw, 1.4em)"
                             :margin    0}]
                  [:span.heading {:font-weight :bold
                                  :font-style  :italic}]
                  [".section > *" {:font-size "1.1em"}]
                  [:.prompt       {:display      :block
                                   :font         "italic clamp(1.3em, 0.3em + 3vw, 1.8em) Arial, san-serif"
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
                [".modal::backdrop" {:backdrop-filter "blur(10px)"}]
                ["body:has(.modal[open])" {:overflow :hidden}]]
     :noscript [[:.modal
                 [:.content
                  [:.prompt.left :.prompt.right {:opacity   1
                                                 :transform :none}]]]]
     :body     [[:button.card {:type    :button
                               :onclick (card-modal-show modal-id)}
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
                 [:img {:src image}]]
                [:dialog.modal {:id      modal-id
                                :onclick (card-modal-hide modal-id)}
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
                          [:p [:span.heading heading]]
                          body
                          (map (fn [prompt, i]
                                 [:q.prompt {:class (if (= 0 (mod i 2))
                                                      "left"
                                                      "right")
                                             :style (g/style {:transition-duration (str (+ 500 (* 500 i)) "ms")})}
                                  prompt])
                               prompts (range))])
                       details)]]]}))

(def features-cards
  (let [cards
        [{:title    "Cruising Guide"
          :subtitle "Boating almanac on the go"
          :image    "/card-guide.jpg"
          :intro    "Searching your local guide has never been easier."
          :details
          [{:heading "Planning and Preparation"
            :body    [:p "Get help preparing and running through checklists for gear, travel documents, and provisioning."]
            :prompts ["What's next on the pre-cruise checklist?"
                      "How do I clear customs in Tortola?"]}
           {:heading "Local Navigation"
            :body    [:p "Double check local weather patterns and seasonal considerations, as well as local rules and regulations."]
            :prompts ["Can we anchor in Cam Bay National Park?"
                      "How are the tides at night?"]}
           {:heading "Shore Support"
            :body    [:p "Easily find fuel, groceries, and other services ashore."]
            :prompts ["Which marinas nearby have laundry?"
                      "Where can I get fuel?"]}]}
         {:title    "Boat Mechanic"
          :subtitle "Manuals, diagrams, schematics and troubleshooting"
          :image    "/card-mechanic.jpg"
          :intro    "With a bit of know-how, you can tackle 90% of repairs to your boat; likely saving your day."
          :details
          [{:heading "Know Your Boat"
            :body    [:p "Gain confidence and clarity with information and instructions derived from the owner&rsquo;s manual, engine diagrams, and maintenance guides."]
            :prompts ["What is our fresh water capacity?"
                      "What is the cruising RPM?"]}
           {:heading "Smart Troubleshooting"
            :body    [:p "Get help with general diagnostic steps when issues arise on board and get assistance with identifying likely causes and recommending next steps to get you back underway."]
            :prompts ["Why does the water pump keep turning on and off?"
                      "Why are the running lights off?"]}
           {:heading "Fix It Yourself"
            :body    [:p "Instructions, tailored to your skill level, for various repairs on the boat. "]
            :prompts ["How do I reset the windlass breaker?"
                      "How do I bleed the fuel lines?"]}]}
         {:title    "Sailing Instructor"
          :subtitle "Instant reference, rules and regulations"
          :image    "/card-instructor.jpg"
          :details
          [{:heading "Rusty Knowledge Fret No More!"
            :body    [:p "One of the biggest fears of charters is rusty or outdated knowledge. The majority of boat charterers go on a boat a few times per year. It is way too easy to forget all the processes and procedures one is expected to know chartering. Now you can simply ask PopAI and you will get a step-by-step reminders."]
            :prompts ["How do you <q>heave to</q>?"
                      "How do you do a Med mooring, step by step?"]}
           {:heading "Never Forget a Boat Term"
            :body    [:p "From the least experienced sailor to the most experienced captain, we all sometimes need a reminder on boat terms."]
            :prompts ["What is the name of the metal plate that attaches the shrouds to the hull?"
                      "How many fathoms are in a shackle?"]}
           {:heading "Instant Reference To All Rules and Regulations"
            :body    [:p "Instant reference to all International Regulations for Preventing Collisions at Sea (COLREGS) and other conventions by the International Maritime Organization, US Coast Guard and other navigation regulating bodies."]
            :prompts ["What do three short horn blasts mean?"
                      "What vessel has two white lights and a yellow light?"]}
           {:heading "Local Laws"
            :body    [:p "There are national, state and local laws that all must be followed when operating vessels. " [:q "Officer, I did not know"] " will probably not avoid a $2000 fine for violating the No Discharge Zone regulations. PopAI is here to help."]
            :prompts ["Can I discharge my black water tanks here?"
                      "Can I anchor at the Rhone Marine Park?"
                      "Do I need to quarantine my dog in when visiting Jamaica?"]}]}
         {:title    "Crew Member"
          :subtitle "Interact, control and monitor"
          :image    "/card-crew.jpg"
          :intro    "Connect PopAI to your boat&rsquo;s Wi-Fi network and turn your boat into a crew member!"
          :details
          [{:heading "Access All Boat Data With Your Voice"
            :body    [:p "PopAI connects to your boat&rsquo;s Wi-Fi data network and allows you to query all data available on the network using your voice. You no longer have to make the trip to a MFD or fight with screen glare or brightness just to get the depth. You can keep your eyes on the water and focus on steering while having full access to all the instruments&rsquo; data."]
            :prompts ["What is the boat speed?"
                      "What is the true wind speed?"
                      "What is the engine RPM?"]}
           {:heading "Simplify and Automate Checklists"
            :body    [:p "Departure and arrival checklists can feel like they take forever &mdash; there are so many steps, it&rsquo;s almost like you need a checklist just to manage your checklists. That&rsquo;s where PopAI comes in. It automates much of the process, saving you time and reducing the risk of oversight. PopAI can create, remember, and manage your checklists for you &mdash; and even handle many of the tasks itself. From checking fuel and oil levels to monitoring water tanks, battery bank status, lights, instruments, GPS, radar, AIS, and more, PopAI takes care of the details so you can focus on the journey."]
            :prompts ["Are we ready to go?"
                      "Walk me through the anchoring checklist."]}
           {:heading "Voice Control Your Boat"
            :body    [:p "PopAI allows you to control with your voice almost any functionality that you can control with you chart plotter display or MFD."]
            :prompts ["Turn on night mode on the instruments."
                      "Turn up the brightness."]}
           {:heading "On Watch"
            :body    [:p "PopAI keeps watch 24 hours a day. It constantly monitors your boat and alerts you when something unusual happens."]
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

  (defn description [code config]
    {:css  [[:.background {:background "rgb(var(--background))"
                           :color      "rgb(var(--foreground))"}]
            [:.description {:margin "3em 0"}]
            [:div.buy-now {:display        :flex
                           :flex-direction :column
                           :align-items    :center
                           :padding        "2em"}]
            ["div.buy-now > a" {:background      :black
                                :font-size       "1.75em"
                                :color           :white
                                :text-decoration :none
                                :text-align      :center
                                :border          "thin rgb(var(--foreground)) solid"
                                :border-radius   "0.5em"
                                :padding         "0.75em 2em"}]]
     :body [[:div.background.full-width
             [:div.description.body-width
              [:p "PopAI is powered by digital almanacs, customized for you and curated from numerous data sources including travel guides, government notices and publications, and local knowledge."]
              [:p "Each purchase of a digital almanac, which covers one model of boat and a particular region, grants lifetime access and includes updates for one year. Additional almanacs can be added each time you charter a new boat or travel to a new region."]
              [:div.buy-now
               [:a {:href (if (and (:boats     config)
                                   (:locations config))
                            (route-with-code route-configure code)
                            (route-with-code route-checkout code))}
                "Customize and Buy"]]]]]})

(defn show-modal-on-load [id]
  {:script [(str "window.addEventListener('load', () => {" (card-modal-show id) "}, false);")]})

(defn popai [request]
  (if-let [[code config] (validate request)]
    (page/from-components nil [page/base
                               ;; (show-modal-on-load "modal-cruising-guide")
                               page/header
                               hero
                               get-to-know
                               features-cards
                               (description code config)
                               about/footer])
    (resp/redirect "/")))


(defn almanac-request [code]
  (let [time-frames [          [2025 2], [2025 3], [2025 4],
                     [2026 1], [2026 2], [2026 3], [2026 4],
                     [2027 1], [2027 2], [2027 3], [2027 4]]]
    {:css  [[:details {:margin-top "3em"}
             [:button {:margin-top "1em"}]]]
     :body [[:details
             [:summary "Don&rsquo;t see your destination or boat?"]
             [:p "Let us know where you&rsquo;re going, what you&rsquo;ll be sailing, and when so we can start working on the almanac. We&rsquo;ll let you know if they&rsquo;ll be ready in time for your trip and follow up once they are."]
             [:form.sku-request {:action (route-with-code route-request-almanac code)
                                 :method :post}
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
            (first (:css (almanac-request code)))]
     :body [[:main.body-width
             [:div#forms.soft-outline
              [:h1 "PopAI Digital Almanac"]
              [:form.sku-selection {:action (route-with-code route-checkout code)
                                    :method :post}
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
             (first (:body (almanac-request code)))]]]}))

(defn configure [request]
  (if-let [[code _config] (validate request)]
    (page/from-components "Configure PopAI" [page/base
                                             page/header
                                             (configuration code)
                                             about/footer])
      (resp/redirect "/")))

(defn thank-you [&{:keys [location boat code]}]
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
                           :grid-template-columns "auto 1fr"
                           :row-gap               "1em"
                           :width                 :fit-content}
             [:label.question {:grid-column "span 2"}]
             [:select {:grid-column "span 2"
                       :margin-bottom "3em"
                       :width "100%"}]
             [:textarea {:grid-column   "span 2"
                         :margin-bottom "3em"}]
             [:label {:grid-column 1}]
             [:input {:grid-column 2}]
             [:button {:grid-column  "span 2"
                       :justify-self :center
                       :margin-top   "1em"
                       :padding      "0.3em 1em"}]]]
     :body [[:main.body-width
             [:p "Thank you for your interest, but unfortunately, this isn&rsquo;t a real product yet. We really appreciate you giving us your attention and we hope we haven&rsquo;t caused any disruption with our experiment."]
             (if (and boat location)
               [:p "As a thank-you, we&rsquo;d like to offer you 75% off your first purchase. The next time you&rsquo;re sailing in " location " or you&rsquo;re on a " boat ", we&rsquo;ll have an almanac ready to go. Just give us an email address and we&rsquo;ll send you a message when it&rsquo;s ready to go. Use the same email address at checkout and the discount will automatically be applied."]
               [:p "As a thank-you, we&rsquo;d like to offer you 75% off your first purchase. The next time you take a sailing trip, we&rsquo;ll have an almanac ready to go. Just give us an email address and we&rsquo;ll send you a message when it&rsquo;s ready to go. Use the same email address at checkout and the discount will automatically be applied."])
             [:p "We&rsquo;d love a bit of feedback on the product before you go. No worries if you&rsquo;d rather skip the survey though &mdash; we&rsquo;ll honor the discount either way. Thanks again!"]
             [:form.survey.soft-outline {:action (route-with-code route-survey code)
                                         :method :post}
              (apply concat (map (fn [&{:keys [name question answers]}]
                                   [[:label.question {:for name} question]
                                    [:select {:id   name
                                              :name name}
                                     (map (fn [v]
                                            [:option {:value v} v])
                                          (flatten ["-- Select One --"
                                                    answers]))]])
                                 survey))
              [:label.question {:for :additionalComments} "Additional comments:"]
              [:textarea#additionalComments {:name :additionalComments}]
              [:label {:for  :emailAddress} "Email Address:"]
              [:input {:name :emailAddress
                       :type :email}]
              [:button {:type :submit} "Submit"]]]]}))

(defn checkout [request]
  (if-let [[code config] (validate request)]
    (let [locations    (:locations config)
          location-key (keyword (:location (:params request)))
          location     (when location-key
                         (location-key (reduce-kv (fn [acc _k v]
                                                    (merge acc v))
                                                  {}
                                                  locations)))
          boats        (:boats config)
          boat-key     (keyword (:boat (:params request)))
          boat         (when boat-key
                         (boat-key boats))]
      (page/from-components
       "Checkout"
       [page/base
        page/header
        (thank-you {:location location
                    :boat     boat
                    :code     code})
        about/footer]))
    (resp/redirect "/")))

(defn request-almanac [request]
  (if-let [[_code _config] (validate request)]
    (let [_ (prn (:params request))
          params  (:params request)
          storage (db/storage)
          conn    (db/connect storage :requested-almanacs)]
      (db/insert-requested-almanac (into {:conn conn} (map (fn [[k v]] [(keyword k) v]) params)))
      (page/from-components
       "Requested Almanac"
       [page/base
        page/header
        {:body [[:main.body-width
                 [:p "Thank you for your submission. We'll let you know when we can support that configuration."]]]}
        about/footer]))
    (resp/redirect "/")))

(defn submit-survey [request]
  (if-let [[code _config] (validate request)]
    (let [storage (db/storage)
          conn    (db/connect storage :survey-responses)
          blob    (pr-str (merge {:code code}
                                 (:params request)))]
      (prn blob)
      (db/insert-survey-response {:conn conn
                                  :blob blob})
      (page/from-components
       "Survey"
       [page/base
        page/header
        {:body [[:main.body-width
                 [:p "Thank you for your submission."]]]}
        about/footer]))
      (resp/redirect "/")))
