(ns gretel.gretel-test
  (:use gretel.gretel :reload-all)
  (:use clojure.test))

(defn reset-context []
  (reset! *context* {:stack [] :log []}))

(testing "The log is built correctly"
  (reset-context)
  (is (= [] (log)))
  (is (= ["A"] (log (trying "A"))))
  (is (= ["A" "B"] (log (trying "B"))))
  (is (= ["A" "B" "C" "D"] (log (trying "C"
                                        (trying "D")))))
  (reset-context)
  (is (= ["A" "B" "C" "D"] (log (trying "A"
                                        (trying "B")
                                        (trying "C"
                                                (trying "D")))))))

(testing "the stack is correctly built"
  (reset-context)
  (is (= [] (context)))
  (is (= ["A"] (trying "A" (context))))
  (is (= ["B"] (trying "B" (context))))
  (is (= ["D" "C"] (trying "C" (trying "D" (context)))))
  (is (= ["C" "C" "A"] (trying "A"
                               (trying "B")
                               (trying "C"
                                       (trying "D" (context)))))))
