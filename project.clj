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

(defproject com.popaithesailor.www "unversioned"
  :url "https://www.popaithesailor.com"
  :dependencies [[clout "2.2.1"]
                 [com.github.sikt-no/clj-jwt "0.5.102"]
                 [com.datomic/local "1.0.285"]
                 [environ "1.2.0"]
                 [garden "1.3.10"]
                 [hiccup "1.0.5"]
                 [org.clojure/clojure "1.11.1"]
                 [ring/ring-core "1.13.0"]
                 [ring/ring-jetty-adapter "1.13.0"]]
  :plugins [[dev.weavejester/lein-cljfmt "0.13.0"]
            [lein-environ "1.2.0"]
            [lein-ring "0.12.6"]]
  :ring {:handler com.popaithesailor.www.core/dev-handler}
  :repl-options {:init-ns com.popaithesailor.www.core}
  :main com.popaithesailor.www.core
  :profiles {:dev {:dependencies [[ring-refresh "0.2.0"]
                                  [ring/ring-devel "1.13.0"]]
                   :env {:db-storage   ":mem"
                         :pretty-print "true"
                         :access-log   "nginx/access.log"}}
             :uberjar {:aot :all}})
