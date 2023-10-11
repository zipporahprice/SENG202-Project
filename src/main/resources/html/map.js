let map, baseLayer, heatmapLayer, markerLayer, drawnItems, drawControl;
var javaScriptBridge; // must be declared as var (will not be correctly assigned in java with let keyword)
let markers = [];
var routes = [];
var crashes = [];

const heatmapCfg = {
    // radius should be small ONLY if scaleRadius is true (or small radius is intended)
    // if scaleRadius is false it will be the constant radius used in pixels
    "radius": 0.1,
    "maxOpacity": .4,
    // scales the radius based on map zoom
    "scaleRadius": true,
    // if set to false the heatmap uses the global maximum for colorization
    // if activated: uses the data maximum within the current map boundaries
    //   (there will always be a red spot with useLocalExtremas true)
    "useLocalExtrema": false,
    // which field name in your data represents the latitude - default "lat"
    latField: 'lat',
    // which field name in your data represents the longitude - default "lng"
    lngField: 'lng',
    // which field name in your data represents the data value - default "value"
    valueField: 'count'
};

/**
 * This object can be returned to our java code, where we can call the functions we define inside it
 */
let jsConnector = {
    addMarker: addMarker,
    displayRoute: displayRoute,
    removeRoute: removeRoute,
    initMap: initMap,
    updateDataShown: updateDataShown,
    drawingModeOn: drawingModeOn,
    drawingModeOff: drawingModeOff
};

/**
 * creates and initialises the map, also defines on click event that calls java code
 */
function initMap() {
    baseLayer= new L.TileLayer('https://tile.csse.canterbury.ac.nz/hot/{z}/{x}/{y}.png', { // UCs tilemap server
        attribution: '© OpenStreetMap contributors<br>Served by University of Canterbury'
    });

    // Setup map
    let mapOptions = {
        center: [-43.5, 172.5],
        zoom: 11,
        layers:[baseLayer],
        zoomControl: false
    };
    map = new L.map('map', mapOptions);

    // Adding zoom control to bottom right
    L.control.zoom({
        position: 'bottomright'
    }).addTo(map);

    // Setup potential layers for views
    newHeatmap();
    markerLayer = L.markerClusterGroup();
    drawnItems = new L.FeatureGroup();
    drawControl = new L.Control.Draw({
        edit: {
            featureGroup: drawnItems,
            remove: false,
            edit: false
        },
        draw: {
            circle: false,
            rectangle: true,
            polygon: false,
            polyline: false,
            marker: false,
            circlemarker: false
        },
        position: 'topright'
    });

    // Initialise layers and setup callbacks
    updateDataShown();
    map.on('zoomend', updateEnabled);
    map.on('moveend', updateEnabled);
    window.addEventListener('resize', newHeatmap);

    mapIsReady();
}

function updateEnabled() {
    javaScriptBridge.enableRefreshButton();
}

function newHeatmap() {
    const heatmapShowing = map.hasLayer(heatmapLayer);

    heatmapLayer = new HeatmapOverlay(heatmapCfg);

    if (heatmapShowing) {
        setHeatmapData();
        map.addLayer(heatmapLayer);
    }
}

function updateDataShown() {
    setFilteringViewport();
    eval(javaScriptBridge.crashes());
    updateView();
}

function mapIsReady() {
    javaScriptBridge.mapLoaded();
}

function adjustHeatmapRadiusBasedOnZoom() {
    let zoomLevel = map.getZoom();

    let newRadius;
    if (zoomLevel >= 17) {
        newRadius = 0.0002;  // For street-level detail
    } else if (zoomLevel >= 15 && zoomLevel < 17) {
        newRadius = 0.001; // For street-level detail
    } else if (zoomLevel >= 13) {
        newRadius = 0.005;  // For neighborhood-level detail
    }
    else if (zoomLevel >= 10) {
        newRadius = 0.01;  // For neighborhood-level detail
    }  else {
        newRadius = 0.1;  // For city-level detail
    }

    heatmapLayer.heatmapCfg.radius=newRadius;

}

function setFilteringViewport() {
    const bounds = map.getBounds();
    const minLatitude = bounds.getSouth();
    const minLongitude = bounds.getWest();
    const maxLatitude = bounds.getNorth();
    const maxLongitude = bounds.getEast();
    javaScriptBridge.setFilterManagerViewport(minLatitude, minLongitude, maxLatitude, maxLongitude);
}

function automaticViewChange() {
    var zoomLevel = map.getZoom();
    if (zoomLevel >= 12) {
        if (map.hasLayer(heatmapLayer)) {
            map.removeLayer(heatmapLayer);
        }
        if (!map.hasLayer(markerLayer)) {
            map.addLayer(markerLayer);
        }
    }
    else {
        if (map.hasLayer(markerLayer)) {
            map.removeLayer(markerLayer);
        }
        if (!map.hasLayer(heatmapLayer)) {
            map.addLayer(heatmapLayer);
        }
    }
}

function showCrashesWithEventCallbacks() {
    map.on('zoomend', updateDataShown);
    map.on('moveend', updateDataShown);
}

function hideCrashesWithEventCallbacks() {
    map.off('zoomend', updateDataShown);
    map.off('moveend', updateDataShown);
}

/**
 * Updates the view according to the user selection
 * Three views available:
 * Automatic - Changes from heatmap to crash locations at "12" zoomed in and back
 * Heatmap - Shows heatmap at all zooms
 * Crash Locations - Shows crash locations in clusters at all zooms
 */
function updateView() {
    var currentView = javaScriptBridge.currentView();

    if (currentView === "Automatic") {
        automaticViewChange();
        map.on('zoomend', automaticViewChange);
        map.on('zoomend', adjustHeatmapRadiusBasedOnZoom);
    } else if (currentView === "Heatmap") {
        map.off('zoomend', automaticViewChange);
        map.on('zoomend', adjustHeatmapRadiusBasedOnZoom);

        if (map.hasLayer(markerLayer)) {
            map.removeLayer(markerLayer);
        }
        map.addLayer(heatmapLayer);
    } else if (currentView === "Crash Locations") {
        map.off('zoomend', adjustHeatmapRadiusBasedOnZoom);
        map.off('zoomend', automaticViewChange);

        if (map.hasLayer(heatmapLayer)) {
            map.removeLayer(heatmapLayer);
        }
        map.addLayer(markerLayer);
    } else {
        // Default with "None" showing
        map.off('zoomend', adjustHeatmapRadiusBasedOnZoom);
        map.off('zoomend', automaticViewChange);

        if (map.hasLayer(heatmapLayer)) {
            map.removeLayer(heatmapLayer);
        }
        if (map.hasLayer(markerLayer)) {
            map.removeLayer(markerLayer);
        }
    }
}


/**
 * Adds a marker to the map and stores it in the markers array for later use (e.g. removal)
 * @param title tooltip to display on hover
 * @param lat latitude to place marker at
 * @param lng longitude to place marker at
 */
function addMarker(title, lat, lng) {
    var m = new L.Marker([lat, lng])
    m.bindPopup(title).openPopup()
    m.addTo(map)
    markers.push(m)
}

/**
 * Displays a route with two or more waypoints for cars (e.g. roads and ferries) and displays it on the map
 * @param waypointsIn a string representation of an array of lat lng json objects [("lat": -42.0, "lng": 173.0), ...]
 */
function displayRoute(routesIn, transportMode, safetyScore) {
    removeRoute();

    var routesArray = JSON.parse(routesIn);
    console.log(routesArray);

    var mode = getMode(transportMode);

    routesArray.forEach(waypointsIn => {
        var waypoints = [];
        var routeColor = getColorForSafetyScore(safetyScore);

        waypointsIn.forEach(element => waypoints.push(new L.latLng(element.lat, element.lng)));
        console.log(waypoints);
        var newRoute = L.Routing.control({
            waypoints: waypoints,
            routeWhileDragging: true,
            router: L.Routing.mapbox('pk.eyJ1IjoiemlwcG9yYWhwcmljZSIsImEiOiJjbG45cWI3OGYwOTh4MnFyMWsya3FpbjF2In0.RM37Ev9aUxEwKS5nMxpCpg', { profile: mode }),
            lineOptions: {
                styles: [
                    {color: routeColor, opacity: 0.8, weight: 6} // color from safety Score
                ]
            }
        }).addTo(map);
        routes.push(newRoute);
    });
}

function getColorForSafetyScore(score) {
    if (score >= 4.0) return 'red';   // least safe
    if (score >= 3.0) return 'orange';  // moderately safe
    if (score >= 2.0) return 'green'; // safest
    if (score >= 1.0) return 'pink'; // impossible to die
    return 'red'; // default if safety score is null
}

function getMode(transportMode) {
    var mode;
    switch (transportMode) {
        case "car":
            mode = 'mapbox/driving';
            break;
        case "bike":
            mode = 'mapbox/cycling';
            break;
        case "walking":
            mode = 'mapbox/walking';
            break;
        default:
            mode = 'mapbox/driving';
            break;
    }
    return mode;
}

/**
 * Removes the current route being displayed (will not do anything if there is no route currently displayed)
 */
function removeRoute() {
    routes.forEach((r) => {
        r.remove();
    });
    routes = [];
}

function getSeverityStringFromValue(severity) {
    switch (severity) {
        case 1: return "Non-Injury";
        case 2: return "Minor Crash";
        case 4: return "Major Crash";
        case 8: return "Death";
        default: return "Invalid";
    }
}

function getMarkerIcon(severity) {
    var iconUrl;
    switch (severity) {
        case 1: // Non-Injury
            iconUrl = 'crash_markers/non_injury.png';
            break;
        case 2: // Minor Crash
            iconUrl = 'crash_markers/minor_crash.png';
            break;
        case 4: // Major Crash
            iconUrl = 'crash_markers/major_crash.png';
            break;
        case 8: // Death
            iconUrl = 'crash_markers/death_crash.png';
            break;
        default: // Default icon
            iconUrl = 'crash_markers/non_injury.png';
            break;
    }

    return L.icon({
        iconUrl: iconUrl,
        iconSize: [24, 32.7], // Adjust the icon size as needed
    });
}

var start = 0;
var end = 0;

function resetLayers() {
    start = performance.now();
    newHeatmap()
    markerLayer.clearLayers();
}

function addPoint(lat, lng, severity, year, weather) {
    const severityString = getSeverityStringFromValue(severity);
    const markerIcon = getMarkerIcon(severity);
    var marker = L.marker(new L.LatLng(lat, lng), { title: severityString, icon: markerIcon });
    marker.bindPopup("<div style='font-size: 16px;' class='popup-content'>" +
        "<p><strong>Latitude:</strong> " + lat + "</p>" +
        "<p><strong>Longitude:</strong> " + lng + "</p>" +
        "<p><strong>Severity:</strong> " + severityString + "</p>" +
        "<p><strong>Year:</strong> " + year + "</p>" + // Add year
        "<p><strong>Weather:</strong> " + weather + "</p>" + // Add weather
        "</div>"
    );
    markerLayer.addLayer(marker);
    crashes.push({"lat": lat, "lng": lng});
}

function setHeatmapData() {
    const testData = {
        max: 200,
        data: crashes
    }
    heatmapLayer.setData(testData);
    end = performance.now();
    javaScriptBridge.printTime(end - start);
}

function drawingModeOn() {
    map.addLayer(drawnItems);
    map.addControl(drawControl);
    map.on('draw:created', handleNewDrawing);
}

function drawingModeOff() {
    map.removeLayer(drawnItems);
    drawnItems.clearLayers();
    map.removeControl(drawControl);
    map.off('draw-created');
}

function handleNewDrawing(event) {
    // Remove previous drawings
    drawnItems.clearLayers();

    // Add new drawing
    const layer = event.layer;
    drawnItems.addLayer(layer);

    if (layer instanceof L.Rectangle) {
        const latLngs = layer.getBounds(); // Get the coordinates within the shape
        const southwest = latLngs.getSouthWest();
        const northeast = latLngs.getNorthEast();

        const southwestLat = southwest.lat;
        const southwestLng = southwest.lng;
        const northeastLat = northeast.lat;
        const northeastLng = northeast.lng;

        javaScriptBridge.setRatingAreaManagerBoundingBox(southwestLat, southwestLng, northeastLat, northeastLng);
    }

    // TODO for implementing a circle
    // else if (layer instanceof L.Circle) {
    //     const center = layer.getLatLng();
    //     const radius = layer.getRadius();
    //
    //     const centerLat = center.lat;
    //     const centerLng = center.lng;
    //
    //     let latLngString = centerLat + " " + centerLng + " " + radius;
    //     javaScriptBridge.printThings(latLngString);
    // }
}