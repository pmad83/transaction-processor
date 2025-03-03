#!/bin/bash
cd $(realpath $(dirname $0))

cd ../transaction-app
./gradlew clean build -x test

docker compose -p transaction-processor -f ../docker/conf/docker-compose.yml build --no-cache

docker compose -p transaction-processor -f ../docker/conf/docker-compose.yml up
