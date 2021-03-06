(ns ads.views
  (:require [clojure.java.io]
            [ring.util.response :refer [response redirect]]
            [ads.models :as m]
            [compojure.route :as route]
            [ads.helpers :as h]
            [ads.validations :as valid]
            ))


(defn index [req]
  (h/file-renderer req "index.html"
                   {
                    :cats (m/get-cats)
                    :ads  (m/get-ads)
                    })
  )

(defn login [req]
  (if (h/is-authenticated? req)
    (redirect "/")
    (h/file-renderer req "login.html" {}))
  )
(defn ads [req]
  (h/file-renderer req "ads.html"
                   {
                    :ads (m/get-ads)
                    }))

(defn add-ads [req]
  (h/file-renderer req "add-ads.html"
                   {
                    :cats (m/get-cats)
                    }))

(defn post-add-ads [{{category_id :category_id title :title discription :description telephone :telephone} :params :as req}]
  (m/insert-ad category_id (get-in req [:session :identity :id] nil) title discription telephone)
  (h/file-renderer req "add-ads.html" {}))

(defn post-login [{{login :login password :password} :params session :session :as req}]
  (let [user-from-db (h/login-user login password)
        counter (h/counter-update req)
        ]
    (if user-from-db
      (do
        (assoc (redirect "/") :session (assoc session :identity (select-keys user-from-db [:login :id]))))
      (do
        (h/file-renderer req "login.html"
                         {
                          :error_message "Something wrong with your credentials!"
                          :q             user-from-db
                          }))
      )
    )
  )

(defn logout [req]
  (h/counter-update req)
  (assoc (redirect "/") :session (assoc (:session req) :identity {})))

(defn signup [req]
  (if (h/is-authenticated? req)
    (redirect "/")
    (h/file-renderer req "signup.html"
                     {
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

                     } :params session :session :as req}]
  (let [login_ok (and (valid/check-range login 2 10) (valid/check-login-free? login))
        passwd_ok (valid/check-range password 2 10)
        password_repeat_ok (= password password_repeat)
        email_ok (and (valid/check-range email 2 32) (valid/check-email? email))
        email_repeat (= email email_repeat)
        sex_ok (valid/check-in sex ["male" "female"])
        name_ok (valid/check-range name 2 10)
        surname_ok (valid/check-range surname 2 10)
        lastname_ok (valid/check-range lastname 2 10)
        country_ok (valid/check-range country 2 10)
        city_ok (valid/check-range city 2 10)
        phone_ok (and (valid/check-len phone 13) (valid/check-phone? phone))
        add_phone_ok (and (valid/check-len add_phone 13) (valid/check-phone? add_phone))
        signed_ok (valid/check-range signed 2 10)

        ]
    (if (and login_ok passwd_ok password_repeat_ok email_ok email_repeat sex_ok name_ok surname_ok lastname_ok country_ok city_ok phone_ok add_phone_ok signed_ok)
      (do
        (m/insert-user
          {
           :login     login
           :password  password
           :email     email
           :sex       sex
           :name      name
           :surname   surname
           :lastname  lastname
           :country   country
           :city      city
           :phone     phone
           :add_phone add_phone
           })
        (assoc (redirect "/") :session (assoc session :identity (select-keys (h/login-user login password) [:login :id])))
        )
      (h/file-renderer req "signup.html"
                       {
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
                        :add_count          (count phone)
                        :add_count2         (valid/check-phone? phone)
                        })))

  )

(defn log-flush [req]
  (h/logger-flush)
  (redirect "/"))


(defn category [category-slug req]
  (let [cat (m/get-cat category-slug)]
    (if cat
      (h/file-renderer req "category.html"
                       {
                        :cat cat
                        :ads (m/get-ads-by-cat category-slug)
                        })
      (route/not-found "Not found"))))

(defn ad [category-slug ad-id req]
  (h/file-renderer req "ad.html" {}))

(defn about [req]
  (h/markup
    (html
      (head)
      (body
        (h1 "Students of group #251003")
        (ul {class "my-list"}
            (li "Sheko Maxim")
            (li "Skavysh Maxim")
            (li "Serkova Olga"))
        (p {class content title "stop pointing at me!"}
           Execution of our lablatory work 3 DSL )))))