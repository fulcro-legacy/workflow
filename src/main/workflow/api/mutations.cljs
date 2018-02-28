(ns workflow.api.mutations
  (:require
    [fulcro.client.mutations :refer [defmutation]]
    [fulcro.client.logging :as log]
    [workflow.ui.components :refer [PetForm]]
    [fulcro.ui.form-state :as fs]
    [fulcro.client.routing :as r]))

;; Place your client mutations here
(defmutation edit-pet
  "Mutation: Edit the pet with the given ID."
  [{:keys [id]}]
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

(defn delete-pet*
  "Helper. Delete the pet with the given ID from the pet list and db."
  [state-map id]
  (let [pet-ident [:pet/by-id id]]
    (-> state-map
      (update :pet/by-id dissoc id)
      (update-in [:pet-list :pane :pet-list/pets] remove-ident pet-ident))))

(defmutation delete-pet
  "Mutation: Delete the given pet by ID."
  [{:keys [id]}]
  (action [{:keys [state]}] (swap! state delete-pet* id))
  (remote [env] true)
  (refresh [env] [:router]))

(defmutation save-edits
  "Mutation: Save the edits from the form."
  [_]
  (action [{:keys [state]}]
    ; the form component is in a well-known location, and the form shares the ident
    (let [pet-ident (get-in @state [:pet-form :pane :the-form])]
      (swap! state (fn [s]
                     (-> s
                       (fs/entity->pristine* pet-ident)
                       (r/set-route* :pet-router [:pet-list :pane]))))))
  (refresh [env] [:router]))

(defmutation cancel-edit
  "Mutation: Cancel the edit and revert the pet entity."
  [_]
  (action [{:keys [state]}]
    ; the form component is in a well-known location, and the form shares the ident
    (let [pet-ident (get-in @state [:pet-form :pane :the-form])]
      (swap! state (fn [s]
                     (-> s
                       (fs/pristine->entity* pet-ident)
                       (r/set-route* :pet-router [:pet-list :pane]))))))
  (refresh [env] [:router]))

