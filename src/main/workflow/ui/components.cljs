(ns workflow.ui.components
  (:require
    fulcro-css.css
    [fulcro.client.primitives :as prim :refer [defsc]]
    [fulcro.client.mutations :as m]
    [fulcro.events :as evt]
    [fulcro.ui.form-state :as fs]
    [fulcro.ui.icons :as icons]
    [fulcro.client.routing :refer [defrouter]]
    [fulcro.client.dom :as dom]))

(defsc PlaceholderImage [this {:keys [w h label]}]
  (let [label (or label (str w "x" h))]
    (dom/svg #js {:width w :height h}
      (dom/rect #js {:width w :height h :style #js {:fill        "rgb(200,200,200)"
                                                    :strokeWidth 2
                                                    :stroke      "black"}})
      (dom/text #js {:textAnchor "middle" :x (/ w 2) :y (/ h 2)} label))))

(def ui-placeholder (prim/factory PlaceholderImage))

(defsc Pet [this {:keys [db/id pet/name]} {:keys [onSelect onDelete]} {:keys [pet-name delete-button-icon]}]
  {:css   [[:.pet-name {:color :red :line-height "30px"}]
           [:.delete-button-icon {:width "20px" :height "20px" :margin-left "5px" :margin-bottom "-5px"}]]
   :ident [:pet/by-id :db/id]
   :query [:db/id :pet/name]}
  (dom/li #js {:className pet-name}
    (dom/span #js {:onClick (fn [] (when onSelect (onSelect id)))} name)
    (when onDelete
      (icons/icon :delete :className [delete-button-icon] :onClick #(onDelete id)))))

(def ui-pet (prim/factory Pet {:keyfn :db/id}))

(defsc PetList [this {:keys [pet-list/pets]} _ css]
  {:css           []
   :css-include   [Pet]
   :ident         (fn [] [:pet-list :pane])
   :initial-state {:pet-list/pets []}
   :query         [{:pet-list/pets (prim/get-query Pet)}]}
  (let [onSelect (fn [id] (prim/transact! this `[(workflow.api.mutations/edit-pet {:id ~id})]))
        onDelete (fn [id] (prim/transact! this `[(workflow.api.mutations/delete-pet {:id ~id})]))]
    (dom/ul nil
      (map (fn [pet] (ui-pet (prim/computed pet {:onSelect onSelect :onDelete onDelete}))) pets))))

(def ui-pet-list (prim/factory PetList))

(defn focus-with-cursor-at-end
  "Focus the given dom input node n, and move the cursor to the end."
  [n]
  (when n
    (.focus n)
    (set! (.-selectionEnd n) 100000)
    (set! (.-selectionStart n) 100000)))

(defsc PetForm [this {:keys [pet/name]} _]
  {:ident             [:pet/by-id :db/id]
   :form-fields       #{:pet/name}
   :query             [:db/id :pet/name fs/form-config-join]
   :componentDidMount (fn [] (when-let [n (dom/node this "input")] (focus-with-cursor-at-end n)))}
  (dom/div nil
    (dom/label #js {:htmlFor "pet-name"} "Name:")
    (dom/input #js {:value     (or name "") :id "pet-name"
                    :ref       "input"
                    :onChange  #(m/set-string! this :pet/name :event %)
                    :onBlur    #(prim/transact! this `[(workflow.api.mutations/save-edits {})])
                    :onKeyDown #(cond
                                  (evt/enter-key? %) (prim/transact! this `[(workflow.api.mutations/save-edits {})])
                                  (evt/escape-key? %) (prim/transact! this `[(workflow.api.mutations/cancel-edit {})]))
                    :type      "text"})))

(def ui-pet-form (prim/factory PetForm))

(defsc PetFormPane [this {:keys [the-form]}]
  {:query [{:the-form (prim/get-query PetForm)}]
   :ident (fn [] [:pet-form :pane])}
  (ui-pet-form the-form))

(defrouter PetWidget :pet-router
  (fn [this props] [(if (contains? props :pet-list/pets)
                      :pet-list :pet-form) :pane])
  :pet-list PetList
  :pet-form PetFormPane)

(def ui-pet-widget (prim/factory PetWidget))
