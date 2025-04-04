(ns net.sailvision.www.nginx
  (:require [clojure.string :as str]))

(def access-log-path "/var/log/nginx/access.log")

(def access-log-pattern
  (re-pattern
   #"(?x)
    ^(\S+)           # remote addr
    \s+(\S+)         # remote user (dash if missing)
    \s+(\S+)         # auth user
    \s+\[([^\]]+)\]  # date/time
    \s+\"([^\"]*)\"  # request line
    \s+(\d{3})       # status
    \s+(\S+)         # body bytes sent
    \s+\"([^\"]*)\"  # referer
    \s+\"([^\"]*)\"$ # user agent
   "))

(defn parse-line [line]
  (when-let [[_ ip remote-user auth-user datetime request status bytes referer user-agent]
             (re-matches access-log-pattern line)]
    {:ip ip
     :remote-user remote-user
     :auth-user auth-user
     :datetime datetime
     :request request
     :status (Integer/parseInt status)
     :bytes (if (= bytes "-") 0 (Integer/parseInt bytes))
     :referer referer
     :user-agent user-agent}))

(defn read-access-log []
  (keep parse-line (str/split-lines (slurp access-log-path))))
