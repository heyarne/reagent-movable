(ns reagent-movable.core
  (:require [reagent.core :as r]
            ["react-movable" :refer [List]]))

(defn reload! []
  (.. js/window -location reload))

(defonce state (r/atom (vec (map #(str "Item " %) (range 1 11)))))

(defn movable []
  (let [items @state]
    [:ul
     (for [[idx item] (map-indexed vector items)]
       ^{:key idx} [:li item])]))

(defn app []
  [:div
   [:h1 "Movable list below"]
   [movable]])

(defn ^:dev/after-load start []
  (println "Mounting component...")
  (r/render [app] (.getElementById js/document "app")))
