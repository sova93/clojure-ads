(ns ads.validations
  (:require [clojure.string :refer [blank?]]))

(defn check-range [str min max]
  (not (or (blank? str) (> (count str) max) (< (count str) min))))