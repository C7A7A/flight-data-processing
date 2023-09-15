
<a name="readme-top"></a>

<!-- TABLE OF CONTENTS -->
<details>
  <summary>Table of Contents</summary>
  <ol>
    <li>
      <a href="#about-the-project">About The Project</a>
    </li>
    <li>
        <a href="#built-with">Built With</a>
    </li>
    <li>
        <a href="#files-and-data">Files and Data</a>
    </li>
    <li>
        <a href="#getting-starded">Getting started</a>
    </li>
    <li>
        <a href="#detailed-description">Detailed description</a>
    </li>
  </ol>
</details>


<!-- ABOUT THE PROJECT -->
## About the project
Data processing engine that uses data from kafka topics, processes it and sends back to other kafka topics. Application can be run locally or on the Google Cloud. Prepared scripts can be used to run on GCP. If you want to run program locally you will have to 
prepare the environement to work with kafka and install the required libraries by yourself.

![Architecture](/other/readmeScreens/Architecture.PNG)

<p align="right">(<a href="#readme-top">back to top</a>)</p>

## Built With
[![Java][Java]][Java-url]
[![Kafka][Kafka]][Kafka-url]
[![Shell][Shell]][Shell-url]
[![GCP][GCP]][GCP-url]

<p align="right">(<a href="#readme-top">back to top</a>)</p>

<!-- FILES AND DATA -->
## Files and data
### Project structure
<ul>    
    <li> KafkaStreamsProjects - processing engine </li>
    <li> jars - Java Archives (KafkaProducer.jar and flight-data-processing-kafka-streams.jar)  </li>
    <li> other - some random files, not relevant (notes, report in polish, python script to test kafka topics) </li>
    <li> scripts - scripts needed to run engine on gcp </li>
</ul>

### Data
Originally data (flights, airports) was located in https://www.kaggle.com/usdot/flight-delays. Altough data was provided to me from http://www.cs.put.poznan.pl/kjankiewicz/bigdata/stream_project and I recommend to use second source. You only need to download airports.csv and flights-2015.zip.

<p align="right">(<a href="#readme-top">back to top</a>)</p>

<!-- GETTING STARTED -->
## Getting Started
### My gcp settings
Environment variables:
```
export REGION=europe-west4
export ZONE=${REGION}-c
export CLUSTER_NAME=bigdata-intro
export PROJECT_ID=$(gcloud config get-value project)
```

bucket name: `flight-data-kafka-streams`

### How to run
1. Clone the repo
   ```
   git clone https://github.com/C7A7A/flight-data-processing
   ```
2. Log in into your gcp account
3. Copy to your bucket (remember your bucket name, it will be used later on)
    ```
    - data about airports airports.csv to airports folder
    - data about flights flights-2015.zip to flights folder
    - kafka producer KafkaProducer.jar
    - processing engine flight-data-processing-kafka-streams.jar
    - scripts folder
    ```
4. Launch the cluster (you need to set CLUSTER_NAME, REGION, PROJECT_ID)
    ```
    gcloud dataproc clusters create ${CLUSTER_NAME} \
    --enable-component-gateway --region ${REGION} --subnet default \
    --master-machine-type n1-standard-2 --master-boot-disk-size 50 \
    --num-workers 2 --worker-machine-type n1-standard-2 --worker-boot-disk-size
    50 \
    --image-version 2.1-debian11 --optional-components DOCKER,ZOOKEEPER \
    --project ${PROJECT_ID} --max-age=3h \
    --metadata "run-on-master=true" \
    --initialization-actions \
    gs://goog-dataproc-initialization-actions-${REGION}/kafka/kafka.sh
    ```
5. Start 4 terminals
    ```
    - server - for running scripts and running kafka producer
    - receiver_1 - for receiving standard data
    - receiver_2 - for receiving anomaly data
    - engine - to run data processing engine
    ```
6. Prepare scripts (server)
    ```
    hadoop fs -copyToLocal gs://YOUR_BUCKET_NAME/scripts
    chmod +x scripts/*
    ```
7. Copy data from bucket (server)
    ```
    ./scripts/01_download_data.sh YOUR_BUCKET_NAME
    
    As an argument pass yout bucket name. Script should copy data about flight and airports, kafka producer, processing engine. Moreover it should create relevant folders, move data to folders, unpack data and delete zip file.
    ```
8. Prepare kafka topics (server)
    ```
    ./scripts/02_prepare_kafka.sh

    Script should delete kafka topics (do not worry about errors if topics don't exist), create kafka topics and send static data about airports to kafka topic.
    ```
9. Run etl consumer (receiver_1)
    ```
    ./scripts/03_start_etl_consumer.sh

    Script should run etl consumer. After activating the command you can move to next step, consumer is working.
    ```
10. Run anomaly consumer (receiver_2)
    ```
    ./scripts/04_start_anomaly_consumer.sh

    Script should run anomaly consumer. After activating the command you can move to next step, consumer is working.
    ```
11. Run kafka producer (server)
    ```
    ./scripts/05_run_kafka_producer.sh

    Producer should generate data about flights from 100 files that store information about flights to kafka topic named flights-input. After activating the command you can move to next step, consumer is working.
    ```
12. Run processing engine (engine)
    ```
    Before turning eninge on set CLUSTER_NAME

    CLUSTER_NAME=$(/usr/share/google/get_metadata_value attributes/dataproc-cluster-name)

    a. Mode A + few anomalies
    java -cp /usr/lib/kafka/libs/*:flight-data-processing-kafka-streams.jar \       org.example.ProcessFlightApplication 45 10 A ${CLUSTER_NAME}-w-0:9092

    b. Mode B + a lot of anomalies
    java -cp /usr/lib/kafka/libs/*:flight-data-processing-kafka-streams.jar \
    org.example.ProcessFlightApplication 60 5 C ${CLUSTER_NAME}-w-0:9092
    ```
13. Watch emerging data in etl and anomaly consumers
    ```
    - you will probably need to wait for a brief moment to see first results
    - if you want to change parameters and run engine again just stop it (ctrl + c) and turn on with new parameters
    ```

<p align="right">(<a href="#readme-top">back to top</a>)</p>


<!-- DETAILED DESCRIPTION -->
## Detailed description
### Goals
#### 1. ETL - real time data
```
Maintain aggregation at the day level. Aggregate values are:
- number of departures
- sum of delays in departures
- number of arrivals
- sum of delays in arrivals
```

#### 2. Anomaly Detection
Let's assume that for each airport, what matters is the number of planes that are heading to it and are currently in the process of flight and will reach airport in a given period of time. In a situation where there are too many planes,
it's necessary to deploy additional people to help at that airport. It is important to know about this fact at least 30 minutes in advance - only then there is enough time to react. Anomaly detection is supposed to report for each airport a situation in which a fixed number N of aircraft that are currently in flight, are scheduled to arrive at that airport in time interval D starting in 30 minutes.
```
The program is to be parameterized by:
- D - the length of the time interval expressed in minutes
- N - the number of in-flight aircraft flying to the specified destination airport
```
Anomaly detection is performed every 10 minutes. For example, for parameters D=60, N=30, the program will report every 10 minutes airports for which within 60
minutes counting from 30 minutes from the current scheduled time, at least 30 planes will arrive.
Reported data includes:
```
- analyzed period - window (start and stop)
- airport name
- IATA code of the airport,
- airport city
- the state in which the airport city is located
- the number of planes arriving at the airport in the specified period
- number of all planes flying to the airport
```
Assume that the data may be unstructured - it may be delayed by 5 minutes.

### Modes
<ul>
    <li> mode A - application provides data to kafka consumer with the least possible delay even if data is not final and there will be need to update resul </li>
    TODO: screen
    <li> mode C - application provides data to kafka consumer as fast as possible but result is final and there is no need for update </li>
    TODO: screen
</ul>

Mode A
![ModeA](/other/readmeScreens/modeA.PNG)

Mode C
![ModeC](/other/readmeScreens/modeC.PNG)

### Code
#### Maintaining a real time data - transformations
![RTDTrans](/other/readmeScreens/RTDTrans.PNG)
```
- filter -> check if event is valid
- selectKey -> select data as key
- groupByKey -> group data by key
- windowedBy -> create windows of length 1 day
- aggregate ->
    - if event is departure (D), then increment sum of departures and add delay to sum of delays
    - if event is arrival (A), then increment sum of arrivals and add delay to sum of arrivals
    - otherwise set previous data
```

#### real time data - mode A
In Kafka Streams mode A works perfectly out of the box, there was no need to do anything in code

#### real time data - mode C
![RTDModeC](/other/readmeScreens/delayC.PNG)
suppress is used to handle mode C, it lets send data to kafka topic only after window is closed.

#### Anomaly detection
![AnomalyDetection](/other/readmeScreens/detectAnomalies.PNG)
```
- filter -> check if event is departure event (D)
- groupByKey -> group data by key (airportIATA)
- windowedBy -> create windows of length 10 minutes with delay possibility of 5 minutes
- aggregate ->
    - increments sum of departures
    - checks if airport will reach the airport between 30 and 30 + D minutes (D is user defined param)
        - if true, then increments sum of approaching flights
        - if false, then set previous data
```

![AnomalyDetection](/other/readmeScreens/anomaliesRecord.PNG)
```
- map -> retrieves data about beginning and end of window
- join -> join data about flights and airports
- mapValues -> return AnomalyRecord object that has all information regarding the anomaly
```
getAnomalyRecord function
![AnomalyDetection](/other/readmeScreens/getAnomalyRecord.PNG)

<p align="right">(<a href="#readme-top">back to top</a>)</p>

<!-- MARKDOWN LINKS & IMAGES -->

<!-- https://www.markdownguide.org/basic-syntax/#reference-style-links -->
[Java]:https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white
[Java-url]: https://docs.oracle.com/en/java/
[Kafka]:https://img.shields.io/badge/Apache%20Kafka-000?style=for-the-badge&logo=apachekafka
[Kafka-url]: https://kafka.apache.org/intro
[Shell]: https://img.shields.io/badge/Shell_Script-121011?style=for-the-badge&logo=gnu-bash&logoColor=white
[Shell-url]: https://www.gnu.org/savannah-checkouts/gnu/bash/manual/bash.html
[GCP]:https://img.shields.io/badge/-GCP-blue?style=for-the-badge&logo=google&logoColor=white
[GCP-url]:https://cloud.google.com/docs
