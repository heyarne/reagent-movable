(ns reagent-movable.core
  (:require [reagent.core :as r]))

(defn app []
  [:div
   [:h1 "Hello world"]
   [:p "If you can read this it means this is working."]])

(defn ^:export mount-into [dom-elem]
  (r/render-component [app] dom-elem))

;; helpers

(defn reload! []
  (.. js/window -location reload))
