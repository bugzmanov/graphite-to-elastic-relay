# graphite-to-elastic-relay
Simple tool to relay metrics data from graphite to elastic-search. Intended for migration purposes

<img width="1384" alt="image" src="https://user-images.githubusercontent.com/502482/84321131-a86a0b00-ab40-11ea-9c8d-0a7d1cf222ee.png">

# Build & Run 

```bash
git clone https://github.com/bugzmanov/graphite-to-elastic-relay.git
cd graphite-to-elastic-relay
make publish
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
