(ns ads.views
  (:require [clojure.java.io]
            [ring.util.response :refer [response redirect]]
            [ads.models :as m]
            [selmer.parser :refer [render-file]]
            [compojure.route :as route]
            [ads.helpers :as h]))


(defn index [session]
  (render-file "index.html" {
                             :cats (m/get-cats)
                             :sess (:session session)})
  )

(defn login [req]
  (render-file "login.html" {}))

(defn post-login [{{login :login password :password} :params session :session :as req}]
  (let [user-from-db (h/login-user login password)]
    (if user-from-db
      (do
        (assoc (redirect "/") :session (assoc session :identity (dissoc user-from-db :password))))
      (do
        (redirect "/"))
      )
    )
  )

(defn category [category-slug]
  (let [cat (m/get-cat category-slug)]
    (if cat
      (render-file "category.html" {:cat cat})
      (route/not-found "Not found"))))




(defn ad [category-slug ad-id]
  (render-file "ad.html" {}))