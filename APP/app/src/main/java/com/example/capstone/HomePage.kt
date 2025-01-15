package com.example.capstone

import android.app.ActivityManager
import android.content.Context
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
import com.mapbox.geojson.Point
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
            // Optionally display a fallback UI or error message
            setContent {
                ErrorUI()
            }
            return
        }

        // Proceed with the Mapbox setup if OpenGL ES 3.0 is supported
        setContent {
            MapTest()
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
fun MyApp() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Hello, World!",
            fontSize = 24.sp
        )

        Spacer(modifier = Modifier.height(16.dp)) // adds a 16 pixel space
        // Counter button
        var count by remember { mutableStateOf(0) }
        Button(onClick = { count++ }) {
            Text(text = "Clicked $count times")
        }
    }
}

@Composable
fun MapTest() {
    MapboxMap(
        Modifier.fillMaxSize(),
        mapViewportState = rememberMapViewportState {
            setCameraOptions {
                zoom(2.0)
                center(Point.fromLngLat(-98.0, 39.5))
                pitch(0.0)
                bearing(0.0)
            }
        }
    )
}
