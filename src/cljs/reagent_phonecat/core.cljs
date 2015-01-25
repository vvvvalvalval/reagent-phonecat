(ns reagent-phonecat.core
    (:require [reagent.core :as rc :refer [atom]]
              )
    )

;; -------------------------
;; Views

(defn basic-component []
  [:p "Nothing here " (str "yet" "!")])

;; -------------------------
;; Initialize app
(defn init! []
  ;; does nothing for now because the list is still hardcoded
  #_(rc/render-component [basic-component] (.getElementById js/document "app")))
