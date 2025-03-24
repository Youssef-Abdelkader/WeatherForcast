package com.youssef.weatherforcast.Favourite

import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.youssef.weatherforcast.Model.Repo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(navController: NavController, repo: Repo) {
    var selectedLocation by remember { mutableStateOf<LatLng?>(null) }

    val cameraPositionState = rememberCameraPositionState {
        position = com.google.android.gms.maps.model.CameraPosition.fromLatLngZoom(LatLng(30.033, 31.233), 10f)
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text(" ") })
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    selectedLocation?.let {
                        Log.d("MapScreen", "Location Selected: $it")
                        navController.popBackStack()
                    }
                },
                containerColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(16.dp)
            ) {
                Icon(imageVector = Icons.Default.Place, contentDescription = "")
            }
        }
    ) { paddingValues ->
        GoogleMap(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            cameraPositionState = cameraPositionState,
            onMapClick = { latLng ->
                selectedLocation = latLng
            }
        ) {
            selectedLocation?.let {
                Marker(
                    state = MarkerState(position = it),
                    title = "الموقع المحدد"
                )
            }
        }
    }
}
