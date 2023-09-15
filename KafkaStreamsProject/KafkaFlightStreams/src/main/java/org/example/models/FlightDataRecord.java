package org.example.models;

public class FlightDataRecord {
    private Integer departureAmount;
    private Long departureDelaySum;
    private Integer arrivalAmount;
    private Long arrivalDelaySum;

    public FlightDataRecord() {
        this.departureAmount = 0;
        this.departureDelaySum = 0L;
        this.arrivalAmount = 0;
        this.arrivalDelaySum = 0L;
    }

    public FlightDataRecord(Integer departureAmount, Long departureDelaySum, Integer arrivalAmount, Long arrivalDelaySum) {
        this.departureAmount = departureAmount;
        this.departureDelaySum = departureDelaySum;
        this.arrivalAmount = arrivalAmount;
        this.arrivalDelaySum = arrivalDelaySum;
    }

    public Integer getDepartureAmount() {
        return departureAmount;
    }

    public void setDepartureAmount(Integer departureAmount) {
        this.departureAmount = departureAmount;
    }

    public Long getDepartureDelaySum() {
        return departureDelaySum;
    }

    public void setDepartureDelaySum(Long departureDelaySum) {
        this.departureDelaySum = departureDelaySum;
    }

    public Integer getArrivalAmount() {
        return arrivalAmount;
    }

    public void setArrivalAmount(Integer arrivalAmount) {
        this.arrivalAmount = arrivalAmount;
    }

    public Long getArrivalDelaySum() {
        return arrivalDelaySum;
    }

    public void setArrivalDelaySum(Long arrivalDelaySum) {
        this.arrivalDelaySum = arrivalDelaySum;
    }

    @Override
    public String toString() {
        return "FlightData{" +
                "departureAmount=" + departureAmount +
                ", departureDelaySum=" + departureDelaySum +
                ", arrivalAmount=" + arrivalAmount +
                ", arrivalDelaySum=" + arrivalDelaySum +
                '}';
    }
}
