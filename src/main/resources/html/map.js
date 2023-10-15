let map, baseLayer, heatmapLayer, markerLayer, drawnItems, drawControl, layerGroup;
var javaScriptBridge; // must be declared as var (will not be correctly assigned in java with let keyword)
let markers = [];
var routes = [];
let markersShowing, heatmapShowing;

const cfg = {
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

var testData = {
    max: 200,
    data: []
}

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
    drawingModeOff: drawingModeOff,
    changeDrawingColourToRating: changeDrawingColourToRating,
    updateView: updateView,
    updateReviewContent: updateReviewContent,
    runDataUpdate: runDataUpdate

};

/**
 * creates and initialises the map, also defines on click event that calls java code
 */
function initMap() {
    baseLayer= new L.TileLayer('https://tile.csse.canterbury.ac.nz/hot/{z}/{x}/{y}.png', { // UCs tilemap server
        attribution: '© OpenStreetMap contributors<br>Served by University of Canterbury'
    });

    const nzBounds = [
        [-47, 166], // Southwest coordinates of nz
        [-34, 179]  // Northeast coordinates of nz
    ];

    const minZoomLevel = 5;
    const maxZoomLevel = 18;
    // Setup map
    let mapOptions = {
        center: [-43.5, 172.5],
        zoom: 11,
        layers:[baseLayer],
        zoomControl: false,
        maxBounds: nzBounds,
        minZoom: minZoomLevel,
        maxZoom: maxZoomLevel
    };
    map = new L.map('map', mapOptions);
    //disables right clicks
    map.on('contextmenu', function (e){
        e.preventDefault();
    });

    // LayerGroup to store the heatmap and crash locations
    layerGroup = L.layerGroup().addTo(map);

    L.CustomItineraryBuilder = L.Routing.ItineraryBuilder.extend({
        createContainer: function(className) {
            // Create a container div.
            var container = L.DomUtil.create('div', className);

            // Create the default table using the base class method.
            var table = L.DomUtil.create('table', 'leaflet-routing-container-table', container);

            // Create the review tab div.
            var reviewTab = L.DomUtil.create('div', 'custom-review-tab', container);
            L.DomEvent.on(reviewTab, 'click', L.DomEvent.stopPropagation);
            L.DomEvent.on(reviewTab, 'click', L.DomEvent.preventDefault);

            L.DomUtil.create('br', 'break-styling', container);

            reviewTab.innerHTML = `
            <h3 style="font-weight: bold">Review:</h3>
            <p class="reviewContent">If you are seeing this there was an error on the java side</p>
        `;

            return container;
        }
    });

    // Adding zoom control to bottom right
    L.control.zoom({
        position: 'topright'
    }).addTo(map);

    // Setup potential layers for views
    heatmapLayer = new HeatmapOverlay(cfg);
    markerLayer = L.markerClusterGroup({
        iconCreateFunction: function (cluster) {
            var averageSeverity = calculateAverageSeverity(cluster);
            var clusterColor = getColorBasedOnSeverity(averageSeverity);


            return L.divIcon({
                html: '<div class="markers-style ' + clusterColor + '">' + cluster.getChildCount() + '</div>',
                className: 'marker-style',
                iconSize: L.point(32, 32)
            });

        }
    });

    drawnItems = new L.FeatureGroup();
    drawControl = new L.Control.Draw({
        edit: {
            featureGroup: drawnItems,
            remove: false,
            edit: false
        },
        draw: {
            circle: true,
            rectangle: true,
            polygon: false,
            polyline: false,
            marker: false,
            circlemarker: false
        },
        position: 'topright',
    });

    // Initialise layers and setup callbacks
    setFilteringViewport();
    updateView();
    map.on('zoomend', updateEnabled);
    map.on('moveend', updateEnabled);
    map.on('zoomend', setFilteringViewport);
    map.on('moveend', setFilteringViewport);
    window.addEventListener('resize', newHeatmap);

    mapIsReady();
}

function updateEnabled() {
    javaScriptBridge.enableRefreshButton();
}

function newHeatmap() {
    const heatmapShowing = layerGroup.hasLayer(heatmapLayer);

    heatmapLayer = new HeatmapOverlay(cfg);
    heatmapLayer.setData(testData);

    if (heatmapShowing) {
        layerGroup.addLayer(heatmapLayer);
    }
}

function updateDataShown() {
    setFilteringViewport();
    javaScriptBridge.setCrashes();
    updateView();
}

function runDataUpdate(script) {
    eval(script);
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

    heatmapLayer.cfg.radius = newRadius;

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
        if (layerGroup.hasLayer(heatmapLayer)) {
            layerGroup.removeLayer(heatmapLayer);
        }
        if (!layerGroup.hasLayer(markerLayer)) {
            layerGroup.addLayer(markerLayer);
        }
    }
    else {
        if (layerGroup.hasLayer(markerLayer)) {
            layerGroup.removeLayer(markerLayer);
        }
        if (!layerGroup.hasLayer(heatmapLayer)) {
            layerGroup.addLayer(heatmapLayer);
        }
    }
}

function radiusChangeOnMapInteraction() {
    adjustHeatmapRadiusBasedOnZoom();
    map.on('zoomend', adjustHeatmapRadiusBasedOnZoom);
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
        radiusChangeOnMapInteraction();
        map.on('zoomend', automaticViewChange);
    } else if (currentView === "Heatmap") {
        radiusChangeOnMapInteraction();
        map.off('zoomend', automaticViewChange);

        if (layerGroup.hasLayer(markerLayer)) {
            layerGroup.removeLayer(markerLayer);
        }
        if (!layerGroup.hasLayer(heatmapLayer)) {
            layerGroup.addLayer(heatmapLayer);
        }
    } else if (currentView === "Crash Locations") {
        map.off('zoomend', adjustHeatmapRadiusBasedOnZoom);
        map.off('zoomend', automaticViewChange);

        if (layerGroup.hasLayer(heatmapLayer)) {
            layerGroup.removeLayer(heatmapLayer);
        }
        if (!layerGroup.hasLayer(markerLayer)) {
            layerGroup.addLayer(markerLayer);
        }
    } else if (currentView === "Heatmap & Crash Locations") {
        radiusChangeOnMapInteraction();
        map.off('zoomend', automaticViewChange);

        if (!layerGroup.hasLayer(heatmapLayer)) {
            layerGroup.addLayer(heatmapLayer);
        }
        if (!layerGroup.hasLayer(markerLayer)) {
            layerGroup.addLayer(markerLayer);
        }
    } else {
        // Default with "None" showing
        map.off('zoomend', adjustHeatmapRadiusBasedOnZoom);
        map.off('zoomend', automaticViewChange);

        if (layerGroup.hasLayer(heatmapLayer)) {
            layerGroup.removeLayer(heatmapLayer);
        }
        if (layerGroup.hasLayer(markerLayer)) {
            layerGroup.removeLayer(markerLayer);
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

function updateReviewContent(dataFromJava) {
    const reviewContentElements = document.querySelectorAll('.reviewContent');
    reviewContentElements.forEach(paragraph => {
        paragraph.textContent = dataFromJava;
    })
}



/**
 * Displays a route with two or more waypoints for cars (e.g. roads and ferries) and displays it on the map
 * @param waypointsIn a string representation of an array of lat lng json objects [("lat": -42.0, "lng": 173.0), ...]
 */
function displayRoute(routesIn, transportMode) {
    removeRoute();

    var routesArray = JSON.parse(routesIn);
    var currentRouteIndex = 0; // Starting index at 0
    var routeIndexMap = new Map();
    var mode = getMode(transportMode);


    routesArray.forEach(waypointsIn => {
        var waypoints = [];
        //var routeColor = getColorForSafetyScore(safetyScore);

        waypointsIn.forEach(element => waypoints.push(new L.latLng(element.lat, element.lng)));

        var newRoute = L.Routing.control({
            addWaypoints: false,
            waypoints: waypoints,
            routeWhileDragging: true,
            showAlternatives: true,
            router: L.Routing.mapbox('pk.eyJ1IjoiemlwcG9yYWhwcmljZSIsImEiOiJjbG45cWI3OGYwOTh4MnFyMWsya3FpbjF2In0.RM37Ev9aUxEwKS5nMxpCpg', { profile: mode }),
            itineraryBuilder: new L.CustomItineraryBuilder(),
        }).addTo(map);


        newRoute.on('routeselected', (e) => {
            var route = e.route;
            var coordinates = route.coordinates;
            var instructions = route.instructions;
            var instructionRoads = [];
            var instructionDistance = [];
            for (var i = 0; i < instructions.length; i++) {
                var instruction = instructions[i];
                instructionRoads.push(instruction.road);
                instructionDistance.push(instruction.distance)
            }



            // Generating or retrieving a unique identifier for the route.
            // You need to replace 'getRouteIdentifier(route)' with your actual logic of getting or generating an identifier.
            var routeId = getRouteIdentifier(route);

            // Check if this routeId has been selected before
            if (!routeIndexMap.has(routeId)) {
                // If not, add it to the map with the current index as its value
                routeIndexMap.set(routeId, currentRouteIndex);
                // Increment the current index for the next new route
                currentRouteIndex += 1;
            }

            // Retrieve the index associated with the routeId from the map
            var indexToSend = routeIndexMap.get(routeId);

            // Prepare and send the coordinates
            var coordinatesJson = JSON.stringify({
                routeId: indexToSend, // Use the index retrieved from the map
                coordinates: coordinates,
                instructionRoads: instructionRoads,
                instructionDistance: instructionDistance
            });
            javaScriptBridge.sendCoordinates(coordinatesJson);
        });

        routes.push(newRoute);
    });
}

function isRoutesPresent(routes) {
    return Array.isArray(routes) && routes.length !== 0;
}

/**
 * Removes the current route being displayed (will not do anything if there is no route currently displayed)
 */
function removeRoute() {
    routes.forEach((r) => {
        r.remove();
    });
    map.removeLayer(markerLayer);
    routes = [];
}

function getColorForSafetyScore(score) {
    if (score >= 4.0) return 'red';   // least safe
    if (score >= 3.0) return 'orange';  // moderately safe
    if (score >= 2.0) return 'yellow'; // safest
    if (score >= 0.0) return 'green';
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

function getRouteIdentifier(route) {
    // Assuming route.coordinates is an array of coordinate objects
    // And each coordinate can be represented as a string
    // You should replace this logic with the actual properties of your route objects
    if (!route || !route.coordinates) return null;

    // Convert each coordinate to a string and concatenate them
    // Ensure this provides a unique and consistent identifier for each route
    return route.coordinates.map(coord => coordToString(coord)).join(',');
}

function coordToString(coord) {
    // Convert coordinate object to a string
    // Replace this with the actual structure of your coordinate objects
    return `${coord.lat},${coord.lng}`; // Assuming a
}
function getSeverityStringFromValue(severity) {
    switch (severity) {
        case 1:
            return "Non-Injury";
        case 4:
            return "Minor Crash";
        case 16:
            return "Major Crash";
        case 64:
            return "Death";
        default:
            return "Invalid";
    }
}


function getMarkerIcon(severity) {
    var iconUrl;
    switch (severity) {
        case 1: // Non-Injury
            iconUrl = 'crash_markers/non_injury.png';
            break;
        case 4: // Minor Crash
            iconUrl = 'crash_markers/minor_crash.png';
            break;
        case 16: // Major Crash
            iconUrl = 'crash_markers/major_crash.png';
            break;
        case 64: // Death
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

function getColorBasedOnSeverity(averageSeverity) {
    // averageSeverity is out of 10

    if (averageSeverity == null) {
        return 'black';
    } else if (averageSeverity < 0.8) {
        return 'green'; // Low severity, green color
    } else if (averageSeverity < 4.3) {
        return 'yellow'; // Moderate severity, yellow color
    } else if (averageSeverity < 7.1) {
        return 'orange'; // High severity, orange color
    } else {
        return 'red'; // Very high severity, red color
    }
}


function calculateAverageSeverity(cluster) {
    var childMarkers = cluster.getAllChildMarkers();
    if (childMarkers == 0) {
        return 0;
    }
    var totalSeverity = 0;

    // Calculate the total severity within the cluster
    for (var i = 0; i < childMarkers.length; i++) {
        totalSeverity += childMarkers[i].options.severity;
    }

    // Score out of 10
    return ((totalSeverity / childMarkers.length - 1.0) / 7.0) * 10.0;
}

function resetLayers() {
    // Set booleans for later use to see if layers should be added back
    markersShowing = layerGroup.hasLayer(markerLayer);
    heatmapShowing = layerGroup.hasLayer(heatmapLayer);

    // Clears all layers so nothing is showing to slow loading down
    layerGroup.eachLayer(function (layer) {
        layerGroup.removeLayer(layer);
    });

    // Emptying heatmap testData data list and markerLayer's markers
    testData.data = [];
    markerLayer.clearLayers();
}

function addPoint(lat, lng, severity, year, weather) {
    const severityString = getSeverityStringFromValue(severity);
    const markerIcon = getMarkerIcon(severity);
    var marker = L.marker(new L.LatLng(lat, lng), {title: severityString, icon: markerIcon, severity: severity});
    marker.bindPopup("<div style='font-size: 16px;' class='popup-content'>" +
        "<p><strong>Latitude:</strong> " + lat + "</p>" +
        "<p><strong>Longitude:</strong> " + lng + "</p>" +
        "<p><strong>Severity:</strong> " + severityString + "</p>" +
        "<p><strong>Year:</strong> " + year + "</p>" + // Add year
        "<p><strong>Weather:</strong> " + weather + "</p>" + // Add weather
        "</div>"
    );
    markerLayer.addLayer(marker);
    testData.data.push({"lat": lat, "lng": lng});
}

function showLayers() {
    heatmapLayer.setData(testData);

    // Adding layers back based on resetLayers function
    // state with what layers were showing
    if (markersShowing) {
        layerGroup.addLayer(markerLayer);
    }
    if (heatmapShowing) {
        layerGroup.addLayer(heatmapLayer);
    }
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
    } else if (layer instanceof L.Circle) {
        const center = layer.getLatLng();
        const radius = (layer.getRadius() / 6371000.0) * (180.0 / Math.PI);

        const centerLat = center.lat;
        const centerLng = center.lng;

        javaScriptBridge.setRatingAreaManagerBoundingCircle(centerLat, centerLng, radius);
    }
}

function changeDrawingColourToRating(rating) {
    const colour = getColorBasedOnSeverity(rating);
    drawnItems.eachLayer(function (layer) {
        layer.setStyle({
            "color": colour
        })
    })
}
