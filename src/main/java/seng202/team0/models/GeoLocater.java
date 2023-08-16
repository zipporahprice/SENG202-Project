package seng202.team0.models;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * Class to return the longitude and latitude of a location requested by the user
 * @author tve21
 */

public class GeoLocater {
    /**
     * Takes in user input and searches for the address using the Nominatim Geolocation API before returning the location
     * @param address user input to find latitude and longitude for
     */

    public Location getLocation(String address){
        String msg = String.format("Searching for %d", address);
        System.out.println(msg);

        address = address.replace(' ', '+');

        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder(
                    URI.create("https://nominatim.openstreetmap.org/search?q=\" + address + \",+New+Zealand&format=json")
            ).build();

            HttpResponse<String> data = client.send(request, HttpResponse.BodyHandlers.ofString());

            JSONParser parser = new JSONParser();
            JSONArray results = (JSONArray) parser.parse(data.body());
            JSONObject best  = (JSONObject) results.get(0);

            double latitude = Double.parseDouble((String)best.get("lat"));
            double longitude = Double.parseDouble((String)best.get("lon"));
            
            return new Location(latitude,longitude);
        } catch (IOException | ParseException e){
            System.err.println(e);
        } catch (InterruptedException ie) {
            System.err.println(ie);
            Thread.currentThread().interrupt();
        }

        return new Location(0d,0d);


    }



}
