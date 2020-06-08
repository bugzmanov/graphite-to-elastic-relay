(ns graphite-to-elastic-relay.elastic.client
  (:require [clojurewerkz.elastisch.rest.document      :as doc]
            [clojurewerkz.elastisch.rest.bulk          :as bulk]
            [clojurewerkz.elastisch.rest.index         :as idx]
            [clojurewerkz.elastisch.rest               :as rest]
            [clojurewerkz.elastisch.aggregation        :as a]
            [clojurewerkz.elastisch.query              :as q]
            [clojurewerkz.elastisch.rest.response :as esrsp]
            [clojurewerkz.elastisch.rest.response :refer [created? acknowledged? conflict? hits-from any-hits? no-hits?]])
  (:use [slingshot.slingshot :only [throw+ try+]]))

(def ^{:const true} index-name "xmetrix")
(def ^{:const true} index-type "metrics")

(defn- all-created? [xs]
  (every? created? xs))

(defn- create-doc [metric]
  (merge
   {"@timestamp" (:timestamp metric) :name (:name metric) :value (:value metric)}
   (:tags metric)))

(defn send-metrics [url metrics]
  (let [conn (rest/connect url {:content-type :application/json})
        documents  (map create-doc metrics)
        for-index (map (fn [doc] (assoc doc :_type index-type)) documents)
        insert-operations (bulk/bulk-index for-index)
        response          (bulk/bulk-with-index conn index-name insert-operations {:refresh true})]
    (if-not (and (false? (:errors response)) (all-created? (->> response :items (map :index))))
      (throw (Exception. (.toString response))))))

(defn- extract-elastic-metric [docs]
  (map (fn [doc] {(:key doc) (->> doc :theMax :value long (* 1000))}) docs))

(defn get-latest-metrics [url]
  (let [conn (rest/connect url {:content-type :application/json})
        response (try+ (doc/search conn index-name nil {:size 0
                                                        :aggs {:yourGroup (merge
                                                                           (a/terms "name.keyword" {:size 1000})
                                                                           {:aggregations {:theMax (a/max "@timestamp")}})}})
                       (catch [:status 404] _ nil))]
    (->> response :aggregations :yourGroup :buckets extract-elastic-metric)))


