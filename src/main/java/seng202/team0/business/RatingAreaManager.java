package seng202.team0.business;

import seng202.team0.models.Location;

/**
 * class for managing bounding boxes for area rating.
 *
 */
public class RatingAreaManager {

    private static RatingAreaManager ratingAreaManager;
    private Location boundingBoxMin;
    private Location boundingBoxMax;

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
}
