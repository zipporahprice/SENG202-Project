package seng202.team0.models;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * Simple example class showing the ability to 'bridge' from javascript to java
 * The functions within can be called from our javascript in the map view when we set an object of this class
 * as a member of the javascript
 * Note: This is a very basic example you can use any java code, though you may need to be careful when working
 * with objects
 * @author Morgan English
 */
public class JavaScriptBridge {

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
}