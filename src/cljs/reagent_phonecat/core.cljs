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
  (rc/render-component [basic-component] (.getElementById js/document "app")))
