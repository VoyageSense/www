(ns net.sailvision.www.admin
  (:require
   [clout.core :as c]
   [garden.def :refer [defstylesheet]]
   [hiccup.page :as h]
   [net.sailvision.www.page :as page]
   [ring.util.response :as resp]))

(defstylesheet base-css
  [:body {:padding "0em 1em"}])

(defn home []
  {:headers {"Content-Type" "text/html"}
   :body (h/html5
          (page/head {:title "Admin"
                      :extra-css base-css})
          [:body
           [:h1 "Admin Panel"]])})

(defn route [request]
  (condp c/route-matches request
    (c/route-compile "/admin") (home)
    (resp/redirect "/admin")))
