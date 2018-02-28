(ns workflow.pet-db-spec
  (:require
    [fulcro-sql.test-helpers :refer [with-database]]
    [fulcro-sql.core :as sql]
    [fulcro-spec.core :refer [specification provided behavior assertions component]]
    [workflow.api.database :as petdb]))

(defn test-database-config []
  {:driver          :h2
   :hikaricp-config "config/test-connection-pool.properties"
   :auto-migrate?   true
   :create-drop?    true
   :migrations      ["classpath:config/migrations"]})

(specification "Pet Database Operations" :integration
  (with-database [db (test-database-config)]
    (let [{:keys [id/camper id/tillie id/buddy]} (sql/seed! db petdb/schema
                                                   [(sql/seed-row :pet {:id :id/camper :name "Camper"})
                                                    (sql/seed-row :pet {:id :id/tillie :name "Tillie"})
                                                    (sql/seed-row :pet {:id :id/buddy :name "Buddy"})])
          rows        (petdb/get-pets db)
          sorted-rows (sort-by :db/id rows)]
      (component "get-pets"
        (assertions
          "Returns a vector"
          (vector? rows) => true
          "Finds all of the pets in the database"
          sorted-rows => [{:db/id camper :pet/name "Camper"}
                          {:db/id tillie :pet/name "Tillie"}
                          {:db/id buddy :pet/name "Buddy"}]))
      (component "delete-pet"
        (petdb/delete-pet db tillie)

        (let [rows        (petdb/get-pets db)
              sorted-rows (sort-by :db/id rows)]
          (assertions
            "Removes a pet from the database"
            sorted-rows => [{:db/id camper :pet/name "Camper"}
                            {:db/id buddy :pet/name "Buddy"}]))))))
