(ns hubotify-cljs.component.listener.proto)

(defprotocol HubotListener
  (activate [f robot]))

(extend-protocol HubotListener
  cljs.core.Fn
  (activate [f robot]
    (f robot)))

(defn hubot-listener? [o]
  (satisfies? HubotListener o))
