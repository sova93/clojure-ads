(ns ads.views
  (:require [clojure.java.io]
            [ring.util.response :refer [response]]
            [ads.models :as m]
            [selmer.parser :refer [render-file]]
            [compojure.route :as route]))


(defn index [session]
  (render-file "index.html" {
                             :cats (m/get-cats)
                             :sess (:session session)})
  )

(defn login [req]
  (render-file "login.html" {}))

(defn post-login [{{login :login password :password} :params :as req}]
  (render-file "login.html" {:arg req :l login :p password}))

(defn category [category-slug]
  (let [cat (m/get-cat category-slug)]
    (if cat
      (render-file "category.html" {:cat cat})
      (route/not-found "Not found"))))




(defn ad [category-slug ad-id]
  (render-file "ad.html" {}))