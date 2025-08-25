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

(ns com.popaithesailor.www.store.route
  (:require
   [clojure.string :as str]))

(def home "/store/popai/:code")
(def configure (str home "/configure"))
(def checkout (str home "/checkout"))
(def request-almanac (str home "/request-almanac"))
(def discount (str home "/discount"))
(def survey (str home "/survey"))
(def about "/about/:code")

(defn with-code [route code]
  (str/replace-first route ":code" (name code)))
