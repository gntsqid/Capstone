<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <title>Load data from an external GeoJSON file</title>
    <meta name="viewport" content="initial-scale=1,maximum-scale=1,user-scalable=no">
    <link href="https://api.mapbox.com/mapbox-gl-js/v3.5.1/mapbox-gl.css" rel="stylesheet">
    <script src="https://api.mapbox.com/mapbox-gl-js/v3.5.1/mapbox-gl.js"></script>
    <style>
        body { margin: 0; padding: 0; }
        #map { position: absolute; top: 0; bottom: 0; width: 100%; }
    </style>
</head>
<body>
    <div id="map"></div>
    <script>
        <?php
        $token = getenv("MAPBOX_DEFAULT_TOKEN"); // Apache Environment Secret: API Key
        if ($token) {
            echo "mapboxgl.accessToken = '$token';";
        } else {
            echo "console.error('Mapbox access token is not set');";
        }
        ?>

        const map = new mapboxgl.Map({
            container: 'map', // container ID
            style: 'mapbox://styles/mapbox/dark-v10', // style URL
            center: [-119.0444412, 34.162571], // starting position [lng, lat]
            zoom: 12 // starting zoom
        });

        map.on('load', () => {
            map.addSource('points', {
                type: 'geojson',
                data: './wardrive-CI.geojson' // Use the URL of your GeoJSON file
            });

            map.addLayer({
                'id': 'points-layer',
                'type': 'circle',
                'source': 'points',
                'paint': {
                    'circle-radius': 1,
                    'circle-stroke-width': 2,
                    'circle-color': '#8A9A5B',  // Yellow color
                    'circle-stroke-color': '#FFFFFF',
                    'circle-opacity': 0.2 
                }
            });

            // click on point to view properties in a popup
            map.on('click', 'points-layer', (event) => {
                const properties = event.features[0].properties;
                new mapboxgl.Popup()
                    .setLngLat(event.features[0].geometry.coordinates)
                    .setHTML(`
                        <strong>Title:</strong> ${properties.title}<br>
                        <strong>SSID:</strong> ${properties.ssid}<br>
                        <strong>Type:</strong> ${properties.type}
                    `)
                    .addTo(map);
            });

            // Change the cursor to a pointer when the mouse is over the points.
            map.on('mouseenter', 'points-layer', () => {
                map.getCanvas().style.cursor = 'pointer';
            });

            // Change it back to a pointer when it leaves.
            map.on('mouseleave', 'points-layer', () => {
                map.getCanvas().style.cursor = '';
            });
        });
    </script>
</body>
</html>

