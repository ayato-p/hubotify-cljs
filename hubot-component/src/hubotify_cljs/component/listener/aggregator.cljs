(ns hubotify-cljs.component.listener.aggregator
  (:require [com.stuartsierra.component :as component]
            [hubotify-cljs.component.listener.proto :as proto]))

(defrecord ListenerAggregator [listeners]
  component/Lifecycle
  (start [c]
    (assoc c :listeners (filter proto/hubot-listener? listeners)))
  (stop [c]
    (dissoc c :listeners)))

(defn new-listeners [& listeners]
  (map->ListenerAggregator {:listeners listeners}))
