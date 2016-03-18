(ns hubotify-cljs.component.hubot
  (:require [com.stuartsierra.component :as component]))

(defprotocol Reloadable
  (reload [this]))

(defrecord Hubot [robot listeners]
  component/Lifecycle
  (start [c]
    (assoc c :robot robot))
  (stop [c]
    (dissoc c :robot robot))

  Reloadable
  (reload [_]
    (.-commands robot)))

(defn new-hubot [robot]
  (map->Hubot {:robot robot}))
