package seng202.team0.models;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import com.google.gson.Gson;
import seng202.team0.business.CrashManager;
import seng202.team0.repository.CrashDAO;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
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
    public String crashes() throws SQLException {
        List<Crash> crashList = crashData.getCrashes();

        List<CrashInfo> crash = crashData.getCrashes().stream().map(crash1-> {
            double latitude = crash1.getLatitude();
            double longitude = crash1.getLongitude();
            return new CrashInfo(latitude, longitude);
        }).toList();


        Gson gson = new Gson();

        String json = gson.toJson(crash);


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
        /**
         * Constructs a CrashInfo object with latitude and longitude.
         *
         * @param lat The latitude of the crash location.
         * @param lng The longitude of the crash location.
         */

        public CrashInfo(double lat, double lng) {
            this.lat = lat;
            this.lng = lng;
        }

    }
}