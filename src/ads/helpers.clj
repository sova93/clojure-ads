(ns ads.helpers
  (:require
    [ads.models :as m]
    [buddy.hashers :as hashers]
    ))

(defn login-user [login password]
  (let [user-from-db (m/get-username login)]
    (if (= 1 (:activated user-from-db))
      (if (hashers/check password (:password user-from-db))
        user-from-db
        nil)
      nil)
    )
  )

(defn is-authenticated? [req]
  (if (get-in req [:session :identity :username] nil)
    true
    false))