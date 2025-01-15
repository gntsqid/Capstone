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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Mapbox imports
import androidx.compose.foundation.layout.fillMaxSize
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.mapbox.geojson.Point
import com.mapbox.maps.Style
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState

class HomePage : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
            // Optionally prompt the user to enable location services
        }

        // Set Mapbox content
        setContent {
            MapTest()
        }
    }

    private fun requestLocationPermissions() {
        // Use `this@HomePage` to explicitly reference the context
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
        // Use `this@HomePage` for context when calling `getSystemService`
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
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
                zoom(14.0) // Adjust zoom level
                center(Point.fromLngLat(-119.0436, 34.1621)) // Center to the given location
                pitch(0.0) // Flat map
                bearing(0.0) // Orient to north
            }
        },
        style = {
            Style.MAPBOX_STREETS // Use a 2D-compatible style
        }
    ).also {
        Log.d("Mapbox", "Map initialized, loading style...")
    }
}
