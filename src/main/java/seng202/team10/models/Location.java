package seng202.team10.models;

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
     * Creates a position by using the longitude and latitude of that position.
     *
     * @param latitude latitude of the given location
     * @param longitude longitude of the given location
     */
    public Location(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    /**
     * Returns the latitude value of the location.
     *
     * @return latitude of location
     */
    public double getLatitude() {
        return latitude;
    }

    /**
     * Returns the longitude value of the location.
     *
     * @return longitude of location
     */
    public double getLongitude() {
        return longitude;
    }

    @Override
    public String toString() {
        return "new Location(" + latitude + ", " + longitude + ")";
    }
}
