(ns reagent-phonecat.core
    (:require [reagent.core :as rc :refer [atom]]
              [clojure.string :as str]
              )
    )

;; -------------------------
;; Logic

(defn matches-query? "Emulates Angular's 'filter:query' filter.
Checks that any string value of the 'data' map matches the 'query' string."
  [query data]
  (let [qp (-> query str/lower-case re-pattern)]
    (->> (vals data)
         (filter string?) (map str/lower-case)
         (some #(re-find qp %))
         )))

;; -------------------------
;; State

(def state
  (rc/atom
   {:phones [{:name "Nexus S"
              :snippet "Fast just got faster with Nexus S."}
             {:name "Motorola XOOM™ with Wi-Fi"
              :snippet "The Next, Next Generation tablet."}
             {:name "MOTOROLA XOOM™"
              :snippet "The Next, Next Generation tablet."}]
    :query ""
    }))

;; -------------------------
;; Views

(defn query-input []
  [:input {:type text
           :value (:query @state)
           :on-change #(swap! state assoc :query (-> % .-target .-value))}])

(defn phone-component "component showing details about one phone"
  [{:keys [name snippet] :as phone}]
  [:li
   [:span name]
   [:p snippet]]
  )

(defn phones-list "Component displaying the list of phones" [query phones]
  [:ul
   (for [phone (filter (partial matches-query? query) phones)]
     [phone-component phone])])


(defn top-cpnt []
  [:div.container-fluid
   [:div.row
    [:div.col-md-2
     ;; sidebar content
     "Search : " [query-input]
     ]
    [:div.col-md-10
     ;; body content
     [phones-list (:query @state) (:phones @state)]
     ]]
   ])
;; -------------------------
;; Initialize app

(defn init! []
  (rc/render-component [top-cpnt] (.getElementById js/document "app")))


;; -------------------------
;; Playground

