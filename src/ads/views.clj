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
  (render-file "login.html" {:sess (:session req)}))

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

(defn logout [req]
  (assoc (redirect "/") :session (assoc (:session req) :identity {})))

(defn signup [req]
  (if (h/is-authenticated? req)
    (redirect "/")
    (render-file "signup.html" {:sess (:session req)}))

  )

(defn post-signup [req]
  )

(defn category [category-slug req]
  (let [cat (m/get-cat category-slug)]
    (if cat
      (render-file "category.html" {:cat cat :sess (:session req)})
      (route/not-found "Not found"))))

(defn ad [category-slug ad-id req]
  (render-file "ad.html" {:sess (:session req)}))