;; Copyright 2025 PopaiTheSailor Authors
;;
;; This program is free software: you can redistribute it and/or modify
;; it under the terms of the GNU Affero General Public License as published by
;; the Free Software Foundation, either version 3 of the License, or
;; (at your option) any later version.
;;
;; This program is distributed in the hope that it will be useful,
;; but WITHOUT ANY WARRANTY; without even the implied warranty of
;; MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
;; GNU Affero General Public License for more details.
;;
;; You should have received a copy of the GNU Affero General Public License
;; along with this program.  If not, see <https://www.gnu.org/licenses/>.

(defun insert-store-code ()
  "Inserts a store code into the buffer"
  (interactive)
  (let ((letters "abcdefghijklmnopqrstuvwxyz")
        (size 5)
        (result ""))
    (dotimes (_ size)
      (setq result (concat result (string (aref letters (random (length letters)))))))
    (insert result)))
