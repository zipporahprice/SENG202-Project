package seng202.team0.models;

public class Location {
    public double latitude;

    public double longitude;

    /**
     * Creates a position by using the longitude and latitude of that position
     * @param latitude
     * @param longitude
     */
    public Location(double latitude, double longitude){
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
