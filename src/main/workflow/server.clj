(ns workflow.server
  (:require
    [fulcro.easy-server :refer [make-fulcro-server]]
    [fulcro-sql.core :as sql]
    [workflow.api.read]
    [workflow.api.mutations]))

(defn build-server
  [{:keys [config] :or {config "config/dev.edn"}}]
  (make-fulcro-server
    :parser-injections #{:config}
    :config-path config))



