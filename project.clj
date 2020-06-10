(defproject graphite-to-elastic-relay "0.1.0-SNAPSHOT"
  :description "Metrics relay from Graphite to ElastiSearch"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :plugins [[lein-cljfmt "0.6.7"]]
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [clj-http "3.10.1"]
                 [clojurewerkz/elastisch "3.0.1"]
                 [slingshot "0.12.2"]
                 [cheshire "5.10.0"]
                 [org.clojure/core.async "1.2.603"]
                 [com.taoensso/timbre "4.10.0"]
                 [yogthos/config "1.1.7"]]
  :main ^:skip-aot graphite-to-elastic-relay.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all :uberjar-name "graphite-to-elastic-relay.jar"}}
  :repl-options {:init-ns graphite-to-elastic-relay.core})
