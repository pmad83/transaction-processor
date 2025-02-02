cd %~dp0..

call docker compose -p transaction-processor -f docker/conf/docker-compose.yml down --volumes
