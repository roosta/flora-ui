(ns tincture.core
  (:require
   [cljs.spec.alpha :as s :include-macros true]
   [re-frame.core :as rf]
   [tincture.events]
   [tincture.subs]
   [tincture.db]
   [clojure.string :as str])
  (:require-macros
   [tincture.macros :as macros])
  )

(defn join-classes
  "take variable number of map keys and return classnames joined as a string "
  [styles & classes]
  (->> (select-keys styles classes)
       vals
       (str/join " ")))

(defn clamp
  [n a b]
  (max (min n b) a))

(defn name->kword
  "Converts strings into punctuation-free keywords
  source: https://github.com/rm-hull/inkspot"
  [s]
  (->
   (name s)
   (str/replace #"\W" " ")
   (str/trim)
   (str/replace #" +" "-")
   (str/lower-case)
   (keyword)))

;; TODO deal with function values and multiples.
(s/def ::valid-timing-fns #{"ease" "ease-in" "ease-out" "ease-in-out" "linear" "step-start" "step-end"})

(defn transition
  "take args and return a transtion string
  Needs work"
  [{:keys [prop duration timing-fn delay]
    :or {prop "all"
         duration "0.5s"
         timing-fn "ease"
         delay "0s"}}]
  (clojure.string/join " " [prop duration timing-fn delay]))

(defn index-of [coll value]
  (some (fn [[idx item]]
          (when (= value item)
            idx))
        (map-indexed vector coll)))

(defn css-wrap-url
  [prop]
  (str "url(" prop ")"))

(s/def ::valid-box-shadow-elevation (set (range 25)))

(defn box-shadow
  [elevation]
  {:pre [(s/valid? ::valid-box-shadow-elevation elevation)]}
  (case elevation
    0 "none"
    1 "0px 1px 3px 0px rgba(0, 0, 0, 0.2),0px 1px 1px 0px rgba(0, 0, 0, 0.14),0px 2px 1px -1px rgba(0, 0, 0, 0.12)"
    2 "0px 1px 5px 0px rgba(0, 0, 0, 0.2),0px 2px 2px 0px rgba(0, 0, 0, 0.14),0px 3px 1px -2px rgba(0, 0, 0, 0.12)"
    3 "0px 1px 8px 0px rgba(0, 0, 0, 0.2),0px 3px 4px 0px rgba(0, 0, 0, 0.14),0px 3px 3px -2px rgba(0, 0, 0, 0.12)"
    4 "0px 2px 4px -1px rgba(0, 0, 0, 0.2),0px 4px 5px 0px rgba(0, 0, 0, 0.14),0px 1px 10px 0px rgba(0, 0, 0, 0.12)"
    5 "0px 3px 5px -1px rgba(0, 0, 0, 0.2),0px 5px 8px 0px rgba(0, 0, 0, 0.14),0px 1px 14px 0px rgba(0, 0, 0, 0.12)"
    6 "0px 3px 5px -1px rgba(0, 0, 0, 0.2),0px 6px 10px 0px rgba(0, 0, 0, 0.14),0px 1px 18px 0px rgba(0, 0, 0, 0.12)"
    7 "0px 4px 5px -2px rgba(0, 0, 0, 0.2),0px 7px 10px 1px rgba(0, 0, 0, 0.14),0px 2px 16px 1px rgba(0, 0, 0, 0.12)"
    8 "0px 5px 5px -3px rgba(0, 0, 0, 0.2),0px 8px 10px 1px rgba(0, 0, 0, 0.14),0px 3px 14px 2px rgba(0, 0, 0, 0.12)"
    9 "0px 5px 6px -3px rgba(0, 0, 0, 0.2),0px 9px 12px 1px rgba(0, 0, 0, 0.14),0px 3px 16px 2px rgba(0, 0, 0, 0.12)"
    10 "0px 6px 6px -3px rgba(0, 0, 0, 0.2),0px 10px 14px 1px rgba(0, 0, 0, 0.14),0px 4px 18px 3px rgba(0, 0, 0, 0.12)"
    11 "0px 6px 7px -4px rgba(0, 0, 0, 0.2),0px 11px 15px 1px rgba(0, 0, 0, 0.14),0px 4px 20px 3px rgba(0, 0, 0, 0.12)"
    12 "0px 7px 8px -4px rgba(0, 0, 0, 0.2),0px 12px 17px 2px rgba(0, 0, 0, 0.14),0px 5px 22px 4px rgba(0, 0, 0, 0.12)"
    13 "0px 7px 8px -4px rgba(0, 0, 0, 0.2),0px 13px 19px 2px rgba(0, 0, 0, 0.14),0px 5px 24px 4px rgba(0, 0, 0, 0.12)"
    14 "0px 7px 9px -4px rgba(0, 0, 0, 0.2),0px 14px 21px 2px rgba(0, 0, 0, 0.14),0px 5px 26px 4px rgba(0, 0, 0, 0.12)"
    15 "0px 8px 9px -5px rgba(0, 0, 0, 0.2),0px 15px 22px 2px rgba(0, 0, 0, 0.14),0px 6px 28px 5px rgba(0, 0, 0, 0.12)"
    16 "0px 8px 10px -5px rgba(0, 0, 0, 0.2),0px 16px 24px 2px rgba(0, 0, 0, 0.14),0px 6px 30px 5px rgba(0, 0, 0, 0.12)"
    17 "0px 8px 11px -5px rgba(0, 0, 0, 0.2),0px 17px 26px 2px rgba(0, 0, 0, 0.14),0px 6px 32px 5px rgba(0, 0, 0, 0.12)"
    18 "0px 9px 11px -5px rgba(0, 0, 0, 0.2),0px 18px 28px 2px rgba(0, 0, 0, 0.14),0px 7px 34px 6px rgba(0, 0, 0, 0.12)"
    19 "0px 9px 12px -6px rgba(0, 0, 0, 0.2),0px 19px 29px 2px rgba(0, 0, 0, 0.14),0px 7px 36px 6px rgba(0, 0, 0, 0.12)"
    20 "0px 10px 13px -6px rgba(0, 0, 0, 0.2),0px 20px 31px 3px rgba(0, 0, 0, 0.14),0px 8px 38px 7px rgba(0, 0, 0, 0.12)"
    21 "0px 10px 13px -6px rgba(0, 0, 0, 0.2),0px 21px 33px 3px rgba(0, 0, 0, 0.14),0px 8px 40px 7px rgba(0, 0, 0, 0.12)"
    22 "0px 10px 14px -6px rgba(0, 0, 0, 0.2),0px 22px 35px 3px rgba(0, 0, 0, 0.14),0px 8px 42px 7px rgba(0, 0, 0, 0.12)"
    23 "0px 11px 14px -7px rgba(0, 0, 0, 0.2),0px 23px 36px 3px rgba(0, 0, 0, 0.14),0px 9px 44px 8px rgba(0, 0, 0, 0.12)"
    24 "0px 11px 15px -7px rgba(0, 0, 0, 0.2),0px 24px 38px 3px rgba(0, 0, 0, 0.14),0px 9px 46px 8px rgba(0, 0, 0, 0.12)"))

(s/def ::valid-text-shadow-elevation (set (range 4)))

(defn text-shadow
  [elevation]
  {:pre [(s/valid? ::valid-text-shadow-elevation elevation)]}
  (case elevation
    0 "none"
    ;; offset-x | offset-y | blur-radius | color
    1 "1px 1px 2px rgba(0, 0, 0, 0.3)"
    2 "2px 2px 4px rgba(0, 0, 0, 0.3)"
    3 "4px 4px 8px rgba(0, 0, 0, 0.3)"
    ))

(def easing
  "https://gist.github.com/bendc/ac03faac0bf2aee25b49e5fd260a727d"
  {:ease-in-quad "cubic-bezier(.55, .085, .68, .53)"
   :ease-in-cubic "cubic-bezier(.550, .055, .675, .19)"
   :ease-in-quart "cubic-bezier(.895, .03, .685, .22)"
   :ease-in-quint "cubic-bezier(.755, .05, .855, .06)"
   :ease-in-expo "cubic-bezier(.95, .05, .795, .035)"
   :ease-in-circ "cubic-bezier(.6, .04, .98, .335)"

   :ease-out-quad "cubic-bezier(.25, .46, .45, .94)"
   :ease-out-cubic "cubic-bezier(.215, .61, .355, 1)"
   :ease-out-quart "cubic-bezier(.165, .84, .44, 1)"
   :ease-out-quint "cubic-bezier(.23, 1, .32, 1)"
   :ease-out-expo "cubic-bezier(.19, 1, .22, 1)"
   :ease-out-circ "cubic-bezier(.075, .82, .165, 1)"

   :ease-in-out-quad "cubic-bezier(.455, .03, .515, .955)"
   :ease-in-out-cubic "cubic-bezier(.645, .045, .355, 1)"
   :ease-in-out-quart "cubic-bezier(.77, 0, .175, 1)"
   :ease-in-out-quint "cubic-bezier(.86, 0, .07, 1)"
   :ease-in-out-expo "cubic-bezier(1, 0, 0, 1)"
   :ease-in-out-circ "cubic-bezier(.785, .135, .15, .86)"}
  )

(def breakpoints {:xs 0
                  :sm 600
                  :md 960
                  :lg 1280
                  :xl 1920})

(defn gradient
  [direction & colors]
  #{"-webkit-linear-gradient(to right, #ffd89b, #19547b)"})

(def ui-gradient
  "Loads gradients from a JSON source as per format here:
   https://github.com/Ghosh/uiGradients/blob/master/gradients.json
  source: https://github.com/rm-hull/inkspot"
  (let [gradients (macros/ui-gradients "gradients.json")]
    (fn [name steps]
      (let [k (name->kword name)
            [col1 col2] (k gradients)]
        (when (and col1 col2)
          [col1 col2])))))

(defn init!
  []
  (rf/dispatch-sync [:tincture/initialize]))