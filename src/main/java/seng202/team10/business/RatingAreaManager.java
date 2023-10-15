package seng202.team10.business;

import kotlin.Pair;
import seng202.team10.models.Location;
import seng202.team10.repository.SqliteQueryBuilder;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * The RatingAreaManager class is responsible for managing the bounding box
 * used to rate areas. It ensures that there is only one instance of this class
 * and provides methods to set and retrieve the minimum and maximum points of
 * the bounding box for rating an area.
 *
 * @author Neil Alombro
 */

public class RatingAreaManager {

    private static RatingAreaManager ratingAreaManager;
    private Location boundingBoxMin;
    private Location boundingBoxMax;
    private Location boundingCircleCentre;
    private double boundingCircleRadius;

    private RatingAreaManager() {

    }

    /**
     * Gets instance of or creates a new RatingAreaManager.
     *
     * @return the ratingAreaManager
     */
    public static RatingAreaManager getInstance() {
        if (ratingAreaManager == null) {
            ratingAreaManager = new RatingAreaManager();
        }
        return ratingAreaManager;
    }

    /**
     * Retrieves the location for the minimum point of the bounding box for rating an area.
     *
     * @return A location of (minLatitude, minLongitude).
     */
    public Location getBoundingBoxMin() {
        return this.boundingBoxMin;
    }

    /**
     * Sets the bounding box minimum location for rating an area.
     *
     * @param minLatitude maximum latitude of bounding box
     * @param minLongitude maximum longitude of bounding box
     */
    public void setBoundingBoxMin(Double minLatitude, Double minLongitude) {
        if (minLatitude == null || minLongitude == null) {
            boundingBoxMin = null;
        } else {
            boundingBoxMin = new Location(minLatitude, minLongitude);
        }
    }

    /**
     * Retrieves the location for the maximum point of the bounding box for rating an area.
     *
     * @return A location of (maxLatitude, maxLongitude).
     */
    public Location getBoundingBoxMax() {
        return this.boundingBoxMax;
    }

    /**
     * Sets the bounding box maximum location for rating an area.
     *
     * @param maxLatitude maximum latitude of bounding box
     * @param maxLongitude maximum longitude of bounding box
     */
    public void setBoundingBoxMax(Double maxLatitude, Double maxLongitude) {
        if (maxLatitude == null || maxLongitude == null) {
            boundingBoxMax = null;
        } else {
            boundingBoxMax = new Location(maxLatitude, maxLongitude);
        }
    }

    /**
     * Retrieves the location for the centre of the bounding circle for rating an area.
     *
     * @return A location of (maxLatitude, maxLongitude).
     */
    public Location getBoundingCircleCentre() {
        return this.boundingCircleCentre;
    }

    /**
     * Sets the bounding circle centre location for rating an area.
     *
     * @param latitude latitude of bounding circle
     * @param longitude longitude of bounding circle
     */
    public void setBoundingCircleCentre(Double latitude, Double longitude) {
        if (latitude == null || longitude == null) {
            boundingCircleCentre = null;
        } else {
            boundingCircleCentre = new Location(latitude, longitude);
        }
    }

    /**
     * Retrieves the location for the radius of the bounding circle for rating an area.
     *
     * @return A radius of the bounding circle.
     */
    public double getBoundingCircleRadius() {
        return this.boundingCircleRadius;
    }

    /**
     * Sets the bounding circle centre location for rating an area.
     *
     * @param radius radius of the bounding circle.
     */
    public void setBoundingCircleRadius(double radius) {
        boundingCircleRadius = radius;
    }

    /**
     * Clears the stored bounding boxes.
     */
    public void clearBoundingBoxes() {
        boundingBoxMax = null;
        boundingBoxMin = null;
    }

    public String rateAreaHelper() {
        // Gets the bounding boxes and bounding circle information
        RatingAreaManager ratingAreaManager = RatingAreaManager.getInstance();
        Location boxMin = ratingAreaManager.getBoundingBoxMin();
        Location boxMax = ratingAreaManager.getBoundingBoxMax();
        Location circleCentre = ratingAreaManager.getBoundingCircleCentre();
        double circleRadius = ratingAreaManager.getBoundingCircleRadius();

        // Gets the boundingWhere string according to
        // what bounding information is not null.
        String boundingWhere = null;
        if (boxMin != null || boxMax != null) {
            boundingWhere = "minX >= " + boxMin.getLongitude()
                    + " AND maxX <= " + boxMax.getLongitude()
                    + " AND minY >= " + boxMin.getLatitude()
                    + " AND maxY <= " + boxMax.getLatitude() + ")";

        } else if (circleCentre != null) {
            // Bounding box to lessen the load
            boundingWhere = "minX >= " + (circleCentre.getLongitude() - circleRadius)
                    + " AND maxX <= " + (circleCentre.getLongitude() + circleRadius)
                    + " AND minY >= " + (circleCentre.getLatitude() - circleRadius)
                    + " AND maxY <= " + (circleCentre.getLatitude() + circleRadius) + ")";

            // Pythagoras theorem calculation compared to circle radius
            boundingWhere += " AND (SQRT(POW(" + circleCentre.getLongitude()
                    + " - longitude, 2) + POW(" + circleCentre.getLatitude()
                    + " - latitude, 2)) <= " + circleRadius + ")";
        }
        return boundingWhere;
    }

    public static Pair<Double, Integer> queryHelper(String boundingWhere) {
        String select = "AVG(severity), COUNT()";
        String from = "crashes";

        FilterManager filterManager = FilterManager.getInstance();
        String filterWhere = filterManager.toString();
        String[] filterList = filterWhere.split(" AND ");

        // Takes away the 4 ANDS that make up the viewport
        // bounds we do not want in our query.
        String filterWhereWithoutViewport = String.join(" AND ",
                Arrays.copyOf(filterList, filterList.length - 4));

        String rtreeFind = "object_id IN (SELECT id FROM rtree_index WHERE " + boundingWhere;

        List severityList = SqliteQueryBuilder
                .create()
                .select(select)
                .from(from)
                .where(filterWhereWithoutViewport + " AND " + rtreeFind)
                .buildGetter();

        HashMap<String, Object> resultHashMap = (HashMap) severityList.get(0);

        // Calculates the score based on the query result
        double score = 0.0;
        int total = 0;
        if (resultHashMap.get("AVG(severity)") != null) {
            double averageSeverity = (double) resultHashMap.get("AVG(severity)");
            total = (int) resultHashMap.get("COUNT()");

            if (total > 0) {
                // Actual average severity will range from 1 to 8
                // Score rating massaged to be out of 10 and in a range from 0 to 10.
                double scaleFactor = 10.0 / Math.log(11.0);
                score = Math.log(averageSeverity + 1) * scaleFactor;
                score = Math.min(10, score);
            }
        }
        return new Pair<>(score, total);
    }

}
