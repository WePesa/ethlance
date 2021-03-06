(ns ethlance.components.chip-input
  (:require
    [cljs-react-material-ui.chip-input.reagent :as material-ui-chip-input]
    [reagent.core :as r]
            [ethlance.utils :as u]
            [ethlance.constants :as constants]
            [medley.core :as medley]))

(defn chip-input [{:keys [:all-items :all-items-val-key :value :on-change]  :as props}]
  (let [all-items-map? (map? all-items)
        data-source (if (map? all-items)
                      (u/map->data-source all-items all-items-val-key)
                      (u/coll->data-source (medley/indexed all-items)))]
    [material-ui-chip-input/chip-input
     (merge
       {:dataSource data-source
        :dataSourceConfig u/data-source-config
        :new-chip-key-codes []
        :full-width true
        :max-search-results 10}
       (dissoc props :value :on-change :all-items :all-items-val-key)
       {:on-request-delete (fn [item]
                             (let [items (into [] (remove (partial = item) value))]
                               (on-change items)))}
       {:on-request-add (fn [item]
                          (let [item (aget item "value")
                                items (conj (into [] value) item)]
                            (on-change items)))}
       (when value
         {:value
          (let [value (filter (partial >= (count all-items)) value)]
            (if all-items-map?
              (u/map->data-source (select-keys all-items value) all-items-val-key)
              (u/results-coll->data-source value all-items)))}))]))