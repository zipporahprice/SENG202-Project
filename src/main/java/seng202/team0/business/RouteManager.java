package seng202.team0.business;

import java.util.List;
import seng202.team0.repository.SqliteQueryBuilder;

/**
 * Singleton class for storing routing options from the FXML controller class.
 *
 * @author Zipporah Price
 */
public class RouteManager {

    private static RouteManager route;
    private String startLocation;
    private String endLocation;
    private String stopLocation;
    private String transportMode;

    /**
     * Initializer of the RouteManager class that sets default null values for
     * Strings startLocation, endLocation, and stopLocation.
     */
    private RouteManager() {
        startLocation = null;
        endLocation = null;
        stopLocation = null;
        transportMode = "car";
    }

    /**
     * Retrieves the singleton RouteManager instance.
     *
     * @return The singleton instance of RouteManager.
     */
    public static RouteManager getInstance() {
        if (route == null) {
            route = new RouteManager();
        }
        return route;
    }

    public String getStartLocation() {
        return startLocation;
    }

    public void setStartLocation(String location) {
        startLocation = location;
    }

    public String getEndLocation() {
        return endLocation;
    }

    public void setEndLocation(String location) {
        endLocation = location;
    }

    public String getStopLocation() {
        return stopLocation;
    }

    public void setStopLocation(String location) {
        stopLocation = location;
    }

    /**
     * Gets favourites from the SQLite database.
     *
     * @return a list of favourites
     */
    public List<?> getFavourites() {
        String columns = "*";
        String table = "favourites";

        List<?> favourites = SqliteQueryBuilder.create()
                                            .select(columns)
                                            .from(table)
                                            .buildGetter();

        return favourites;
    }

    /**
     * Clears the route.
     */
    public void clearRoute() {
        startLocation = null;
        endLocation = null;
        stopLocation = null;
    }

    public void setTransportMode(String mode) {
        transportMode = mode;
    }

    public String getTransportMode() {
        return transportMode;
    }

}
