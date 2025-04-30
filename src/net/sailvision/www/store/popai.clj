(ns net.sailvision.www.store.popai
  (:require
   [clojure.java.io :as io]
   [net.sailvision.www.about :as about]
   [net.sailvision.www.page :as page]
   [net.sailvision.www.store.request :as request]
   [net.sailvision.www.store.route :as route]
   [net.sailvision.www.util :refer [long-str]]
   [garden.core :as g]
   [garden.stylesheet :as s]
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

(defn page [request]
  (if-let [[code config] (request/validate request)]
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
              [:a {:href (route/with-code route/configure code)} "Configure and Buy"]])
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
               [:a {:href (route/with-code route/configure code)} "Configure and Buy"]]])
      (flyout [{:left  "Is there anything left on my return checklist?"
                :right "Nope, that's it!"}]
              {:color       "white"
               :text-shadow "0.1em 0.2em 0.6em black"})
      about/footer])
    (resp/redirect "/")))
