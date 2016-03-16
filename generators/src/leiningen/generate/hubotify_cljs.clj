(ns leiningen.generate.hubotify-cljs
  (:require [leiningen.generate.templates :as tmpl]
            [leiningen.new.templates :refer [name-to-path render-text]]
            [leiningen.core.main :as main]
            [clojure.string :as str]))

(def render (tmpl/renderer "hubotify-cljs"))

(defn- snake-case [hyphenated]
  (str/replace hyphenated #"-" "_"))

(defn- ->relative-path [path]
  (str/replace-first path #"^scripts" "."))

(defn- extract-build-config [project]
  (let [builds (get-in project [:cljsbuild :builds])]
    (->> (cond-> builds (map? builds) vals)
         (map :compiler)
         (map #(select-keys % [:main :output-to])))))

(defn hubotify-cljs [project]
  (doseq [conf (extract-build-config project)
          :let [data {:module-name (snake-case (:main conf))
                      :js-file-path (->relative-path (:output-to conf))}]]
    (main/info (pr-str data))
    (main/info "Wrote:" (render-text "scripts/{{module-name}}.coffee" data))
    (tmpl/->files data ["scripts/{{module-name}}.coffee" (render "source.clj" data)])))
