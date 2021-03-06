(ns ethlance.components.skills-chips
  (:require
    [cljs-react-material-ui.reagent :as ui]
    [ethlance.components.misc :refer [row-plain]]
    [ethlance.styles :as styles]
    [ethlance.utils :as u]
    [re-frame.core :refer [subscribe dispatch]]
    [reagent.core :as r]
    ))

(def max-count 5)

(defn skills-chips []
  (let [all-skills (subscribe [:app/skills])
        show-all? (r/atom false)]
    (fn [{:keys [:selected-skills :on-touch-tap :always-show-all?]}]
      [row-plain
       {:middle "xs"
        :style styles/chip-list-row}
       (for [skill-id (if (and (< max-count (count selected-skills))
                               (not @show-all?)
                               (not always-show-all?))
                        (take max-count selected-skills)
                        selected-skills)]
         (let [skill-name (get-in @all-skills [skill-id :skill/name])]
           (when (seq skill-name)
             [ui/chip
              (r/merge-props
                {:key skill-id
                :style styles/chip-in-list}
                (when on-touch-tap
                  {:on-touch-tap #(on-touch-tap skill-id skill-name)}))
              skill-name])))
       (when (and (< max-count (count selected-skills))
                  (not @show-all?))
         [:span {:style (merge styles/more-text
                               {:color styles/primary1-color})
                 :on-click #(reset! show-all? true)} "More"])])))