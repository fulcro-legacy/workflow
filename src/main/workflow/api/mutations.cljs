(ns workflow.api.mutations
  (:require
    [fulcro.client.mutations :refer [defmutation]]
    [fulcro.client.logging :as log]
    [workflow.ui.components :refer [PetForm]]
    [fulcro.ui.form-state :as fs]
    [fulcro.client.routing :as r]))

;; Place your client mutations here
(defmutation edit-pet [{:keys [id]}]
  (action [{:keys [state]}]
    (let [pet-ident [:pet/by-id id]]
      (swap! state (fn [s]
                     (-> s
                       (fs/add-form-config* PetForm pet-ident)
                       (fs/entity->pristine* pet-ident)     ; add won't update pristine if it is already there
                       (assoc-in [:pet-form :pane :the-form] pet-ident)
                       (r/set-route* :pet-router [:pet-form :pane]))))))
  (refresh [env] [:router]))

(defn remove-ident
  "Remove the ident i from the vector of idents v."
  [v i]
  (into [] (filter #(not= i %)) v))

(defn delete-pet* [state-map id]
  (let [pet-ident [:pet/by-id id]]
    (-> state-map
      (update :pet/by-id dissoc id)
      (update-in [:pet-list :pane :pet-list/pets] remove-ident pet-ident))))

(defmutation delete-pet [{:keys [id]}]
  (action [{:keys [state]}] (swap! state delete-pet* id))
  (refresh [env] [:router]))

(defmutation save-edits [_]
  (action [{:keys [state]}]
    ; the form component is in a well-known location, and the form shares the ident
    (let [pet-ident (get-in @state [:pet-form :pane :the-form])]
      (swap! state (fn [s]
                     (-> s
                       (fs/entity->pristine* pet-ident)
                       (r/set-route* :pet-router [:pet-list :pane]))))))
  (refresh [env] [:router]))

(defmutation cancel-edit [_]
  (action [{:keys [state]}]
    ; the form component is in a well-known location, and the form shares the ident
    (let [pet-ident (get-in @state [:pet-form :pane :the-form])]
      (swap! state (fn [s]
                     (-> s
                       (fs/pristine->entity* pet-ident)
                       (r/set-route* :pet-router [:pet-list :pane]))))))
  (refresh [env] [:router]))

(defmutation show-pet-form [parms]
  (actions [{:keys [state]}]
    (swap! state r/set-route :pet-router [:pet-form :pane])))

(defmutation show-pet-list [parms]
  (actions [{:keys [state]}]
    (swap! state r/set-route :pet-router [:pet-list :pane])))

