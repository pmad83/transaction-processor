#!/bin/bash
cd $(realpath $(dirname $0))

docker compose -p transaction-processor -f ../docker/conf/docker-compose.yml down --volumes
