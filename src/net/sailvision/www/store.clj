(ns net.sailvision.www.store
  (:require
   [clojure.string :as str]
   [hiccup.page :as h]
   [net.sailvision.www.db :as db]
   [net.sailvision.www.page :as page]
   [net.sailvision.www.util :refer [long-str]]
   [garden.core :as g]
   [garden.stylesheet :as s]
   [ring.util.codec :as codec]
   [ring.util.response :as resp]))

(def route-home "/store/:token")
(def route-checkout (str route-home "/checkout"))
(def route-request-almanac "/store/request-almanac")

(defn boats [token]
  (case token
    :popai {:sun-odyssey-410 "Jeanneau Sun Odyssey 410"
            :oceanis-42.3    "Beneteau Oceanis 42.3"
            :dufour-41       "Dufour 41"
            :dufour-44       "Dufour 44"
            :oceanis-46.1    "Beneteau Oceanis 46.1"}
    nil))

(defn locations [token]
  (case token
    :popai {"Carribean"
            {:usvi-bvi        "Virgin Islands (British and United States)"
             :leeward-islands "Leeward Islands"
             :turks-caicos    "Turks and Caicos Islands"}
            "South Pacific"
            {:tahiti          "Tahiti"}}
    nil))

(def time-frames [[2025 2], [2025 3], [2025 4],
                  [2026 1], [2026 2], [2026 3], [2026 4],
                  [2027 1], [2027 2], [2027 3], [2027 4]])

(def header
  [:header
   [:h1 "PopAI"]
   [:h2 "The ultimate boating companion"]
   [:div]
   [:h3
    [:span "Keep your hands on the helm and eyes on the water"]
    [:span "Use the power of your voice to manage your boating experience"]]])

(def hero-css-loaded
  {:opacity   1
   :transform :none})

(def hero-css
  [[:.hero {:width          "100%"
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
   [:.loaded hero-css-loaded]
   [:.hero-mask {:width      "100%"
                 :height     "calc(min(70vw, 80vh))"
                 :background "linear-gradient(to bottom, transparent 85%, rgba(var(--background), 0.9))"
                 :z-index    -1}]
   [:.over-hero {:background "rgba(var(--background), 0.9)"}]])

(def hero-image-noscript
  [:.hero hero-css-loaded])

(def hero-image-light
  [:img.hero.light {:src    "/popai-hero-background-light.jpg"
                    :alt    "Looking over the bow of a boat sailing in the San Francisco bay, the city in the background"
                    :onload "this.classList.add('loaded')"}])

(def hero-image-dark
  [:img.hero.dark {:src    "/popai-hero-background-dark.jpg"
                   :alt    "Looking over the bow of a boat sailing in the San Francisco bay, the sunset in the background"
                   :onload "this.classList.add('loaded')"}])

(def hero-mask
  [:div.hero-mask])

(def almanac-request
  [:details
   [:summary "Don't see your destination or boat?"]
   [:p "Let us know where you're going, what you'll be sailing, and when so we can start working on the almanac. We'll
      let you know if they'll be ready in time for your trip and follow up once they are."]
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
    [:button {:type :submit} "Request Almanac"]]])

(def header-css
  [[:body
    [:header {:padding     "0.8em"
              :display     :flex
              :overflow    :hidden
              :gap         "0.5em"
              :align-items "last baseline"
              :color       "rgb(var(--bold-foreground))"
              :text-shadow "0.05em 0.1em 0.2em rgb(var(--bold-background))"}
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
                 [:h3 {:display :none}]]])])

(defn base-css [& extra-css]
  (g/css
   (page/pretty-print)
   [:main
    {:margin "1em 1em"}]
   [:details :form
    {:margin "0em 1em"}]
   (s/at-media {:prefers-color-scheme :dark}
               [":root" {:--bold-foreground  "240 240 240"
                         :--bold-background  "1 1 1"
                         :--light-visibility "hidden"
                         :--dark-visibility  "visible"}]
   (s/at-media {:prefers-color-scheme :light}
               [":root" {:--bold-foreground  "20 20 20"
                         :--bold-background  "255 255 255"
                         :--light-visibility "visible"
                         :--dark-visibility  "hidden"}]))
   [:details
    {:margin-top "1em"}
    [:summary
     {:cursor :pointer}]]
   [:form
    {:display               :grid
     :grid-template-columns "auto 1fr"
     :gap   "0.3em"
     :width :fit-content}
    [:button
     {:grid-column  "span 2"
      :justify-self :center
      :padding      "0.3em 1em"}]]
   header-css
   extra-css))

(def form-validation-css
  (g/css
   (page/pretty-print)
   ["input:not([type=\"submit\"])"
    {:box-sizing :border-box}
    {:border "medium solid transparent"}]
   ["input:valid:not(:focus):not(:placeholder-shown)"
    {:border "medium solid #00FF0060"}]
   ["input:invalid:not(:focus):not(:placeholder-shown)"
    {:border "medium solid #FF000090"}]
   [:dialog
    {:border     0
     :background :transparent}]
   ["dialog::backdrop"
    {:backdrop-filter "blur(3px)"}]
   [:#dialog-content
    {:display         :flex
     :width           "100%"
     :height          "100%"
     :align-items     :center
     :justify-content :center}]))

(defn checkout-form [{:keys [boats locations token]}]
  [:form.sku-selection {:action (str/replace-first route-checkout ":token" (name token))}
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
   [:button {:type :submit} "Checkout"]])

(def get-to-know-css
  [:.get-to-know {:margin 0
                  :padding "4em 2em"}
   [:h1 {:margin  0
         :display :inline}]
   [:h2 {:font-size-adjust "0.3"
         :padding-left     "0.4em"
         :display          :inline}]
   [:.emphasis {:font-weight     :bold
                :font-style      :italic
                :text-decoration :underline}]])

(def get-to-know
  [:div.get-to-know.over-hero
   [:h1 "Get to know PopAI"]
   [:h2 "pronounced " [:q "Popeye"]]
   [:p "PopAI is a voice controlled boating helper. It is a part "
    [:span.emphasis "guide"] ", part "
    [:span.emphasis "mechanic"]", part "
    [:span.emphasis "instructor"] ", and part "
    [:span.emphasis "crew member" "."]]])

(def features-panels-css
  (let [soft-image-border 2]
    [[:.panels {:display :flex
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
       [:span.body    {:font-size "1.1em"}]
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
     ["dialog.feature::backdrop" {:backdrop-filter "blur(10px)"}]]))

(def features-panels-noscript
  [:dialog.feature
   [:.content
    [:.prompt.left :.prompt.right {:opacity   1
                                   :transform :none}]]])

(defn panel-script [id]
  [:script
   (long-str
    [(str "document.getElementById('" id "').addEventListener('toggle', (event) => {")
     "shownfn = event.target.matches(':popover-open')"
     "? (prompt) => prompt.classList.add('shown')"
     ": (prompt) => prompt.classList.remove('shown');"
     "event.target.querySelectorAll('.prompt').forEach(shownfn);"
     "});"])])

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
               [:p [:span.heading heading] " " [:span.body body]]
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
  [:div.panels.over-hero
   (panel {:title      "Cruising Guide"
           :subtitle   "Boating almanac on the go"
           :image-path "/panel-boat.png"
           :intro      "... talking to a local guide has never been simpler."
           :details    [{:heading "Local Navigation"
                         :body    (long-str
                                   ["Local weather patterns and seasonal considerations, local rules and regulations,"
                                    "fuel docks and provisioning spots, customs and immigration procedures."
                                    "PopAI is there to help make your boating experience smooth and stress free."])
                         :prompts ["PopAI, what are the predominant winds for this part of the year?"
                                   "PopAI, can we anchor in Cam Bay National Park?"
                                   "PopAI, how do I clear customs in Tortola?"]}
                        {:heading "Marinas, Anchorages and Points of Interest."
                         :body    (long-str
                                   ["Planning your day has never been easier."
                                    "Simply tell PopAI what activities you want to do and it will suggest areas"
                                    "around you where you can do those."])
                         :prompts ["PopAI, where do I snorkel to see Manta rays?"
                                   "What types of fish are visible at this diving spot?"
                                   "Where is a child friendly beach to anchor?"
                                   "PopAI, I need fuel, fresh water and a hot shower tonight. Which marina should I go to?"
                                   "How do I call the marina?"]}
                        {:heading "Off The Water Insights"
                         :body    (long-str
                                   ["From the best restaurants and bars, to where to get groceries and services"
                                    "in town, PopAI knows the area as a local."])
                         :prompts ["PopAI, where do we go dancing?"
                                   "PopAI, is there a laundry in town?"
                                   "Where do I buy ice?"
                                   "Where is the best playground in town?"]}
                        {:heading "Etiquette, customs and more"
                         :body    (long-str
                                   ["PopAI is there to help you learn about local history and culture."
                                    "It can help prepare you for things you should know before you get"
                                    "to your destination. "
                                    "It can also offer popular itineraries once you get to an area."])
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
           :image-path "/panel-glenn.png"})])

(def description-css
  ["body > .description" {:margin  0
                          :padding "4em 1em"}])

(def description
  [:p.description.over-hero
   (long-str
    ["Are you chartering a boat and going cruising?"
     "Will you be in an area with internet connectivity?"
     "The PopAI App is for you."
     "The PopAI App is a lightweight app that will give you access to the latest sailing almanac for your charter destination, all manufacturer diagrams, schematics and manuals for systems on your boat."
     "The app also acts as a sailing instructor who knows all rules and regulations, can remind you common sailing terms and can walk you through how to do most popular maneuvers, etc."
     "It has all COLREGS, immigration rules and regulations, lights and markers, etc."
     "With the PopAI  App you will never feel unprepared for a charter again."
     "Simply download the app on your favorite mobile device (phone or tablet) and talk to it with your preferred method."])])

(defn popai [request]
  (let [token             (keyword (:token request))
        [boats locations] [(boats     token)
                           (locations token)]]
    (if (and boats locations)
      {:headers page/headers
       :body
       (h/html5
        (page/head :extra-css (base-css hero-css
                                        get-to-know-css
                                        features-panels-css
                                        description-css)
                   :noscript  [:style
                               (g/css
                                (page/pretty-print)
                                [hero-image-noscript
                                 features-panels-noscript])])
        [:body
         header
         hero-image-light
         hero-image-dark
         hero-mask
         get-to-know
         features-panels
         description])}
      (resp/redirect "/"))))

(def checkout-modal
  [:div#dialog-content
   [:p "Sorry to mislead, but this isn't a real product (yet!)"]])

(defn checkout [request]
  (let [token    (keyword (:token request))
        params   (codec/form-decode (:query-string request))
        location (get (reduce-kv (fn [acc k v]
                                   (merge acc v)) {} (locations token))
                      (keyword (get params "location")))
        boat     (get (boats token)
                      (keyword (get params "boat")))]
    (if (and location boat)
      {:headers page/headers
       :body
       (h/html5
        (page/head {:title "Checkout" :extra-css form-validation-css})
        [:body
         [:p (str "Purchasing almanac for " location " aboard a " boat ".")]
         [:dialog#modal {:popover true}
          checkout-modal]])}
      {:status 401
       :headers page/headers
       :body "invalid product configuration"})))

(defn request-almanac [request]
  (let [params  (codec/form-decode (:query-string request))
        storage (db/storage)
        conn    (db/connect storage :requested-almanacs)]
    (db/insert-requested-almanac (into {:conn conn} (map (fn [[k v]] [(keyword k) v]) params)))))
