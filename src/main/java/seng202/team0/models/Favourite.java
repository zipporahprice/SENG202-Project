package seng202.team0.models;

public class Favourite {
    private double startLat;
    private double startLong;
    private double endLat;
    private double endLong;
    private String filters;

    private String startAddress;

    private String endAddress;

    public Favourite(String startAddress, String endAddress, double startLat, double startLong, double endLat, double endLong, String filters) {
        this.startAddress = startAddress;
        this.endAddress = endAddress;
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

    public String getStartAddress() {return startAddress;}

    public String getEndAddress() {return endAddress;}
}
