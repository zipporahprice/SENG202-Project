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
     * Uses a builder pattern to construct a Crash object with relevant information.
     */
    public Crash(Builder builder) {
        this.objectId = builder.id;
        this.speedLimit = builder.speedLimit;
        this.crashYear = builder.year;
        this.crashLocation1 = builder.location1;
        this.crashLocation2 = builder.location2;
        this.severity = builder.severity;
        this.region = builder.region;
        this.weather = builder.weather;
        this.longitude = builder.longitude;
        this.latitude = builder.lat;
        this.bicycleInvolved = builder.bicycleInvolved;
        this.busInvolved = builder.busInvolved;
        this.carInvolved = builder.carInvolved;
        this.holiday = builder.holiday;
        this.mopedInvolved = builder.mopedInvolved;
        this.motorcycleInvolved = builder.motorcycleInvolved;
        this.parkedVehicleInvolved = builder.parkedVehicleInvolved;
        this.pedestrianInvolved = builder.pedestrianInvolved;
        this.schoolBusInvolved = builder.schoolBusInvolved;
        this.trainInvolved = builder.trainInvolved;
        this.truckInvolved = builder.truckInvolved;
        this.severeInt = builder.severeInt;
    }

    /**
     * Static builder class for constructing instances of the Crash class.
     * Allows for the setting of various Crash attributes before creating it.
     */
    public static class Builder {
        private int id;
        private int speedLimit;
        private int year;
        private String location1;
        private String location2;
        private CrashSeverity severity;
        private Region region;
        private Weather weather;
        private double longitude;
        private double lat;
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
        private int severeInt;

        /**
         * Constructs a Builder for a Crash with given latitude, longitude, and severity values.
         *
         * @param latitude Latitude of a crash location.
         * @param longitude Longitude of a crash location.
         * @param severeInt Integer representation of a crash's severity.
         */
        public Builder(double latitude, double longitude, int severeInt) {
            this.longitude = longitude;
            this.lat = latitude;
            this.severeInt = severeInt;
        }

        /**
         * Constructs a Builder for a Crash with a given id.
         *
         * @param id A crash object's identifier.
         */
        public Builder(int id) {
            this.id = id;
        }

        /**
         * Sets a crash's identifier.
         *
         * @param id A crash object's identifier.
         * @return Builder instance for method chaining.
         */
        public Builder id(int id) {
            this.id = id;
            return this;
        }

        /**
         * Sets a crash's speed limit.
         *
         * @param speedLimit A crash object's speed limit.
         * @return Builder instance for method chaining.
         */
        public Builder speedLimit(int speedLimit) {
            this.speedLimit = speedLimit;
            return this;
        }

        /**
         * Sets a crash's year.
         *
         * @param year A crash object's year.
         * @return Builder instance for method chaining.
         */
        public Builder year(int year) {
            this.year = year;
            return this;
        }

        /**
         * Sets a crash's first location.
         *
         * @param location1 A crash object's first location.
         * @return Builder instance for method chaining.
         */
        public Builder location1(String location1) {
            this.location1 = location1;
            return this;
        }

        /**
         * Sets a crash's second location.
         *
         * @param location2 A crash object's second location.
         * @return Builder instance for method chaining.
         */
        public Builder location2(String location2) {
            this.location2 = location2;
            return this;
        }

        /**
         * Sets a crash's severity.
         *
         * @param severity A crash object's severity.
         * @return Builder instance for method chaining.
         */
        public Builder severity(String severity) {
            this.severity = CrashSeverity.stringToCrashSeverity(severity);
            return this;
        }

        /**
         * Sets a crash's region.
         *
         * @param region A crash object's region.
         * @return Builder instance for method chaining.
         */
        public Builder region(String region) {
            this.region = Region.stringToRegion(region);
            return this;
        }

        /**
         * Sets a crash's weather.
         *
         * @param weather A crash object's weather.
         * @return Builder instance for method chaining.
         */
        public Builder weather(String weather) {
            this.weather = Weather.stringToWeather(weather);
            return this;
        }

        /**
         * Sets a crash's longitude value.
         *
         * @param longitude A crash object's longitude.
         * @return Builder instance for method chaining.
         */
        public Builder longitude(double longitude) {
            this.longitude = longitude;
            return this;
        }

        /**
         * Sets a crash's latitude value.
         *
         * @param lat A crash object's latitude.
         * @return Builder instance for method chaining.
         */
        public Builder latitude(double lat) {
            this.lat = lat;
            return this;
        }

        /**
         * Sets if a bicycle was involved in a crash.
         *
         * @param bicycleInvolved Boolean indicating if a bicycle was involved.
         * @return Builder instance for method chaining.
         */
        public Builder bicycleInvolved(boolean bicycleInvolved) {
            this.bicycleInvolved = bicycleInvolved;
            return this;
        }

        /**
         * Sets if a bus was involved in a crash.
         *
         * @param busInvolved Boolean indicating if a bus was involved.
         * @return Builder instance for method chaining.
         */
        public Builder busInvolved(boolean busInvolved) {
            this.busInvolved = busInvolved;
            return this;
        }

        /**
         * Sets if a car was involved in a crash.
         *
         * @param carInvolved Boolean indicating if a car was involved.
         * @return Builder instance for method chaining.
         */
        public Builder carInvolved(boolean carInvolved) {
            this.carInvolved = carInvolved;
            return this;
        }

        /**
         * Sets if a crash occurred on a holiday.
         *
         * @param holiday Boolean indicating if crash was on a holiday.
         * @return Builder instance for method chaining.
         */
        public Builder holiday(boolean holiday) {
            this.holiday = holiday;
            return this;
        }

        /**
         * Sets if a moped was involved in a crash.
         *
         * @param mopedInvolved Boolean indicating if a moped was involved.
         * @return Builder instance for method chaining.
         */
        public Builder mopedInvolved(boolean mopedInvolved) {
            this.mopedInvolved = mopedInvolved;
            return this;
        }

        /**
         * Sets if a motorcycle was involved in a crash.
         *
         * @param motorcycleInvolved Boolean indicating if a motorcycle was involved.
         * @return Builder instance for method chaining.
         */
        public Builder motorcycleInvolved(boolean motorcycleInvolved) {
            this.motorcycleInvolved = motorcycleInvolved;
            return this;
        }

        /**
         * Sets if a parked vehicle was involved in a crash.
         *
         * @param parkedVehicleInvolved Boolean indicating if a parked vehicle was involved.
         * @return Builder instance for method chaining.
         */
        public Builder parkedVehicleInvolved(boolean parkedVehicleInvolved) {
            this.parkedVehicleInvolved = parkedVehicleInvolved;
            return this;
        }

        /**
         * Sets if a pedestrian was involved in a crash.
         *
         * @param pedestrianInvolved Boolean indicating if a pedestrian was involved.
         * @return Builder instance for method chaining.
         */
        public Builder pedestrianInvolved(boolean pedestrianInvolved) {
            this.pedestrianInvolved = pedestrianInvolved;
            return this;
        }

        /**
         * Sets if a school bus was involved in a crash.
         *
         * @param schoolBusInvolved Boolean indicating if a school bus was involved.
         * @return Builder instance for method chaining.
         */
        public Builder schoolBusInvolved(boolean schoolBusInvolved) {
            this.schoolBusInvolved = schoolBusInvolved;
            return this;
        }

        /**
         * Sets if a train was involved in a crash.
         *
         * @param trainInvolved Boolean indicating if a train was involved.
         * @return Builder instance for method chaining.
         */
        public Builder trainInvolved(boolean trainInvolved) {
            this.trainInvolved = trainInvolved;
            return this;
        }

        /**
         * Sets if a truck was involved in a crash.
         *
         * @param truckInvolved Boolean indicating if a truck was involved.
         * @return Builder instance for method chaining.
         */
        public Builder truckInvolved(boolean truckInvolved) {
            this.truckInvolved = truckInvolved;
            return this;
        }

        /**
         * Sets a crash's severity number.
         *
         * @param severeInt Integer representation of a crash's severity.
         * @return Builder instance for method chaining.
         */
        public Builder severeInt(int severeInt) {
            this.severeInt = severeInt;
            return this;
        }

        /**
         * Builds a new Crash object using the attributes set.
         *
         * @return Newly constructed Crash object with attributes specified in the builder.
         */
        public Crash build() {
            return new Crash(this);
        }
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
