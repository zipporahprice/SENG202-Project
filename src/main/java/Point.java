//import org.javatuples.Triplet;

class Point {

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

    public void Point(int id, int speedLimit,int year, String location1, String location2, String region, String weather, float longitude, float lat,
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





    public static void main(String[] args) {

    }
}