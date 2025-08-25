;;;; Copyright 2025 PopaiTheSailor Authors
;;;;
;;;; This program is free software: you can redistribute it and/or modify
;;;; it under the terms of the GNU Affero General Public License as published by
;;;; the Free Software Foundation, either version 3 of the License, or
;;;; (at your option) any later version.
;;;;
;;;; This program is distributed in the hope that it will be useful,
;;;; but WITHOUT ANY WARRANTY; without even the implied warranty of
;;;; MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
;;;; GNU Affero General Public License for more details.
;;;;
;;;; You should have received a copy of the GNU Affero General Public License
;;;; along with this program.  If not, see <https://www.gnu.org/licenses/>.

(ns com.popaithesailor.www.core
  (:gen-class)
  (:require
   [clojure.java.io :as io]
   [clout.core :as c]
   [com.github.sikt-no.clj-jwt :as clj-jwt]
   [com.popaithesailor.www.about :as about]
   [com.popaithesailor.www.admin :as admin]
   [com.popaithesailor.www.page :as page]
   [com.popaithesailor.www.store.checkout :as store-checkout]
   [com.popaithesailor.www.store.configuration :as store-configuration]
   [com.popaithesailor.www.store.popai :as store-popai]
   [com.popaithesailor.www.store.route :as store-route]
   [environ.core :refer [env]]
   [ring.adapter.jetty :as jetty]
   [ring.middleware.content-type :as content-type]
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

(def home
  (page/from-components
   nil
   [page/base
    {:css  [[:.banner {:display         :flex
                       :align-items     :center
                       :justify-content :center
                       :font-size       "1.2em"}]]
     :body [[:p.body-width.banner "If you&rsquo;re looking for a specific page, please be sure to follow the exact link that was provided."]]}]))

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
               (= oidc-repo "VoyageSense/www"))
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

(defn tarpit []
  (Thread/sleep 10000)
  {:status 429})

(def internal-error
  (page/from-components
   "Error"
   [page/base
    {:css  [[:.banner {:display         :flex
                       :flex-direction  :column
                       :align-items     :center
                       :justify-content :center}]]
     :body [[:div.body-width.banner
             [:h1 "Something appears to have gone wrong"]
             [:p "Sorry about that! Please try again in a bit or send us a message and we'll take a look."]]]}]))

(defn wrap-params [request handler]
  #((keyword-params/wrap-keyword-params handler) (merge % (params/params-request request))))

(defn route [request]
  (condp c/route-matches request
    (c/route-compile store-route/home)            :>> (wrap-params request store-popai/page)
    (c/route-compile store-route/configure)       :>> (wrap-params request store-configuration/page)
    (c/route-compile store-route/request-almanac) :>> (wrap-params request store-configuration/request-almanac)
    (c/route-compile store-route/checkout)        :>> (wrap-params request store-checkout/page)
    (c/route-compile store-route/discount)        :>> (wrap-params request store-checkout/discount)
    (c/route-compile store-route/survey)          :>> (wrap-params request store-checkout/submit-survey)
    (c/route-compile store-route/about)           :>> (wrap-params request about/home)
    (c/route-compile "/robots.txt")               (robots-exclusion)
    (c/route-compile "/i/deploy")                 (deploy request)
    (c/route-compile "/5xx.html")                 internal-error
    (c/route-compile "/tarpit")                   (tarpit)
    (c/route-compile "/")                         home
    (resp/redirect   "/")))

(defn wrap-cache-control [handler]
  (fn [request]
    (let [response (handler request)]
      (assoc-in response [:headers "Cache-Control"] "public"))))

(defn middlewares [handler]
  (-> handler
      (resource/wrap-resource "public")
      (content-type/wrap-content-type)
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
