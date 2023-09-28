package seng202.team0.models;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import com.google.gson.Gson;
import seng202.team0.business.CrashManager;
import seng202.team0.business.FilterManager;
import seng202.team0.gui.MainController;
import seng202.team0.repository.CrashDAO;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;


/**
 * Provides a bridge between JavaScript and Java for handling crash data.
 * This class retrieves crash data, converts it to a JSON format, and exposes it
 * to JavaScript for integration with web applications.
 * @author toddv
 */
public class JavaScriptBridge {
    CrashManager crashData = new CrashManager();


    /**
     * Retrieves a list of crash data and converts it to a JSON format.
     *
     * @return A JSON representation of crash data containing latitude and longitude information.
     * @throws SQLException If there is an error while retrieving crash data from the database.
     */
    public String crashes() {
        // TODO currently hard coding difference in having filters or not, have a think about how to not do this
        List crashList = crashData.getCrashLocations().stream().map(crash -> {
            if (crash instanceof Crash) {
                Crash crash1 = (Crash) crash;
                double latitude = crash1.getLatitude();
                double longitude = crash1.getLongitude();
                int severity = crash1.getSeverity().getValue();
                String year = Integer.toString(crash1.getCrashYear());
                String weather = crash1.getWeather().toString();
                return new CrashInfo(latitude, longitude, severity, year, weather);
            } else {
                HashMap crash1 = (HashMap) crash;
                double latitude = (double) crash1.get("latitude");
                double longitude = (double) crash1.get("longitude");
                int severity = (int) crash1.get("severity");
                String year = (String) crash1.get("year");
                String weather = (String) crash1.get("weather");
                return new CrashInfo(latitude, longitude, severity, year, weather);
            }
        }).toList();

        Gson gson = new Gson();

        String json = gson.toJson(crashList);

        return json;

    }
    /**
     * Represents crash information containing latitude and longitude.
     */
    protected static class CrashInfo {
        /**
         * The latitude of the crash location.
         */
        public double lat;
        /**
         * The longitude of the crash location.
         */
        public double lng;
        public int severity;

        public String crash_year; // Add year
        public String weather;

        /**
         * Constructs a CrashInfo object with latitude and longitude.
         *
         * @param lat The latitude of the crash location.
         * @param lng The longitude of the crash location.
         */
        public CrashInfo(double lat, double lng, int severity, String year, String weather) {
            this.lat = lat;
            this.lng = lng;
            this.severity = severity;
            this.crash_year = year;
            this.weather = weather;

        }

    }

    /**
     * Retrieves the name of the current view in the application.
     *
     * @return The name of the current view.
     */
    public String currentView() {
        return MainController.currentView;
    }
}