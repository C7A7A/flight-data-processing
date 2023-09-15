package org.example.models;

public class AnomalyRecord {
    private String period;
    private String airportName;
    private String airportIATA;
    private String airportCity;
    private String airportState;
    private Integer upcomingFlights;
    private Integer totalFlights;

    public AnomalyRecord() {
    }

    public AnomalyRecord(String period, String airportName, String airportIATA, String airportCity, String airportState, Integer upcomingFlights, Integer totalFlights) {
        this.period = period;
        this.airportName = airportName;
        this.airportIATA = airportIATA;
        this.airportCity = airportCity;
        this.airportState = airportState;
        this.upcomingFlights = upcomingFlights;
        this.totalFlights = totalFlights;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public String getAirportName() {
        return airportName;
    }

    public void setAirportName(String airportName) {
        this.airportName = airportName;
    }

    public String getAirportIATA() {
        return airportIATA;
    }

    public void setAirportIATA(String airportIATA) {
        this.airportIATA = airportIATA;
    }

    public String getAirportCity() {
        return airportCity;
    }

    public void setAirportCity(String airportCity) {
        this.airportCity = airportCity;
    }

    public String getAirportState() {
        return airportState;
    }

    public void setAirportState(String airportState) {
        this.airportState = airportState;
    }

    public Integer getUpcomingFlights() {
        return upcomingFlights;
    }

    public void setUpcomingFlights(Integer upcomingFlights) {
        this.upcomingFlights = upcomingFlights;
    }

    public Integer getTotalFlights() {
        return totalFlights;
    }

    public void setTotalFlights(Integer totalFlights) {
        this.totalFlights = totalFlights;
    }

    @Override
    public String toString() {
        return "AnomalyRecord{" +
                "period='" + period + '\'' +
                ", airportName='" + airportName + '\'' +
                ", airportIATA='" + airportIATA + '\'' +
                ", airportCity='" + airportCity + '\'' +
                ", airportState='" + airportState + '\'' +
                ", upcomingFlights='" + upcomingFlights + '\'' +
                ", totalFlights='" + totalFlights + '\'' +
                '}';
    }
}
