(ns net.sailvision.www.store
  (:require
   [clojure.java.io :as io]
   [clojure.string :as str]
   [net.sailvision.www.about :as about]
   [net.sailvision.www.db :as db]
   [net.sailvision.www.page :as page]
   [net.sailvision.www.util :refer [inline]]
   [garden.core :as g]
   [ring.util.response :as resp]))

(def route-home "/store/popai/:code")
(def route-configure (str route-home "/configure"))
(def route-checkout (str route-home "/checkout"))
(def route-request-almanac (str route-home "/request-almanac"))
(def route-discount (str route-home "/discount"))
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
            [:p "PopAI (pronounced " [:q "Popeye"] ") is a voice-controlled boating assistant which runs on your phone or tablet and integrates with the systems on your boat. It&rsquo;s an interactive boating companion &mdash; always ready when you need it."]
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
                      "Can I fish here?"]}
           {:heading "Shore Support"
            :body    [:p "Easily find fuel, groceries, and other services ashore."]
            :prompts ["Which marinas nearby have laundry?"
                      "Where can I get fuel?"]}]}
         {:title    "Boat Mechanic"
          :subtitle "Manuals, diagrams, schematics and troubleshooting"
          :image    "/card-mechanic.jpg"
          :intro    "With a little know-how, you can handle 90% of boat repairs yourself &mdash; and probably save the day in the process."
          :details
          [{:heading "Know Your Boat"
            :body    [:p "Gain confidence and clarity with information and instructions derived from the owner&rsquo;s manual, engine diagrams, and maintenance guides."]
            :prompts ["What is our fresh water capacity?"
                      "What is the cruising RPM?"]}
           {:heading "Interactive Troubleshooting"
            :body    [:p "Get help with general diagnostic steps when issues arise on board and get assistance with identifying likely causes and recommending next steps to get you back underway."]
            :prompts ["Why is the water pump still running?"
                      "Why are the running lights off?"]}
           {:heading "Fix It Yourself"
            :body    [:p "Instructions, tailored to your skill level, for various repairs on the boat. "]
            :prompts ["How do I reset the windlass breaker?"
                      "How do I bleed the fuel lines?"]}]}
         {:title    "Maritime Reference"
          :subtitle "Instant access to rules and regulations"
          :image    "/card-instructor.jpg"
          :intro    "A quick reminder is usually all it takes to get back into the swing of things."
          :details
          [{:heading "Boating Terms"
            :body    [:p "A glossary of terms and phrases, right in your ear."]
            :prompts ["What&rsquo;s the metal plate that attaches the shrouds to the hull?"
                      "How many fathoms are in a shackle?"]}
           {:heading "Rules and Regulations"
            :body    [:p "Quick and easy access to all regulations, domestic and international, and other conventions."]
            :prompts ["What does three short horn blasts mean?"
                      "Do I need quarantine my dog when visiting Jamaca?"]}
           {:heading "General Knowledge"
            :body    [:p "Get answers about boat operations, radio protocols, signaling, and more."]
            :prompts ["Which VHF channels should I monitor?"
                      "What does a white flare mean?"]}]}
         {:title    "Crew Member"
          :subtitle "Interact, control and monitor"
          :image    "/card-crew.jpg"
          :intro    "Connect to the onboard network and turn your boat into a crew member."
          :details
          [{:heading "Boat Systems"
            :body    [:p "Connect to your boat&rsquo;s Wi-Fi data network and query the systems aboard using your voice &mdash; above or below deck."]
            :prompts ["What is the name of the boat ahead?"
                      "What is the depth?"]}
           {:heading "On Watch"
            :body    [:p "Keep a constant eye on your boat and be alerted when something unusual happens."]
            :prompts ["Let me know when the water tank is half empty."
                      "Tell me if the depth gets below ten feet."]}
           {:heading "Tips and Guidance"
            :body    [:p "Close the loop and improve your sailing performance with feedback guided by your sails&rsquo; load charts and polar diagrams."]
            :prompts ["How's my speed look for these conditions?"
                      "Am I heeling too much?"]}]}]]
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
                   (inline (map #(-> % card :body) cards))]]]]}))

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
        survey [{:id       :most-interesting
                 :question "Which of the functions did you find most interesting?"
                 :answers  functions}
                {:id       :offline
                 :question "Are you interested in a version of the product that can work entirely offline?"
                 :answers  ["Yes"
                            "No"]}
                {:id       :remote
                 :question "Are you interested in the capability to monitor and control your boat remotely?"
                 :answers  ["Yes"
                            "No"]}]]
    {:css  [[:main.side-by-side {:display   :flex
                                 :flex-wrap :wrap}]
            ["main.side-by-side > .side" {:margin "0 2em"
                                          :flex   "1 1 50ch"}]
            [:form.email {:margin                "2em auto"
                          :display               :grid
                          :grid-template-columns "auto 1fr"
                          :row-gap               "1em"
                          :width                 :fit-content}
             [:p {:grid-column "span 2"
                  :margin-top  0}]
             [:button {:grid-column "span 2"
                       :justify-self :center
                       :padding     "0.3em 1em"}]]
            [:form.survey {:margin                "2em auto"
                           :display               :grid
                           :grid-template-columns "auto 1fr"
                           :row-gap               "0.5em"
                           :column-gap            "0.5em"
                           :width                 :fit-content}
             [:p {:grid-column "span 2"
                  :margin-top  0}]
             [:label.question {:grid-column "span 2"}]
             ["label.question:not(:first-child)" {:margin-top "1em"}]
             [:select {:grid-column "span 2"
                       :margin-bottom "3em"
                       :width "100%"}]
             [:textarea {:grid-column   "span 2"}]
             [:label {:grid-column 2}]
             [:input {:grid-column 1}]
             [:button {:margin-top "0.5em"
                       :grid-column  "span 2"
                       :justify-self :center
                       :padding      "0.3em 1em"}]]]
     :body [[:main.body-width.side-by-side
             [:p "Thank you for your interest, but unfortunately, this isn&rsquo;t a real product yet. We appreciate your attention and hope we haven&rsquo;t caused any disruption with our experiment."]
             [:div.side
              [:form.email.soft-outline {:action (route-with-code route-discount code)
                                         :method :post}
               (if (and boat location)
                [:p "As a thank-you, we&rsquo;d like to offer you " [:b "75% off your first purchase"] ". The next time you&rsquo;re sailing in " location " or you&rsquo;re on a " boat ", we&rsquo;ll have an almanac ready to go. Just give us an email address and we&rsquo;ll send you a message when it&rsquo;s ready to go. Use the same email address at checkout and the discount will automatically be applied."]
                [:p "As a thank-you, we&rsquo;d like to offer you " [:b "75% off your first purchase"] ". Give us an email address and we&rsquo;ll send you a message when we&rsquo;re ready to go. Use the same email address at checkout and the discount will automatically be applied."])
               [:input {:type  :hidden
                        :name  :store-code
                        :value code}]
               [:label {:for  :emailAddress} "Email Address:"]
               [:input {:name :emailAddress
                        :type :email}]
               [:button {:type :submit} "Get Discount"]]]
             [:div.side
              [:form.survey.soft-outline {:action (route-with-code route-survey code)
                                          :method :post}
               [:p "We&rsquo;d love a bit of feedback on the product before you go. No worries if you&rsquo;d rather skip the survey though &mdash; we&rsquo;ll honor the discount either way. Thanks again!"]
               (inline (map (fn [&{:keys [id question answers]}]
                              (let [other-id (keyword (str (name id) "-other"))]
                                [[:label.question {:for id} question]
                                 (inline (map (fn [answer]
                                                (let [option-id (str/join "-" (-> answer
                                                                                  (str/lower-case)
                                                                                  (str/split #" ")))]
                                                  [[:input {:type  :radio
                                                            :name  option-id
                                                            :id    id
                                                            :value id}]
                                                   [:label {:for id} answer]]))
                                              answers))
                                 [:input {:type :radio
                                          :name  id
                                          :id    other-id
                                          :value "other"}]
                                 [:div
                                  [:label {:for  other-id} "Other"]
                                  [:input {:name other-id}]]]))
                            survey))
               [:label.question {:for :additionalComments} "Additional comments:"]
               [:textarea#additionalComments {:name :additionalComments}]
               [:button {:type :submit} "Submit"]]]]]
     :script [(slurp (io/resource "discount-signup.js"))]}))

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

(defn discount [request]
  (if-let [[code _config] (validate request)]
    (let [storage (db/storage)
          conn    (db/connect storage :discount-signups)
          address (:emailAddress (:params request))]
      (db/insert-discount-signup {:conn          conn
                                  :store-code    (name code)
                                  :email-address address})
      (page/from-components
       "Discount Signup"
       [page/base
        page/header
        {:body [[:main.body-width
                 [:p "Got it! Thanks again."]]]}
        about/footer]))
      (resp/redirect "/")))

(defn submit-survey [request]
  (if-let [[code _config] (validate request)]
    (let [storage (db/storage)
          conn    (db/connect storage :survey-responses)
          blob    (pr-str (merge {:code (name code)}
                                 (:params request)))]
      (db/insert-survey-response {:conn conn
                                  :blob blob})
      (page/from-components
       "Survey"
       [page/base
        page/header
        {:body [[:main.body-width
                 [:p "Thank you for your feedback!"]]]}
        about/footer]))
      (resp/redirect "/")))
