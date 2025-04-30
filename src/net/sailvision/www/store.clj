(ns net.sailvision.www.store
  (:require
   [clojure.java.io :as io]
   [clojure.string :as str]
   [net.sailvision.www.about :as about]
   [net.sailvision.www.db :as db]
   [net.sailvision.www.page :as page]
   [net.sailvision.www.util :refer [inline long-str]]
   [garden.core :as g]
   [garden.stylesheet :as s]
   [hiccup.core :as h]
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
  (let [dummy {:boats     {:sun-odyssey-410 "Jeanneau Sun Odyssey 410"
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
        get-to-using {:feature-get-to-using true}]
    {:watermellon (merge dummy get-to-using)
     :mtklk dummy
     :ulrbt dummy
     :pwhom dummy
     :ocbto dummy
     :aocbq dummy
     :bsfqn dummy
     :ysquo dummy
     :lhztx dummy
     :xqccm dummy
     :plpgm dummy
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
     :1kbsu {}}))

(defn validate [request]
  (let [code   (keyword (:code request))
        config (when code
                 (code targets))]
    (when (and code config)
      [code config])))

(def hero
  (let [loaded       {:opacity   1
                      :transform :none}
        mask-opacity 0.85
        flyouts      [{:left  "Fix the brightness"
                       :right "Done"}
                      {:left  "Do another thing"}
                      {:left  "3"
                       :right "4"}
                      {:left  "5"
                       :right "6"}
                      {:left  "7"
                       :right "8"}
                      {:left  "9"
                       :right "10"}
                      {:left  "11"
                       :right "12"}
                      {:left  "13"
                       :right "14"}
                      {:left  "15"
                       :right "16"}
                      {:left  "17"
                       :right "18"}]
        flyout-time  6]
    {:css      (let [flyout-begin  2
                     flyout-pause  4
                     flyout-resume 8
                     flyout-end    10
                     %             #(str % "%")]
                 [[:body
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
                  [:.flyouts {:font-size "2em"
                              :margin    "4em 2em"}
                   (let [duration (* (count flyouts) flyout-time)]
                     [:.pair {:display :flex}
                      [:div {:flex-grow 1}]
                      [:q {:color       "rgb(var(--bold-foreground))"
                           :text-shadow "0.05em 0.1em 0.5em #404040"}]
                      [:q.left {:animation (str "slideInOutLeft " duration "s ease-in-out infinite both")}]
                      [:q.right {:animation (str "slideInOutRight " duration "s ease-in-out infinite both")}]])]
                  (s/at-keyframes "slideInOutLeft"
                                  [:0% {:opacity   0
                                        :transform :none}]
                                  [(% flyout-begin) {:opacity   0
                                                     :transform "translateX(-1em)"}]
                                  [(% flyout-pause) {:opacity   1
                                                     :transform :none}]
                                  [(% flyout-resume) {:opacity   1
                                                      :transform :none}]
                                  [(% flyout-end) {:opacity   0
                                                   :transform "translateX(1em)"}]
                                  [:100% {:opacity   0
                                          :transform :none}])
                  (s/at-keyframes "slideInOutRight"
                                  [:0% {:opacity   0
                                        :transform :none}]
                                  [(% (+ 1 flyout-begin)) {:opacity   0
                                                           :transform "translateX(1em)"}]
                                  [(% (+ 1 flyout-pause)) {:opacity   1
                                                           :transform :none}]
                                  [(% (+ 1 flyout-resume)) {:opacity   1
                                                            :transform :none}]
                                  [(% (+ 1 flyout-end)) {:opacity   0
                                                         :transform "translateX(-1em)"}]
                                  [:100% {:opacity   0
                                          :transform :none}])
                  [:.over-hero {:background  (str "rgba(var(--background), " mask-opacity ")")}]])
                 :noscript [[:.hero loaded]]
     :body     [[:img.hero.light
                 {:src    "/popai-hero-background-light.jpg"
                  :alt    "Looking over the bow of a boat sailing in the San Francisco bay, city in the background"
                  :onload "this.classList.add('loaded')"}]
                [:img.hero.dark
                 {:src    "/popai-hero-background-dark.jpg"
                  :alt    "Looking over the bow of a boat sailing in the San Francisco bay, sunset in the background"
                  :onload "this.classList.add('loaded')"}]
                [:div.flyouts.body-width
                 (map-indexed (fn [i {:keys [left right]}]
                                (let [delay (str (* i flyout-time) "s")]
                                  [:div.pair
                                   (when left
                                     [:q.left  {:style (g/style {:animation-delay delay})}
                                      left])
                                   [:div]
                                   (when right
                                     [:q.right {:style (g/style {:animation-delay delay
                                                                 :margin-top      "2em"})}
                                      right])]))
                              flyouts)]]}))

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

(defn style [content]
  {:style (g/style content)})

(defn topic
  ([body]
   (topic nil body))
  ([css body]
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
     {:css [(s/at-media {:prefers-color-scheme :dark}
                        [":root" {:--accent  "0 164 230"}])
            (s/at-media {:prefers-color-scheme :light}
                        [":root" {:--accent "0, 117, 164"}])
            [:.wave {:height      "1.4em"
                     :mask-size   "100% 100%"
                     :mask-repeat :no-repeat
                     :background  "rgb(var(--background))"}]
            [:.head-wave {:mask-image (datauri head-wave)
                          :margin-bottom "-1px"}]
            [:.tail-wave {:mask-image (datauri tail-wave)
                          :margin-top "-1px"}]
            [:.topic css]]
      :body [[:div.full-width.wave.head-wave]
             [:div.full-width.topic (style {:padding    "5em 0"
                                            :background "rgb(var(--background))"})
              body]
             [:div.full-width.wave.tail-wave ]]})))

(defn popai [request]
  (if-let [[code config] (validate request)]
    (page/from-components
     nil
     [page/base
      page/header
      background-image
      (flyout[{:left  "Asking a question?"
               :right "Yes, you are."}
              {:left  "And a follow-up?"
               :right "Yep."}]
             {:color       "white"
              :text-shadow "0.1em 0.2em 0.6em black"
              :height      "70vh"}
             700)
      (topic [:div.body-width (style {:display :flex
                                      :flex-flow "column nowrap"})
              [:div (style {:display   :flex
                            :flex-flow "row nowrap"})
               [:div (style {:flex-grow 1
                             :min-width "30vw"})]
               [:div (style {:color "rgb(var(--accent))"})
                [:h1 (style {:display :block}) "Ready to Cast Off"]
                [:p "After months of preparation you are finally on a new charter boat with all your family and friends. The adventure begins …"]]]
              [:div (style {:margin "3em 0"})
               [:h1 "Pre-Cruise Checklists"]
               [:p "PopAI turns your boat into a powerful trusty deckhand who knows their way around the boat and automates going over tedious checklists for you. Save time and have confidence that you will never miss a critical task."]]
              [:div (style {:display   :flex
                            :flex-flow "row nowrap"})
               [:div (style {:flex-grow 1
                             :min-width "10vw"})]
               [:div
                [:h1 "Simple and reliable voice interface"]
                [:p "Operating a unfamiliar vessel is stressful enough. No need to fight screen glare or brightness just to have to find where is the depth or boat speed. Simply ask PopAI and get an instant update."]]]])
      (flyout [{:right  "Air pressure has been dropping &mdash; expect high winds soon."}
               {:left "Okay, where can I anchor around here?"}]
              {:color       "white"
               :text-shadow "0.1em 0.2em 0.6em black"})
      (topic [:div.body-width (style {:display :flex
                                      :flex-flow "column nowrap"})
              [:div (style {:display   :flex
                            :flex-flow "row nowrap"})
               [:div (style {:flex-grow 1
                             :min-width "30vw"})]
               [:div (style {:color "rgb(var(--accent))"})
                [:h1 (style {:display :block})"Cruising"]
                [:p "It is a glorious day and everybody onboard is having a blast!"]]]
              [:div (style {:margin "3em 0"})
               [:h1 "PopAI has your back"]
               [:p "PopAI reads all your instruments' data and will notify you when prompted. Setting an alarm is as easy as saying it out loud."]]
              [:div (style {:display   :flex
                            :flex-flow "row nowrap"})
               [:div (style {:flex-grow 1
                             :min-width "10vw"})]
               [:div
                [:h1 "Local guide knowledge when you need it"]
                [:p "When you need to change your plans for whatever reason, PopAI is there to help you find the best destination. PopAI has a detailed local area crusing knowledge, that you can access just by asking."]]]])
      (flyout [{:left  "Asking a question?"
                :right "Yes, you are."}]
              {:color       "white"
               :text-shadow "0.1em 0.2em 0.6em black"})
      (topic [[:a {:grid-column     2
                   :user-select     :none
                   :text-decoration :none
                   :font-size       "1.2em"
                   :border-radius   "1em"
                   :padding         "1em"
                   :cursor          :pointer
                   :color           :white
                   :background      "rgb(var(--accent))"
                   :box-shadow      "3px 3px 0px rgba(var(--foreground), 0.9)"}]
              ["a:hover"        {:background "color-mix(in srgb, rgb(var(--accent)), white 15%)"}]
              ["a:hover:active" {:background "color-mix(in srgb, rgb(var(--accent)), black 15%)"
                                 :box-shadow :none
                                 :transform  "translate(3px, 3px)"}]]
             [:div.body-width (style {:display               :grid
                                      :grid-template-columns "1fr auto 1fr"})
              [:a {:href (route-with-code route-configure code)} "Configure and Buy"]])
      (flyout [{:left  "Asking a question?"
                :right "Yes, you are."}]
              {:color       "white"
               :text-shadow "0.1em 0.2em 0.6em black"})
      (topic [:div.body-width (style {:display   :flex
                                      :flex-flow "column nowrap"
                                      :gap       "4em"})
              [:h1 (style {:margin 0}) "Instant, Quality Answers"]
              [:div (style {:display               :grid
                            :grid-template-columns "auto 1fr 5vw"
                            :grid-template-rows    "auto auto"
                            :gap                   "1em"
                            :text-align            :left})
               [:img (merge {:src "/change-run.png"}
                            (style {:grid-column 1
                                    :grid-row    "1 / -1"
                                    :align-self  :center
                                    :height      "3em"}))]
               [:h2 (style {:margin      0
                            :grid-column 2}) "Instant reference"]
               [:p (style {:margin      0
                           :grid-column 2}) "Access live data from the boat’s instruments, including information about other vessels through AIS."]]
              [:div (style {:display               :grid
                            :grid-template-columns "5vw 1fr auto"
                            :grid-template-rows    "auto auto"
                            :gap                   "1em"
                            :text-align            :right})
               [:svg (merge {:width "35.341206mm"
                             :height "27.330044mm"
                             :viewBox "0 0 35.341206 27.330044"
                             :version "1.1"
                             :id "svg1"
                             "xml:space" :preserve
                             :xmlns "http://www.w3.org/2000/svg"
                             "xmlns:svg" "http://www.w3.org/2000/svg"}
                      (style {:grid-column 3
                              :grid-row    "1 / -1"
                              :align-self  :center
                              :height      "3em"}))
                [:style (g/css [[:path {:fill :none
                                       :stroke "rgb(var(--foreground))"
                                       :stroke-width 1.265
                                       :stroke-linecap :round
                                       :stroke-linejoin :round
                                        :stroke-opacity  1}]
                                [:path.accent {:stroke "rgb(var(--accent))"}]])]
                [:defs {:id "defs1"}]
                [:g {:id "layer1"
                     :transform "translate(-86.072637,-135.23065)"}
                 [:path {:style "fill:#6b6b6b;stroke-width:1.265"
                         :d "m 104.81604,145.45514 v 3.84935"}]
                 [:path {:d "m 94.719374,149.30449 13.819816,0.12621 3.72315,4.10177 8.51906,-0.0631 -4.79592,7.69871"}]
                 [:path {:d "m 97.81148,149.3676 -1.766916,6.1211"}]
                 [:path {:d "m 112.32544,153.53247 -4.4804,0.0631 -3.47073,2.08243 -15.776042,0.0631 1.893126,5.61627"}]
                 [:path {:style "stroke-width:1.865"
                         :d "m 107.30865,153.28006 -0.59948,-0.75726 -6.56284,0.0631"}]
                 [:path {:d "m 104.75294,149.05207 0.0631,-3.53383"}]
                 [:path {:class :accent
                         :d "m 102.6074,143.12028 c 0.61489,-0.49606 1.42053,-0.74926 2.20864,-0.69414 0.64111,0.0448 1.26672,0.29061 1.76692,0.69414"}]
                 [:path {:class :accent
                         :d "m 100.65117,140.65922 c 1.14959,-0.93 2.6231,-1.4514 4.10177,-1.4514 1.47867,0 2.95218,0.5214 4.10177,1.4514"}]
                 [:path {:class :accent
                         :d "m 98.442522,138.13505 c 1.740378,-1.44161 3.987448,-2.25873 6.247308,-2.27175 2.33061,-0.0134 4.65665,0.83033 6.43663,2.33485"}]
                 [:path {:d "m 86.705143,160.85255 c 1.16629,0.69912 2.55892,1.01358 3.912459,0.88346 0.866873,-0.0833 1.705708,-0.3416 2.544564,-0.57556 0.838857,-0.23395 1.696094,-0.44634 2.566877,-0.4341 0.960413,0.0135 1.893747,0.29921 2.81027,0.58654 0.916523,0.28733 1.845987,0.58139 2.805997,0.61244 1.1142,0.036 2.20685,-0.28403 3.26616,-0.63131 1.05932,-0.34728 2.12259,-0.72803 3.23357,-0.82009 1.14173,-0.0946 2.28614,0.12027 3.39913,0.39182 1.113,0.27154 2.21627,0.60163 3.35302,0.74406 1.70023,0.21304 3.45286,-0.006 5.04834,-0.63105"}]]]
               [:h2 (style {:margin      0
                            :grid-column 2}) "Concrete, exact answers"]
               [:p (style {:margin       0
                            :grid-column 2}) "Answers from the boat and engine operational manuals, maintenance guides, and other manufacturer documentation."]]
              [:div (style {:display               :grid
                            :grid-template-columns "auto 1fr 5vw"
                            :grid-template-rows    "auto auto"
                            :gap                   "1em"
                            :text-align            :left})
               [:img (merge {:src "/change-encyclopedia.png"}
                            (style {:grid-column 1
                                    :grid-row    "1 / -1"
                                    :align-self  :center
                                    :height      "3em"}))]
               [:h2 (style {:margin      0
                            :grid-column 2}) "Encyclopedic detail"]
               [:p (style {:margin      0
                           :grid-column 2}) "Look up applicable navigation rules and maritime regulations for your cruising area. From COLREGS to Local Notice to Mariners, PopAI has your back."]]
              [:div.body-width (style {:display               :grid
                                      :grid-template-columns "1fr auto 1fr"})
               [:a {:href (route-with-code route-configure code)} "Configure and Buy"]]])
      (flyout [{:left  "Is there anything left on my return checklist?"
                :right "Nope, that's it!"}]
              {:color       "white"
               :text-shadow "0.1em 0.2em 0.6em black"})
      about/footer])
    (resp/redirect "/")))

(defn almanac-request [code]
  {:css  [[:details {:margin-top "3em"}
           [:button {:margin-top "1em"}]]]
   :body [[:details
           [:summary "Don&rsquo;t see your destination or boat?"]
           [:p "Let us know where you&rsquo;re going and what you&rsquo;ll be sailing so we can start working on the almanac. We&rsquo;ll follow up once it&rsquo;s ready."]
           [:form.sku-request {:action (route-with-code route-request-almanac code)
                               :method :post}
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
            [:button {:type :submit} "Request Almanac"]]]]})

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
                [:option {:value ""} "-- Select One --"]
                (map (fn [[area locations]]
                       [:optgroup {:label area}
                        (map (fn [[k v]] [:option {:value k} v]) locations)])
                     locations)]
               [:label       {:for  :boat} "Boat:"]
               [:select#boat {:name :boat}
                [:option {:value ""} "-- Select One --"]
                (map (fn [[k v]] [:option {:value k} v]) boats)]
               [:p.total "Subtotal: $" price]
               [:button {:type :submit} "Checkout"]]
              (first (:body (almanac-request code)))]]]
     :script [(slurp (io/resource "almanac-checkout.js"))]}))

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
    (let [params  (:params request)
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
