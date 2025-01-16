package com.example.capstone

import android.Manifest
import android.app.ActivityManager
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.mapbox.common.MapboxOptions
import com.mapbox.geojson.Point
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.search.autocomplete.PlaceAutocomplete
import com.mapbox.search.autocomplete.PlaceAutocompleteResult
import kotlinx.coroutines.launch

class HomePage : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Mapbox Access Token
        MapboxOptions.accessToken = getString(R.string.mapbox_access_token)

        // Check OpenGL ES version
        val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val glEsVersion = activityManager.deviceConfigurationInfo.glEsVersion
        Log.d("GLVersion", "OpenGL ES version: $glEsVersion")

        // Ensure the device supports OpenGL ES 3.0
        if (glEsVersion.toDouble() < 3.0) {
            Log.e("Mapbox", "OpenGL ES 3.0 is not supported on this device.")
            setContent {
                ErrorUI()
            }
            return
        }

        // Request location permissions
        requestLocationPermissions()

        // Check if location services are enabled
        if (!isLocationEnabled()) {
            Log.e("HomePage", "Location services are not enabled!")
        }

        // Retrieve the API key from resources
        val accessToken = getString(R.string.mapbox_access_token)

        // Set Mapbox content
        setContent {
            Column {
                //MapTest()
                Spacer(modifier = Modifier.height(16.dp))
                SearchUI(accessToken) { query, callback ->
                    performSearch(query, accessToken, callback)
                }
            }
        }
    }

    private fun requestLocationPermissions() {
        if (ContextCompat.checkSelfPermission(
                this@HomePage,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this@HomePage,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                1001
            )
        }
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    private fun performSearch(
        query: String,
        accessToken: String,
        onResults: (List<PlaceAutocompleteResult>) -> Unit
    ) {
        val placeAutocomplete = PlaceAutocomplete.create(locationProvider = null)

        lifecycleScope.launch {
            val response = placeAutocomplete.suggestions(query)
            if (response.isValue) {
                val suggestions = response.value.orEmpty()
                Log.i("Search", "Suggestions: $suggestions")

                if (suggestions.isNotEmpty()) {
                    val result = placeAutocomplete.select(suggestions.first())
                    result.onValue { searchResults ->
                        onResults(listOf(searchResults)) // Pass the results to the callback
                    }
                    result.onError { error ->
                        Log.e("Search", "Error selecting suggestion", error)
                    }
                }
            } else {
                Log.e("Search", "Error fetching suggestions: ${response.error}")
            }
        }
    }
}

@Composable
fun ErrorUI() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "This device does not support OpenGL ES 3.0, required for Mapbox.",
            fontSize = 16.sp
        )
    }
}

@Composable
fun MapTest() {
    MapboxMap(
        modifier = Modifier.fillMaxSize(),
        mapViewportState = rememberMapViewportState {
            setCameraOptions {
                zoom(14.0) // FULL CAMPUS
                center(Point.fromLngLat(-119.0436, 34.1621)) // CSUCI
                pitch(0.0) // Flat map
                bearing(342.0) // 0 orients to north off of 360 degrees
            }
        }
    )
}

@Composable
fun SearchUI(
    accessToken: String,
    performSearch: (String, (List<PlaceAutocompleteResult>) -> Unit) -> Unit
) {
    var query by remember { mutableStateOf("") }
    var results by remember { mutableStateOf(listOf<PlaceAutocompleteResult>()) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(
            value = query,
            onValueChange = { query = it },
            label = { Text("Search for a place") }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                performSearch(query) { searchResults ->
                    results = searchResults
                }
            }
        ) {
            Text("Search")
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(results) { result ->
                Text(result.name ?: "Unknown result")
            }
        }
    }
}
