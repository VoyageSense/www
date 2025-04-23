(ns net.sailvision.www.admin
  (:require
   [clojure.string :as str]
   [clout.core :as c]
   [garden.core :as g]
   [hiccup.page :as h]
   [net.sailvision.www.db :as db]
   [net.sailvision.www.nginx :as nginx]
   [net.sailvision.www.page :as page]
   [ring.util.response :as resp]))

(def route-home "/admin")
(def route-requested-almanacs "/admin/requested-almanacs")
(def route-page-views "/admin/page-views")
(def route-discount-signups "/admin/discount-signups")
(def route-survey-responses "/admin/survey-responses")

(defn base-css [& extra-css]
  (g/css
   (page/pretty-print)
   [:body {:padding "0em 1em"}]
   [:body {:padding "0em 1em"}]
   [:table {:border-collapse :collapse}]
   [:td :th {:border  "thin solid"
             :padding "0.2em 0.4em"}]
   [:th {:background-color "#80808030"}]
   ["tr:nth-child(odd)"
    [:td {:background-color "#80808018"}]]
   extra-css))

(defn home []
  {:headers page/headers
   :body (h/html5
          (page/head {:title     "Admin"
                      :extra-css (base-css)})
          [:body
           [:h1 "Admin Panel"]
           [:ul
            [:li [:a {:href route-requested-almanacs} "Requested Almanacs"]]
            [:li [:a {:href route-page-views}         "Page Views"]]
            [:li [:a {:href route-discount-signups}   "Discount Signups"]]
            [:li [:a {:href route-survey-responses}   "Survey Responses"]]]])})

(defn requested-almanacs []
  (let [storage (db/storage)
        conn    (db/connect storage :requested-almanacs)]
    {:headers page/headers
     :body (h/html5
            (page/head {:title     "Admin - Requested Almanacs"
                        :extra-css (base-css [:td {:text-align :center}])})
            [:body
             [:h1 "Requested Almanacs"]
             [:table
              [:tr
               [:th "Destination"]
               [:th "Boat"]
               [:th "Email Address"]]
              (map (fn [request]
                     [:tr
                      [:td (:destination   request)]
                      [:td (:boat          request)]
                      [:td (:email-address request)]])
                   (db/list-requested-almanacs {:conn conn}))]])}))

(defn page-views []
  (let [log         (nginx/read-access-log)
        store-gets  (filter #(and (str/starts-with? (:request %) "GET /store")
                                  (= (:status %) 200))
                            log)
        view-counts (frequencies (map :request    store-gets))
        user-agents (frequencies (map :user-agent store-gets))]
    {:headers page/headers
     :body (h/html5
            (page/head {:title     "Admin - Page Views"
                        :extra-css (base-css [:table {:max-width    "100%"
                                                      :table-layout :auto}])})
            [:body
             [:h1 "Store Pages (OK)"]
             [:table
              [:tr
               [:th "Request"]
               [:th "Count"]]
              (map (fn [[req count]]
                     [:tr [:td req] [:td count]])
                   (reverse (sort-by (fn [[_ v]] v) view-counts)))]
             [:br]
             [:table
              [:tr
               [:th "User Agent"]
               [:th "Count"]]
              (map (fn [[agent count]]
                     [:tr [:td agent] [:td count]])
                   (reverse (sort-by (fn [[_ v]] v) user-agents)))]])}))

(defn discount-signups []
  (let [storage (db/storage)
        conn    (db/connect storage :discount-signups)
        signups (db/list-discount-signups {:conn conn})]
    {:headers page/headers
     :body (h/html5
            (page/head {:title     "Admin - Discount Signups"
                        :extra-css (base-css [:td {:text-align :center}])})
            [:body
             [:h1 "Discount Signups"]
             [:table
              [:tr
               [:th "Store Code"]
               [:th "Email Address"]]
              (map (fn [response]
                     [:tr
                      [:td (:store-code    response)]
                      [:td (:email-address response)]])
                   signups)]])}))

(defn survey-responses []
  (let [storage   (db/storage)
        conn      (db/connect storage :survey-responses)
        responses (db/list-survey-responses {:conn conn})]
    {:headers page/headers
     :body (h/html5
            (page/head {:title     "Admin - Survey Responses"
                        :extra-css (base-css [:td {:text-align :center}])})
            [:body
             [:h1 "Survey Responses"]
             [:table
              [:tr [:th "Blob"]]
              (map (fn [response]
                     [:tr [:td response]])
                   responses)]])}))

(defn route [request]
  (condp c/route-matches request
    (c/route-compile route-requested-almanacs) (requested-almanacs)
    (c/route-compile route-page-views)         (page-views)
    (c/route-compile route-discount-signups)   (discount-signups)
    (c/route-compile route-survey-responses)   (survey-responses)
    (c/route-compile route-home)               (home)
    (resp/redirect   route-home)))
