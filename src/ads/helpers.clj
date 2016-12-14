(ns ads.helpers
  :require
  [
   [buddy.hashers :as hashers]
   ]
  (:require [buddy.hashers :as hashers]))

(defn login-user [login password]
  (let [hashed-prompted-password (hashers/encrypt password)]
    )
  nil)