(ns net.sailvision.www.about
  (:require
   [net.sailvision.www.page :as page]
   [net.sailvision.www.store.request :as request]
   [net.sailvision.www.store.route :as route]
   [net.sailvision.www.util :refer [long-str]]
   [ring.util.response :as resp]))

(def about-us
  {:body [[:main.body-width
           [:p (long-str "We are a small company based out of San Francisco, California that is focused on"
                         "making the joy and sport of sailing safer and more approachable to people of all skill"
                         "levels. The founders, George and Alex, each have extensive experience developing technology"
                         "ranging from cloud infrastructure, to personalized search, to hand-held consumer electronics."
                         "While completing their Coastal Passage Making certification from US Sailing, they couldn't"
                         "help but notice a few aspects of the sailing and chartering experience that could be"
                         "improved.")]
           [:p (long-str "Like what we're doing and are interested in joining the team? We'd love to talk! Send us a"
                         "note using the email address in the footer.")]]]})

(defn footer [code]
  {:css  [[:footer {:padding "1em 0"
                    :display :flex}
           [:.spacer {:flex-grow 1}]
           [:a {:color "rgb(var(--link-unvisited))"}]
           ["a:visited" {:color "rgb(var(--link-visited))"}]]
          [:.footer-background {:color "rgb(var(--foreground))"}]]
   :body [[:div.footer-background.full-width
           [:footer.body-width
            [:span "&copy; 2025 SailVisionPro, LLC"]
            [:div.spacer]
            [:a {:href (route/with-code route/about code)} "About"]
            [:div.spacer]
            [:a {:href "mailto:contact@sailvisionpro.com"} "Contact"]]]]})

(defn home [request]
  (if-let [[code _config] (request/validate request)]
    (page/from-components
     "About"
     [page/base
      (page/header code)
      page/header-spacer
      about-us
      footer])
    (resp/redirect "/")))
