(ns reagent-phonecat.core
    (:require [reagent.core :as rg :refer [atom]]
              [reagent.cursor :as rc]
              [ajax.core :as ajx]
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
  (rg/atom {:phones []}))

;; -------------------------
;; Server communication

(defn refresh-phones! "Fetches the list of phones from the server, then updates the global state." []
  (ajx/GET "/phones/phones.json"
           {:handler (fn [phones] (swap! state assoc :phones phones))
            :error-handler (fn [details] (.warn js/console (str "Failed to refresh phones from server: " details)))
            :response-format :json
            :keywords? true}))


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
  (refresh-phones!)
  (rg/render-component [top-cpnt] (.getElementById js/document "app")))


;; -------------------------
;; Playground

