(ns graphite-to-elastic-relay.graphite.client
  (:require [clj-http.client :as client]
            [cheshire.core :refer :all]))

(defn get-metrics [url metrics]
  (let [metrics-str (clojure.string/join "&" (map (partial str "target=") metrics))
        http-resp (client/post
                   (str url "/render?from=-1min&noNullPoints&until=now&format=json&maxDataPoints=720&" metrics-str))]
    (parse-string (:body http-resp) true)))

(defn- empty-key? [[key _]]
  (nil? key))

(defn- transform-graphite-metric [metric]
  (let [doc (:tags metric)
        name (:target metric)
        non-empty-points (remove empty-key? (:datapoints metric))
        points (map (fn [[value timestamp]]  {:timestamp timestamp :value value}) non-empty-points)]
    (map (fn [p] (merge doc p {:name name})) points)))

(defn get-metrics-async [url metrics fn-success fn-fail]
  (let [metrics-str (clojure.string/join "&" (map (partial str "target=") metrics))]
    (client/post
     (str url "/render?from=-1min&until=now&format=json&maxDataPoints=720&" metrics-str)
     {:async? true}
     (fn [response] (let [json (parse-string (:body response) true)
                          metrics (flatten (map transform-graphite-metric  json))]
                      (fn-success metrics)))
     fn-fail)))
