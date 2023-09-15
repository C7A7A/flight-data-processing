package org.example.models;

public class EnrichedFlightRecord {
    private final CountFlightsPeriodRecord countFlightsRecord;
    private final AirportRecord airportRecord;

    public EnrichedFlightRecord(CountFlightsPeriodRecord countFlightsRecord, AirportRecord airportRecord) {
        this.countFlightsRecord = countFlightsRecord;
        this.airportRecord = airportRecord;
    }

    @Override
    public String toString() {
        return "EnrichedFlightRecord{" +
                "flightRecord=" + countFlightsRecord.toString() +
                ", airportRecord=" + airportRecord.toString() +
                '}';
    }

    public CountFlightsPeriodRecord getCountFlightsRecord() {
        return countFlightsRecord;
    }

    public AirportRecord getAirportRecord() {
        return airportRecord;
    }
}
