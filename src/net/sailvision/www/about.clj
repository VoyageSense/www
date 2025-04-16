(ns net.sailvision.www.about
  (:require
   [net.sailvision.www.page :as page]
   [net.sailvision.www.util :refer [long-str]]))

(def route-home "/about")

(def about-us
  {:body [[:main
           [:p (long-str "We are a small company based out of San Francisco, California that is focused on"
                         "making the joy and sport of sailing safer and more approachable to people of all skill"
                         "levels. The founders, George and Alex, each have extensive experience developing technology"
                         "ranging from cloud infrastructure, to personalized search, to hand-held consumer electronics."
                         "While completing their Coastal Passage Making certification from US Sailing, they couldn't"
                         "help but notice a few aspects of the sailing and chartering experience that could be"
                         "improved.")]
           [:p (long-str "Like what we're doing and are interested in joining the team? We'd love to talk! Send us a"
                         "note using the email address in the footer.")]]]})

(def footer
  {:css  [[:footer {:margin     0
                    :padding    "3em 1em"
                    :display    :flex
                    :background "rgb(var(--background))"
                    :color      "rgb(var(--foreground))"}
           [:.spacer {:flex-grow 1}]]]
   :body [[:footer
           [:span "&copy; 2025 SailVisionPro, LLC"]
           [:div.spacer]
           [:a {:href route-home} "About Us"]
           [:div.spacer]
           [:a {:href "mailto:contact@sailvisionpro.com"} "Contact Us"]]]})

(defn home []
  (page/from-components
   "About"
   [page/base
    about-us
    footer]))
