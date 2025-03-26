package com.youssef.weatherforcast.Favourite


import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.youssef.weatherforcast.Model.FavoriteLocation
import com.youssef.weatherforcast.Model.Repo
import com.youssef.weatherforcast.Model.WeatherResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MapScreenViewModel(private val repository: Repo) : ViewModel() {

    private var _insertState = MutableStateFlow<Boolean?>(null)
    val insertState = _insertState.asStateFlow()


    fun insertFavoriteLocation(lat: Double, lon: Double, name: String, ) {
        viewModelScope.launch {
            try {
                val favoriteLocation = FavoriteLocation(
                    latitude = lat,
                    longitude = lon,
                    locationName = name,
                )
                repository.insertFavorite(favoriteLocation)
                _insertState.value = true
                Log.d("MapScreenViewModel", "Location saved: $favoriteLocation")
            } catch (e: Exception) {
                _insertState.value = false

                Log.e("MapScreenViewModel", "Error saving location: ${e.message}")
            }
        }
    }
}

class MapScreenViewModelFactory(private val repo: Repo) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MapScreenViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MapScreenViewModel(repo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
