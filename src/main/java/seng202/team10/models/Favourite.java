package seng202.team10.models;

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

public class Favourite {
    private double startLat;
    private double startLong;
    private double endLat;
    private double endLong;
    private String filters;

    private String startAddress;

    private String endAddress;
    private String transportMode;

    /**
     * Constructs a Favourite object with relevant favourite route information.
     *
     * @param startAddress The starting address.
     * @param endAddress   The ending address.
     * @param startLat     Latitude of the starting location.
     * @param startLong    Longitude of the starting location.
     * @param endLat       Latitude of the ending location.
     * @param endLong      Longitude of the ending location.
     * @param filters      Route filters.
     */
    public Favourite(String startAddress, String endAddress, double startLat, double startLong,
                     double endLat, double endLong, String filters, String transportMode) {
        this.startAddress = startAddress;
        this.endAddress = endAddress;
        this.startLat = startLat;
        this.startLong = startLong;
        this.endLat = endLat;
        this.endLong = endLong;
        this.filters = filters;
        this.transportMode = transportMode;
    }

    /**
     * Get the start location latitude associated with the favourite.
     *
     * @return The start location latitude.
     */
    public double getStartLat() {
        return startLat;
    }

    /**
     * Get the start location longitude associated with the favourite.
     *
     * @return The start location longitude.
     */
    public double getStartLong() {
        return startLong;
    }

    /**
     * Get the end location latitude associated with the favourite.
     *
     * @return The end location latitude.
     */
    public double getEndLat() {
        return endLat;
    }

    /**
     * Get the end location longitude associated with the favourite.
     *
     * @return The end location longitude.
     */
    public double getEndLong() {
        return endLong;
    }

    /**
     * Get the filters associated with the favourite.
     * This string is a representation of the WHERE clause of an SQL query into the crashes table.
     *
     * @return The filters as a string representation of a WHERE clause in an SQL query.
     */
    public String getFilters() {
        return filters;
    }

    /**
     * Get the start address associated with the favourite.
     *
     * @return The start address.
     */
    public String getStartAddress() {
        return startAddress;
    }

    /**
     * Get the end address associated with the favourite.
     *
     * @return The end address.
     */
    public String getEndAddress() {
        return endAddress;
    }

    /**
     * Gets the transport mode associated with the favourite.
     *
     * @return transport mode of the Favourite route.
     */
    public String getTransportMode() {
        return transportMode;
    }

}
