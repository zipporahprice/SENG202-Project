package seng202.team0.models;

public class CrashInfo {
    /**
     * The latitude of the crash location.
     */
    public double lat;
    /**
     * The longitude of the crash location.
     */
    public double lng;
    public int severity;
    /**
     * Constructs a CrashInfo object with latitude and longitude.
     *
     * @param lat The latitude of the crash location.
     * @param lng The longitude of the crash location.
     */

    public CrashInfo(double lat, double lng, int severity) {
        this.lat = lat;
        this.lng = lng;
        this.severity = severity;
    }

}