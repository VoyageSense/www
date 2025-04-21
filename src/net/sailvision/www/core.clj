(ns net.sailvision.www.core
  (:gen-class)
  (:require
   [clojure.java.io :as io]
   [clout.core :as c]
   [com.github.sikt-no.clj-jwt :as clj-jwt]
   [environ.core :refer [env]]
   [garden.core :as g]
   [hiccup.page :as h]
   [net.sailvision.www.about :as about]
   [net.sailvision.www.admin :as admin]
   [net.sailvision.www.page :as page]
   [net.sailvision.www.store :as store]
   [ring.adapter.jetty :as jetty]
   [ring.middleware.not-modified :as not-modified]
   [ring.middleware.keyword-params :as keyword-params]
   [ring.middleware.params :as params]
   [ring.middleware.resource :as resource]
   [ring.util.response :as resp]))

(defonce public-server (atom nil))
(defonce admin-server (atom nil))

(defn stop-server []
  (when-let [srv @public-server]
    (.stop srv))
  (when-let [srv @admin-server]
    (.stop srv))
  (shutdown-agents))

(defn home []
  {:headers page/headers
   :body
   (h/html5
       (page/head
        {:extra-css
         (g/css
          (page/pretty-print)
          [:#banner {:display         :flex
                     :align-items     :center
                     :justify-content :center
                     :height          "100vh"
                     :margin          0
                     :font-family     "Arial, sans-serif"}])})
     [:body
      [:h1#banner "If you're looking for a specific page, please be sure to follow the exact link that was provided to you. Thank you!"]])})

(defn robots-exclusion []
  {:headers {"Content-Type" "text/plain"}
   :body "User-agent: *\nDisallow: /"})

(defn deploy [request]
  (if-let [next-path (env :next-path)]
    (let [auth    (get (:headers request) "authorization")
          jwk-url "https://token.actions.githubusercontent.com/.well-known/jwks"
          uberjar (:body request)
          oidc    (try
                    (clj-jwt/unsign jwk-url auth)
                    (catch Throwable t
                      (println "Failed to unsign OIDC token:" (ex-message t))))
          oidc-aud  (:aud oidc)
          oidc-repo (:repository oidc)]
      (if (and (= oidc-aud  "prod-deploy")
               (= oidc-repo "SailVision/www"))
        (do
          (println "Writing uberjar to" next-path)
          (with-open [out (io/output-stream next-path)]
            (io/copy uberjar out))
          (println "Finished writing uberjar")
          (future (stop-server))
          {:status 201})
        (do
          (println "Unauthorized deployment attempted - aud:" oidc-aud " repo:" oidc-repo)
          {:status 403})))
    (do
      (println "Deployment attempted but NEXT_PATH is not set")
      {:status 400})))

(defn internal-error []
  {:headers page/headers
   :body (h/html5
          (page/head)
          [:body
           [:div#banner
            [:div
             [:h1 "Something appears to have gone wrong"]
             [:p "Sorry about that! Please try again in a bit or send us a message and we'll take a look."]]]])})

(defn wrap-params [request handler]
  #((keyword-params/wrap-keyword-params handler) (merge % (params/params-request request))))

(defn route [request]
  (condp c/route-matches request
    (c/route-compile store/route-home)            :>> (wrap-params request store/popai)
    (c/route-compile store/route-configure)       :>> (wrap-params request store/configure)
    (c/route-compile store/route-checkout)        :>> (wrap-params request store/checkout)
    (c/route-compile store/route-request-almanac) :>> (wrap-params request store/request-almanac)
    (c/route-compile store/route-survey)          :>> (wrap-params request store/submit-survey)
    (c/route-compile about/route-home)            (about/home)
    (c/route-compile "/robots.txt")               (robots-exclusion)
    (c/route-compile "/i/deploy")                 (deploy request)
    (c/route-compile "/5xx.html")                 (internal-error)
    (c/route-compile "/")                         (home)
    (resp/redirect   "/")))

(defn wrap-cache-control [handler]
  (fn [request]
    (let [response (handler request)]
      (assoc-in response [:headers "Cache-Control"] "public"))))

(defn middlewares [handler]
  (-> handler
      (resource/wrap-resource "public")
      (not-modified/wrap-not-modified)
      (wrap-cache-control)))

(when-let [wrap-refresh (resolve 'ring.middleware.refresh/wrap-refresh)]
  (def dev-handler
    (wrap-refresh
     (fn dev-route [request]
       (condp c/route-matches request
         (c/route-compile (str admin/route-home "*")) ((middlewares admin/route) request)
         ((middlewares route) request))))))

(defn -main []
  (let [public (jetty/run-jetty
                (middlewares route) {:host  "localhost"
                                     :port  8080
                                     :join? false})
        admin  (jetty/run-jetty
                (middlewares admin/route) {:host  "localhost"
                                           :port  9080
                                           :join? false})]
    (reset! public-server public)
    (reset! admin-server admin)))
