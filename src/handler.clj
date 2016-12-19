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
            [ads.models :as m]
            [ads.helpers :as h])
  (:import (java.util Date)))



(defroutes app-routes
           (GET "/" [req] v/index)
           (GET "/ads" [req] v/ads)
           (GET "/login" [req] v/login)
           (GET "/add-ads" [req] v/add-ads)
           (POST "/add-ads" [req] v/post-add-ads)
           (POST "/login" [req] v/post-login)
           (GET "/signup" [req] v/signup)
           (POST "/signup" [req] v/post-signup)
           (GET "/logout" [req] v/logout)
           (GET "/log-flush" [req] v/log-flush)
            (GET "/about" [req] v/about)

           (GET "/:category-slug" [category-slug :as req] (v/category category-slug req))
           (GET "/:category-slug/:ad-id" [category-slug ad-id :as req] (v/ad category-slug ad-id req))



           (route/resources "/")
           (route/not-found "Not Found"))

(defn now [] (.. (Date.) (toString)))

(defn wrap-custom [handler]
  (fn [request]
    (let [request (assoc request :counter (h/counter-update (:uri request)))
          response (handler request)
          req-ip (:remote-addr request)
          response-len (count (:body response))
          method (:request-method request)
          uri (:uri request)
          response-status (:status response)
          log-str (str (now) " " req-ip " " method " " uri " " response-status " " response-len)
          ]

      (println log-str)
      (h/log-request log-str)
      response)))

(def app
  (-> app-routes
      (wrap-defaults
        (assoc site-defaults
          :session {
                    :store (session-store/cookie-store
                             {
                              :key "aaaaaaaaaaaaaaaa"
                              })
                    }))
      (wrap-authentication (backends/session))
      (wrap-authorization (backends/session))
      (wrap-custom)                                         ; atom and agent logic here
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