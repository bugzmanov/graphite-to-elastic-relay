build:
	source ./bin/check_java.sh && ./bin/lein uberjar

publish: build
	docker build --pull . -t bugzmanov/graphite-elastic-relay:0.1
