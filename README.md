# Graphite to ElasticSearch data relay
Simple tool to transfer metrics data from graphite to elastic-search. Intended for migration purposes

<img width="1384" alt="image" src="https://user-images.githubusercontent.com/502482/84321131-a86a0b00-ab40-11ea-9c8d-0a7d1cf222ee.png">

# Build & Run 

## Build
```bash
git clone https://github.com/bugzmanov/graphite-to-elastic-relay.git
cd graphite-to-elastic-relay
make build
```

## Run

### Without docker

Using environment variables

```bash
java -DGRAPHITE_URL="http://localhost:80/graphite" -DELASTIC_URL="http://elasticsearch:9200" \
     -DMETRICS_INDEX=metric -DGRAPHITE_METRICS="[\"stats_counts.statsd.*\" \"*.agents.*.*\"]" \
     -jar target/uberjar/graphite-to-elastic-relay.jar
```

Using configuration file:

See config file [example](https://raw.githubusercontent.com/bugzmanov/graphite-to-elastic-relay/master/resources/config.edn)

```bash
java -Dconfig="relay-config.edn" -jar target/uberjar/graphite-to-elastic-relay.jar
```

### Using docker

Build:

```bash
make publish
```

Run:

```bash
docker run -e GRAPHITE_URL="http://graphite:80" -e ELASTIC_URL="http://elasticsearch:9200" \
       -e METRICS_INDEX=xmetrix -e GRAPHITE_METRICS="[\"stats_counts.statsd.*\" \"*.agents.*.*\"]" \
        bugzmanov/graphite-elastic-relay:0.1 
```

Build custom docker image with config:

```
FROM bugzmanov/graphite-elastic-relay:0.1
COPY custom-config.edn /opt/relay-config.edn

```

## Quick demo

Prerequisites:
- docker
- docker-compose 

```bash
make run-demo
```

Clean up: 
```bash 
make clean
```

docker-compose definition:  https://github.com/bugzmanov/graphite-to-elastic-relay/blob/master/demo/docker-compose.yml
