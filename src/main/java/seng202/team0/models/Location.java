package seng202.team0.models;

/**
 * Represents a geographic location with latitude and longitude coordinates.
 *
 * @author Angelica Silva
 * @author Christopher Wareing
 * @author Neil Alombro
 * @author Todd Vermeir
 * @author William Thompson
 * @author Zipporah Price
 *
 */
public class Location {
    private double latitude;

    private double longitude;

    /**
     * Creates a position by using the longitude and latitude of that position
     * @param latitude
     * @param longitude
     */
    public Location(double latitude, double longitude){
        this.latitude = latitude;
        this.longitude = longitude;
    }

    /**
     * Returns the latitude value of the location
     * @return latitude
     */
    public double getLatitude() {
        return latitude;
    }

    /**
     * Returns the longitude value of the location
     * @return longitude
     */
    public double getLongitude() {
        return longitude;
    }
}
