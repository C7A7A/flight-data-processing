package org.example.models;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AirportRecord implements Serializable {
    private static final long serialVersionId = 1L;
//    private static final Logger logger = Logger.getLogger("AirportLogger");
    private static final String LOG_ENTRY_PATTERN = "^([^,]+),([^,]+),([^,]+),([^,]+),([^,]+),([^,]+),([^,]+),([^,]+),([^,]+),([^,]+),([^,]+),([^,]+),([^,]+),([^,]+)$";
    private static final Pattern PATTERN = Pattern.compile(LOG_ENTRY_PATTERN);
    private String airportId;
    private String name;
    private String city;
    private String country;
    private String iata;
    private String icao;
    private String latitude;
    private String longitude;
    private String altitude;
    private String timezone;
    private String dst;
    private String timezoneName;
    private String type;
    private String state;

    public AirportRecord() {

    }

    public AirportRecord(String airportId, String name, String city, String country, String iata, String icao, String latitude, String longitude, String altitude, String timezone, String dst, String timezoneName, String type, String state) {
        this.airportId = airportId;
        this.name = name;
        this.city = city;
        this.country = country;
        this.iata = iata;
        this.icao = icao;
        this.latitude = latitude;
        this.longitude = longitude;
        this.altitude = altitude;
        this.timezone = timezone;
        this.dst = dst;
        this.timezoneName = timezoneName;
        this.type = type;
        this.state = state;
    }

    public static AirportRecord parseFromLine(String line) {
        Matcher matcher = PATTERN.matcher(line);
        if (!matcher.find()) {
//            logger.log(Level.ALL, "Cannot parse line" + line);
            throw new RuntimeException("Error parsing line: " + line);
        }

        AirportRecord airportRecord = new AirportRecord(
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
                matcher.group(14)
        );

//        System.out.println(airportRecord.toString());

        return airportRecord;
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

    public String getAirportId() {
        return airportId;
    }

    public void setAirportId(String airportId) {
        this.airportId = airportId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getIata() {
        return iata;
    }

    public void setIata(String iata) {
        this.iata = iata;
    }

    public String getIcao() {
        return icao;
    }

    public void setIcao(String icao) {
        this.icao = icao;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getAltitude() {
        return altitude;
    }

    public void setAltitude(String altitude) {
        this.altitude = altitude;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public String getDst() {
        return dst;
    }

    public void setDst(String dst) {
        this.dst = dst;
    }

    public String getTimezoneName() {
        return timezoneName;
    }

    public void setTimezoneName(String timezoneName) {
        this.timezoneName = timezoneName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    @Override
    public String toString() {
        return "AirportData{" +
                "airportId='" + airportId + '\'' +
                ", name='" + name + '\'' +
                ", city='" + city + '\'' +
                ", country='" + country + '\'' +
                ", iata='" + iata + '\'' +
                ", icao='" + icao + '\'' +
                ", latitude='" + latitude + '\'' +
                ", longitude='" + longitude + '\'' +
                ", altitude='" + altitude + '\'' +
                ", timezone='" + timezone + '\'' +
                ", dst='" + dst + '\'' +
                ", timezoneName='" + timezoneName + '\'' +
                ", type='" + type + '\'' +
                ", state='" + state + '\'' +
                '}';
    }
}
