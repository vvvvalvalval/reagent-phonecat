(ns reagent-phonecat.core
    (:require [reagent.core :as rg :refer [atom]]
              [reagent.cursor :as rc]
              [ajax.core :as ajx]
              [secretary.core :as sec :include-macros true]
              [goog.events :as events]
              [goog.history.EventType :as EventType]
              [clojure.string :as str]
              )
    (:import goog.History))

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
  (rg/atom {:phones []
            :phone-by-id {}
                        
            :current-page nil
            :route-params {}}))


;; -------------------------
;; Server communication

(defn refresh-phones! "Fetches the list of phones from the server, then updates the global state." []
  (ajx/GET "phones/phones.json"
           {:handler (fn [phones] (swap! state assoc :phones phones))
            :error-handler (fn [details] (.warn js/console (str "Failed to refresh phones from server: " details)))
            :response-format :json
            :keywords? true}))

(defn fetch-phone! [phone-id]
  (ajx/GET (str "phones/" phone-id ".json")
           {:handler (fn [phone] (swap! state assoc-in [:phone-by-id phone-id] phone))
            :error-handler (fn [details] (.warn js/console (str "Failed to fetchphone from server: " details)))
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
  [{:keys [name snippet id imageUrl] :as phone}]
  (let [phone-href (str "#/phones/" id)]
    [:li.thumbnail
     [:a.thumb {:href phone-href} [:img {:src imageUrl}]]
     [:a {:href phone-href} name]
     [:p snippet]]
    ))

(defn phones-list "Component displaying the list of phones"
  [{:keys [query order-fn] :as diplay-opts} phones]
  [:ul.phones
   (for [phone (->> phones
                    (filter (partial matches-query? query))
                    (sort-by order-fn))]
     ^{:key phone} [phone-component phone])])

(defn phones-page "Component containing the display options and phone list, featuring local UI state."
  [_]
  (let [display-state (rg/atom {:query ""    ;; query text to match against 
                                :order-fn :age ;; property of phones to sort with
                                })
        query-cursor (rc/cursor [:query] display-state)
        order-cursor (rc/cursor [:order-fn] display-state)
        ]
    (fn []
      [:div.container-fluid
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
       ]
      )))

(defn phone-spec-cpnt [title kvs]
  [:li
   [:span title]
   [:dl (->> kvs (mapcat (fn [[t & ds]]
                           [[:dt t] (for [d ds][:dd d])]
                           )))]])

(defn checkmark [input] (if input \u2713 \u2718))

(defn phone-detail-cpnt [phone]
  (let [{:keys [images name description availability additionalFeatures]
         {:keys [ram flash]} :storage
         {:keys [type talkTime standbyTime]} :battery
         {:keys [cell wifi bluetooth infrared gps]} :connectivity
         {:keys [os ui]} :android
         {:keys [dimensions weight]} :sizeAndWeight
         {:keys [screenSize screenResolution touchScreen]} :display
         {:keys [cpu usb audioJack fmRadio accelerometer]} :hardware
         {:keys [primary features]} :camera
         } phone]
    [:div
     [:img.phone {:src (first images)}]
     [:h1 name]
     [:p description]

     [:ul.phone-thumbs
      (for [img images]
        [:li [:img {:src img}]])]
     
     [:ul.specs
      [phone-spec-cpnt "Availability and Networks" [(cons "Availability" availability)]]
      [phone-spec-cpnt "Battery" [["Type" type] ["Talk Time" talkTime] ["Standby time (max)" standbyTime]]]
      [phone-spec-cpnt "Storage and Memory" [["RAM" ram] ["Internal Storage" flash]]]
      [phone-spec-cpnt "Connectivity" [["Network Support" cell] ["WiFi" wifi] ["Bluetooth" bluetooth] ["Infrared" (checkmark infrared)] ["GPS" (checkmark gps)]]]
      [phone-spec-cpnt "Android" [["OS Version" os] ["UI" ui]]]
      [phone-spec-cpnt "Size and Weight" [(cons "Dimensions" dimensions) ["Weight" weight]]]
      [phone-spec-cpnt "Display" [["Screen size" screenSize] ["Screen resolution" screenResolution] ["Touch screen" (checkmark touchScreen)]]]
      [phone-spec-cpnt "Hardware" [["CPU" cpu] ["USB" usb] ["Audio / headphone jack" audioJack] ["FM Radio" (checkmark fmRadio)] ["Accelerometer" (checkmark accelerometer)]]]
      [phone-spec-cpnt "Camera" [["Primary" primary] ["Features" (str/join ", " features)]]]
      [:li
       [:span "Additional Features"]
       [:dd additionalFeatures]]
      ]
     ]))

(defn phone-detail-page [{:keys [phone-id]}]
  (let [phone-c (rc/cursor [:phone-by-id phone-id] state)]
    (.debug js.console "Fetching phone data for" phone-id)
    (fetch-phone! phone-id)
    (fn []
      (if-let [phone @phone-c]
        [phone-detail-cpnt phone]
        [:div]))
    ))

(defn top-cpnt []
  (if-let [page (:current-page @state)]
     [page (:route-params @state)]
     [:div])
  )

;; -------------------------
;; Routes utilities
(sec/set-config! :prefix "#")

(def h (History.))

(defn go-to-page! "Changes global state to the specified page component with the specified :route-params. Note that this will NOT change the location hash. To change the location and page programmatically, use navigate-to!"
  ([page-cpnt route-params]
   (swap! state assoc :current-page page-cpnt :route-params route-params))
  ([page-cpnt] (go-to-page! page-cpnt {})))

(defn hook-browser-navigation! "Listen to navigation events and dispatches a route change accordingly through secretary." []
  (doto h
    (events/listen
     EventType/NAVIGATE
     (fn [event]
       (.log js/console (str "Received NAVIGATE event for " (.-token event)))
       (sec/dispatch! (.-token event))))
    (.setEnabled true)))

(defn navigate-to! "Programmatically changes the location hash." [location] 
  (.setToken h location))

;; -------------------------
;; Routes declarations

(sec/defroute "/phones" []
  (go-to-page! phones-page))

(sec/defroute "/phones/:phone-id" {:keys [phone-id]}
  (go-to-page! phone-detail-page {:phone-id phone-id}))

(sec/defroute "*" []
  (navigate-to! "/phones"))

;; -------------------------
;; Initialize app

(defn init! []
  (refresh-phones!)
  (hook-browser-navigation!)
  (rg/render-component [top-cpnt] (.getElementById js/document "app")))


;; -------------------------
;; Playground

