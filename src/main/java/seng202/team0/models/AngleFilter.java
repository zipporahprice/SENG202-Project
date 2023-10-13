package seng202.team0.models;
import java.util.ArrayList;
import java.util.List;

public class AngleFilter {

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

    private static double calculateAngle(Location prev, Location curr, Location next) {
        // Convert latitude and longitude to radians for calculation
        double lat1 = Math.toRadians(curr.latitude - prev.latitude);
        double lon1 = Math.toRadians(curr.longitude - prev.longitude);
        double lat2 = Math.toRadians(next.latitude - curr.latitude);
        double lon2 = Math.toRadians(next.longitude - curr.longitude);

        // Compute the bearing (angle) between the two points
        double angle1 = Math.atan2(Math.sin(lon1) * Math.cos(lat1), Math.cos(prev.latitude) * Math.sin(curr.latitude) - Math.sin(prev.latitude) * Math.cos(curr.latitude) * Math.cos(lon1));
        double angle2 = Math.atan2(Math.sin(lon2) * Math.cos(lat2), Math.cos(curr.latitude) * Math.sin(next.latitude) - Math.sin(curr.latitude) * Math.cos(next.latitude) * Math.cos(lon2));

        // Return the difference of the two angles
        return Math.toDegrees(angle2 - angle1);
    }
}
