package seng202.team0.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Simple class representing a route as any number of positions
 */
public class Route {
    private List<Location> route = new ArrayList<>();

    /**
     * Create a new route with any number of positions
     * @param points points along the route in order first to last
     */
    public Route(Location ...points) {
        Collections.addAll(route, points);
    }

    /**
     * Returns the route as a JSON array
     * @return route object as JSON array
     */
    public String toJSONArray() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("[");
        route.forEach(pos -> stringBuilder.append(
                String.format("{\"lat\": %f, \"lng\": %f}, ", pos.latitude, pos.longitude)));
        if (stringBuilder.length() > 2)
            stringBuilder.setLength(stringBuilder.length() -2);
        stringBuilder.append("]");
        return stringBuilder.toString();
    }
}
