package org.example.joiners;

import org.apache.kafka.streams.kstream.ValueJoiner;
import org.example.models.AnomalyRecord;
import org.example.models.EnrichedFlightRecord;

public class AnomalyJoiner implements ValueJoiner<EnrichedFlightRecord, Long, AnomalyRecord> {
        @Override
        public AnomalyRecord apply(EnrichedFlightRecord enrichedFlightRecord, Long upcomingFlights) {
                return new AnomalyRecord(
                        "PERIOD",
                        enrichedFlightRecord.getAirportRecord().getName(),
                        enrichedFlightRecord.getAirportRecord().getIata(),
                        enrichedFlightRecord.getAirportRecord().getCity(),
                        enrichedFlightRecord.getAirportRecord().getState(),
                        upcomingFlights.intValue(),
                        (int)Math.floor(upcomingFlights * 1.5)
                );
        }
}
