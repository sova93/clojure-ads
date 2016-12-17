(ns ads.views
  (:require [clojure.java.io]
            [ring.util.response :refer [response redirect]]
            [ads.models :as m]
            [selmer.parser :refer [render-file]]
            [compojure.route :as route]
            [ads.helpers :as h]
            [ads.validations :as valid]))


(defn index [session]
  (render-file "index.html" {
                             :cats (m/get-cats)
                             :ads (m/get-ads)
                             :sess (:session session)})
  )

(defn login [req]
  (render-file "login.html" {:sess (:session req)}))

(defn add-ads [req]

  (render-file "add-ads.html" {:cats (m/get-cats)}))

(defn post-add-ads [{{category_id :category_id title :title discription :description telephone :telephone} :params :as req}]
  (m/insert-ads (get-in req [:session :identity :id] nil) category_id title discription telephone )
  (render-file "add-ads.html" [req]))

(defn post-login [{{login :login password :password} :params session :session :as req}]
  (let [user-from-db (h/login-user login password)]
    (if user-from-db
      (do
        (assoc (redirect "/") :session (assoc session :identity (select-keys user-from-db [:login :id]))))
      (do
        (render-file "login.html" {:error_message "Something wrong with your credentials!" :q user-from-db :req req}))
      )
    )
  )

(defn logout [req]
  (assoc (redirect "/") :session (assoc (:session req) :identity {})))

(defn signup [req]
  (if (h/is-authenticated? req)
    (redirect "/")
    (render-file "signup.html" {
                                :sess               (:session req)
                                :login_ok           true
                                :passwd_ok          true
                                :password_repeat_ok true
                                :email_ok           true
                                :email_repeat       true
                                :sex_ok             true
                                :name_ok            true
                                :surname_ok         true
                                :lastname_ok        true
                                :country_ok         true
                                :city_ok            true
                                :phone_ok           true
                                :add_phone_ok       true
                                :signed_ok          true
                                }))

  )

(defn post-signup [{{
                     login           :login
                     password        :password
                     password_repeat :password_repeat
                     email           :email
                     email_repeat    :email_repeat
                     sex             :sex
                     name            :name
                     surname         :surname
                     lastname        :lastname
                     country         :country
                     city            :city
                     phone           :phone
                     add_phone       :add_phone
                     signed          :signed

                     } :params :as req}]
  (let [login_ok (and (valid/check-range login 2 10) (valid/check-login-free? login))
        passwd_ok (valid/check-range password 2 10)
        password_repeat_ok (= password password_repeat)
        email_ok (valid/check-range email 2 10)
        email_repeat (= email email_repeat)
        sex_ok (valid/check-in sex ["male" "female"])
        name_ok (valid/check-range name 2 10)
        surname_ok (valid/check-range surname 2 10)
        lastname_ok (valid/check-range lastname 2 10)
        country_ok (valid/check-range country 2 10)
        city_ok (valid/check-range city 2 10)
        phone_ok (valid/check-range phone 2 10)
        add_phone_ok (valid/check-range add_phone 2 10)
        signed_ok (valid/check-range signed 2 10)
        ]
    (if (and login_ok passwd_ok password_repeat_ok email_ok email_repeat sex_ok name_ok surname_ok lastname_ok country_ok city_ok phone_ok add_phone_ok signed_ok)
      (do
        (m/insert-user {:login     login
                        :password  password
                        :email     email
                        :sex       sex
                        :name      name
                        :surname   surname
                        :lastname  lastname
                        :country   country
                        :city      city
                        :phone     phone
                        :add_phone add_phone})
        ;(h/login-user login password)
        )
      (render-file "signup.html" {:req                req
                                  :login              login
                                  :login_ok           login_ok
                                  :passwd_ok          passwd_ok
                                  :password_repeat_ok password_repeat_ok
                                  :email              email
                                  :email_ok           email_ok
                                  :email_repeat       email_repeat
                                  :sex                sex
                                  :sex_ok             sex_ok
                                  :name               name
                                  :name_ok            name_ok
                                  :surname            surname
                                  :surname_ok         surname_ok
                                  :lastname           lastname
                                  :lastname_ok        lastname_ok
                                  :country            country
                                  :country_ok         country_ok
                                  :city               city
                                  :city_ok            city_ok
                                  :phone              phone
                                  :phone_ok           phone_ok
                                  :add_phone          add_phone
                                  :add_phone_ok       add_phone_ok
                                  :signed_ok          signed_ok

                                  })))

  )

(defn category [category-slug req]
  (let [cat (m/get-cat category-slug)]
    (if cat
      (render-file "category.html" {:cat cat :sess (:session req)})
      (route/not-found "Not found"))))

(defn ad [category-slug ad-id req]
  (render-file "ad.html" {:sess (:session req)}))