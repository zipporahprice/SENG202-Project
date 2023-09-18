package seng202.team0.models;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import com.google.gson.Gson;
import seng202.team0.business.CrashManager;
import seng202.team0.business.FilterManager;
import seng202.team0.repository.CrashDAO;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;


/**
 * Simple example class showing the ability to 'bridge' from javascript to java
 * The functions within can be called from our javascript in the map view when we set an object of this class
 * as a member of the javascript
 * Note: This is a very basic example you can use any java code, though you may need to be careful when working
 * with objects
 * @author Morgan English
 */
public class JavaScriptBridge {
    CrashManager crashData = new CrashManager();
    /**
     * Function called from js when map clicked. In a real application you will want to do something other than printing
     * the information to the console
     * @param latlng co-ordinates clicked at in JSON object format {"lat":number, "lng":number}
     */
    public void addMarkerFromClick(String latlng){
        JSONParser parser = new JSONParser();
        try {
            JSONObject latlng_json = (JSONObject) parser.parse(latlng);
            float lat = ((Double)latlng_json.get("lat")).floatValue();
            float lng = ((Double) latlng_json.get("lng")).floatValue();
            String logMessage = String.format("From Java: you clicked at %s, %s", lat, lng);
            System.out.println(logMessage);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public String crashes() throws SQLException {
        // TODO currently hard coding difference in having filters or not, have a think about how to not do this
        List crashList = crashData.getCrashLocations().stream().map(crash -> {
            if (crash instanceof Crash) {
                Crash crash1 = (Crash) crash;
                double latitude = crash1.getLatitude();
                double longitude = crash1.getLongitude();
                int severity = crash1.getSeverity().getValue();
                return new CrashInfo(latitude, longitude, severity);
            } else {
                HashMap crash1 = (HashMap) crash;
                double latitude = (double) crash1.get("latitude");
                double longitude = (double) crash1.get("longitude");
                int severity = (int) crash1.get("severity");
                return new CrashInfo(latitude, longitude, severity);
            }
        }).toList();

        Gson gson = new Gson();

        String json = gson.toJson(crashList);

        return json;

    }

    protected static class CrashInfo {

        public double lat;
        public double lng;
        public int severity;

        public CrashInfo(double lat, double lng, int severity) {
            this.lat = lat;
            this.lng = lng;
            this.severity = severity;
        }

    }
}