(ns workflow.client
  (:require [fulcro.client :as fc]
            [workflow.ui.components :refer [Pet]]
            [fulcro.client.data-fetch :as df]))

(defonce app (atom (fc/new-fulcro-client
                     :started-callback (fn [app]
                                         (df/load app :root/all-pets Pet {:target [:pet-list :pane :pet-list/pets]})))))
