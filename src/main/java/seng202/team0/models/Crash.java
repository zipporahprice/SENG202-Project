package seng202.team0.models;

import javafx.util.Pair;

/**
 * Represents a crash event with attributes such as year, location, severity.
 *
 * @author Angelica Silva
 * @author Christopher Wareing
 * @author Neil Alombro
 * @author Todd Vermeir
 * @author William Thompson
 * @author Zipporah Price
 */
public class Crash {

    private int objectId;
    private int speedLimit;
    private int crashYear;


    private String crashLocation1;
    private String crashLocation2;
    private Region region;
    private Weather weather;

    private double longitude;
    private double latitude;

    private boolean bicycleInvolved;
    private boolean busInvolved;
    private boolean carInvolved;
    private boolean holiday;
    private boolean mopedInvolved;
    private boolean motorcycleInvolved;
    private boolean parkedVehicleInvolved;
    private boolean pedestrianInvolved;
    private boolean schoolBusInvolved;
    private boolean trainInvolved;
    private boolean truckInvolved;
    private CrashSeverity severity;

    private int severeInt;

    /**
     * Constructs a Crash object with relevant crash information.
     *
     * @param id                        Crash ID.
     * @param speedLimit                Speed limit at the crash location.
     * @param year                      Year of the crash.
     * @param location1                 First crash location.
     * @param location2                 Second crash location.
     * @param severity                  Severity of the crash.
     * @param region                    Region where the crash occurred.
     * @param weather                   Weather conditions at the time of the crash.
     * @param longitude                 Longitude of the crash location.
     * @param lat                       Latitude of the crash location.
     * @param bicycleInvolved           Indicates bicycle involvement.
     * @param busInvolved               Indicates bus involvement.
     * @param carInvolved               Indicates car involvement.
     * @param holiday                   Indicates if it occurred on a holiday.
     * @param mopedInvolved             Indicates moped involvement.
     * @param motorcycleInvolved        Indicates motorcycle involvement.
     * @param parkedVehicleInvolved     Indicates parked vehicle involvement.
     * @param pedestrianInvolved        Indicates pedestrian involvement.
     * @param schoolBusInvolved         Indicates school bus involvement.
     * @param trainInvolved             Indicates train involvement.
     * @param truckInvolved             Indicates truck involvement.
     */
    public Crash(int id, int speedLimit, int year, String location1, String location2, String severity,
                 String region, String weather, double longitude, double lat, boolean bicycleInvolved,
                 boolean busInvolved, boolean carInvolved, boolean holiday, boolean mopedInvolved,
                 boolean motorcycleInvolved, boolean parkedVehicleInvolved, boolean pedestrianInvolved,
                 boolean schoolBusInvolved, boolean trainInvolved, boolean truckInvolved) {
        this.objectId = id;
        this.speedLimit = speedLimit;
        this.crashYear = year;
        this.crashLocation1 = location1;
        this.crashLocation2 = location2;
        this.severity = CrashSeverity.stringToCrashSeverity(severity);
        this.region = Region.stringToRegion(region);
        this.weather = Weather.stringToWeather(weather);
        this.longitude = longitude;
        this.latitude = lat;
        this.bicycleInvolved = bicycleInvolved;
        this.busInvolved = busInvolved;
        this.carInvolved = carInvolved;
        this.holiday = holiday;
        this.mopedInvolved = mopedInvolved;
        this.motorcycleInvolved = motorcycleInvolved;
        this.parkedVehicleInvolved = parkedVehicleInvolved;
        this.pedestrianInvolved = pedestrianInvolved;
        this.schoolBusInvolved = schoolBusInvolved;
        this.trainInvolved = trainInvolved;
        this.truckInvolved = truckInvolved;

    }

    /**
     * Constructor of Crash object but only with location and severity parameters.
     *
     * @param latitude                  Latitude of the crash location.
     * @param longitude                 Longitude of the crash location.
     * @param severity                  Severity of the crash.
     */
    public Crash(double latitude, double longitude, int severity) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.severeInt = severity;

    }

    /**
     * Get the object ID associated with this crash.
     *
     * @return The object ID of the crash.
     */
    public int getObjectId() {
        return objectId;
    }

    /**
     * Get the speed limit at the crash location.
     *
     * @return The speed limit at the crash location.
     */
    public int getSpeedLimit() {
        return speedLimit;
    }

    /**
     * Get the year in which the crash occurred.
     *
     * @return The year of the crash.
     */
    public int getCrashYear() {
        return crashYear;
    }

    /**
     * Get the first crash location description.
     *
     * @return The description of the first crash location.
     */
    public String getCrashLocation1() {
        return crashLocation1;
    }

    /**
     * Get the second crash location description.
     *
     * @return The description of the second crash location.
     */
    public String getCrashLocation2() {
        return crashLocation2;
    }

    /**
     * Get the severity level of the crash.
     *
     * @return The severity level of the crash.
     */
    public CrashSeverity getSeverity() {
        return severity;
    }

    /**
     * Get the region where the crash occurred.
     *
     * @return The region of the crash.
     */
    public Region getRegion() {
        return region;
    }

    /**
     * Get the weather conditions at the time of the crash.
     *
     * @return The weather conditions at the time of the crash.
     */
    public Weather getWeather() {
        return weather;
    }

    /**
     * Get the longitude coordinate of the crash location.
     *
     * @return The longitude coordinate of the crash location.
     */
    public Double getLongitude() {
        return longitude;
    }

    /**
     * Get the latitude coordinate of the crash location.
     *
     * @return The latitude coordinate of the crash location.
     */
    public Double getLatitude() {
        return latitude;
    }

    /**
     * Get the longitude and latitude coordinates of the crash location as a pair.
     *
     * @return A pair containing the longitude and latitude coordinates of the crash location.
     */
    public Pair<Double, Double> getLongitudeAndLatitude() {
        Pair geoLocation = new Pair(longitude, latitude);
        return geoLocation;
    }

    /**
     * Check if bicycles were involved in the crash.
     *
     * @return True if bicycles were involved, false otherwise.
     */
    public boolean isBicycleInvolved() {
        return bicycleInvolved;
    }

    /**
     * Check if trucks were involved in the crash.
     *
     * @return True if trucks were involved, false otherwise.
     */
    public boolean isTruckInvolved() {
        return truckInvolved;
    }

    /**
     * Check if a school bus was involved in the crash.
     *
     * @return True if a school bus was involved, false otherwise.
     */
    public boolean isSchoolBusInvolved() {
        return schoolBusInvolved;
    }

    /**
     * Check if buses were involved in the crash.
     *
     * @return True if buses were involved, false otherwise.
     */
    public boolean isBusInvolved() {
        return busInvolved;
    }

    /**
     * Check if a train was involved in the crash.
     *
     * @return True if a train was involved, false otherwise.
     */
    public boolean isTrainInvolved() {
        return trainInvolved;
    }

    /**
     * Check if cars were involved in the crash.
     *
     * @return True if cars were involved, false otherwise.
     */
    public boolean isCarInvolved() {
        return carInvolved;
    }

    /**
     * Check if motorcycles were involved in the crash.
     *
     * @return True if motorcycles were involved, false otherwise.
     */
    public boolean isMotorcycleInvolved() {
        return motorcycleInvolved;
    }

    /**
     * Check if the crash occurred on a holiday.
     *
     * @return True if the crash occurred on a holiday, false otherwise.
     */
    public boolean isHoliday() {
        return holiday;
    }

    /**
     * Check if pedestrians were involved in the crash.
     *
     * @return True if pedestrians were involved, false otherwise.
     */
    public boolean isPedestrianInvolved() {
        return pedestrianInvolved;
    }

    /**
     * Check if parked vehicles were involved in the crash.
     *
     * @return True if parked vehicles were involved, false otherwise.
     */
    public boolean isParkedVehicleInvolved() {
        return parkedVehicleInvolved;
    }

    /**
     * Check if mopeds were involved in the crash.
     *
     * @return True if mopeds were involved, false otherwise.
     */
    public boolean isMopedInvolved() {
        return mopedInvolved;
    }

    /**
     * Gets the integer value of severity.
     *
     * @return severeInt
     */
    public int getSevereInt() {
        return severeInt;
    }
}