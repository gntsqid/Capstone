package com.example.capstone

import android.os.Bundle
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

// mapbox uses
import androidx.compose.foundation.layout.fillMaxSize
import com.mapbox.geojson.Point
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState

class HomePage : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            //MyApp() // TEST FUNCTION
            MapTest() // mapbox test
        }
    }
}

// TEST FUNCTION
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
        // counter button
        var count by remember { mutableStateOf(0) }
        Button(onClick = { count++ }) {
            Text(text = "Clicked $count times")
        }
    }
}

@Composable
fun MapTest() {
  MapboxMap(Modifier.fillMaxSize(),
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

