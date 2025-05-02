(ns net.sailvision.www.store.popai
  (:require
   [clojure.java.io :as io]
   [net.sailvision.www.about :as about]
   [net.sailvision.www.page :as page]
   [net.sailvision.www.store.request :as request]
   [net.sailvision.www.store.route :as route]
   [net.sailvision.www.util :refer [long-str]]
   [garden.core :as g]
   [hiccup.core :as h]
   [ring.util.response :as resp]))

(defn style [content]
  {:style (g/style content)})

(defn background-mask
  ([image body] (background-mask image body nil))
  ([image body end]
   (let [datauri #(str "url('data:image/svg+xml;utf8," % "')")
         wave-base {:xmlns               "http://www.w3.org/2000/svg"
                    :viewBox             "0 0 200 100"
                    :width               200
                    :height              100
                    :preserveAspectRatio :none}
         height  "1.4em"
         wave    {:height      height
                  :grid-column "1 / -1"
                  :mask-size   "100% 100%"
                  :mask-repeat :no-repeat
                  :background  "rgb(var(--background))"}
         head-wave [:div (style (merge wave {:margin-top "-1px"
                                             :margin-bottom (str "calc(1px - " height ")")
                                             :mask-image (datauri (h/html [:svg wave-base
                                                                           [:path {:d (long-str "M -25 50"
                                                                                                "Q 0 10, 25 50"
                                                                                                "T 75 50"
                                                                                                "T 125 50"
                                                                                                "T 175 50"
                                                                                                "T 225 50"
                                                                                                "L 225 0"
                                                                                                "L -25 0"
                                                                                                "Z")}]]))}))]
         tail-wave [:div (style (merge wave {:margin-top    height
                                             :margin-bottom "-1px"
                                             :mask-image (datauri (h/html [:svg wave-base
                                                                           [:path {:d (long-str "M 0 50"
                                                                                                "Q 25 90, 50 50"
                                                                                                "T 100 50"
                                                                                                "T 150 50"
                                                                                                "T 200 50"
                                                                                                "L 200 100"
                                                                                                "L 0 100"
                                                                                                "Z")}]]))}))]]
     [:div.body-width-no-edge
      (when image
        [:div.mobile-hide (style {:width                 "calc(min(100%, 2em + var(--max-body-width)))"
                                  :height                "100%"
                                  :position              :absolute
                                  :z-index               -1
                                  :background-image      (str "url(" image ")")
                                  :background-attachment :fixed
                                  :background-position   :center
                                  :background-repeat     :no-repeat
                                  :background-size       "calc(2em + var(--max-body-width)) 100vh"})])
      (if (= :top end)
        [:div (style {:height "5em"})]
        head-wave)
      body
      (if (= :bottom end)
        [:div (style {:height "5em"})]
        tail-wave)])))

(defn flyout
  ([voices image] (flyout voices image nil))
  ([voices image end]
   (let [duration 1
         quote-style (fn [delay]
                       {:transition       (str "transform " duration "s, opacity " duration "s")
                        :transition-delay delay
                        :background       "rgba(0,0,0,0.4)"
                        :border-radius    "0.4em"
                        :padding          "0.5em"})
         spacer     {:flex-grow 1
                     :min-width "5vw"}]
     {:css    [[:html.js
                [:.flyout-pair
                 [:q       {:opacity   0}]
                 [:q.left  {:transform "translateX(-1em)"}]
                 [:q.right {:transform "translateX(1em)"}]]
                [:.flyout-pair.visible
                 [:q {:opacity   1
                      :transform :none}]]]]
      :body   [(background-mask
                image
                [:div.flyouts.full-width (style (merge {:display :grid
                                                        :row-gap "1em"}
                                                       (when (= end :top)
                                                         {:min-height "60vh"})))
                 [:div.body-width (style {:display     :flex
                                          :flex-flow   "column nowrap"
                                          :gap         "3em"
                                          :padding     "2em 0"
                                          :color       "white"
                                          :font-size   "1.75em"
                                          :font-style  :italic
                                          :quotes      :none})
                  (map-indexed (fn [i [side utterance]]
                                 (let [row-delay (str (* 400 i) "ms")]
                                   (into [:div.flyout-pair.body-width (style {:display    :flex
                                                                              :padding    "0 3vw"})]
                                         (case side
                                           :left [[:q.left (style (quote-style row-delay)) utterance]
                                                  [:div (style spacer)]]
                                           :right [[:div (style spacer)]
                                                   [:q.right (style (quote-style row-delay)) utterance]]))))
                               voices)]]
                end)]
      :script [(slurp (io/resource "flyout.js"))]})))

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
   {:body [[:div.full-width.topic (style {:background "rgb(var(--background))"
                                          :padding    "3em 0"})
            (concat
             body
             (when code
               [[:a.button {:href (route/with-code route/configure code)
                            :style (g/style {:margin "5em 0 3em"})}
                 "Configure and Buy"]]))]]}))

(defn elaboration
  ([chunks] (elaboration nil chunks))
  ([code chunks]
   (topic
    code
    (let [[accent & points] chunks
          [head body]       accent

          gap "2em"]
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

(def mobile-background
  {:css  [[:html.js
           [:img {:transform "translateY(20px)"
                  :opacity   0}]
           [:img.visible {:transform :none
                          :opacity   1}]]]
   :body [[:img.mobile-show.is-visible (merge {:src "/popai-hero-background-light.jpg"}
                                              (style {:width      "100vw"
                                                      :height     "100vh"
                                                      :position   :fixed
                                                      :object-fit :cover
                                                      :transition "opacity 0.8s linear, transform 0.8s ease"
                                                      :z-index    -1
                                                      :visibility "var(--light-visibility)"}))]
          [:img.mobile-show.is-visible (merge {:src "/popai-hero-background-dark.jpg"}
                                              (style {:width      "100vw"
                                                      :height     "100vh"
                                                      :position   :fixed
                                                      :object-fit :cover
                                                      :transition "opacity 0.8s linear, transform 0.8s ease"
                                                      :z-index    -1
                                                      :visibility "var(--dark-visibility)"}))]]
   :script (slurp (io/resource "visible.js"))})

(defn page [request]
  (if-let [[code config] (request/validate request)]
    (page/from-components
     nil
     [page/base
      page/header
      {:css [buy-button-css
             [:header {:color       "#f8f8f8"
                       :text-shadow "0.05em 0.1em 0.5em #404040"}]]}
      mobile-background
      (flyout [[:left  "PopAI, can we make it to Simpson Bay this afternoon?"]
               [:right "Yes, but winds are light, so you'll have to motor sail."]]
              "/popai-hero-background-light.jpg"
              :top)
      (elaboration [["Ready to Cast Off"
                     "After months of preparation you are finally on your charter boat with all your family and friends. The adventure begins …"]
                    ["Verbal Checklists"
                     "PopAI verbally helps you run through step-by-step procedures – everything from pre-cruise checkout to <b><i>something impressive</i></b>. Save time and have confidence that you’ll never miss a critical task."]
                    ["Simple and reliable voice interface"
                     "Simply ask PopAI for any instrument data and get an instant update in your ear. Operating an unfamiliar vessel is stressful enough – no more straining agaist glare and hunting for information."]
                    ["Expert handling charter companies"
                     "It’s easy to overlook things when checking out a charter boat &mdash; you’re relying on the charter company to have everything set up correctly. PopAI helps you take control with smart checklists: what gear should be onboard, what to test, what to ask about, and how to operate key systems. It also provides clear return instructions to ensure a smooth handover."]])
      (flyout [[:left  "PopAI, let me know if the depth goes below 15 feet."]
               [:right "Okay, I'll notify you if that happens."]]
              "/cruising.jpg")
      (elaboration [["Cruising"
                     "It's a glorious day and everybody onboard is having a blast!"]
                    ["PopAI has your back"
                     "PopAI monitors the systems aboard – alerting when it sees trouble unfolding – giving you a “second set of eyes”."]
                    ["Control your boat"
                     "Control your boat seamlessly with your voice. Adjust the display brightness, turn on the foredeck light, and interact with other systems aboard, using the network already installed."]
                    ["Activity oriented answers"
                     "From picturesque anchorages to the hottest local dive spot. PopAI is your instant reference."]])
      (flyout [[:right "Skipper, I noticed the engine is getting hot. Is there water still coming out of the exhaust port?"]
               [:left  "PopAI, I didn’t see any. And I shut off the engine. What’s wrong?"]]
              "/tow.jpg")
      (elaboration [["Breakdown"
                     "You’re finally on your way – open sea, no signal, and no distractions. And then… silence. The engine has died."]
                    ["Instant access to your boat’s documentation"
                     "PopAI gets its knowledge from the documentation provided by the boat’s manufacturer. Instant, accurate answers and help with troubleshooting are there when you need it."]
                    ["Interactive Troubleshooting"
                     "Get help with troubleshooting, using information from the engine manual itself. In a stressful moment, a clear, methodical approach can mean the difference between getting back underway – or drifting for hours in frustration."]
                    ["Repair guidance tailored to your skill level"
                     "With a little know-how, you can handle 90% of boat repairs yourself. PopAI recognizes when it needs to detail steps and when it should let you think."]])
      (flyout [[:left  "PopAI, how should I hail the Simpson Bay Bridge on VHF?"]
               [:right "Use channel 12 and say: “Simpson Bay Bridge, Simpson Bay Bridge, Simpson Bay Bridge, this is sailing vessel Kayo, over.”"]]
              "/bridge.jpg")
      (elaboration [["Land Ahoy"
                     "You’ve reached your final destination. The crew is exhausted and ready to rest. One final challenge awaits: navigate the coastal waters and dock in an unfamiliar marina."]
                    ["VHF instructions"
                     "PopAI is an instant guide to VHF communication &mdash; from choosing the right channel to how to hail a vessel or request assistance, all a question away."]
                    ["Rules of the road"
                     "Nav lights, call signs, fog horns, buoys, COLREGS &mdash; just ask PopAI. Easy access to boating rules and conventions, domestic and international."]
                    ["Pilot Plan"
                     "When you need to change your plans for whatever reason, PopAI is there to help you find the best destination. Just like a cruising guide PopAI has a detailed local knowledge you can query with your voice."]])
      (flyout [[:left  "PopAI, can I anchor here?"]
               [:right "You can, but you'll be half-a-foot in the mud at low tide tonight."]]
              "/marina.jpg")
      (elaboration code
                   [["Safely in the marina!"
                     "What an adventure! You’re safely at your destination, the crew is happily worn out from a day on the water &mdash; and now, it’s time to kick back and unwind."]
                    ["Subtle help at critical moments"
                     "PopAI won’t distract during high-stress moments like docking &mdash; but it’s there when you just need a little reminder."]
                    ["Rest assured"
                     "PopAI monitors your vessel’s status and can help you remember tasks &mdash; like turning off running lights and power winches while at anchor."]])
      (flyout [[:left "PopAI, make sure the anchor light is on and turn everything else off."]
               [:right "Done"]]
              "/popai-hero-background-light.jpg")
      (topic code
             (let [outline (fn [align title text]
                             (into [:div
                                    (style (merge {:background-color   "rgb(var(--bold-background))"
                                                   :padding            "2em"
                                                   :display            :grid
                                                   :align-items        :center
                                                   :grid-template-rows "auto auto"
                                                   :gap                "1em"}
                                                  (case align
                                                    :left {:grid-template-columns "auto 1fr"
                                                           :text-align            :left}
                                                    :right {:grid-template-columns "1fr auto"
                                                            :text-align            :right})))]
                                   [[:h2 (style {:margin      0
                                                 :color "rgb(var(--accent))"
                                                 :grid-column "1 / -1"}) title]
                                    [:p (style {:margin      0
                                                :grid-column 1}) text]]))]
               [[:div.body-width (style {:display   :flex
                                         :background "rgb(var(--bold-background))"
                                         :box-shadow "0 0px 10px rgba(var(--foreground), 0.1)"
                                         :margin    "2em 0"
                                         :padding   "2em 0"
                                         :flex-flow "column nowrap"
                                         :gap       "2em"})
                 [:h1 (style {:margin "1em"}) "PopAI is software for your existing device"]
                 (outline :left 
                          "Install In Three Easy Steps"
                          "You purchase a data package tailored to your boat and location, install it on your existing mobile device, and connect the device to your boat’s MFD Wi-Fi network.")
                 (outline :right 
                          "Buy Once and Use Forever"
                          "You buy a data package for your boat and location. You get free data updates for your data for a year.")
                 (outline :left 
                          "Concrete Precise Answers Based on Trustworthy Data"
                          "PopAI is powered by your boat's sensor data, propriatory data and curated data sources including travel guides, government noticaes and publications, local knowledge and manufacturer's documentation.")]]))
      (flyout [] nil :bottom)
      about/footer])
    (resp/redirect "/")))
