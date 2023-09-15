#!/bin/sh

BUCKET_NAME="$1"

echo "Copying flight-data-processing-kafka-streams.jar from bucket"
hadoop fs -copyToLocal gs://$BUCKET_NAME/flight-data-processing-kafka-streams.jar

echo "Copying KafkaProducer.jar from bucket"
hadoop fs -copyToLocal gs://$BUCKET_NAME/KafkaProducer.jar

echo "Copying airports folder from bucket"
hadoop fs -copyToLocal gs://$BUCKET_NAME/airports

echo "Copying flights folder from bucket"
hadoop fs -copyToLocal gs://$BUCKET_NAME/flights

echo "Preparing folders for data"
mkdir data
mv airports/ data/
mv flights/ data/

echo "Unzipiping flight files"
unzip data/flights/flights-2015.zip -d data/flights/

echo "Cleaning up"
rm -rf data/flights/flights-2015.zip

echo "Done"
