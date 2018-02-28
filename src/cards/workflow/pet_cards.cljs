(ns workflow.pet-cards
  (:require [fulcro.client.cards :refer [defcard-fulcro]]
            [fulcro.client.primitives :as prim :refer [defsc]]
            [workflow.ui.components :refer [Pet]]
            [workflow.ui.root :refer [Root]]
            [devcards.core :as dc :refer-macros [defcard]]))

(defcard-fulcro pet-routing
  Root
  {}
  {:inspect-data true
   :fulcro       {:started-callback (fn [{:keys [reconciler]}]
                                      (prim/merge-component! reconciler Pet {:db/id 42 :pet/name "Suzi"} :append [:pet-list :pane :pet-list/pets])
                                      (prim/merge-component! reconciler Pet {:db/id 32 :pet/name "Fido"} :append [:pet-list :pane :pet-list/pets])
                                      (prim/merge-component! reconciler Pet {:db/id 2 :pet/name "Cruppy"} :append [:pet-list :pane :pet-list/pets]))}})
