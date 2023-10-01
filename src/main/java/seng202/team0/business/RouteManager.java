package seng202.team0.business;

import java.util.ArrayList;

/**
 * Singleton class for storing routing options from the FXML controller class.
 */
public class RouteManager {

    private static RouteManager route;
    private String startLocation;
    private String endLocation;
    private String stopLocation;

    private RouteManager() {

        startLocation = null;

        endLocation = null;

        stopLocation = null;

    }

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

}
