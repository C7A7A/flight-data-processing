package org.example;

import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.*;
import org.example.joiners.FlightAirportJoiner;
import org.example.models.*;
import org.example.serialization.GenericSerde;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

public class ProcessFlightApplication {
    public static void main(String[] args) {
        int D = Integer.parseInt(args[0]);
        int N = Integer.parseInt(args[1]);
        String delay = args[2];
        String bootstrapServers = args[3];
        if (bootstrapServers.isEmpty()) {
            bootstrapServers = "${CLUSTER_NAME}-w-0:9092";
        }

        System.out.println("=== PARAMS ===");
        System.out.println("D: " + D + ", N: " + N + ", delay: " + delay + ", server: " + bootstrapServers);

        Properties properties = new Properties();
        properties.put(StreamsConfig.APPLICATION_ID_CONFIG, "process-flight-data");
        properties.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        properties.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        properties.put(StreamsConfig.DEFAULT_TIMESTAMP_EXTRACTOR_CLASS_CONFIG, EventTimeExtractor.class);

        Serde<AirportRecord> airportSerde = new GenericSerde<>(AirportRecord.class);
        Serde<FlightRecord> flightSerde = new GenericSerde<>(FlightRecord.class);
        Serde<AnomalyRecord> anomalySerde = new GenericSerde<>(AnomalyRecord.class);
        Serde<FlightDataRecord> flightDataSerde = new GenericSerde<>(FlightDataRecord.class);
        Serde<CountFlightsRecord> countFlightsSerde = new GenericSerde<>(CountFlightsRecord.class);
        Serde<EnrichedFlightRecord> enrichedFlightSerde = new GenericSerde<>(EnrichedFlightRecord.class);
        Serde<CountFlightsPeriodRecord> flightsPeriodRecordSerde = new GenericSerde<>(CountFlightsPeriodRecord.class);

        String airportsInputTopic = "airports-input";
        String airportsOutputTopic = "airports-output";
        String flightsInputTopic = "flights-input";
        String flightsOutputTopic = "flights-output";

        StreamsBuilder builder = new StreamsBuilder();

        // = AIRPORTS =
        KTable<String, AirportRecord> airportsTable = getAirportsTable(airportSerde, airportsInputTopic, builder);
//        airportsTable.toStream().foreach((key, value) -> System.out.println("SYSTEMOUT: " + key + ": " + value.toString()));

        // = FLIGHTS =
        KStream<String, FlightRecord> flightRecords = getFlightRecords(flightsInputTopic, builder);
        KStream<String, FlightRecord> flightRecordsIata = flightRecords.selectKey((key, value) -> value.getDestAirport());

        // = ANOMALY =
        KStream<Windowed<String>, CountFlightsRecord> countFlights = countFlightsPerAirport(flightSerde, countFlightsSerde, D, flightRecordsIata, delay);

//        countFlights.toStream().to(airportsOutputTopic);
//        countFlights.toStream().foreach((key, value) -> System.out.println("COUNT " + key + " " + value.toString()));

        KTable<String, AnomalyRecord> anomalyRecords = getAnomalyRecords(anomalySerde, flightsPeriodRecordSerde, enrichedFlightSerde, airportSerde, airportsTable, countFlights);

//        anomalyRecords
//                .toStream()
//                .foreach((key, value) -> System.out.println("ANOMALY " + key + " " + value.toString()));
        anomalyRecords
                .toStream()
                .filter((key, value) -> value.getUpcomingFlights() > N)
                .to(airportsOutputTopic);

//       = ETL =
         KTable<Windowed<String>, FlightDataRecord> flightsETL = getETLData(flightSerde, flightDataSerde, flightRecords, delay);

        flightsETL.toStream()
                .selectKey((windowedKey, value) -> windowedKey.key())
                .to(flightsOutputTopic);

        // = STREAM =
        KafkaStreams streams = new KafkaStreams(builder.build(), properties);
        streams.start();
    }

    private static KTable<Windowed<String>, FlightDataRecord> getETLData(
            Serde<FlightRecord> flightSerde,
            Serde<FlightDataRecord> flightDataSerde,
            KStream<String, FlightRecord> flightRecords,
            String delay) {

        KTable<Windowed<String>, FlightDataRecord> flightDataRecords = flightRecords
                .filter((key, value) -> isCorrectInfoType(value))
                .selectKey((key, value) -> FlightRecord.parseOrderColumns(value.getOrderColumn()))
                .groupByKey(Grouped.with(Serdes.String(), flightSerde))
                .windowedBy(TimeWindows.ofSizeWithNoGrace(Duration.ofDays(1)))
                .aggregate(
                        FlightDataRecord::new,
                        (key, value, aggregate) -> {
                            if (value.getInfoType().equals("D")) {
                                aggregate.setDepartureAmount(aggregate.getDepartureAmount() + 1);
                                Long departureDelaySum = FlightRecord.subtractTwoDepartureDates(value.getScheduledDepartureTime(), value.getDepartureTime());
                                aggregate.setDepartureDelaySum(aggregate.getDepartureDelaySum() + departureDelaySum);

                                aggregate.setArrivalAmount(aggregate.getArrivalAmount());
                                aggregate.setArrivalDelaySum(aggregate.getArrivalDelaySum());
                            } else if (value.getInfoType().equals("A")) {
                                aggregate.setArrivalAmount(aggregate.getArrivalAmount() + 1);
                                Long arrivalDelaySum = FlightRecord.subtractTwoArrivalDates(value.getScheduledArrivalTime(), value.getArrivalTime());
                                aggregate.setArrivalDelaySum(aggregate.getArrivalDelaySum() + arrivalDelaySum);

                                aggregate.setDepartureAmount(aggregate.getDepartureAmount());
                                aggregate.setDepartureDelaySum(aggregate.getDepartureDelaySum());
                            } else {
                                aggregate.setDepartureDelaySum(aggregate.getDepartureDelaySum());
                                aggregate.setDepartureAmount(aggregate.getDepartureAmount());
                                aggregate.setArrivalAmount(aggregate.getArrivalAmount());
                                aggregate.setArrivalDelaySum(aggregate.getArrivalDelaySum());
                            }

                            return aggregate;
                        },
                        Materialized.with(Serdes.String(), flightDataSerde)
                );
        if (delay.equals("C")) {
            return flightDataRecords.suppress(Suppressed.untilWindowCloses(Suppressed.BufferConfig.unbounded()));
        }

        return flightDataRecords;
    }

    private static boolean isCorrectInfoType(FlightRecord value) {
        return value.getInfoType().equals("D") || value.getInfoType().equals("A");
    }

    private static KTable<String, AnomalyRecord> getAnomalyRecords(
            Serde<AnomalyRecord> anomalySerde,
            Serde<CountFlightsPeriodRecord> flightsPeriodRecordSerde,
            Serde<EnrichedFlightRecord> enrichedFlightSerde,
            Serde<AirportRecord> airportSerde,
            KTable<String, AirportRecord> airportsTable,
            KStream<Windowed<String>, CountFlightsRecord> countFlights) {

        final FlightAirportJoiner flightAirportJoiner = new FlightAirportJoiner();

        return countFlights
                .map(ProcessFlightApplication::extractWindowTimestamps)
                .join(airportsTable, flightAirportJoiner, Joined.with(Serdes.String(), flightsPeriodRecordSerde, airportSerde))
                .toTable(Materialized.with(Serdes.String(), enrichedFlightSerde))
                .mapValues((key, value) -> getAnomalyRecord(value), Materialized.with(Serdes.String(), anomalySerde));
    }

    private static AnomalyRecord getAnomalyRecord(EnrichedFlightRecord value) {
        AirportRecord airportRecord = value.getAirportRecord();
        CountFlightsPeriodRecord countFlightsPeriodRecord = value.getCountFlightsRecord();

        return new AnomalyRecord(
                "from: " + countFlightsPeriodRecord.getStartTs() + " to: " + countFlightsPeriodRecord.getEndTs(),
                airportRecord.getName(),
                airportRecord.getIata(),
                airportRecord.getCity(),
                airportRecord.getState(),
                countFlightsPeriodRecord.getCountFlightsRecord().getUpcomingFlights(),
                countFlightsPeriodRecord.getCountFlightsRecord().getTotalFlights()
        );
    }

    private static KeyValue<String, CountFlightsPeriodRecord> extractWindowTimestamps(Windowed<String> key, CountFlightsRecord value) {
        return KeyValue.pair(key.key(), new CountFlightsPeriodRecord(
                value,
                key.window().startTime().toString(),
                key.window().endTime().toString()
        ));
    }

    private static KStream<Windowed<String>, CountFlightsRecord> countFlightsPerAirport(
            Serde<FlightRecord> flightSerde,
            Serde<CountFlightsRecord> countFlightsSerde,
            int D, KStream<String,
            FlightRecord> flightRecordsIata,
            String delay) {

        KTable<Windowed<String>, CountFlightsRecord> countFlightsRecords = flightRecordsIata
                .filter((key, value) -> value.getInfoType().equals("D"))
                .groupByKey(Grouped.with(Serdes.String(), flightSerde))
                .windowedBy(TimeWindows.ofSizeAndGrace(Duration.ofMinutes(10), Duration.ofMinutes(5)))
                .aggregate(
                        CountFlightsRecord::new,
                        (key, value, aggregate) -> {
                            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            try {
                                aggregate.setTotalFlights(aggregate.getTotalFlights() + 1);

                                Date timeNow = dateFormat.parse(value.getOrderColumn());
                                Date timeArrival = dateFormat.parse(value.getScheduledArrivalTime());
                                long duration = timeArrival.getTime() - timeNow.getTime();
                                long diffInMinutes = TimeUnit.MILLISECONDS.toMinutes(duration);

                                if (diffInMinutes > 30 && diffInMinutes <= (30 + D)) {
                                    aggregate.setUpcomingFlights(aggregate.getUpcomingFlights() + 1);
                                } else {
                                    aggregate.setUpcomingFlights(aggregate.getUpcomingFlights());
                                }

                            } catch (ParseException e) {
                                throw new RuntimeException(e);
                            }

                            return aggregate;
                        }, Materialized.with(Serdes.String(), countFlightsSerde)
                );

        if (delay.equals("C")) {
            return countFlightsRecords
                    .suppress(Suppressed.untilWindowCloses(Suppressed.BufferConfig.unbounded()))
                    .toStream();
        }

        return countFlightsRecords.toStream();
    }

    private static KStream<String, FlightRecord> getFlightRecords(String flightsInputTopic, StreamsBuilder builder) {
        return builder
                .stream(flightsInputTopic, Consumed.with(Serdes.String(), Serdes.String()))
                .filter((key, value) -> FlightRecord.isLineCorrect(value))
                .mapValues(FlightRecord::parseFromLine);
    }

    private static KTable<String, AirportRecord> getAirportsTable(Serde<AirportRecord> airportSerde, String airportsInputTopic, StreamsBuilder builder) {
        return builder
                .stream(airportsInputTopic, Consumed.with(Serdes.String(), Serdes.String()))
                .filter((key, line) -> AirportRecord.isLineCorrect(line))
                .mapValues(AirportRecord::parseFromLine)
                .selectKey((key, value) -> value.getIata())
                .toTable(Materialized.with(Serdes.String(), airportSerde));
    }
}