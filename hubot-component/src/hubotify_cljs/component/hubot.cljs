(ns hubotify-cljs.component.hubot
  "Reload idea from:
  https://github.com/vinta/hubot-reload-scripts/blob/master/src/reload-scripts.coffee"
  (:require-macros [fence.core :refer [+++]])
  (:require [cljs.nodejs :as nodejs]
            [com.stuartsierra.component :as component]
            [hubotify-cljs.component.listener.proto :as proto]))

(def fs (nodejs/require "fs"))
(def path (nodejs/require "path"))

(defprotocol Reloadable
  (reload [this]))

(defn- clear-all-commands! [robot]
  (aset robot "commands" #js []))

(defn- clear-all-listeners! [robot]
  (aset robot "listeners" #js []))

(defn- load-scripts! [robot]
  (+++ (let [script-path (.resolve path "." "scripts")]
         (.load robot script-path))))

(defn- load-src-scripts! [robot]
  (+++ (let [script-path (.resolve path "." "src" "scripts")]
         (.load robot script-path))))

(defn- load-hubot-scripts! [robot]
  (+++ (let [hubot-script-path (.resolve path "." "hubot-scripts.json")]
         (.readFile fs hubot-script-path #js {:encoding "UTF-8"}
                    (fn [err data]
                      (if (> (.-length data) 0)
                        (try
                          (let [scripts (.parse js/JSON data)
                                scripts-path (.resolve path "node_modules" "hubot-scripts" "src" "scripts")]
                            (.loadHubotScripts robot scripts-path scripts))
                          (catch js/Error err
                            (.error js/console "Error parsing JSON data from hubot-scripts.json: " err)))))))))

(defn- load-external-scripts! [robot]
  (+++ (let [external-scripts (.resolve path "." "external-scripts.json")]
         (.readFile fs external-scripts #js {:encoding "UTF-8"}
                    (fn [err data]
                      (if (> (.-length data) 0)
                        (try
                          (let [scripts (.parse js/JSON data)]
                            (.loadExternalScripts robot scripts))
                          (catch js/Error err
                            (.error js/console "Error parsing JSON data from external-scripts.json: " err)))))))))

(defn- activate-all-listeners! [robot listeners]
  (doseq [l listeners]
    (proto/activate l robot)))

(defrecord Hubot [robot listeners]
  component/Lifecycle
  (start [c]
    (activate-all-listeners! robot listeners)
    (assoc c :robot robot))
  (stop [c]
    (dissoc c :robot robot))

  Reloadable
  (reload [_]
    ;; Clear all commands/listeners
    (clear-all-commands! robot)
    (clear-all-listeners! robot)

    ;; Load all scripts
    (load-scripts! robot)
    (load-src-scripts! robot)
    (load-hubot-scripts! robot)
    (load-external-scripts! robot)
    (+++ (.info js/console "Reloaded all scripts"))))

(defn new-hubot [robot]
  (map->Hubot {:robot robot}))
