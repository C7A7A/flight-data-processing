#!/bin/sh

CLUSTER_NAME=$(/usr/share/google/get_metadata_value attributes/dataproc-cluster-name)

kafka-console-consumer.sh \
--bootstrap-server ${CLUSTER_NAME}-w-0:9092  \
--topic flights-output \
--formatter kafka.tools.DefaultMessageFormatter \
--property print.key=true \
--property print.value=true