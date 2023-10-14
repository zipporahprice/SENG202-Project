package seng202.team0.models;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.controlsfx.control.PopOver;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import seng202.team0.App;


/**
 * Provides geolocation functionality using the Nominatim Geolocation API.
 *
 * @author Angelica Silva
 * @author Christopher Wareing
 * @author Neil Alombro
 * @author Todd Vermeir
 * @author William Thompson
 * @author Zipporah Price
 */
public class GeoLocator {
    private static final Logger log = LogManager.getLogger(App.class);

    private PopOver popOver;

    /**
     * Takes user input and searches for the address using Nominatim Geolocation API.
     * Returns the location.
     *
     * @param address user input to find latitude and longitude for
     */
    public Pair<Location, String> getLocation(String address) {
        address = address.replaceAll("[ ,/]", "+");
        address = address.replaceAll("\\++", " "); // Replace one or more + with a single space
        address = address.replaceAll(" +", "+");
        String[] addressParts = address.split("\\+");
        StringBuilder finalAddress = new StringBuilder(addressParts[0]);
        for (int i = 1; i < addressParts.length - 3; i++) {
            finalAddress.append("+").append(addressParts[i]);
        }
        System.out.println(finalAddress);
        try {
            // Creating the http request
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder(
                    URI.create("https://nominatim.openstreetmap.org/search?q="
                            + finalAddress + ",+New+Zealand&format=json")
            ).build();
            // Getting the response
            HttpResponse<String> response = client.send(request,
                    HttpResponse.BodyHandlers.ofString());
            // Parsing the json response to get the latitude and longitude co-ordinates
            JSONParser parser = new JSONParser();
            JSONArray results = (JSONArray) parser.parse(response.body());

            if (results.isEmpty()) {
                return new Pair<>(null, "The address " + address
                        + " is invalid or couldn't be found.");
            }

            JSONObject bestResult = (JSONObject) results.get(0);
            double lat = Double.parseDouble((String) bestResult.get("lat"));
            double lng = Double.parseDouble((String) bestResult.get("lon"));
            return new Pair<>(new Location(lat, lng), null);
        } catch (IOException | ParseException e) {
            log.error(e);
        } catch (InterruptedException ie) {
            log.error(ie);
            Thread.currentThread().interrupt();
        }
        return new Pair<>(new Location(0d, 0d), null);
    }

    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Takes user input and searches for the closest address based off of the
     * text supplied.
     *
     * @param address user input to find latitude and longitude for
     */
    public ObservableList<String> getAddressOptions(String address) {
        address = address.replaceAll("[ ,/]", "+");
        address = address.replaceAll("\\++", " "); // Replace one or more + with a single space
        address = address.replaceAll(" +", "+");
        String[] addressParts = address.split("\\+");
        StringBuilder finalAddress = new StringBuilder(addressParts[0]);
        for (int i = 1; i < addressParts.length - 3; i++) {
            finalAddress.append("+").append(addressParts[i]);
        }
        try {
            // Creating the http request
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder(
                    URI.create("https://nominatim.openstreetmap.org/search?q="
                            + finalAddress + ",+New+Zealand&format=json")
            ).build();
            // Getting the response
            HttpResponse<String> response = client.send(request,
                    HttpResponse.BodyHandlers.ofString());
            // Parsing the json response
            JSONParser parser = new JSONParser();
            JSONArray results = (JSONArray) parser.parse(response.body());
            ObservableList<String> output = FXCollections.observableArrayList();
            for (Object result : results) {
                JSONObject jsonObject = (JSONObject) result;
                String output1 = (String) jsonObject.get("display_name");
                output.add(output1);
            }
            return output;
        } catch (IOException | ParseException | InterruptedException e) {
            log.error("Exception while fetching address options: ", e);
            throw new RuntimeException(e);
        }
    }





    /**
     * Retrieves the address for a given latitude and longitude using the Nominatim Geolocation API.
     *
     * @param lat The latitude coordinate.
     * @param lng The longitude coordinate.
     * @return Address corresponding to provided latitude and longitude, else "No Address Found".
     */
    public String getAddress(Double lat, Double lng, String location) {
        try {
            // Creating the http request
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder(
                    URI.create("https://nominatim.openstreetmap.org/reverse?lat="
                            + lat + "&lon=" + lng + "&format=json")
            ).build();
            // Getting the response
            HttpResponse<String> response = client.send(request,
                    HttpResponse.BodyHandlers.ofString());
            // Parsing the json response to get the latitude and longitude co-ordinates
            JSONParser parser = new JSONParser();
            JSONObject result = (JSONObject) parser.parse(response.body());

            if (result.isEmpty()) {
                showErrorAlert("Invalid " + location + " Address",
                        "The " + location.toLowerCase()
                                + " provided is invalid or couldn't be found.");
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
    //        // Call the Nominatim API similarly to getLocation but fetch
    //        multiple results and return them
    //        // For now, let's assume it returns a dummy list for simplicity
    //        return Arrays.asList("123 Main St", "456 Elm St");
    //    }
}
