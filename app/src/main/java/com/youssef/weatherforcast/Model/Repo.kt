// Repo.kt
package com.youssef.weatherforcast.Model

import com.youssef.weatherforcast.Model.FavoriteLocation
import com.youssef.weatherforcast.WeatherAlert.WeatherAlert
import kotlinx.coroutines.flow.Flow

interface Repo {
    // Weather Data Operations
    suspend fun getWeather(lat: Double, lon: Double, units: String, language: String): WeatherResponse
    suspend fun getForecast(lat: Double, lon: Double, units: String, language: String): ForecastResponse

    // Settings Operations
    fun saveSetting(key: String, value: String)
    fun getSetting(key: String, defaultValue: String): String

    // Favorite Locations Operations
    suspend fun insertFavorite(favoriteLocation: FavoriteLocation)
    suspend fun deleteFavorite(favoriteLocation: FavoriteLocation)
    fun getAllFavorites(): Flow<List<FavoriteLocation>>

    // Weather Alerts Operations
    suspend fun insertAlert(weatherAlert: WeatherAlert)
    suspend fun deleteAlert(weatherAlert: WeatherAlert)
    fun getAllAlerts(): Flow<List<WeatherAlert>>
}