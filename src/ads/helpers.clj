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
  (if (get-in req [:session :identity :login] nil)
    true
    false))

(def counter (atom {}))

(defn register-new-request-to-counter [req]
  (let [uri (:uri req)]
    (get (swap! counter assoc uri (inc (get @counter uri 1))) uri)
    ))

