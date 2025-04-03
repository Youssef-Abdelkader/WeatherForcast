package com.youssef.weatherforcast.Favourite

import android.location.Geocoder
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.airbnb.lottie.BuildConfig
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.LocationBias
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.PlaceTypes
import com.google.android.libraries.places.api.model.RectangularBounds
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.kotlin.awaitFindAutocompletePredictions
import com.google.android.libraries.places.compose.autocomplete.components.PlacesAutocompleteTextField
import com.google.android.libraries.places.compose.autocomplete.models.AutocompletePlace
import com.google.android.libraries.places.compose.autocomplete.models.toPlaceDetails
import com.google.maps.android.compose.*
import com.youssef.weatherforcast.Data.RemoteDataSource.Constants
import com.youssef.weatherforcast.Home.HomeViewModel
import com.youssef.weatherforcast.Model.Repo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.Locale
import kotlin.time.Duration.Companion.milliseconds
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(navController: NavController, repo: Repo,    homeViewModel: HomeViewModel? = null
) {
    var selectedLocation by remember { mutableStateOf<LatLng?>(null) }
    val context = LocalContext.current
    val mapScreenViewModel: MapScreenViewModel = viewModel(factory = MapScreenViewModelFactory(repo))
    val insertState by mapScreenViewModel.insertState.collectAsStateWithLifecycle()


    LaunchedEffect(Unit) {
        if (!Places.isInitialized()) {
            try {
                Places.initialize(context, Constants.GeoApi)
            } catch (e: Exception) {
                Log.e("MapScreen", "Places initialization failed", e)
                Toast.makeText(context, "Maps service initialization failed", Toast.LENGTH_LONG).show()
            }
        }
    }

    Places.initializeWithNewPlacesApiEnabled(context, Constants.GeoApi)
    val placesClient = remember {
        Places.createClient(context)
    }
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(30.033, 31.233), 10f)
    }
    DisposableEffect(Unit) {
        onDispose {
            try {
                (placesClient as? AutoCloseable)?.close()
            } catch (e: Exception) {
                Log.e("MapScreen", "Error closing Places client", e)
            }
        }
    }

    var searchText by remember { mutableStateOf("") }
    var predictions by remember { mutableStateOf(emptyList<AutocompletePrediction>()) }
    val searchTextFlow = remember { MutableStateFlow("") }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(insertState) {
        if (insertState == true) {
            Toast.makeText(context, "Location saved successfully", Toast.LENGTH_SHORT).show()
            navController.popBackStack()
        } else if (insertState == false) {
            Toast.makeText(context, "Error saving location", Toast.LENGTH_SHORT).show()
        }
    }

    fun getCountryFromLatLng(lat: Double, lon: Double): String {
        return try {
            val geocoder = Geocoder(context, Locale.getDefault())
            val addresses = geocoder.getFromLocation(lat, lon, 1)
            addresses?.firstOrNull()?.countryName ?: "Unknown Location"
        } catch (e: Exception) {
            Log.e("MapScreen", "Error getting country: ${e.message}")
            "Unknown Location"
        }
    }

    LaunchedEffect(searchTextFlow) {
        searchTextFlow.debounce(300.milliseconds).collect { query ->
            if (query.isNotEmpty()) {
                try {
                    val response = placesClient.awaitFindAutocompletePredictions {
                        typesFilter = listOf(PlaceTypes.CITIES)
                        this.query = query
                    }
                    predictions = response.autocompletePredictions
                } catch (e: Exception) {
                    Log.e("MapScreen", "Error fetching predictions: ${e.message}")
                }
            }
        }
    }


    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    selectedLocation?.let {
                        homeViewModel?.updateManualCoordinates(it.latitude, it.longitude)
                        mapScreenViewModel.insertFavoriteLocation(
                            lat = it.latitude,
                            lon = it.longitude,
                            name = searchText,
                            context = context
                        )
                    }
                },
                modifier = Modifier.padding(16.dp)
            ) {
                Icon(Icons.Default.Favorite, "Select Location")
            }
        }
    )
    { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            GoogleMap(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                cameraPositionState = cameraPositionState,
                onMapClick = { latLng ->
                    selectedLocation = latLng
                    searchText = getCountryFromLatLng(latLng.latitude, latLng.longitude) // ✅ تحديث `searchText` باسم الدولة
                    cameraPositionState.move(CameraUpdateFactory.newLatLngZoom(latLng, 10f)) // ✅ تحريك الكاميرا إلى الموقع
                }
            ) {
                selectedLocation?.let {
                    Marker(state = MarkerState(it), title = searchText)
                }
            }

            PlacesAutocompleteTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                searchText = searchText,
                predictions = predictions.map { it.toPlaceDetails() },
                onQueryChanged = { query ->
                    searchText = query
                    searchTextFlow.value = query
                },
                onSelected = { autocompletePlace ->
                    predictions = emptyList()
                    searchText = autocompletePlace.primaryText.toString()
                    coroutineScope.launch {
                        try {
                            val request = FetchPlaceRequest.newInstance(
                                autocompletePlace.placeId,
                                listOf(Place.Field.LAT_LNG)
                            )
                            val response = placesClient.fetchPlace(request).await()
                            response.place.latLng?.let { latLng ->
                                selectedLocation = latLng
                                cameraPositionState.move(
                                    CameraUpdateFactory.newLatLngZoom(latLng, 12f)
                                )
                            }
                        } catch (e: Exception) {
                            Log.e("MapScreen", "Error fetching place: ${e.message}")
                        }
                    }
                }
            )
        }
    }
}


data class AutocompletePlace(
    val placeId: String,
    val primaryText: String,
    val secondaryText: String
)

private fun AutocompletePrediction.toPlaceDetails(): com.youssef.weatherforcast.Favourite.AutocompletePlace {
    return AutocompletePlace(
        placeId = this.placeId,
        primaryText = this.getPrimaryText(null).toString(),
        secondaryText = this.getSecondaryText(null).toString()
    )
}