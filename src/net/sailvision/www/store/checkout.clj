(ns net.sailvision.www.store.checkout
  (:require
   [clojure.java.io :as io]
   [clojure.string :as str]
   [net.sailvision.www.about :as about]
   [net.sailvision.www.db :as db]
   [net.sailvision.www.page :as page]
   [net.sailvision.www.store.route :as route]
   [net.sailvision.www.store.request :as request]
   [net.sailvision.www.util :refer [inline]]
   [ring.util.response :as resp]))

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
              [:form.email.soft-outline {:action (route/with-code route/discount code)
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
              [:form.survey.soft-outline {:action (route/with-code route/survey code)
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

(defn page [request]
  (if-let [[code config] (request/validate request)]
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
        (page/header code)
        page/header-spacer
        (thank-you {:location location
                    :boat     boat
                    :code     code})
        (about/footer code)]))
    (resp/redirect "/")))

;;
;; Form Handlers
;;

(defn submit-survey [request]
  (if-let [[code _config] (request/validate request)]
    (let [storage (db/storage)
          conn    (db/connect storage :survey-responses)
          blob    (pr-str (merge {:code (name code)}
                                 (:params request)))]
      (db/insert-survey-response {:conn conn
                                  :blob blob})
      (page/from-components
       "Survey"
       [page/base
        (page/header code)
        page/header-spacer
        {:body [[:main.body-width
                 [:p "Thank you for your feedback!"]]]}
        (about/footer code)]))
    (resp/redirect "/")))


(defn discount [request]
  (if-let [[code _config] (request/validate request)]
    (let [storage (db/storage)
          conn    (db/connect storage :discount-signups)
          address (:emailAddress (:params request))]
      (db/insert-discount-signup {:conn          conn
                                  :store-code    (name code)
                                  :email-address address})
      (page/from-components
       "Discount Signup"
       [page/base
        (page/header code)
        page/header-spacer
        {:body [[:main.body-width
                 [:p "Got it! Thanks again."]]]}
        (about/footer code)]))
    (resp/redirect "/")))
