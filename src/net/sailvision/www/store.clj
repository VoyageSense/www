(ns net.sailvision.www.store
  (:require
   [clojure.string :as str]
   [net.sailvision.www.about :as about]
   [net.sailvision.www.db :as db]
   [net.sailvision.www.page :as page]
   [net.sailvision.www.util :refer [long-str]]
   [garden.core :as g]
   [garden.stylesheet :as s]
   [ring.util.codec :as codec]
   [ring.util.response :as resp]))

(def route-home "/store/popai")
(def route-configure (str route-home "/configure"))
(def route-checkout (str route-home "/checkout"))
(def route-request-almanac "/store/request-almanac")

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
                 :price     300}})

(def header
  {:css  [[:body
           [:header {:padding     "0.8em"
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
   :body [[:header
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
                [:.hero-mask {:width      "100%"
                              :height     "calc(min(70vw, 80vh))"
                              :background (str
                                           "linear-gradient(to bottom, transparent 85%, rgba(var(--background),"
                                           mask-opacity
                                           "))")
                              :z-index    -1}]
                [:.over-hero {:background (str "rgba(var(--background), " mask-opacity ")")}]]
     :noscript [[:.hero loaded]]
     :body     [[:img.hero.light
                 {:src    "/popai-hero-background-light.jpg"
                  :alt    "Looking over the bow of a boat sailing in the San Francisco bay, city in the background"
                  :onload "this.classList.add('loaded')"}]
                [:img.hero.dark
                 {:src    "/popai-hero-background-dark.jpg"
                  :alt    "Looking over the bow of a boat sailing in the San Francisco bay, sunset in the background"
                  :onload "this.classList.add('loaded')"}]
                [:div.hero-mask]]}))

(def get-to-know
  {:css  [[:.get-to-know {:margin   0
                          :padding  "4em 2em"}
           [:h1 {:margin  0
                 :display :inline}]
           [:h2 {:font-size-adjust "0.3"
                 :padding-left     "0.4em"
                 :display          :inline}]
           [:div {:display :inline}]
           [:.emphasis {:font-weight     :bold
                        :font-style      :italic
                        :text-decoration :underline}]]]
   :body [[:div.get-to-know.over-hero
           [:div [:h1 "Get to know PopAI"]]
           [:div [:h2 "pronounced " [:q "Popeye"]]]
           [:p "PopAI is a voice controlled boating helper. It is a part "
            [:span.emphasis "guide"] ", part "
            [:span.emphasis "mechanic"]", part "
            [:span.emphasis "instructor"] ", and part "
            [:span.emphasis "crew member" "."]]]]})

(defn panel-script [id]
  [:script
   (long-str
    (str "document.getElementById('" id "').addEventListener('toggle', (event) => {")
     "shownfn = event.target.matches(':popover-open')"
     "? (prompt) => prompt.classList.add('shown')"
     ": (prompt) => prompt.classList.remove('shown');"
     "event.target.querySelectorAll('.prompt').forEach(shownfn);"
     "});")])

(defn panel [{:keys [title subtitle intro image-path details]}]
  (let [modal-id (str/join "-" (flatten
                                ["modal"
                                 (-> title
                                     (str/lower-case)
                                     (str/split #" "))]))]
    [:button.panel {:type          :button
                    :popovertarget modal-id}
     [:h3 [:i title]]
     [:h4 subtitle]
     [:div.space]
     [:img {:src image-path}]
     [:dialog.feature {:id      modal-id
                       :popover true}
      [:div.content
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
       (panel-script modal-id)]]]))

(def features-panels
  (let [soft-image-border 2]
    {:noscript [[:dialog.feature
                 [:.content
                  [:.prompt.left :.prompt.right {:opacity   1
                                                 :transform :none}]]]]
     :css       [[:.panels {:display :flex
                            :gap     "2em"
                            :overflow-x :auto
                            :padding    "2em"}]
                 [:.panel {:display        :flex
                           :flex-direction :column
                           :gap            "1.2em"
                           :padding        "1em"
                           :border         0
                           :border-radius  "1em"
                           :background     "rgb(var(--bold-background))"
                           :color          "rgb(var(--bold-foreground))"
                           :cursor         :pointer
                           :transition     "transform 0.2s ease-out"}
                  [:h3 :h4 {:text-align  :center
                            :margin      0
                            :font-family "Arial,sans-serif"}]
                  [:h3     {:font-size "1.8em"}]
                  [:h4     {:font-size "1em"}]
                  [:img    {:width      "calc(min(60vw, 50ch))"
                            :height     "calc(1.3 * min(60vw, 50ch))"
                            :mask-image (str "linear-gradient(to top"
                                             ",transparent"
                                             ",black " soft-image-border "%"
                                             ",black " (- 100 soft-image-border) "%"
                                             ",transparent)"
                                             ",linear-gradient(to left"
                                             ",transparent"
                                             ",black " soft-image-border "%"
                                             ",black " (- 100 soft-image-border) "%"
                                             ",transparent)")
                            :mask-composite :intersect
                            :mask-size      "100% 100%"}]
                  [:.space {:flex-grow 1}]]
                 [:.panel:hover {:transform "scale(1.03)"}]
                 [:dialog.feature {:width      "100%"
                                   :height     "100%"
                                   :border     0
                                   :background :transparent
                                   :animation-duration "0.3s"}
                  [:.content {:margin        "5vh 5vw"
                              :width         "auto"
                              :padding       "2em"
                              :border-radius "1em"
                              :text-align    :start
                              :color         "rgb(var(--foreground))"
                              :background    "rgb(var(--background))"}
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
     :body     [[:div.panels.over-hero
                 (panel {:title      "Cruising Guide"
                         :subtitle   "Boating almanac on the go"
                         :image-path "/panel-boat.png"
                         :intro      "... talking to a local guide has never been simpler."
                         :details    [{:heading "Local Navigation"
                                       :body    [(long-str
                                                  "Local weather patterns and seasonal considerations, local rules and"
                                                  "regulations, fuel docks and provisioning spots, customs and"
                                                  "immigration procedures. PopAI is there to help make your boating"
                                                  "experience smooth and stress free.")]
                                       :prompts ["PopAI, what are the predominant winds for this part of the year?"
                                                 "PopAI, can we anchor in Cam Bay National Park?"
                                                 "PopAI, how do I clear customs in Tortola?"]}
                                      {:heading "Marinas, Anchorages and Points of Interest."
                                       :body    [(long-str
                                                  "Planning your day has never been easier. Simply tell PopAI what"
                                                  "activities you want to do and it will suggest areas around you where"
                                                  "you can do those.")]
                                       :prompts ["PopAI, where do I snorkel to see Manta rays?"
                                                 "What types of fish are visible at this diving spot?"
                                                 "Where is a child friendly beach to anchor?"
                                                 (long-str "PopAI, I need fuel, fresh water and a hot shower tonight."
                                                           "Which marina should I go to?")
                                                 "How do I call the marina?"]}
                                      {:heading "Off The Water Insights"
                                       :body    [(long-str
                                                  "From the best restaurants and bars, to where to get groceries and"
                                                  "services in town, PopAI knows the area as a local.")]
                                       :prompts ["PopAI, where do we go dancing?"
                                                 "PopAI, is there a laundry in town?"
                                                 "Where do I buy ice?"
                                                 "Where is the best playground in town?"]}
                                      {:heading "Etiquette, customs and more"
                                       :body    [(long-str
                                                  "PopAI is there to help you learn about local history and culture. It"
                                                  "can help prepare you for things you should know before you get to"
                                                  "your destination. It can also offer popular itineraries once you get"
                                                  "to an area.")]
                                       :prompts ["When did BVI become British?"
                                                 "Who discovered the Virgin Islands?"
                                                 "PopAI, what should I do in Virgin Gorda on a Tuesday?"
                                                 "PopAI, how do you say <q>Hi</q> in Croatian?"]}]})

                 (panel {:title      "Boat Mechanic"
                         :subtitle   "You've never been more prepared"
                         :image-path "/panel-engine.png"})

                 (panel {:title      "Sailing Instructor"
                         :subtitle   "Checklists, rules and regulations"
                         :image-path "/panel-textbook.png"})

                 (panel {:title      "Crew Member"
                         :subtitle   "Interact, control and monitor"
                         :image-path "/panel-glenn.png"})]]}))

(defn description [code]
  {:css  [["body > .description" {:margin     0
                                  :padding    "4em 1em"
                                  :background "rgb(var(--background))"
                                  :color      "rgb(var(--foreground))"}]]
   :body [[:div.description
           [:p
            (long-str
             "Are you chartering a boat and going cruising? Will you be in an area with internet connectivity? The"
             "PopAI App is for you. The PopAI App is a lightweight app that will give you access to the latest sailing"
             "almanac for your charter destination, all manufacturer diagrams, schematics and manuals for systems on"
             "your boat. The app also acts as a sailing instructor who knows all rules and regulations, can remind you"
             "common sailing terms and can walk you through how to do most popular maneuvers, etc. It has all COLREGS,"
             "immigration rules and regulations, lights and markers, etc. With the PopAI App you will never feel"
             "unprepared for a charter again. Simply download the app on your favorite mobile device (phone or tablet)"
             "and talk to it with your preferred method.")]
           [:a {:href (str "/store/popai/configure?code=" (name code))} "Configure and Buy Now"]]]})

(defn popai [request]
  (let [code (keyword (:code (:params request)))]
    (if (and code (code targets))
      (page/from-components "PopAI" [page/base
                                     header
                                     hero
                                     get-to-know
                                     features-panels
                                     (description code)
                                     about/footer])
      (resp/redirect "/"))))


(def almanac-request
  (let [time-frames [          [2025 2], [2025 3], [2025 4],
                     [2026 1], [2026 2], [2026 3], [2026 4],
                     [2027 1], [2027 2], [2027 3], [2027 4]]]
    {:body [[:details
             [:summary "Don't see your destination or boat?"]
             [:p (long-str "Let us know where you're going, what you'll be sailing, and when so we can start working on"
                           "the almanac. We'll let you know if they'll be ready in time for your trip and follow up"
                           "once they are.")]
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
    {:css  [[:.total {:grid-column "1 / -1"
                      :text-align  :right
                      :font-size "1.1em"}]]
     :body [[:main
             [:form.sku-selection {:action route-checkout}
              [:input {:type  :hidden
                       :name  :code
                       :value code}]
              [:input {:type  :hidden
                       :name  :product
                       :value :popai}]
              [:label           {:for :location} "Location:"]
              [:select#location {:name :location}
               (map (fn [[area locations]]
                      [:optgroup {:label area}
                       (map (fn [[k v]] [:option {:value k} v]) locations)])
                    locations)]
              [:label       {:for :boat} "Boat:"]
              [:select#boat {:name :boat}
               (map (fn [[k v]] [:option {:value k} v]) boats)]
              [:p.total "Price: $" price]
              [:button {:type :submit} "Checkout"]]
             (first (:body almanac-request))]]}))

(defn configure [request]
  (let [code (keyword (:code (:params request)))]
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
    {:body [[:main
             [:p (long-str "Thank you for your interest, but unfortunately, this isn't a real product yet. We really"
                           "appreciate you giving us your attention and we hope we haven't caused any disruption with our"
                           "experiment.")]
             [:p (long-str "As a thank-you, we'd like to offer you a coupon for 75% off. Hopefully the next time you're"
                           "sailing in" location "or you're on a" (str boat ",") "we'll have an almanac ready to go."
                           "Just give us an email address and we'll send you a message when it's ready to go. Use the"
                           "same email address at checkout and the discount will automatically be applied.")]
             [:p (long-str "Oh, and if you wouldn't mind, we'd love a bit of feedback on the product before you go. No"
                           "worries if you'd rather skip the survey though &mdash; we'll honor the coupon either way."
                           "Thanks again!")]
             [:form
              (apply concat (map (fn [&{:keys [name question answers]}]
                                   [[:label  {:for  name} question]
                                    [:select {:id   name
                                              :name name}
                                     (map (fn [v]
                                            [:option {:value v} v])
                                          (flatten ["-- Select One --"
                                                    answers]))]])
                                 survey))
              [:label              {:for  :emailAddress} "Email Address:"]
              [:input#emailAddress {:name :emailAddress
                                    :type :email}]
              [:button {:type :submit} "Submit"]]]]}))

(defn checkout [request]
  (let [code         (keyword (:code (:params request)))
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
