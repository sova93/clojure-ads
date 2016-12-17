(ns handler
  (:use
    [ring.util.response])
  (:require [clojure.java.io]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.reload :as reload]
            [compojure.handler :refer [site]]
            [ring.util.anti-forgery :refer [anti-forgery-field]]

            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [ring.middleware.session :as session]
            [ring.middleware.session.cookie :as session-store]
            [buddy.auth.backends :as backends]
            [buddy.auth.middleware :refer [wrap-authentication wrap-authorization]]
            [selmer.parser :as parser]
            [ads.views :as v]
            [ads.models :as m])
  )



(defroutes app-routes
           (GET "/" [req] v/index)
           (GET "/ads" [ :as req] v/ads)
           (GET "/login" [:as req] v/login)
           (GET "/add-ads" [:as req] v/add-ads)
           (POST "/add-ads" [:as req] v/post-add-ads)
           (POST "/login" [req] v/post-login)
           (GET "/signup" [req] v/signup)
           (POST "/signup" [req] v/post-signup)
           (GET "/logout" [req] v/logout)

           (GET "/:category-slug" [category-slug :as req] (v/category category-slug req))
           (GET "/:category-slug/:ad-id" [category-slug ad-id :as req] (v/ad category-slug ad-id req))



           (route/resources "/")
           (route/not-found "Not Found"))


(def app
  (->
    (wrap-defaults app-routes (assoc site-defaults :session {
                                                             :store (session-store/cookie-store {
                                                                                                 :key "aaaaaaaaaaaaaaaa"
                                                                                                 })
                                                             }))
    (wrap-authentication (backends/session))
    (wrap-authorization (backends/session))
    (reload/wrap-reload {:dirs ["src" "resources/pages"]})

    )
  )


(defn main []

  )



(defn app_init []
  (m/init-db)
  (parser/set-resource-path! (clojure.java.io/resource "pages"))
  ;; need for forms with fields
  (parser/add-tag! :csrf-field (fn [_ _] (anti-forgery-field)))
  )