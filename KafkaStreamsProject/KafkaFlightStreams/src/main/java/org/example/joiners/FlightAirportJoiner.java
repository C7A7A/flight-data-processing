package org.example.joiners;

import org.apache.kafka.streams.kstream.ValueJoiner;
import org.example.models.*;

public class FlightAirportJoiner implements ValueJoiner<CountFlightsPeriodRecord, AirportRecord, EnrichedFlightRecord> {
    @Override
    public EnrichedFlightRecord apply(CountFlightsPeriodRecord countFlightsPeriodRecord, AirportRecord airportRecord) {
        return new EnrichedFlightRecord(countFlightsPeriodRecord, airportRecord);
    }
}
