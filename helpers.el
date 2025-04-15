(defun insert-store-code ()
  "Inserts a store code into the buffer"
  (interactive)
  (let ((letters "abcdefghijklmnopqrstuvwxyz")
        (size 5)
        (result ""))
    (dotimes (_ size)
      (setq result (concat result (string (aref letters (random (length letters)))))))
    (insert result)))
