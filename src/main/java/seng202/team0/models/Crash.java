package seng202.team0.models;

import javafx.util.Pair;

public class Crash {

    private int objectID;
    private int speedLimit;
    private int crashYear;


    private String crashLocation1;
    private String crashLocation2;
    private String region;
    private String weather;

    private float longitude;
    private float latitude;

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

    public Crash(int id, int speedLimit, int year, String location1, String location2, String region, String weather, float longitude, float lat,
                 boolean bicycleInvolved, boolean busInvolved, boolean carInvolved, boolean holiday, boolean mopedInvolved, boolean motorcycleInvolved,
                 boolean parkedVehicleInvolved, boolean pedestrianInvolved, boolean schoolBusInvolved, boolean trainInvolved, boolean truckInvolved){
        this.objectID = id;
        this.speedLimit = speedLimit;
        this.crashYear = year;
        this.crashLocation1 = location1;
        this.crashLocation2 = location2;
        this.region = region;
        this.weather = weather;
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

    public int getObjectId(){
        return objectID;
    }

    public int getSpeedLimit(){
        return speedLimit;
    }

    public int getCrashYear(){
        return crashYear;
    }

    public String getCrashLocation1(){
        return crashLocation1;
    }

    public String getCrashLocation2(){
        return crashLocation2;
    }

    public String getRegion(){
        return region;
    }

    public String getWeather(){
        return weather;
    }

    public Float getLongitude(){
        return longitude;
    }

    public Float getLatitude(){
        return latitude;
    }

    public Pair<Float,Float> getLongitudeAndLatitude(){
        Pair geoLocation = new Pair(longitude, latitude);
        return geoLocation;
    }

    public boolean isBicycleInvolved(){
        return bicycleInvolved;
    }

    public boolean isTruckInvolved() {
        return truckInvolved;
    }

    public boolean isSchoolBusInvolved() {
        return schoolBusInvolved;
    }

    public boolean isBusInvolved() {
        return busInvolved;
    }

    public boolean isTrainInvolved() {
        return trainInvolved;
    }

    public boolean isCarInvolved() {
        return carInvolved;
    }

    public boolean isMotorcycleInvolved() {
        return motorcycleInvolved;
    }

    public boolean isHoliday() {
        return holiday;
    }

    public boolean isPedestrianInvolved() {
        return pedestrianInvolved;
    }

    public boolean isParkedVehicleInvolved() {
        return parkedVehicleInvolved;
    }

    public boolean isMopedInvolved() {
        return mopedInvolved;
    }

    public static void main(String[] args) {

    }
}