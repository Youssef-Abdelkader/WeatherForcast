package com.youssef.weatherforcast.Favourite

import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.airbnb.lottie.BuildConfig
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.LocationBias
import com.google.android.libraries.places.api.model.PlaceTypes
import com.google.android.libraries.places.api.model.RectangularBounds
import com.google.android.libraries.places.api.net.kotlin.awaitFindAutocompletePredictions
import com.google.android.libraries.places.compose.autocomplete.components.PlacesAutocompleteTextField
import com.google.android.libraries.places.compose.autocomplete.models.AutocompletePlace
import com.google.android.libraries.places.compose.autocomplete.models.toPlaceDetails
import com.google.maps.android.compose.*
import com.youssef.weatherforcast.Data.RemoteDataSource.Constants
import com.youssef.weatherforcast.Model.Repo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlin.time.Duration.Companion.milliseconds

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(navController: NavController, repo: Repo) {
    var selectedLocation by remember { mutableStateOf<LatLng?>(null) }
    val context = LocalContext.current

    // تهيئة Places API
    Places.initializeWithNewPlacesApiEnabled(context, Constants.GeoApi)
    val placesClient = Places.createClient(context)

    val bias: LocationBias = RectangularBounds.newInstance(
        LatLng(39.9, -105.5),
        LatLng(40.1, -105.0) // NE lat, lng
    )

    var searchText by remember { mutableStateOf("") }
    var predictions by remember { mutableStateOf(emptyList<AutocompletePrediction>()) }

    // استخدام Flow مع debounce لمنع استدعاء البحث في كل إدخال
    val searchTextFlow = remember { MutableStateFlow("") }

    LaunchedEffect(searchTextFlow) {
        searchTextFlow.collect { query ->
            if (query.isNotEmpty()) {
                try {
                    val response = placesClient.awaitFindAutocompletePredictions {
                       // locationBias = bias
                        typesFilter = listOf(PlaceTypes.CITIES)
                        this.query = query
                        Log.e("MapScreen", "Error fetching query: ${query}")

                        //countries = listOf("US") // اجعلها ديناميكية إذا كنت تريد دعم دول أخرى
                    }
                    Log.e("MapScreen", "log fetching : ")

                    predictions = response.autocompletePredictions
                    Log.e("MapScreen", "Error fetching : ${predictions.get(0)}")

                } catch (e: Exception) {

                    Log.e("MapScreen", "Error fetching predictions: ${e.message}")
                }
            }
        }
    }

    val cameraPositionState = rememberCameraPositionState {
        position = com.google.android.gms.maps.model.CameraPosition.fromLatLngZoom(
            LatLng(30.033, 31.233), 10f
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Select Location") })
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    selectedLocation?.let {
                        navController.popBackStack()
                    }
                },
                containerColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(16.dp)
            ) {
                Icon(imageVector = Icons.Default.Place, contentDescription = "Select Location")
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
                    title = "Selected Location"
                )
            }
        }

        PlacesAutocompleteTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 100.dp, ),
            searchText = searchText,

            predictions = predictions.map {
                it.toPlaceDetails()
            },
            onQueryChanged = { query ->
                searchText = query
                searchTextFlow.value = query
            },
            onSelected = { autocompletePlace: AutocompletePlace ->
                predictions= emptyList()
            },

        )
    }
}
