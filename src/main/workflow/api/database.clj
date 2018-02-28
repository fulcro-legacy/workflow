(ns workflow.api.database
  (:require [fulcro-sql.core :as sql]))

(defn database [test?]
  (cond-> {:hikaricp-config "/usr/local/etc/production-pet-db-pool.properties"
           :driver          :h2
           :migrations      ["classpath:config/migrations"]}
    test? (merge {:hikaricp-config "config/connection-pool.properties"
                  :auto-migrate?   true
                  :create-drop?    true})))

(def schema
  {::sql/joins      {}
   ::sql/driver     :h2
   ::sql/graph->sql {}
   ::sql/pks        {}})
