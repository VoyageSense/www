(ns com.sailvisionpro.www
  (:require [clout.core :as c]
            [ring.middleware.refresh :as refresh]
            [ring.middleware.resource :as resource]
            [hiccup.page :as h]
            [ring.adapter.jetty :as jetty]
            [garden.core :as g]
            [garden.stylesheet :as s]))

(def css
  (g/css [:body
          {:margin 0}]
         [:#slogan
          {:display :flex}
          {:align-items :center}
          {:justify-content :center}
          {:height "100vh"}
          {:margin 0}
          {:font-family "Arial, sans-serif"}]
         (s/at-media {:prefers-color-scheme :dark}
                     [:body
                      {:background (s/rgb 30 30 30)}
                      {:color (s/rgb 200 200 200)}])
         (s/at-media {:prefers-color-scheme :light}
                     [:body
                      {:background (s/rgb 245 245 245)}
                      {:color (s/rgb 50 50 50)}])))

(defn home []
  {:headers {"Content-Type" "text/html"}
   :body (h/html5
          [:head
           [:title "SailVision"]
           [:link {:rel "icon" :type "image/png" :href "/favicon.svg"}]
           [:style css]]
          [:body
           [:h1#slogan "The future of sailing is here"]])})

(defn not-found []
  {:status 404
   :headers {"Content-Type" "text/plain"}
   :body "not found"})

(defn route [request]
  (condp c/route-matches request
    (c/route-compile "/") (home)
    (not-found)))

(def handler
  (resource/wrap-resource route "public"))

(def refreshingHandler
  (refresh/wrap-refresh handler))

(defn -main []
  (jetty/run-jetty handler {:port 8080}))
