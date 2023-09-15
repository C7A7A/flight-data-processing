#!/bin/sh

set -e

CLUSTER_NAME=$(/usr/share/google/get_metadata_value attributes/dataproc-cluster-name)

echo "Checking if kafka topics already exist"
kafka-topics.sh --delete --bootstrap-server ${CLUSTER_NAME}-w-0:9092 --topic flights-input || true
kafka-topics.sh --delete --bootstrap-server ${CLUSTER_NAME}-w-0:9092 --topic flights-output || true
kafka-topics.sh --delete --bootstrap-server ${CLUSTER_NAME}-w-0:9092 --topic airports-input || true
kafka-topics.sh --delete --bootstrap-server ${CLUSTER_NAME}-w-0:9092 --topic airports-output || true

echo "Creating kafka topics"
kafka-topics.sh --create --topic flights-input --bootstrap-server ${CLUSTER_NAME}-w-0:9092 --replication-factor 1 --partitions 1
kafka-topics.sh --create --topic flights-output --bootstrap-server ${CLUSTER_NAME}-w-0:9092 --replication-factor 1 --partitions 1
kafka-topics.sh --create --topic airports-input --bootstrap-server ${CLUSTER_NAME}-w-0:9092 --config cleanup.policy=compact --replication-factor 1 --partitions 1
kafka-topics.sh --create --bootstrap-server ${CLUSTER_NAME}-w-0:9092 --topic airports-output --replication-factor 1 --partitions 1

echo "Sending airport data to kafka topic"
cat data/airports/airports.csv | awk -F ',' '{print $5 ":" $1 "," $2 "," $3 "," $4 "," $5 "," $6 "," $7 "," $8 "," $9 "," $10 "," $11 "," $12 "," $13 "," $14 ""}' | kafka-console-producer.sh --bootstrap-server ${CLUSTER_NAME}-w-0:9092 --topic airports-input --property key.separator=: --property parse.key=true

echo "Done"
