(ns workflow.api.database
  (:require [fulcro-sql.core :as sql]
            [clojure.java.jdbc :as jdbc]
            [clojure.set :as set]))

(def schema
  {::sql/joins      {}
   ::sql/driver     :h2
   ::sql/graph->sql {}
   ::sql/pks        {}})

(defn get-pets [db]
  (let [rows (jdbc/query db ["SELECT id, name FROM pet"])]
    (mapv #(set/rename-keys % {:id :db/id :name :pet/name}) rows)))

(defn delete-pet [db id]
  (jdbc/execute! db ["DELETE FROM pet WHERE id = ?" id]))