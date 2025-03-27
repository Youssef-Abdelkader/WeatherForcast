package com.youssef.weatherforcast.Favourite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.youssef.weatherforcast.Model.Repo
import com.youssef.weatherforcast.Model.FavoriteLocation
import com.youssef.weatherforcast.Model.WeatherResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FavoriteViewModel(private val repo: Repo) : ViewModel() {
    // Changed to StateFlow with initial value
    private val _favorites = MutableStateFlow<List<FavoriteLocation>>(emptyList())
    val favorites: StateFlow<List<FavoriteLocation>> = _favorites

    // Added error handling state
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    init {
        loadFavorites()
    }

    // Modified to use stateIn for better Flow handling
    private fun loadFavorites() {
        viewModelScope.launch {
            repo.getAllFavorites()
                .catch { e ->
                    _errorMessage.value = "Error loading favorites: ${e.message}"
                }
                .stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(5000),
                    initialValue = emptyList()
                )
                .collect { list ->
                    _favorites.value = list
                }
        }
    }

    // Added null safety and error handling
    suspend fun getWeatherSafely(lat: Double, lon: Double): WeatherResponse? {
        return try {
            repo.getWeather(lat, lon, "metric", "en")
        } catch (e: Exception) {
            _errorMessage.value = "Failed to get weather: ${e.message}"
            null
        }
    }

    // Modified to handle possible null values
    fun removeFavorite(location: FavoriteLocation) {
        viewModelScope.launch {
            try {
                repo.deleteFavorite(location)
            } catch (e: Exception) {
                _errorMessage.value = "Delete failed: ${e.message}"
            }
        }
    }
}

class FavoriteFactory(private val repo: Repo) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // Added null check and type safety
        if (modelClass.isAssignableFrom(FavoriteViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FavoriteViewModel(repo) as T
        }
        throw IllegalArgumentException("Invalid ViewModel class")
    }
}