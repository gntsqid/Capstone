package com.example.capstone

import Machine
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mapbox.android.gestures.BuildConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomePage : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            var machineList by remember { mutableStateOf<List<Machine>>(emptyList()) }
            var errorMessage by remember { mutableStateOf("") }

            LaunchedEffect(Unit) {
                val apiService = RetrofitClient.instance
                val apiKey = BuildConfig.CAPSTONE_API_KEY
                apiService.getMachines(apiKey).enqueue(object : Callback<List<Machine>> {
                    override fun onResponse(call: Call<List<Machine>>, response: Response<List<Machine>>) {
                        if (response.isSuccessful) {
                            machineList = response.body() ?: emptyList()
                        } else {
                            errorMessage = "Error: ${response.code()}"
                            Log.e("API_ERROR", errorMessage)
                        }
                    }

                    override fun onFailure(call: Call<List<Machine>>, t: Throwable) {
                        errorMessage = "Failed to fetch data"
                        Log.e("API_FAILURE", "Failed to fetch data", t)
                    }
                })
            }

            Scaffold(
                topBar = { TopAppBar(title = { Text("Machine List") }) }
            ) { paddingValues ->
                Column(modifier = Modifier.padding(paddingValues)) {
                    if (errorMessage.isNotEmpty()) {
                        Text(text = errorMessage, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(8.dp))
                    }
                    machineList.forEach { machine ->
                        Text(
                            text = "${machine.hostname} - ${if (machine.online == 1) "Online" else "Offline"}",
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
            }
        }
    }
}
