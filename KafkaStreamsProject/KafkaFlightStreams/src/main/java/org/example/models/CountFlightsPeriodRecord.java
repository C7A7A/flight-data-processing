package org.example.models;

public class CountFlightsPeriodRecord {
    private CountFlightsRecord countFlightsRecord;
    private String startTs;
    private String endTs;

    public CountFlightsPeriodRecord() {
    }

    public CountFlightsPeriodRecord(CountFlightsRecord countFlightsRecord, String startTs, String endTs) {
        this.countFlightsRecord = countFlightsRecord;
        this.startTs = startTs;
        this.endTs = endTs;
    }

    public CountFlightsRecord getCountFlightsRecord() {
        return countFlightsRecord;
    }

    public void setCountFlightsRecord(CountFlightsRecord countFlightsRecord) {
        this.countFlightsRecord = countFlightsRecord;
    }

    public String getStartTs() {
        return startTs;
    }

    public void setStartTs(String startTs) {
        this.startTs = startTs;
    }

    public String getEndTs() {
        return endTs;
    }

    public void setEndTs(String endTs) {
        this.endTs = endTs;
    }

    @Override
    public String toString() {
        return "CountFlightsPeriodRecord{" +
                "countFlightsRecord=" + countFlightsRecord +
                ", startTs='" + startTs + '\'' +
                ", endTs='" + endTs + '\'' +
                '}';
    }
}
