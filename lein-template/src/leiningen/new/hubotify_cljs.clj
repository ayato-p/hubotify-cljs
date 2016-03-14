(ns leiningen.new.hubotify-cljs
  (:require [leiningen.new.templates :as tmpl]
            [leiningen.core.main :as main]))

(def render (tmpl/renderer "hubotify-cljs"))

(defn hubotify-cljs
  "FIXME: write documentation"
  [name]
  (let [data {:name name
              :sanitized (tmpl/name-to-path name)}]
    (main/info "Generating fresh 'lein new' hubotify-cljs project.")
    (tmpl/->files data
                  ["src/{{sanitized}}/foo.clj" (render "foo.clj" data)])))
