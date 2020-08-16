#!/usr/bin/env bash

echo "Starting mysql-test-db ..."
docker build -t mysql-test-db .
docker-compose up -d
