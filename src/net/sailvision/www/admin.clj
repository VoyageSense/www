(ns net.sailvision.www.admin
  (:require
   [clout.core :as c]
   [garden.def :refer [defstylesheet]]
   [hiccup.page :as h]
   [net.sailvision.www.db :as db]
   [net.sailvision.www.page :as page]
   [ring.util.response :as resp]))

(def route-requested-almanacs "/admin/requested-almanacs")

(defstylesheet base-css
  [:body {:padding "0em 1em"}]
  [:body {:padding "0em 1em"}]
  [:table {:border-collapse :collapse}]
  [:td :th {:border  "thin solid"
            :padding "0.2em 0.4em"}]
  [:td {:text-align :center}]
  [:th {:background-color "#80808030"}]
  ["tr:nth-child(odd)"
   [:td {:background-color "#80808018"}]])

(defn home []
  {:headers page/headers
   :body (h/html5
          (page/head {:title     "Admin"
                      :extra-css base-css})
          [:body
           [:h1 "Admin Panel"]
           [:ul
            [:li [:a {:href route-requested-almanacs} "Requested Almanacs"]]]])})

(defn requested-almanacs []
  (let [storage (db/storage)
        conn    (db/connect storage :requested-almanacs)]
    {:headers page/headers
     :body (h/html5
            (page/head {:title     "Admin - Requested Almanacs"
                        :extra-css base-css})
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

(defn route [request]
  (condp c/route-matches request
    (c/route-compile route-requested-almanacs) (requested-almanacs)
    (c/route-compile "/admin")                 (home)
    (resp/redirect "/admin")))
