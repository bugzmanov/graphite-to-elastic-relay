(ns graphite-to-elastic-relay.core (:gen-class)
    (:require [graphite-to-elastic-relay.graphite.client :as graphite]
              [graphite-to-elastic-relay.elastic.client :as elastic]
              [graphite-to-elastic-relay.sync.synchronize :as replay]
              [clojure.core.async :as async]
              [config.core :refer [env]]))

(defn -main [& args]
  (async/<!!  (replay/run env)))

