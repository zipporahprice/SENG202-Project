package seng202.team0.models;

import javafx.scene.control.Alert;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import seng202.team0.App;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.Collection;

/**
 * Provides geolocation functionality using the Nominatim Geolocation API.
 *
 * @author Team 10
 */
public class GeoLocator {
    private static final Logger log = LogManager.getLogger(App.class);

    /**
     * Takes in user input and searches for the address using the Nominatim Geolocation API before returning the location
     * @param address user input to find latitude and longitude for
     */

    public Location getLocation(String address) {
        String logMessage = String.format("Requesting geolocation from Nominatim for address: %s, New Zealand", address);
        log.error(logMessage);
        address = address.replace(' ', '+');
        try {
            // Creating the http request
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder(
                    URI.create("https://nominatim.openstreetmap.org/search?q=" + address + ",+New+Zealand&format=json")
            ).build();
            // Getting the response
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            // Parsing the json response to get the latitude and longitude co-ordinates
            JSONParser parser = new JSONParser();
            JSONArray results = (JSONArray) parser.parse(response.body());

            if (results.isEmpty()) {
                showErrorAlert("Invalid Address", "The address provided is invalid or couldn't be found.");
                return null; // or return a default location, depending on your use-case
            }

            JSONObject bestResult = (JSONObject) results.get(0);
            double lat = Double.parseDouble((String) bestResult.get("lat"));
            double lng = Double.parseDouble((String) bestResult.get("lon"));
            return new Location(lat, lng);
        } catch (IOException | ParseException e) {
            log.error(e);
        } catch (InterruptedException ie) {
            log.error(ie);
            Thread.currentThread().interrupt();
        }
        return new Location(0d, 0d);
    }

    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


    /**
     * Retrieves the address for a given latitude and longitude using the Nominatim Geolocation API.
     *
     * @param lat The latitude coordinate.
     * @param lng The longitude coordinate.
     * @return The address corresponding to the provided latitude and longitude, or "No Address Found" if none is found or an error occurs.
     */
    public String getAddress(Double lat, Double lng) {
        try {
            // Creating the http request
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder(
                    URI.create("https://nominatim.openstreetmap.org/reverse?lat=" + lat + "&lon=" + lng+"&format=json")
            ).build();
            // Getting the response
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            // Parsing the json response to get the latitude and longitude co-ordinates
            JSONParser parser = new JSONParser();
            JSONObject result = (JSONObject) parser.parse(response.body());

            if (result.isEmpty()) {
                showErrorAlert("Invalid Address", "The address provided is invalid or couldn't be found.");
                return null; // or return a default location, depending on your use-case
            }

            JSONObject address = (JSONObject) result.get("address");
            String houseNumber = (String) address.get("house_number");
            String state = (String) address.get("state");
            String road = (String) address.get("road");
            if (houseNumber != null) {
                return houseNumber + " " + road + " " + state;
            }
            return road + " " + state;
        } catch (IOException | ParseException e) {
            log.error(e);
        } catch (InterruptedException ie) {
            log.error(ie);
            Thread.currentThread().interrupt();
        }
        return "No Address Found";
    }



    /*Need to check out the API usage policies before getting into this*/
//    public Collection<String> getAddressSuggestions(String userInput) {
//        // Call the Nominatim API similarly to getLocation but fetch multiple results and return them
//        // For now, let's assume it returns a dummy list for simplicity
//        return Arrays.asList("123 Main St", "456 Elm St");
//    }




}
