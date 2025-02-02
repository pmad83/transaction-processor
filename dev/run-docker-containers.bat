cd %~dp0..

call ./transaction-app/gradlew -p ./transaction-app clean build -x test

call docker compose -p transaction-processor -f ./docker/conf/docker-compose.yml build --no-cache

call docker compose -p transaction-processor -f ./docker/conf/docker-compose.yml up
