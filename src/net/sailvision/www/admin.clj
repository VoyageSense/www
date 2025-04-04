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

(defn base-css [& extra-css]
  (g/css
   {:pretty-print? false}
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
            [:li [:a {:href route-page-views}         "Page Views"]]]])})

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
               [:th "Time Frame"]
               [:th "Email Address"]]
              (map (fn [request]
                     [:tr
                      [:td (:destination   request)]
                      [:td (:boat          request)]
                      [:td (:time-frame    request)]
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

(defn route [request]
  (condp c/route-matches request
    (c/route-compile route-requested-almanacs) (requested-almanacs)
    (c/route-compile route-page-views)         (page-views)
    (c/route-compile route-home)                 (home)
    (resp/redirect   route-home)))
