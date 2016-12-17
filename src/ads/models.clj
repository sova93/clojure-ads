(ns ads.models
  (:require [clojure.java.jdbc :as jdbc]
            [buddy.hashers :as hashers]))

(def db {
         :classname   "org.sqlite.JDBC"
         :subprotocol "sqlite"
         :subname     "test.db"})

(defn create-tables []
  (jdbc/db-do-commands db
                       (jdbc/create-table-ddl
                         :cats
                         [[:id :integer :primary :key :AUTOINCREMENT]
                          [:name "varchar(128)"]
                          [:slug "varchar(128)"]]))
  (jdbc/db-do-commands db
                       (jdbc/create-table-ddl
                         :ads
                         [[:id :integer :primary :key :AUTOINCREMENT]
                          [:category_id :integer]
                          [:user_id :integer]
                          [:title "varchar(128)"]
                          [:description "varchar(128)"]
                          [:tel "varchar(128)"]]))
  (jdbc/db-do-commands db
                       (jdbc/create-table-ddl
                         :users
                         [[:id :integer :primary :key :AUTOINCREMENT]
                          [:login "varchar(128)"]
                          [:password "varchar(128)"]
                          [:email "varchar(256)"]
                          [:sex "varchar(6)"]
                          [:name "varchar(128)"]
                          [:surname "varchar(128)"]
                          [:lastname "varchar(128)"]
                          [:country "varchar(128)"]
                          [:city "varchar(128)"]
                          [:phone "varchar(128)"]
                          [:add_phone "varchar(128)"]

                          [:activated :integer]
                          ]))
  (jdbc/execute! db "CREATE UNIQUE INDEX IF NOT EXISTS unique_login ON users (login)")
  )




(defn insert-sample-data []
  (jdbc/insert-multi! db
                      :cats [{
                              :name "Category 1"
                              :slug "cat_1"},
                             {:name "Category 2"
                              :slug "cat_2"}])
  (jdbc/insert-multi! db
                      :users [{
                               :login     "test"
                               :password  (hashers/encrypt "test123")
                               :email     "none@none.com"
                               :activated 1
                               }])
  (jdbc/insert-multi! db
                      :ads [{
                                :category_id  1
                                :user_id  1
                                :title  "Test"
                                :description  "Bla bla bla"
                                :tel "+234221321"
                                }])
  )

(defn init-db []
  (try
    (do
      (jdbc/db-do-commands db (jdbc/drop-table-ddl :cats))
      (jdbc/db-do-commands db (jdbc/drop-table-ddl :users))
      (jdbc/db-do-commands db (jdbc/drop-table-ddl :ads))
      (create-tables)
      (insert-sample-data))))
;(catch java.sql.BatchUpdateException e)))
; tables already exists


(defn get-cats []
  (jdbc/query db "SELECT * FROM cats"))

(defn get-ads []
  (jdbc/query db "SELECT * FROM ads"))

(defn insert-ads [category_id user_id title description tel]
  (jdbc/insert! db :ads {:category_id category_id :user_id user_id :title title :description description :tel tel} ))

(defn insert-user [map]
  (jdbc/insert! db :users (assoc (assoc map :password (hashers/encrypt (:password map))) :activated 1)))

(defn get-cat [category-slug]
  (first
    (jdbc/query
      db
      ["SELECT * FROM cats WHERE slug = ? LIMIT 1" category-slug])))


(defn get-username [username]
  (first
    (jdbc/query db
                ["SELECT * FROM users WHERE login = ?" username]
                )))