package seng202.team0.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for filtering a list of locations based on angle thresholds.
 * Provides methods to select points from a list of locations such that the
 * angle between consecutive points is above a given threshold.
 *
 * @author team 10
 */
public class AngleFilter {

    /**
     * Filters the provided list of locations, ensuring the angle between consecutive
     * points is above the given threshold.
     * <p>
     * If the list contains less than three locations, the original list is returned
     * as not enough points are present to perform the filter.
     * </p>
     *
     * @param coordinates     The list of locations to filter.
     * @param angleThreshold  The angle threshold, in degrees. Locations will be included
     *                        if the angle they make with adjacent locations exceeds this threshold.
     * @return A list of filtered locations based on angle constraints.
     */
    public static List<Location> filterLocationsByAngle(List<Location> coordinates, double angleThreshold) {
        if (coordinates.size() < 3) {
            return coordinates; // Not enough points to filter
        }

        List<Location> filteredCoordinates = new ArrayList<>();
        filteredCoordinates.add(coordinates.get(0));

        Location lastSelectedLocation = coordinates.get(0);
        for (int i = 1; i < coordinates.size() - 1; i++) {
            Location potentialNextLocation = coordinates.get(i);

            int j = i + 1;
            while (j < coordinates.size()) {
                Location next = coordinates.get(j);

                double angle = calculateAngle(lastSelectedLocation, potentialNextLocation, next);

                if (Math.abs(angle) > angleThreshold) {
                    filteredCoordinates.add(potentialNextLocation);
                    lastSelectedLocation = potentialNextLocation;
                    i = j;
                    break;
                }

                potentialNextLocation = next;
                j++;
            }
        }

        filteredCoordinates.add(coordinates.get(coordinates.size() - 1)); // Always include the last point
        return filteredCoordinates;
    }

    /**
     * Calculates the angle difference between two consecutive points with respect to a reference point.
     * The angle is calculated based on the bearing between the points. The bearing is the
     * angle formed between the north direction and the line connecting the two points.
     * @param prev The reference location point.
     * @param curr The first location point.
     * @param next The second location point.
     * @return The difference in degrees between the bearings of the two segments formed by the provided points.
     */
    private static double calculateAngle(Location prev, Location curr, Location next) {
        // Convert latitude and longitude to radians for calculation
        double lat1 = Math.toRadians(curr.getLatitude() - prev.getLatitude());
        double lon1 = Math.toRadians(curr.getLongitude() - prev.getLongitude());
        double lat2 = Math.toRadians(next.getLatitude() - curr.getLatitude());
        double lon2 = Math.toRadians(next.getLongitude() - curr.getLongitude());

        // Compute the bearing (angle) between the two points
        double angle1 = Math.atan2(Math.sin(lon1) * Math.cos(lat1), Math.cos(prev.getLatitude()) * Math.sin(curr.getLatitude()) - Math.sin(prev.getLatitude()) * Math.cos(curr.getLatitude()) * Math.cos(lon1));
        double angle2 = Math.atan2(Math.sin(lon2) * Math.cos(lat2), Math.cos(curr.getLatitude()) * Math.sin(next.getLatitude()) - Math.sin(curr.getLatitude()) * Math.cos(next.getLatitude()) * Math.cos(lon2));

        // Return the difference of the two angles
        return Math.toDegrees(angle2 - angle1);
    }
}
