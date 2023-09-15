package org.example.models;

public class CountFlightsRecord {
    private Integer upcomingFlights;
    private Integer totalFlights;

    public CountFlightsRecord() {
        this.upcomingFlights = 0;
        this.totalFlights = 0;
    }
    public CountFlightsRecord(Integer upcomingFlights, Integer totalFlights) {
        this.upcomingFlights = upcomingFlights;
        this.totalFlights = totalFlights;
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
        return "CountFlightsRecord{" +
                "upcomingFlights=" + upcomingFlights +
                ", totalFlights=" + totalFlights +
                '}';
    }
}
