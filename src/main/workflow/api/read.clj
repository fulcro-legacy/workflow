(ns workflow.api.read
  (:require
    [fulcro.server :refer [defquery-entity defquery-root]]
    [taoensso.timbre :as timbre]
    [workflow.api.database :as petdb]
    [fulcro-sql.core :as sql]))

;; Server queries can go here
(defquery-root :root/all-pets
  (value [{:keys [databases]} params]
    (let [db (sql/get-dbspec databases :pets)]
      (petdb/get-pets db))))

