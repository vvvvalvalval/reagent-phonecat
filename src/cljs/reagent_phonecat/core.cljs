(ns reagent-phonecat.core
    (:require [reagent.core :as rg :refer [atom]]
              [reagent.cursor :as rc]
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
  (rg/atom
   {:phones [{:name "Nexus S"
              :snippet "Fast just got faster with Nexus S."
              :age 1}
             {:name "Motorola XOOM™ with Wi-Fi"
              :snippet "The Next, Next Generation tablet."
              :age 2}
             {:name "MOTOROLA XOOM™"
              :snippet "The Next, Next Generation tablet."
              :age 3}]
    }))

;; -------------------------
;; Views

(defn cursor-input "Generic input field that stays synchronized with the provided reagent cursor."
  ([opts cursor]
   [:input (merge {:type "text" 
                   :value @cursor
                   :on-change #(reset! cursor (-> % .-target .-value))}
                  opts)])
  ([cursor] (cursor-input {} cursor)))

(defn order-select [cursor]
  [:select {:value @cursor
            :on-change #(reset! cursor (-> % .-target .-value keyword))}
   [:option {:value :name} "Alphabetical"]
   [:option {:value :age} "Newest"]
   ])

(defn phone-component "component showing details about one phone"
  [{:keys [name snippet] :as phone}]
  [:li
   [:span name]
   [:p snippet]]
  )

(defn phones-list "Component displaying the list of phones"
  [{:keys [query order-fn] :as diplay-opts} phones]
  [:ul
   (for [phone (->> phones
                    (filter (partial matches-query? query))
                    (sort-by order-fn))]
     ^{:key phone} [phone-component phone])])

(defn phones-page "Component containing the display options and phone list, featuring local UI state." []
  (let [display-state (rg/atom {:query ""    ;; query text to match against 
                                :order-fn :age ;; property of phones to sort with
                                })
        query-cursor (rc/cursor [:query] display-state)
        order-cursor (rc/cursor [:order-fn] display-state)
        ]
    (fn []
      [:div.row
       [:div.col-md-2
        ;; sidebar content
        "Search: " [cursor-input query-cursor]
        "Sort by: " [order-select order-cursor] 
        ]
       [:div.col-md-10
        ;; body content
        [phones-list @display-state (:phones @state)]
        ]]
      )))

(defn top-cpnt []
  [:div.container-fluid
   [phones-page]
   ])
;; -------------------------
;; Initialize app

(defn init! []
  (rg/render-component [top-cpnt] (.getElementById js/document "app")))


;; -------------------------
;; Playground

