(ns cljs.user
  (:require
    [fulcro.client :as fc]
    [workflow.client :as core]
    [workflow.ui.root :as root]
    [fulcro.logging :as log]))

(enable-console-print!)

(log/set-level! :debug)

(defn mount []
  (reset! core/app (fc/mount @core/app root/Root "app")))

(mount)
