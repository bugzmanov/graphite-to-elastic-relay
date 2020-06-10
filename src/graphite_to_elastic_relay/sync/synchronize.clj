(ns graphite-to-elastic-relay.sync.synchronize
  (:require [graphite-to-elastic-relay.graphite.client :as graphite]
            [clojure.core.async :as async :refer [<! >!]]
            [taoensso.timbre :as timbre]
            [graphite-to-elastic-relay.elastic.client :as elastic]))

(defn exponential-backoff [time rate max terminate? f]
  (if (and terminate? (>= time max))
    (f)
    (try
      (f)
      (catch Throwable t
        (timbre/warn t (format "Execution failure. Retrying in %d msec" time))
        (Thread/sleep time)
        (exponential-backoff (min (* time rate) max) rate max terminate? f)))))

(defmacro try-backoff [[time rate max terminate?] & body]
  `(exponential-backoff (or ~time 1000) ;; defaults!
                        (or ~rate 2)
                        (or ~max 10000)
                        (and ~terminate? (constantly true))
                        (fn [] ~@body)))

(def metrics-chan (async/chan 1))

(defn run [cfg]
  (async/go-loop []
    (graphite/get-metrics-async (:graphite-url cfg) (:graphite-metrics cfg)
                                (fn [metrics] (async/go (>! metrics-chan metrics)))
                                (fn [exception] (timbre/error exception "Failed to read data from graphite")))
    (<! (async/timeout (* 1000 (:poll-period-seconds cfg))))
    (recur))

  (let [url (:elastic-url cfg)
        index (:metrics-index cfg)
        auth (:elastic-auth cfg)
        opts {:basic-auth auth}
        es-latest-metrics (try-backoff [1000 2 32000 true] (elastic/get-latest-metrics url opts index))
        send-metrics (elastic/with-dedup es-latest-metrics elastic/send-metrics)]

    (async/go-loop []
      (let [metrics (<! metrics-chan)]
        (if-not (empty? metrics)
          (do
            (async/thread (try-backoff [1000 2 32000 false] (send-metrics url index opts metrics)))
            (timbre/debug (format "%d metrics sent to elastic-search" (count metrics)))))
        (recur)))))
