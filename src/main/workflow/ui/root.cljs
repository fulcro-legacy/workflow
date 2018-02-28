(ns workflow.ui.root
  (:require
    [fulcro.client.mutations :as m]
    [fulcro.client.data-fetch :as df]
    [fulcro.client.dom :as dom]
    [fulcro-css.css :as css]
    [workflow.api.mutations :as api]
    [workflow.ui.components :refer [PetWidget PetList PetFormPane ui-pet-widget]]
    [fulcro.client.primitives :as prim :refer [defsc]]))

;; The main UI of your application

(defsc Root [this {:keys [router]}]
  {:query         [{:router (prim/get-query PetWidget)}]
   :css-include   [PetList PetFormPane]
   :initial-state (fn [p] {:router (prim/get-initial-state PetWidget {})})}
  (dom/div nil
    (css/style-element Root)
    (dom/h4 nil "Pet App")
    (ui-pet-widget router)))

