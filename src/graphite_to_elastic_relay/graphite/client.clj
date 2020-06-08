(ns graphite-to-elastic-relay.graphite.client
  (:require [clj-http.client :as client]
            [cheshire.core :refer :all]))

(defn get-metrics [url metrics]
  (let [metrics-str (clojure.string/join "&" (map (partial str "target=") metrics))
        http-resp (client/post
                   (str url "/render?from=-1min&until=now&format=json&maxDataPoints=720&" metrics-str))]
    (parse-string (:body http-resp) true)))

