(ns ads.validations
  (:require [clojure.string :refer [blank?]]
            [ads.models :as m]))

(defn check-range [str min max]
  (not (or (blank? str) (> (count str) max) (< (count str) min))))

(defn check-in [vl coll]
  (.contains coll vl))

(defn check-login-free? [login]
  (let [user-from-db (m/get-username login)]
    (if user-from-db
      false
      true)
    )
  )