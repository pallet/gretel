(ns gretel.gretel-test
  (:use gretel.gretel :reload-all)
  (:use clojure.test))

(defn reset-context []
  (reset! *context* {:stack [] :log []}))

  (defn sl [m]
    (Thread/sleep m))

(deftest all-tests
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
    (is (= ["D" "C" "A"] (trying "A"
                                 (trying "B")
                                 (trying "C"
                                         (trying "D" (context)))))))

  (testing "multithread"
    (reset-context)
    (trying "A"
            (map #(is (= ["A" %] (trying % (context)))) [ "B" "C" "D"]))
    (is (= [] (context)))
    (trying "A"
            (pmap (fn [[s m]]
                    (sl (* 100 s))
                    (is (= ["A" m])
                        (trying m ))) [[3 "B"] [2 "C"] [1 "D"]]))
    (reset-context)
    (pmap (fn [[s m]]
            (sl (* 100 s))
            (trying m))
          [[3 "A"] [2 "B"] [1 "C"]])
    (println (log))
    (is ( = ["A" "B" "C"] (log)))))
