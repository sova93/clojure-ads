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
                         :users
                         [[:id :integer :primary :key :AUTOINCREMENT]
                          [:username "varchar(128)"]
                          [:password "varchar(128)"]
                          [:email "varchar(256)"]
                          [:activated :integer]

                          ])))




(defn insert-sample-data []
  (jdbc/insert-multi! db
                      :cats [{
                              :name "Category 1"
                              :slug "cat_1"},
                             {:name "Category 2"
                              :slug "cat_2"}])
  (jdbc/insert-multi! db
                      :users [{
                               :username  "test"
                               :password  (hashers/encrypt "test123")
                               :email     "none@none.com"
                               :activated 1
                               }])
  )

(defn init-db []
  (try
    (do
      (jdbc/db-do-commands db (jdbc/drop-table-ddl :cats))
      (jdbc/db-do-commands db (jdbc/drop-table-ddl :users))
      (create-tables)
      (insert-sample-data))))
;(catch java.sql.BatchUpdateException e)))
; tables already exists


(defn get-cats []
  (jdbc/query db "SELECT * FROM cats"))

(defn get-cat [category-slug]
  (first
    (jdbc/query
      db
      ["SELECT * FROM cats WHERE slug = ? LIMIT 1" category-slug])))





