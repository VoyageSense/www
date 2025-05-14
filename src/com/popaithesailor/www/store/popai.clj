(ns com.popaithesailor.www.store.popai
  (:require
   [clojure.java.io :as io]
   [com.popaithesailor.www.about :as about]
   [com.popaithesailor.www.page :as page]
   [com.popaithesailor.www.store.request :as request]
   [com.popaithesailor.www.store.route :as route]
   [com.popaithesailor.www.util :refer [long-str style]]
   [garden.core :as g]
   [hiccup.core :as h]
   [ring.util.response :as resp]))

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
      (when (= :top end)
        [:div (style {:height "5em"})])
      (when (= :bottom end)
        head-wave)
      body
      (if (= :bottom end)
        [:div (style {:height "5em"})]
        tail-wave)])))

(defn flyout
  ([voices image] (flyout voices image nil))
  ([voices image end]
   (let [duration 1
         quote-style (fn [dir delay]
                       {:transition       (str "transform " duration "s, opacity " duration "s")
                        :transition-delay delay
                        :background       "rgba(0,0,0,0.4)"
                        :border-radius    (case dir
                                            :left  "0.4em 0.4em 0.4em 0"
                                            :right "0.4em 0.4em 0 0.4em")
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
                [:div.flyouts.full-width (style (merge {:display  :grid
                                                        :row-gap  "1em"
                                                        :overflow :hidden}
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
                  [:div (style {:flex-grow 1})]
                  (map-indexed (fn [i [side utterance]]
                                 (let [row-delay (str (* 400 i) "ms")]
                                   (into [:div.flyout-pair.is-visible.body-width (style {:display    :flex
                                                                                         :padding    "0 3vw"})]
                                         (case side
                                           :left [[:q.left (style (quote-style :left row-delay)) utterance]
                                                  [:div (style spacer)]]
                                           :right [[:div (style spacer)]
                                                   [:q.right (style (quote-style :right row-delay)) utterance]]))))
                               voices)]]
                end)]
      :script [(slurp (io/resource "visible.js"))]})))

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
  ([body] (topic nil nil body))
  ([code config body]
   {:body [[:div.full-width.topic (style {:background "rgb(var(--background))"
                                          :padding    "1em 0 5em"})
            (concat
             body
             (when code
               [[:a.button {:href (if (empty? config)
                                    (route/with-code route/checkout code)
                                    (route/with-code route/configure code))
                            :style (g/style {:margin "5em 0 3em"})}
                 "Learn More and Buy"]]))]]}))

(defn elaboration [chunks]
  (topic
   (let [gap "2em"]
     [[:div.body-width (style {:display :flex
                               :flex-flow "column nowrap"})
       (map-indexed (fn [i point]
                      (let [[head body] point]
                        (if (= 0 (mod i 2))
                          [:div (style {:margin-bottom gap})
                           [:h1 (style {:font-weight :lighter
                                        :color       "rgb(var(--accent))"}) head]
                           [:p body]]
                          [:div (style {:margin-bottom gap
                                        :display       :flex
                                        :flex-flow     "row nowrap"})
                           [:div (style {:flex-grow 1
                                         :min-width "10vw"})]
                           [:div
                            [:h1 (style {:font-weight :lighter
                                         :color       "rgb(var(--accent))"}) head]
                            [:p body]]])))
                    chunks)]])))

(def mobile-background
  {:css  [[:html.js
           [:img {:transform "translateY(20px)"
                  :opacity   0}]
           [:img.visible {:transform :none
                          :opacity   1}]]]
   :body (let [base-style {:width      "100vw"
                           :height     "100vh"
                           :position   :fixed
                           :object-fit :cover
                           :transition "opacity 0.8s linear, transform 0.8s ease"
                           :z-index    -1}]
           [[:img.mobile-show (merge {:src    "/popai-hero-background-light.webp"
                                      :onload "this.classList.add('visible')"}
                                     (style (merge base-style {:visibility "var(--light-visibility)"})))]
            [:img.mobile-show (merge {:src    "/popai-hero-background-dark.webp"
                                      :onload "this.classList.add('visible')"}
                                     (style (merge base-style {:visibility "var(--dark-visibility)"})))]])
   :script (slurp (io/resource "visible.js"))})

(defn box-outline [title body]
  [:div (style {:background-color "rgb(var(--bold-background))"
                :box-shadow       "0 0px 10px rgba(var(--foreground), 0.1)"
                :padding          "2em"
                :margin-top       "1em"
                :display          :flex
                :flex-flow        "column nowrap"})
   [:h2 (style {:margin      "0 0 1em"
                :font-weight :lighter}) title]
   body])

(defn page [request]
  (if-let [[code config] (request/validate request)]
    (page/from-components
     nil
     [page/base
      (page/header code)
      {:css [buy-button-css
             [:header {:color       "#f8f8f8"
                       :text-shadow "0.05em 0.1em 0.5em #404040"}]]}
      mobile-background
      (flyout [[:left  "Popai, can we make it to Simpson Bay for the 5pm bridge opening?"]
               [:right "Yes, but winds are light, so you'll have to motor sail."]]
              "/popai-hero-background-light.webp"
              :top)
      (elaboration [["Verbal Checklists"
                     "Popai talks you through checklists to save time and have confidence you will not miss anything."]
                    ["Simple and reliable voice interface"
                     "Instantly reads any instrument data for you. No more getting distracted by a screen while maneuvering in close quarters."]])
      (flyout [[:left  "Popai, let me know if the depth goes below 15 feet."]
               [:right "Okay, I'll notify you if that happens."]]
              "/cruising.webp")
      (elaboration [["Second set of eyes"
                     "Popai monitors the systems aboard, alerting when it sees trouble ahead."]
                    ["Hands free control"
                     "Controls display brightness, lights, pumps, systems and anything available on the boat network already installed."]
                    ["Cruising guide"
                     "Answers any questions about local marinas, anchorages, dive spots, amenities ashore, and more."]])
      (flyout [[:right "Skipper, I noticed the engine is getting hot. Is there water still coming out of the exhaust port?"]
               [:left  "Popai, I didn’t see any, and I shut off the engine. What’s wrong?"]]
              "/tow.webp")
      (elaboration [["Your boat’s documentation"
                     "Popai gets its knowledge from the boat manufacturer’s documentation. Instant, accurate answers are there when you need them."]
                    ["Interactive troubleshooting"
                     "Troubleshoots with engine and systems manuals. A clear, methodical approach can mean getting back underway&mdash;or drifting for hours."]
                    ["Repair guidance"
                     "With a little know-how, you can handle 90% of boat repairs yourself. Popai recognizes when it needs to detail steps and when to let you think."]])
      (flyout [[:left  "Popai, how should I hail the Simpson Bay Bridge on VHF?"]
               [:right "Use channel 12 and say: “Simpson Bay Bridge, Simpson Bay Bridge, Simpson Bay Bridge, this is sailing vessel Kayo, over.”"]]
              "/bridge.webp")
      (elaboration [["VHF instructions"
                     "Popai guides your VHF communication with ease &mdash; from choosing the right channel to how to hail a vessel or request assistance."]
                    ["Rules of the road"
                     "Nav lights, call signs, fog horns, buoys, COLREGS &mdash; Popai answers any question about boating rules and conventions, both domestic and international."]
                    ["Pilot Plan"
                     "When you need to change your plans for any reason, Popai helps you find the best destination. Like a cruising guide, it offers detailed local knowledge you can query with your voice."]])
      (flyout [[:left  "Popai, can I anchor here?"]
               [:right "You can, but you'll be half-a-foot in the mud at low tide tonight."]]
              "/marina.webp")
      (elaboration [["Subtle help at critical moments"
                     "Popai won’t distract during high-stress moments like docking &mdash; but it’s there when you just need a little reminder."]
                    ["Rest assured"
                     "Popai monitors your vessel’s status and can help you remember tasks, like turning off running lights and power winches while at anchor."]])
      (flyout [[:left "Popai, turn on the anchor light and turn everything else off."]
               [:right "All set."]]
              "/popai-hero-background-light.webp")
      (topic code
             config
             [[:div.body-width (style {:display    :flex
                                       :flex-flow  "column nowrap"
                                       :margin-top "3em"})
               (box-outline "Install in Three Easy Steps"
                            [:ol (style {:margin 0})
                             [:li "Purchase a data package for your boat and cruising location"]
                             [:li "Install it on your mobile device"]
                             [:li "Connect your mobile device to your boat’s MFD Wi-Fi network."]])
               (box-outline "Buy Once and Use Forever"
                            [:p (style {:margin 0})"Buy the data for your boat and cruising location and get free updates for a year."])
               (box-outline "Trustworthy Answers"
                            [:p (style {:margin 0}) "Popai reads your boat's sensor data and uses curated data from sources including government notices and publications, travel guides, local knowledge and manufacturer documentation."])]])
      (flyout [] nil :bottom)
      (about/footer code)])
    (resp/redirect "/")))
