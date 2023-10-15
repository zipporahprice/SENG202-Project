package seng202.team10.business;

import static seng202.team10.gui.RoutingMenuController.updateCrashes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import javafx.util.Pair;
import seng202.team10.models.Location;
import seng202.team10.models.Review;
import seng202.team10.repository.SqliteQueryBuilder;



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
    private boolean removeRouteDisabled;

    /**
     * Initializer of the RouteManager class that sets default null values for
     * Strings startLocation, endLocation, and stopLocation.
     */
    private RouteManager() {
        startLocation = null;
        endLocation = null;
        stopLocation = null;
        transportMode = "car";
        removeRouteDisabled = true;
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

    /**
     * Getter method for startLocation.
     *
     * @return String object of start location.
     */
    public String getStartLocation() {
        return startLocation;
    }

    /**
     * Setter method for startLocation.
     *
     * @param location String object of start location
     */
    public void setStartLocation(String location) {
        startLocation = location;
    }

    /**
     * Getter method for endLocation.
     *
     * @return String object of end location.
     */
    public String getEndLocation() {
        return endLocation;
    }

    /**
     * Setter method for endLocation.
     *
     * @param location String object of end location
     */
    public void setEndLocation(String location) {
        endLocation = location;
    }

    /**
     * Getter method for stopLocation.
     *
     * @return String object of stop location.
     */
    public String getStopLocation() {
        return stopLocation;
    }

    /**
     * Setter method for stopLocation.
     *
     * @param location String object of stop location
     */
    public void setStopLocation(String location) {
        stopLocation = location;
    }

    /**
     * Getter method for removeRouteDisabled.
     *
     * @return Boolean object of removeRouteButton.
     */
    public boolean getRemoveRouteDisabled() {
        return removeRouteDisabled;
    }

    /**
     * Setter method for stopLocation.
     *
     * @param removeRouteDisabled boolean of whether the route remove button is disabled.
     */
    public void setRemoveRouteDisabled(boolean removeRouteDisabled) {
        this.removeRouteDisabled = removeRouteDisabled;
    }

    /**
     * Gets favourites' names from the SQLite database.
     *
     * @return a list of favourite names
     */
    public static List<?> getFavouriteNames() {
        String columns = "route_name";
        String table = "favourites";

        // Gets all the favourites from the database.
        List<?> favourites = SqliteQueryBuilder.create()
                                            .select(columns)
                                            .from(table)
                                            .buildGetter();

        return favourites;
    }

    public void setTransportMode(String mode) {
        transportMode = mode;
    }

    public String getTransportMode() {
        return transportMode;
    }

    /**
     * Calculates and returns a type Result that contains information on points along a route.
     * The function checks segments of the path between the given coordinates, calculating severity,
     * weather conditions, and other relevant metrics to provide a result.
     *
     * @param coordinates List of locations representing the path of the route.
     * @param roads List of road names corresponding to the segments between provided coordinates.
     * @param distances List of distances that a route must continue before the next instruction.
     *
     * @return Result object
     */
    public static Review getOverlappingPoints(List<Location> coordinates,
                                              List<String> roads, List<Double> distances) {
        double totalValue = 0;
        double maxSegmentSeverity = Double.MIN_VALUE;
        String finalRoad = roads.get(0);
        Map<String, Integer> weatherSeverityTotal = new HashMap<>();
        Map<String, Integer> weatherTotals = new HashMap<>();
        double totalDistances = 0;
        double totalDistance = 0;
        Set<Integer> objectIdSet = new HashSet<>();
        List<HashMap<String, Object>> crashes = new ArrayList<>();

        int j = 0;
        for (int i = 0; i < coordinates.size() - 1; i += 1) {
            Location segmentStart = coordinates.get(i);
            Location segmentEnd = coordinates.get(i + 1);
            double distance = haversineDistance(segmentStart, segmentEnd);
            totalDistance += distance;
            if (totalDistance > totalDistances && j < distances.size()) {
                totalDistances += distances.get(j);
                j++;
            }

            Pair<Double, List<HashMap<String, Object>>> segmentInfo =
                    calculateSegmentInfo(segmentStart, segmentEnd, objectIdSet);

            double segmentSeverity = segmentInfo.getKey();
            crashes.addAll(segmentInfo.getValue());

            // Updating the weather maps
            updateWeatherMaps(segmentInfo.getValue(), weatherSeverityTotal, weatherTotals);

            if (segmentSeverity > maxSegmentSeverity) {
                maxSegmentSeverity = segmentSeverity;
                if (!Objects.equals(roads.get(j), "")) {
                    finalRoad = roads.get(j);
                }
            }
            totalValue += segmentSeverity;
        }

        String maxWeather = getMaxSeverityWeather(weatherSeverityTotal, weatherTotals);

        Pair<Integer, Double> danger = calculateDanger(objectIdSet.size(), totalValue);
        FilterManager filterManager = FilterManager.getInstance();
        int startYear = filterManager.getEarliestYear();
        int endYear = filterManager.getLatestYear();

        return new Review(danger.getValue(), maxSegmentSeverity, maxWeather, startYear,
                endYear, danger.getKey(), finalRoad, crashes);
    }


    /**
     * Calculates segment information within a given geographic
     * segment defined by its start and end locations.
     * This method performs a bounding box search for crashes
     * within the segment and calculates the total severity
     * of those crashes while ensuring that duplicate crashes
     * with the same object ID are not included.
     *
     * @param segmentStart The start location of the geographic segment.
     * @param segmentEnd   The end location of the geographic segment.
     * @param objectIdSet  A set containing unique object IDs to prevent duplicate crash entries.
     * @return A Pair containing the total severity within the segment
     *          (as a Double) and a list of crash data
     *          within the segment (as a List of HashMaps).
     */
    private static Pair<Double, List<HashMap<String, Object>>> calculateSegmentInfo(
            Location segmentStart, Location segmentEnd, Set<Integer> objectIdSet) {
        List<?> crashList = boundingBoxSegmentSearch(segmentStart, segmentEnd);
        double segmentSeverity = 0;
        List<HashMap<String, Object>> crashes = new ArrayList<>();
        for (Object severityMap : crashList) {
            HashMap<String, Object> map = (HashMap<String, Object>) severityMap;
            int objectId = (int) map.get("object_id");
            if (!objectIdSet.contains(objectId)) {
                objectIdSet.add(objectId);
                crashes.add(map);
                int currentSeverity = (int) map.get("severity");
                segmentSeverity += currentSeverity;
            }
        }
        return new Pair<>(segmentSeverity, crashes);
    }


    /**
     * Finds the weather type with the highest severity ratio.
     *
     * @param weatherSeverityTotal Map of total severity for each weather type.
     * @param weatherTotals Map of total occurrences of each weather type.
     * @return Weather type with the highest severity ratio.
     *          Returns an empty string if input is invalid or all ratios are zero.
     */
    public static String getMaxSeverityWeather(Map<String, Integer> weatherSeverityTotal,
                                               Map<String, Integer> weatherTotals) {
        double maxWeatherSeverity = Double.MIN_VALUE;
        String maxWeather = "";
        for (String weather : weatherSeverityTotal.keySet()) {
            double currentWeatherSeverity = (double) weatherSeverityTotal.get(weather)
                    / weatherTotals.get(weather);
            if (currentWeatherSeverity > maxWeatherSeverity) {
                maxWeatherSeverity = currentWeatherSeverity;
                maxWeather = weather;
            }
        }
        return maxWeather;
    }

    private static void updateWeatherMaps(List<HashMap<String, Object>> crashes,
                                          Map<String, Integer> weatherSeverityTotal,
                                          Map<String, Integer> weatherTotals) {
        for (HashMap<String, Object> crash : crashes) {
            String weather = (String) crash.get("weather");
            int currentSeverity = (int) crash.get("severity");
            weatherSeverityTotal.put(weather,
                    weatherSeverityTotal.getOrDefault(weather, 0) + currentSeverity);
            weatherTotals.put(weather,
                    weatherTotals.getOrDefault(weather, 0) + 1);
        }
    }


    /**
     * Calculates the Haversine distance between two geographic
     * coordinates using the Haversine formula.
     * The Haversine formula is used to compute
     * the distance between two points on the Earth's surface
     * given their latitude and longitude coordinates.
     *
     * @param loc1 The first location with latitude and longitude coordinates.
     * @param loc2 The second location with latitude and longitude coordinates.
     * @return The Haversine distance between the two locations in meters.
     */

    public static double haversineDistance(Location loc1, Location loc2) {
        double r = 6371000; // Earth radius in meters
        double deltaLat = Math.toRadians(loc2.getLatitude() - loc1.getLatitude());
        double deltaLon = Math.toRadians(loc2.getLongitude() - loc1.getLongitude());

        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2)
                + Math.cos(Math.toRadians(loc1.getLatitude()))
                * Math.cos(Math.toRadians(loc2.getLatitude()))
                * Math.sin(deltaLon  / 2) * Math.sin(deltaLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return r * c;
    }

    /**
     * Takes in two locations of a start and end location and queries the database
     * for an average severity of crashes within a 1km radius along the line between
     * the two locations.
     *
     * @param startLocation location the route segment starts at
     * @param endLocation location the route segment ends at
     * @return double of average severity
     */
    public static List boundingBoxSegmentSearch(Location startLocation, Location endLocation) {
        // 100 metres away
        double oneKilometreInDegrees = 0.008;
        double dist = oneKilometreInDegrees * 0.1;

        double minLat = Math.min(startLocation.getLatitude(), endLocation.getLatitude()) - dist;
        double minLong = Math.min(startLocation.getLongitude(), endLocation.getLongitude()) - dist;
        double maxLat = Math.max(startLocation.getLatitude(), endLocation.getLatitude()) + dist;
        double maxLong = Math.max(startLocation.getLongitude(), endLocation.getLongitude()) + dist;

        FilterManager filterManager = FilterManager.getInstance();
        String filterWhere = filterManager.toString();
        String[] filterList = filterWhere.split(" AND ");

        // 4 ANDS to take away to get rid of the viewport
        String filterWhereWithoutViewport = String.join(" AND ",
                Arrays.copyOf(filterList, filterList.length - 4));

        String select = "object_id, longitude, latitude, severity, crash_year, weather";
        String from = "crashes";
        String where = filterWhereWithoutViewport + " AND "
                + "object_id IN (SELECT id FROM rtree_index WHERE minX >= " + minLong
                + " AND maxX <= " + maxLong
                + " AND minY >= " + minLat
                + " AND maxY <= " + maxLat + ")";


        List<?> severityList = SqliteQueryBuilder
                .create()
                .select(select)
                .from(from)
                .where(where)
                .buildGetter();

        return severityList;
    }

    /**
     * Calculates danger rating and final size based on set size and total value.
     *
     * @param setSize The set size.
     * @param totalValue The total value associated with the set.
     * @return A Pair with the final size and danger rating out of 10.
     */
    public static Pair<Integer, Double> calculateDanger(int setSize, double totalValue) {
        int finalSize;
        double dangerRatingOutOf10;
        if (setSize == 0) {
            finalSize = -1;
            dangerRatingOutOf10 = 0;
        } else {
            double averageSeverity = totalValue / setSize;
            double scaleFactor = 10.0 / Math.log(11.0);
            dangerRatingOutOf10 = Math.log(averageSeverity + 1) * scaleFactor;
            dangerRatingOutOf10 = Math.min(10, dangerRatingOutOf10);
            finalSize = setSize;
        }
        return new Pair<>(finalSize, dangerRatingOutOf10);
    }

}
