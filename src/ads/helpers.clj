(ns ads.helpers
  (:require
    [ads.models :as m]
    [buddy.hashers :as hashers]
    [selmer.parser :refer [render-file cache-off! cache-on!]]
    ))

(cache-off!)
;(cache-on!)

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

(defn get-counter-value [k]
  (get counter k))

(defn register-new-request-to-counter [uri]
  (get (swap! counter assoc uri (inc (get @counter uri 0))) uri)
  )

(defn file-renderer [req filename context-map & xs]
  ;(render-file filename context-map xs)
  (render-file filename (-> context-map
                            (assoc :req req)
                            (assoc :sess (:session req))
                            (assoc :counter (:counter req))
                            ) xs)
  )