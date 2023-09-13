package seng202.team0.gui;

import com.sun.javafx.webkit.WebConsoleListener;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import netscape.javascript.JSObject;
import seng202.team0.models.Crash;
import seng202.team0.models.JavaScriptBridge;
import seng202.team0.models.Location;
import seng202.team0.models.Route;
import seng202.team0.models.GeoLocator;

public class MapController {

    public Button generateRoute;
    @FXML
    private WebView webView;

    @FXML
    private TextField startLocation;

    @FXML
    private TextField endLocation;

    private Stage stage;

    private GeoLocator geolocator;
    private WebEngine webEngine;
    private JavaScriptBridge javaScriptBridge;
    JSObject javaScriptConnector;

    public void init(Stage stage) {
        this.stage = stage;
        javaScriptBridge = new JavaScriptBridge();
        geolocator = new GeoLocator();
        initMap();
        stage.sizeToScene();
    }

    /**
     * Initialises the WebView loading in the appropriate html and initialising important communicator
     * objects between Java and Javascript
     */
    private void initMap() {
        webEngine = webView.getEngine();
        webEngine.setJavaScriptEnabled(true);
        webEngine.load(getClass().getClassLoader().getResource("html/map.html").toExternalForm());
        // Forwards console.log() output from any javascript to info log
        WebConsoleListener.setDefaultListener((view, message, lineNumber, sourceId) ->
                System.out.println(String.format("Map WebView console log line: %d, message : %s", lineNumber, message)));

        webEngine.getLoadWorker().stateProperty().addListener(
                (ov, oldState, newState) -> {
                    // if javascript loads successfully
                    if (newState == Worker.State.SUCCEEDED) {
                        // set our bridge object
                        JSObject window = (JSObject) webEngine.executeScript("window");
                        window.setMember("javaScriptBridge", javaScriptBridge);
                        // get a reference to the js object that has a reference to the js methods we need to use in java
                        javaScriptConnector = (JSObject) webEngine.executeScript("jsConnector");
                        // call the javascript function to initialise the map
                        javaScriptConnector.call("initMap");
                    }
                });
    }

    /**
     * Adds a location with given crash
     */
    private void addLocation(Crash crash) {
        javaScriptConnector.call("addMarker", crash.getCrashLocation1() + "-" + crash.getCrashLocation2(),
                crash.getLatitude(), crash.getLongitude());
    }

    /**
     * Displays a route on the WebView map using the underlying js command
     * @param newRoute route to be displayed, made up of 2 or more Positions
     */
    private void displayRoute(Route newRoute) {
        javaScriptConnector.call("displayRoute", newRoute.toJSONArray());
    }

    /**
     * Removes the route from the WebView map (if currently shown)
     */
    private void removeRoute() {
        javaScriptConnector.call("removeRoute");
    }

    /**
     * Adds a location when the "Add Location" button is clicked.
     * Uses Geolocator class to turn the address into a lat, lng pair
     */

    @FXML
    private Location addStart() {
        String address = startLocation.getText().trim();
        if (address.isEmpty()) {
            // Log or show an alert to user about the empty address
            return null;
        }
        Location newMarker = geolocator.getLocation(address);
        //javaScriptConnector.call("addMarker", address, newMarker.lat, newMarker.lng);
        return newMarker;
    }

    @FXML
    private Location addEnd() {
        String address = endLocation.getText().trim();
        if (address.isEmpty()) {
            // Log or show an alert to user about the empty address
            return null;
        }
        Location newMarker = geolocator.getLocation(address);
        //javaScriptConnector.call("addMarker", address, newMarker.lat, newMarker.lng);
        return newMarker;
    }

    @FXML
    private void generateRouteAction() {
        Location start = addStart();
        Location end = addEnd();

        if (start != null && end != null) {
            Route route = new Route(start, end);
            displayRoute(route);
        }
    }
}
