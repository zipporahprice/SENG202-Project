package seng202.team10.models;

import java.util.HashMap;
import java.util.List;

/**
 * Represents the result of an analysis for overlapping points and their severity
 * on a given path defined by a list of locations.
 *
 */
public class Review {
    public double dangerRating;

    public double maxSegmentSeverity;
    public String maxWeather;
    public int startYear;
    public int endYear;

    public int totalNumPoints;
    public String finalRoad;

    public List<HashMap<String, Object>> crashes;

    /**
     * Constructs a new Result object with the provided metrics.
     *
     * @param dangerRating Overall danger rating of the path.
     * @param maxSegmentSeverity The highest severity encountered along the path.
     * @param maxWeather The most severe weather condition encountered.
     * @param startYear The beginning year of the data range considered.
     * @param endYear The end year of the data range considered.
     * @param totalNumPoints Total number of unique overlapping points encountered.
     * @param finalRoad Name of the road where the most severe overlapping point was found.
     * @param crashes Map of Lists of all of the crashes for the segments.
     */
    public Review(double dangerRating, double maxSegmentSeverity, String maxWeather, int startYear,
                  int endYear, int totalNumPoints, String finalRoad,
                  List<HashMap<String, Object>> crashes) {
        this.dangerRating = dangerRating;
        this.maxSegmentSeverity = maxSegmentSeverity;
        this.maxWeather = maxWeather;
        this.startYear = startYear;
        this.endYear = endYear;
        this.totalNumPoints = totalNumPoints;
        this.finalRoad = finalRoad;
        this.crashes = crashes;
    }

    public double getDangerRating() {
        return dangerRating;
    }

    public double getMaxSegmentSeverity() {
        return maxSegmentSeverity;
    }

    public String getMaxWeather() {
        return maxWeather;
    }

    public int getStartYear() {
        return startYear;
    }

    public int getEndYear() {
        return endYear;
    }

    public int getTotalNumPoints() {
        return totalNumPoints;
    }

    public String getFinalRoad() {
        return finalRoad;
    }

    // Setters
    public void setDangerRating(double dangerRating) {
        this.dangerRating = dangerRating;
    }


    public void setMaxSegmentSeverity(double maxSegmentSeverity) {
        this.maxSegmentSeverity = maxSegmentSeverity;
    }

    public void setMaxWeather(String maxWeather) {
        this.maxWeather = maxWeather;
    }

    public void setStartYear(int startYear) {
        this.startYear = startYear;
    }

    public void setEndYear(int endYear) {
        this.endYear = endYear;
    }

    public void setTotalNumPoints(int totalNumPoints) {
        this.totalNumPoints = totalNumPoints;
    }

    public void setFinalRoad(String finalRoad) {
        this.finalRoad = finalRoad;
    }

    @Override
    public String toString() {
        String reviewString;
        if (getTotalNumPoints() == -1) {
            reviewString = String.format("This route has zero crashes and hence is as safe"
                    + " as can be!");
        } else {
            String baseFormat = "This route has a %.2f/10 danger rating, "
                    + "there have been %d crashes since %d up till %d.";
            String conditionFormat = "The worst crashes occur during %s conditions, "
                    + "the most dangerous segment is on %s with a danger rating of %.2f.";
            String format = baseFormat + " " + conditionFormat;

            reviewString = String.format(
                    format,
                    getDangerRating(), getTotalNumPoints(),
                    getStartYear(), getEndYear(),
                    getMaxWeather(), getFinalRoad(), getMaxSegmentSeverity()
            );
        }
        return reviewString;
    }

}