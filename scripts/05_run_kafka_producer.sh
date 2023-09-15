#!/bin/sh

CLUSTER_NAME=$(/usr/share/google/get_metadata_value attributes/dataproc-cluster-name)

java -cp /usr/lib/kafka/libs/*:KafkaProducer.jar \
org.example.bigdata.TestProducer data/flights 10 flights-input \
1 ${CLUSTER_NAME}-w-0:9092
