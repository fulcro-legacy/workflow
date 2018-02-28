(ns workflow.pet-db-spec
  (:require
    [fulcro-sql.test-helpers :refer [with-database]]
    [fulcro-sql.core :as sql]
    [clojure.java.jdbc :as jdbc]
    [fulcro-spec.core :refer [specification provided behavior assertions component]]
    [workflow.api.database :as petdb]
    [clojure.set :as set]))

(specification "Pet Database Operations" :integration
  (with-database [db (petdb/database true)]
    (let [{:keys [id/camper id/tillie id/buddy]} (sql/seed! db petdb/schema
                                                   [(sql/seed-row :pet {:id :id/camper :name "Camper"})
                                                    (sql/seed-row :pet {:id :id/tillie :name "Tillie"})
                                                    (sql/seed-row :pet {:id :id/buddy :name "Buddy"})])
          row (jdbc/query db ["SELECT id, name FROM pet where id = ?" camper]
                {:result-set-fn first})]
      (assertions
        "Can insert and find a seeded account row"
        row => {:id camper :name "Camper"}))))
