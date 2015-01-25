(ns reagent-phonecat.core
    (:require [reagent.core :as rc :refer [atom]]
              )
    )

;; -------------------------
;; State

(def phones "The list of phones. It is an atom so that you can modify it live." 
  (rc/atom 
   [{:name "Nexus S"
     :snippet "Fast just got faster with Nexus S."}
    {:name "Motorola XOOM™ with Wi-Fi"
     :snippet "The Next, Next Generation tablet."}
    {:name "MOTOROLA XOOM™"
     :snippet "The Next, Next Generation tablet."}]
   ))

;; -------------------------
;; Views

(defn phone-component "component showing details about one phone"
  [{:keys [name snippet] :as phone}]
  [:li
   [:span name]
   [:p snippet]]
  )

(defn phones-list "Component displaying the list of phones" []
  [:ul
   (for [phone @phones]
     [phone-component phone])])

;; experiments
(def name (rc/atom "World"))
(defn hello-cpnt []
  [:p (str "Hello, " @name "!")])

(defn simple-table []
  [:table
   [:tr [:th "row number"]]
   (for [i (range 8)]
     [:tr [:td (inc i)]])
   ])

(defn less-simple-table []
  [:table
   (for [i (range 8)]
     [:tr (for [j (range 8)]
            [:td (inc i) (inc j)])])
   ])

;; -------------------------
;; Initialize app
(defn top-cpnt []
  [:div 
   [phones-list]

   [:h2 "Experiments"]
   [:h4 "Hello World"]
   [hello-cpnt]

   [:h4 "Simple table"]
   [simple-table]

   [:h4 "Less simple table"]
   [less-simple-table]
   ])

(defn init! []
  (rc/render-component [top-cpnt] (.getElementById js/document "app")))


;; -------------------------
;; Playground
(comment
  ;; live coding: change the list of phones by eval-ing this form. Notice the change is immediately reflected in the browser, without reloading the page.
  (swap! phones #(drop 1 %))
  )
