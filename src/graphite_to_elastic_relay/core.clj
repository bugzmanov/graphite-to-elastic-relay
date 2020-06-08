(ns graphite-to-elastic-relay.core (:gen-class)
    (:require [graphite-to-elastic-relay.graphite.client :as graphite])
    (:require [graphite-to-elastic-relay.elastic.client :as elastic]))

(defn- empty-key? [[key _]]
  (nil? key))

(defn transform-graphite-metric [metric]
  (let [doc (:tags metric)
        name (:target metric)
        non-empty-points (remove empty-key? (:datapoints metric))
        points (map (fn [[value timestamp]]  {:timestamp timestamp :value value}) non-empty-points)]
    (map (fn [p] (merge doc p {:name name})) points)))

(defn -main [& args]
  (let
   [graphite-metrics (graphite/get-metrics "http://localhost:8081" ["jvm.memory.max.area.*.*.*"])
    metrics (flatten (map transform-graphite-metric  graphite-metrics))]
    (elastic/send-metrics "http://localhost:9200" metrics)
    ;(println (elastic/get-latest-metrics "http://localhost:9200"))
    ))

