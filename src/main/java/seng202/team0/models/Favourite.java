package seng202.team0.models;

public class Favourite {
    private int id;
    private float startLat;
    private float startLong;
    private float endLat;
    private float endLong;
    private String filters;

    public Favourite(int id, float startLat, float startLong, float endLat, float endLong, String filters) {
        this.id = id;
        this.startLat = startLat;
        this.startLong = startLong;
        this.endLat = endLat;
        this.endLong = endLong;
        this.filters = filters;

    }
    public int getId() {
        return id;
    }

    public float getStartLat() {
        return startLat;
    }

    public float getStartLong() {
        return startLong;
    }

    public float getEndLat() {
        return endLat;
    }

    public float getEndLong() {
        return endLong;
    }

    public String getFilters() {
        return filters;
    }
}
