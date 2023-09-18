package seng202.team0.models;

public class Favourite {
    private double startLat;
    private double startLong;
    private double endLat;
    private double endLong;
    private String filters;

    public Favourite(double startLat, double startLong, double endLat, double endLong, String filters) {
        this.startLat = startLat;
        this.startLong = startLong;
        this.endLat = endLat;
        this.endLong = endLong;
        this.filters = filters;

    }

    public double getStartLat() {
        return startLat;
    }

    public double getStartLong() {
        return startLong;
    }

    public double getEndLat() {
        return endLat;
    }

    public double getEndLong() {
        return endLong;
    }

    public String getFilters() {
        return filters;
    }
}
