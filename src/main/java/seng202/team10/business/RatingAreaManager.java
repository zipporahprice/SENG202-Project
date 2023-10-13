package seng202.team10.business;

import seng202.team10.models.Location;

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
}
