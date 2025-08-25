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

(ns com.popaithesailor.www.about
  (:require
   [com.popaithesailor.www.page :as page]
   [com.popaithesailor.www.store.request :as request]
   [com.popaithesailor.www.store.route :as route]
   [com.popaithesailor.www.util :refer [long-str]]
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
            [:span "&copy; 2025 Voyage Sense, Inc."]
            [:div.spacer]
            [:a {:href (route/with-code route/about code)} "About"]
            [:div.spacer]
            [:a {:href "mailto:contact@voyagesense.com"} "Contact"]]]]})

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
