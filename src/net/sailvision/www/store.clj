(ns net.sailvision.www.store
  (:require
   [clojure.java.io :as io]
   [clojure.string :as str]
   [net.sailvision.www.about :as about]
   [net.sailvision.www.db :as db]
   [net.sailvision.www.page :as page]
   [net.sailvision.www.util :refer [inline]]
   [garden.core :as g]
   [garden.stylesheet :as s]
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
          [:.background {:position :absolute
                         :z-index  -2}
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

(def spacer
  {:css  [[:.spacer {:height     "60vh"
                     :background "rgb(var(--background))"}]]
   :body [[:div.spacer.full-width]]})

(defn popai [request]
  (if-let [[code config] (validate request)]
    (page/from-components nil [page/base
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
                               spacer
                               (flyout [{:left  "Asking a question?"
                                         :right "Yes, you are."}]
                                       {:color       "white"
                                        :text-shadow "0.1em 0.2em 0.6em black"})
                               spacer
                               (flyout [{:left  "Asking a question?"
                                         :right "Yes, you are."}]
                                       {:color       "white"
                                        :text-shadow "0.1em 0.2em 0.6em black"})
                               spacer
                               (flyout [{:left  "Asking a question?"
                                         :right "Yes, you are."}]
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
