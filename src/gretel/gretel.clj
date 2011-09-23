(ns gretel.gretel
  (:use [ slingshot.core :only [try+]] ))
(def ^{:dynamic true} *context* (atom {:stack [] :log []}))

(defn context [& _]
  (:stack @*context*))

(defn log [& _]
  (:log @*context*))

(defmacro trying [message & body]
  `(try
     (swap! *context* (fn [ctx#]
                       {:stack (cons ~message (:stack ctx#))
                        :log (conj (:log ctx#) ~message)}))
    ~@body
    (finally
     (swap! *context* (fn [ctx#]
                         {:stack (rest (:stack ctx#))
                          :log (:log ctx#)})))))
