(ns graphite-to-elastic-relay.core (:gen-class)
    (:require [graphite-to-elastic-relay.graphite.client :as graphite]
              [graphite-to-elastic-relay.elastic.client :as elastic]
              [graphite-to-elastic-relay.sync.synchronize :as replay]
              [clojure.core.async :as async]
              [taoensso.timbre :as timbre]
              [config.core :refer [env]]
              [clojure.java.io :as io]
              [clojure.edn :as edn]))

(defn -main [& args]
  (timbre/info "Starting with config:")
  (println
   (select-keys env (->> (io/resource "config.edn") slurp edn/read-string keys)))
  (async/<!!  (replay/run env)))

