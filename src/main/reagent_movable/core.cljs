(ns reagent-movable.core
  (:require [reagent.core :as r]
            ["react-sortable-hoc" :refer [SortableContainer SortableElement SortableHandle]]))

(defn reload! []
  (.. js/window -location reload))

(defonce state (r/atom (vec (map #(str "Item " %) (range 1 11)))))

;; this code is taken from https://github.com/reagent-project/reagent/blob/72c95257c13e5de1531e16d1a06da7686041d3f4/examples/react-sortable-hoc/src/example/core.cljs
(def DragHandle
  (SortableHandle.
    ;; Alternative to r/reactify-component, which doens't convert props and hiccup,
    ;; is to just provide fn as component and use as-element or create-element
    ;; to return React elements from the component.
    (fn []
      (r/as-element [:span {:style {"-webkit-touch-callout" "none"
                                    "-webkit-user-select" "none"
                                    "-khtml-user-select" "none"
                                    "-moz-user-select" "none"
                                    "-ms-user-select" "none"
                                    "user-select" "none"}}
                     "::"]))))

(def SortableItem
  (SortableElement.
    (r/reactify-component
      (fn [{:keys [value]}]
        [:li
         [:> DragHandle]
         value]))))

;; Alternative without reactify-component
;; props is JS object here
#_
(def SortableItem
  (js/SortableHOC.SortableElement.
    (fn [props]
      (r/as-element
        [:li
         [:> DragHandle]
         (.-value props)]))))

(def SortableList
  (SortableContainer.
    (r/reactify-component
      (fn [{:keys [items]}]
        [:ul
         (for [[value index] (map vector items (range))]
           ;; No :> or adapt-react-class here because that would convert value to JS
           (r/create-element
             SortableItem
             #js {:key (str "item-" index)
                  :index index
                  :value value}))]))))

(defn vector-move [coll prev-index new-index]
  (let [items (into (subvec coll 0 prev-index)
                    (subvec coll (inc prev-index)))]
    (-> (subvec items 0 new-index)
        (conj (get coll prev-index))
        (into (subvec items new-index)))))

(comment
  (= [0 2 3 4 1 5] (vector-move [0 1 2 3 4 5] 1 4)))

(defn sortable-component []
  (fn []
    (r/create-element
     SortableList
     #js {:items @state
          :onSortEnd (fn [event]
                       (swap! state vector-move (.-oldIndex event) (.-newIndex event)))
          :useDragHandle true})))

(defn app []
  [:div
   [:h1 "Movable list below"]
   [sortable-component]])

(defn ^:export ^:dev/after-load start []
  (println "Mounting component...")
  (r/render [app] (.getElementById js/document "app")))
