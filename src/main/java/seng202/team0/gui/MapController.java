package seng202.team0.gui;

import javafx.collections.FXCollections;
import com.sun.javafx.webkit.WebConsoleListener;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import netscape.javascript.JSObject;
import org.controlsfx.control.textfield.TextFields;
import org.json.simple.JSONArray;
import seng202.team0.models.Crash;
import seng202.team0.models.JavaScriptBridge;
import seng202.team0.models.Location;
import seng202.team0.models.Route;
import seng202.team0.models.GeoLocator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;


public class MapController {

    public Button generateRoute;
    @FXML
    private WebView webView;

    @FXML
    private TextField startLocation;

    @FXML
    private TextField endLocation;

    @FXML
    private TextField stopLocation;

    private Stage stage;

    private GeoLocator geolocator;
    private WebEngine webEngine;
    private JavaScriptBridge javaScriptBridge;

    JSObject javaScriptConnector;
    public void setWebView(WebView webView) {
        this.webView = webView;
    }
    public void init(Stage stage) {
        this.stage = stage;
        javaScriptBridge = new JavaScriptBridge();
        geolocator = new GeoLocator();
        //TextFields.bindAutoCompletion(startLocation.getEditor(), t -> getSuggestions(t.getUserText()));
        initMap();
        webView.prefWidthProperty().bind(stage.widthProperty().multiply(0.5));
        webView.prefHeightProperty().bind(stage.heightProperty().multiply(0.97));
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

    private void displayRoute(Route... routes) {
        List<Route> routesList = new ArrayList<>();
        Collections.addAll(routesList, routes);
        javaScriptConnector.call("displayRoute", Route.routesToJSONArray(routesList));
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
            return null;
        }
        Location newMarker = geolocator.getLocation(address);
        //javaScriptConnector.call("addMarker", address, newMarker.lat, newMarker.lng);
        return newMarker;
    }

    @FXML
    private Location addStop() {
        String address = stopLocation.getText().trim();
        if (address.isEmpty()) {
            return null;
        }
        Location newMarker = geolocator.getLocation(address);
        //javaScriptConnector.call("addMarker", address, newMarker.lat, newMarker.lng);
        return newMarker;
    }

    @FXML
    private void generateStop() {
        Location stop = addStop();
        Location start = addStart();
        Location end = addEnd();
        if (start != null && end != null && stop != null) {
            Route route1 = new Route(start, stop);
            Route route2 = new Route(stop, end);

            List<Route> routesList = new ArrayList<>();
            routesList.add(route1);
            routesList.add(route2);

            javaScriptConnector.call("displayRoute", Route.routesToJSONArray(routesList));
        }
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

//    private Collection<String> getSuggestions(String userInput) {
//        // Call the GeoLocator method to get address suggestions based on userInput
//        return geolocator.getAddressSuggestions(userInput);
//    }
}
