package seng202.team0.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Simple class representing a route as any number of positions.
 *
 * @author Angelica Silva
 * @author Christopher Wareing
 * @author Neil Alombro
 * @author Todd Vermeir
 * @author William Thompson
 * @author Zipporah Price
 *
 */
public class Route {
    public List<Location> route = new ArrayList<>();

    /**
     * Create a new route with any number of positions.
     *
     * @param points points along the route in order first to last
     */
    public Route(List<Location> points) {
        this.route.addAll(points);
    }

    /**
     * Returns the route as a JSON array.
     *
     * @return route object as JSON array
     */
    public String toJsonArray() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("[");
        route.forEach(pos -> stringBuilder.append(
                String.format("{\"lat\": %f, \"lng\": %f}, ",
                        pos.getLatitude(), pos.getLongitude())));
        if (stringBuilder.length() > 2) {
            stringBuilder.setLength(stringBuilder.length() - 2);
        }
        stringBuilder.append("]");
        return stringBuilder.toString();
    }

    /**
     * Returns the list of routes as a JSON array of arrays.
     *
     * @param routes list of route objects
     * @return list of route objects as a JSON array of arrays
     */
    public static String routesToJsonArray(List<Route> routes) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("[");
        routes.forEach(route -> stringBuilder.append(route.toJsonArray()).append(", "));
        if (stringBuilder.length() > 2) {
            stringBuilder.setLength(stringBuilder.length() - 2);
        }
        stringBuilder.append("]");
        return stringBuilder.toString();
    }
}
