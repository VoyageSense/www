(defproject com.sailvisionpro.www "unversioned"
  :url "https://sailvisionpro.com"
  :dependencies [[org.clojure/clojure "1.11.1"]
                 [ring/ring-core "1.13.0"]
                 [ring/ring-jetty-adapter "1.13.0"]
                 [hiccup "1.0.5"]
                 [clout "2.2.1"]
                 [garden "1.3.10"]]
  :plugins [[lein-ring "0.12.6"]]
  :ring {:handler com.sailvisionpro.www/refreshingHandler}
  :repl-options {:init-ns com.sailvisionpro.www}
  :main com.sailvisionpro.www
  :profiles {:dev {:dependencies [[ring-refresh "0.2.0"]
                                  [ring/ring-devel "1.13.0"]]}
             :uberjar {:aot :all}})
