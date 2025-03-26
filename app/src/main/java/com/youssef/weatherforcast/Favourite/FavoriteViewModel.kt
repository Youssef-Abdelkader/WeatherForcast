package com.youssef.weatherforcast.Favourite

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.youssef.weatherforcast.Model.Repo
import com.youssef.weatherforcast.Model.FavoriteLocation
import com.youssef.weatherforcast.Model.WeatherResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class FavoriteViewModel(private val repo: Repo) : ViewModel() {
    private val _favorites = MutableStateFlow<List<FavoriteLocation>>(emptyList())
    val favorites = _favorites.asStateFlow()

    init {
        getAllFavorites()
    }

    fun getAllFavorites() {
        viewModelScope.launch {
            try {
                repo.getAllFavorites().collectLatest { favoriteList ->
                    _favorites.value = favoriteList
                    Log.d("FavoriteViewModel", "Favorites loaded: $favoriteList")
                }
            } catch (e: Exception) {
                Log.e("FavoriteViewModel", "Error loading favorites: ${e.message}")
            }
        }
    }

    suspend fun getWeather(lat: Double, lon: Double): WeatherResponse? {
        return repo.getWeather(lat, lon, "metric", "en")
    }

    fun addFavorite(location: FavoriteLocation) {
        viewModelScope.launch {
            try {
                repo.insertFavorite(location)
                getAllFavorites()
                Log.d("FavoriteViewModel", "Added to favorites: $location")
            } catch (e: Exception) {
                Log.e("FavoriteViewModel", "Error adding favorite: ${e.message}")
            }
        }
    }

    fun removeFavorite(location: FavoriteLocation) {
        viewModelScope.launch {
            try {
                repo.deleteFavorite(location)
                getAllFavorites()
                Log.d("FavoriteViewModel", "Removed from favorites: $location")
            } catch (e: Exception) {
                Log.e("FavoriteViewModel", "Error removing favorite: ${e.message}")
            }
        }
    }
}

class FavoriteFactory(private val repo: Repo) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FavoriteViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FavoriteViewModel(repo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
