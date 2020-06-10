build:
	source ./bin/check_java.sh && ./bin/lein uberjar

publish: build
	docker build --pull . -t bugzmanov/graphite-elastic-relay:0.1

run-demo: publish
	docker-compose -f demo/docker-compose.yml up --build -d
	sleep 5
	-curl -X PUT --header User-Agent: --header "Content-Type: application/json" \
				--header "Accept: application/json" --header "Authorization: Basic YWRtaW46YWRtaW4=" \
				--data-binary "{ \"theme\": \"dark\", \"homeDashboardId\":1, \"timezone\":\"\" }" \
				http://localhost:3000/api/org/preferences
	@echo ""
	@echo ""
	@echo "\033[0;32mLogin to: \033[0;33mhttp://localhost:3000/\033[0m"

clean:
	-docker-compose -f demo/docker-compose.yml down
	-docker-compose -f demo/docker-compose.yml rm -f
