(ns workflow.pet-cards
  (:require [fulcro.client.cards :refer [defcard-fulcro]]
            [fulcro.client.primitives :as prim :refer [defsc]]
            [fulcro-css.css :as css]
            [fulcro.client.routing :as r :refer [defrouter]]
            [fulcro.ui.forms :as forms]
            [devcards.core :as dc :refer-macros [defcard]]
            [fulcro.client.dom :as dom]
            [fulcro.client.mutations :as m :refer [defmutation]]))

(defsc Pet [this {:keys [db/id pet/name pet/breed]} {:keys [onSelect]} {:keys [pet-name]}]
  {:css           [[:.pet-name {:color :red}]]
   :ident         [:pet/by-id :db/id]
   :initial-state (fn [{:keys [id name]}] {:db/id id :pet/name name})
   :query         [:db/id :pet/name :pet/breed]}
  (dom/li #js {:className pet-name :onClick (fn [] (when onSelect
                                                     (onSelect id)))}
    name))

(def ui-pet (prim/factory Pet {:keyfn :db/id}))

(defmutation edit-pet [{:keys [id]}]
  (action [{:keys [state]}]
    (let [pet-ident [:pet/by-id id]]
      (swap! state (fn [s]
                     (-> s
                       (assoc-in [:pet-form :pane :the-form] pet-ident)
                       (r/set-route :pet-router [:pet-form :pane]))))))
  (refresh [env] [:router]))

(defmutation save-edits [_]
  (action [{:keys [state]}]
    (swap! state r/set-route :pet-router [:pet-list :pane]))
  (refresh [env] [:router]))

(defsc PetList [this {:keys [pet-list/pets]} _ css]
  {:css           []
   :css-include   [Pet]
   :ident         (fn [] [:pet-list :pane])
   :initial-state (fn [{:keys [pets]}] {:pet-list/pets (if (seq pets) pets [])})
   :query         [{:pet-list/pets (prim/get-query Pet)}]}
  (let [onSelect (fn [id] (prim/transact! this `[(edit-pet {:id ~id})]))]
    (dom/ul nil
      (map (fn [pet] (ui-pet (prim/computed pet {:onSelect onSelect}))) pets))))

(def ui-pet-list (prim/factory PetList))

(defsc PetForm [this {:keys [pet/name]} _]
  {:ident         [:pet/by-id :db/id]
   :initial-state (fn [{:keys [id name]}] {:db/id id :pet/name name})
   :query         [:db/id :pet/name]}
  (dom/div nil
    (dom/label #js {:htmlFor "pet-name"} "Name:")
    (dom/input #js {:value    (or name "") :id "pet-name"
                    :onChange #(m/set-string! this :pet/name :event %)
                    :onBlur   #(prim/transact! this `[(save-edits {})])
                    :type     "text"})))

(def ui-pet-form (prim/factory PetForm))

(defsc PetFormPane [this {:keys [the-form]}]
  {:query         [{:the-form (prim/get-query PetForm)}]
   :ident         (fn [] [:pet-form :pane])
   :initial-state (fn [p] {:the-form (prim/get-initial-state PetForm {:id 1 :name "Fido"})})}
  (ui-pet-form the-form))

(defrouter PetWidget :pet-router
  (ident [this props] [(if (contains? props :pet-list/pets)
                         :pet-list :pet-form) :pane])
  :pet-list PetList
  :pet-form PetFormPane)

(def ui-pet-widget (prim/factory PetWidget))

(defmutation show-pet-form [parms]
  (actions [{:keys [state]}]
    (swap! state r/set-route :pet-router [:pet-form :pane])))

(defmutation show-pet-list [parms]
  (actions [{:keys [state]}]
    (swap! state r/set-route :pet-router [:pet-list :pane])))

(defsc AppRoot [this {:keys [router]}]
  {:query         [{:router (prim/get-query PetWidget)}]
   :initial-state (fn [p] {:router (prim/get-initial-state PetWidget {})})}
  (dom/div nil
    (dom/h4 nil "Pet App")
    (ui-pet-widget router)))

(defcard-fulcro pet-routing
  AppRoot
  {}
  {:inspect-data true
   :fulcro       {:started-callback (fn [{:keys [reconciler]}]
                                      (prim/merge-component! reconciler Pet {:db/id 42 :pet/name "Suzi"} :append [:pet-list :pane :pet-list/pets])
                                      (prim/merge-component! reconciler Pet {:db/id 32 :pet/name "Fido"} :append [:pet-list :pane :pet-list/pets])
                                      (prim/merge-component! reconciler Pet {:db/id 2 :pet/name "Cruppy"} :append [:pet-list :pane :pet-list/pets]))}})
