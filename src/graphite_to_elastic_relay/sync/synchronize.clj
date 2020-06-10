(ns graphite-to-elastic-relay.sync.synchronize
  (:require [graphite-to-elastic-relay.graphite.client :as graphite]
            [clojure.core.async :as async :refer [<! >!]]
            [taoensso.timbre :as timbre]
            [graphite-to-elastic-relay.elastic.client :as elastic]))

(def metrics-chan (async/chan 1))

(defn run [cfg]
  (async/go-loop []
    (graphite/get-metrics-async (:graphite-url cfg) (:graphite-metrics cfg)
                                (fn [metrics] (async/go (>! metrics-chan metrics)))
                                (fn [exception] (timbre/error exception)))
    (<! (async/timeout (* 1000 (:poll-period-seconds cfg))))
    (recur))

  (let [url (:elastic-url cfg)
        index (:metrics-index cfg)
        auth (:elastic-auth cfg)
        opts {:basic-auth auth}
        send-metrics (elastic/with-dedup (elastic/get-latest-metrics url opts index) elastic/send-metrics)]

    (async/go-loop []
      (let [metrics (<! metrics-chan)]
        (if-not (empty? metrics)
          (do
            (async/thread (send-metrics url index opts metrics))
            (timbre/info "Send metrics")))
        (recur)))))
