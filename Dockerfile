FROM expert/docker-java-minimal:jdk12-alpine

WORKDIR /

COPY target/uberjar/graphite-to-elastic-relay.jar ges-relay.jar

CMD java -Dconfig="/opt/relay-config.edn" -jar ges-relay.jar
