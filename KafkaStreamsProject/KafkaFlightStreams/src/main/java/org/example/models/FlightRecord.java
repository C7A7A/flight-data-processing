package org.example.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DateTimeException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FlightRecord implements Serializable {
    private static final long serialVersionId = 1L;
//    private static final Logger logger = Logger.getLogger("FlightLogger");
    private static final String LOG_ENTRY_PATTERN = "^([^,]+),([^,]+),([^,]+),([^,]+),([^,]+),([^,]+),([^,]+),([^,]+),([^,]+),([^,]+),([^,]+),([^,]+),([^,]+),([^,]+),([^,]+),([^,]+),([^,]+),([^,]+),([^,]+),([^,]+),([^,]+),([^,]+),([^,]+),([^,]+),([^,]+)$";
    private static final Pattern PATTERN = Pattern.compile(LOG_ENTRY_PATTERN);

    private String airline;
    private String flightNumber;
    private String tailNumber;
    private String startAirport;
    private String destAirport;
    private String scheduledDepartureTime;
    private String scheduledDepartureDayOfWeek;
    private String scheduledFlightTime;
    private String scheduledArrivalTime;
    private String departureTime;
    private String taxiOut;
    private String distance;
    private String taxiIn;
    private String arrivalTime;
    private String diverted;
    private String cancelled;
    private String cancellationReason;
    private String airSystemDelay;
    private String securityDelay;
    private String airlineDelay;
    private String lateAircraftDelay;
    private String weatherDelay;
    private String cancellationTime;
    private String orderColumn;
    private String infoType;

    public FlightRecord() {

    }
    public FlightRecord(String airline, String flightNumber, String tailNumber, String startAirport, String destAirport, String scheduledDepartureTime, String scheduledDepartureDayOfWeek, String scheduledFlightTime, String scheduledArrivalTime, String departureTime, String taxiOut, String distance, String taxiIn, String arrivalTime, String diverted, String cancelled, String cancellationReason, String airSystemDelay, String securityDelay, String airlineDelay, String lateAircraftDelay, String weatherDelay, String cancellationTime, String orderColumn, String infoType) {
        this.airline = airline;
        this.flightNumber = flightNumber;
        this.tailNumber = tailNumber;
        this.startAirport = startAirport;
        this.destAirport = destAirport;
        this.scheduledDepartureTime = scheduledDepartureTime;
        this.scheduledDepartureDayOfWeek = scheduledDepartureDayOfWeek;
        this.scheduledFlightTime = scheduledFlightTime;
        this.scheduledArrivalTime = scheduledArrivalTime;
        this.departureTime = departureTime;
        this.taxiOut = taxiOut;
        this.distance = distance;
        this.taxiIn = taxiIn;
        this.arrivalTime = arrivalTime;
        this.diverted = diverted;
        this.cancelled = cancelled;
        this.cancellationReason = cancellationReason;
        this.airSystemDelay = airSystemDelay;
        this.securityDelay = securityDelay;
        this.airlineDelay = airlineDelay;
        this.lateAircraftDelay = lateAircraftDelay;
        this.weatherDelay = weatherDelay;
        this.cancellationTime = cancellationTime;
        this.orderColumn = orderColumn;
        this.infoType = infoType;
    }

    public static FlightRecord parseFromLine(String line) {
        Matcher matcher = PATTERN.matcher(line);
        if (!matcher.find()) {
//            logger.log(Level.ALL, "Cannot parse line" + line);
            throw new RuntimeException("Error parsing line: " + line);
        }

        return new FlightRecord(
                matcher.group(1),
                matcher.group(2),
                matcher.group(3),
                matcher.group(4),
                matcher.group(5),
                matcher.group(6),
                matcher.group(7),
                matcher.group(8),
                matcher.group(9),
                matcher.group(10),
                matcher.group(11),
                matcher.group(12),
                matcher.group(13),
                matcher.group(14),
                matcher.group(15),
                matcher.group(16),
                matcher.group(17),
                matcher.group(18),
                matcher.group(19),
                matcher.group(20),
                matcher.group(21),
                matcher.group(22),
                matcher.group(23),
                matcher.group(24),
                matcher.group(25)
        );
    }

    public static boolean isLineCorrect(String line) {
//        System.out.println(line);

        Matcher matcher = PATTERN.matcher(line);
        if (matcher.find()) {
//            System.out.println("CORRECT LINE");
            return true;
        }

//        System.out.println("INCORRECT LINE");
        return false;
    }

    public static String parseOrderColumns(String orderColumn) {
//        System.out.println(orderColumn);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDate localDate = LocalDate.parse(orderColumn, formatter);
        return localDate.toString();
    }

    public long extractTimestampInMillis() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        Date date;

        try {
            date = sdf.parse(this.orderColumn);
            return date.getTime();
        } catch (ParseException e) {
            System.out.println("ORDER DATE PARSE EXCEPTION");
            return -1;
        }
    }

    public static String getFullInfoType(String infoType) {
        if (infoType.equals("D")) return "departure";
        if (infoType.equals("A")) return "arrival";
        if (infoType.equals("C")) return "cancellation";

        return "unknown";
    }

    public static Long subtractTwoDepartureDates(String date1, String date2) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        LocalDateTime dateTime1 = LocalDateTime.parse(date1, formatter);
//        System.out.println(dateTime1);

        LocalDateTime dateTime2;
        DateTimeFormatter formatter2;
        try {
            formatter2 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            dateTime2 = LocalDateTime.parse(date2, formatter2);
        } catch (DateTimeException e) {
            dateTime2 = dateTime1;
        }
//        System.out.println(dateTime2);

        if (dateTime1.isBefore(dateTime2)) {
            Duration duration = Duration.between(dateTime1, dateTime2);
//            System.out.println(duration.getSeconds());
            return duration.getSeconds();
        }

        return 0L;
    }

    public static Long subtractTwoArrivalDates(String date1, String date2) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime dateTime1 = LocalDateTime.parse(date1, formatter);
//        System.out.println(dateTime1);

        LocalDateTime dateTime2;
        try {
            dateTime2 = LocalDateTime.parse(date2, formatter);
        } catch (DateTimeException e) {
            dateTime2 = dateTime1;
        }
//        System.out.println(dateTime2);

        if (dateTime1.isBefore(dateTime2)) {
            Duration duration = Duration.between(dateTime1, dateTime2);
//            System.out.println(duration.getSeconds());
            return duration.getSeconds();
        }

        return 0L;
    }

    public String getAirline() {
        return airline;
    }

    public void setAirline(String airline) {
        this.airline = airline;
    }

    public String getFlightNumber() {
        return flightNumber;
    }

    public void setFlightNumber(String flightNumber) {
        this.flightNumber = flightNumber;
    }

    public String getTailNumber() {
        return tailNumber;
    }

    public void setTailNumber(String tailNumber) {
        this.tailNumber = tailNumber;
    }

    public String getStartAirport() {
        return startAirport;
    }

    public void setStartAirport(String startAirport) {
        this.startAirport = startAirport;
    }

    public String getDestAirport() {
        return destAirport;
    }

    public void setDestAirport(String destAirport) {
        this.destAirport = destAirport;
    }

    public String getScheduledDepartureTime() {
        return scheduledDepartureTime;
    }

    public void setScheduledDepartureTime(String scheduledDepartureTime) {
        this.scheduledDepartureTime = scheduledDepartureTime;
    }

    public String getScheduledDepartureDayOfWeek() {
        return scheduledDepartureDayOfWeek;
    }

    public void setScheduledDepartureDayOfWeek(String scheduledDepartureDayOfWeek) {
        this.scheduledDepartureDayOfWeek = scheduledDepartureDayOfWeek;
    }

    public String getScheduledFlightTime() {
        return scheduledFlightTime;
    }

    public void setScheduledFlightTime(String scheduledFlightTime) {
        this.scheduledFlightTime = scheduledFlightTime;
    }

    public String getScheduledArrivalTime() {
        return scheduledArrivalTime;
    }

    public void setScheduledArrivalTime(String scheduledArrivalTime) {
        this.scheduledArrivalTime = scheduledArrivalTime;
    }

    public String getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(String departureTime) {
        this.departureTime = departureTime;
    }

    public String getTaxiOut() {
        return taxiOut;
    }

    public void setTaxiOut(String taxiOut) {
        this.taxiOut = taxiOut;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getTaxiIn() {
        return taxiIn;
    }

    public void setTaxiIn(String taxiIn) {
        this.taxiIn = taxiIn;
    }

    public String getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(String arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public String getDiverted() {
        return diverted;
    }

    public void setDiverted(String diverted) {
        this.diverted = diverted;
    }

    public String getCancelled() {
        return cancelled;
    }

    public void setCancelled(String cancelled) {
        this.cancelled = cancelled;
    }

    public String getCancellationReason() {
        return cancellationReason;
    }

    public void setCancellationReason(String cancellationReason) {
        this.cancellationReason = cancellationReason;
    }

    public String getAirSystemDelay() {
        return airSystemDelay;
    }

    public void setAirSystemDelay(String airSystemDelay) {
        this.airSystemDelay = airSystemDelay;
    }

    public String getSecurityDelay() {
        return securityDelay;
    }

    public void setSecurityDelay(String securityDelay) {
        this.securityDelay = securityDelay;
    }

    public String getAirlineDelay() {
        return airlineDelay;
    }

    public void setAirlineDelay(String airlineDelay) {
        this.airlineDelay = airlineDelay;
    }

    public String getLateAircraftDelay() {
        return lateAircraftDelay;
    }

    public void setLateAircraftDelay(String lateAircraftDelay) {
        this.lateAircraftDelay = lateAircraftDelay;
    }

    public String getWeatherDelay() {
        return weatherDelay;
    }

    public void setWeatherDelay(String weatherDelay) {
        this.weatherDelay = weatherDelay;
    }

    public String getCancellationTime() {
        return cancellationTime;
    }

    public void setCancellationTime(String cancellationTime) {
        this.cancellationTime = cancellationTime;
    }

    public String getOrderColumn() {
        return orderColumn;
    }

    public void setOrderColumn(String orderColumn) {
        this.orderColumn = orderColumn;
    }

    public String getInfoType() {
        return infoType;
    }

    public void setInfoType(String infoType) {
        this.infoType = infoType;
    }

    @Override
    public String toString() {
        return "AccessFlightRecord{" +
                "airline='" + airline + '\'' +
                ", flightNumber='" + flightNumber + '\'' +
                ", tailNumber='" + tailNumber + '\'' +
                ", startAirport='" + startAirport + '\'' +
                ", destAirport='" + destAirport + '\'' +
                ", scheduledDepartureTime=" + scheduledDepartureTime +
                ", scheduledDepartureDayOfWeek=" + scheduledDepartureDayOfWeek +
                ", scheduledFlightTime=" + scheduledFlightTime +
                ", scheduledArrivalTime=" + scheduledArrivalTime +
                ", departureTime=" + departureTime +
                ", taxiOut=" + taxiOut +
                ", distance=" + distance +
                ", taxiIn=" + taxiIn +
                ", arrivalTime=" + arrivalTime +
                ", diverted=" + diverted +
                ", cancelled=" + cancelled +
                ", cancellationReason='" + cancellationReason + '\'' +
                ", airSystemDelay=" + airSystemDelay +
                ", securityDelay=" + securityDelay +
                ", airlineDelay=" + airlineDelay +
                ", lateAircraftDelay=" + lateAircraftDelay +
                ", weatherDelay=" + weatherDelay +
                ", cancelationTime=" + cancellationTime +
                ", orderColumn='" + orderColumn + '\'' +
                ", infoType='" + infoType + '\'' +
                '}';
    }
}
