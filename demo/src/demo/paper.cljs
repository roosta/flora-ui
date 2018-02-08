(ns demo.paper
  (:require [reagent.core :as r]
            [garden.units :refer [px]]
            [herb.macro :refer-macros [with-style]]
            [flora-ui.paper :refer [paper]]))

(defn paper-style
  []
  {:width (px 300)
   :height (px 200)})

(defn main []
  [:div [:h2 "Paper demo"]
   [:div [:a {:href "/"} "go to the home page"]]
   [paper {:class (with-style paper-style)
           :elevation 1}
    ]])
