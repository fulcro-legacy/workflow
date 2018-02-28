(ns workflow.api.mutations
  (:require
    [taoensso.timbre :as timbre]
    [fulcro.server :refer [defmutation]]
    [fulcro-sql.core :as sql]
    [workflow.api.database :as petdb]))

;; Place your server mutations here
(defmutation delete-pet [{:keys [id]}]
  (action [{:keys [databases]}]
    (let [db (sql/get-dbspec databases :pets)]
      (petdb/delete-pet db id)
      nil)))

