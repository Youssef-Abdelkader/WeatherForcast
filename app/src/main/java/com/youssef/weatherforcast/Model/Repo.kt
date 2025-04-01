package com.youssef.weatherforcast.Model

import kotlinx.coroutines.flow.Flow
import java.util.Locale

interface Repo {
    // Weather Data Operations
   suspend fun getWeather(lat: Double, lon: Double, units: String, language: String): Flow<WeatherResponse>
   suspend fun getForecast(lat: Double, lon: Double, units: String, language: String): Flow<ForecastResponse>

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

    suspend fun insertHomeData(homeData: HomeData)
    fun getHomeDate(): Flow<HomeData?>

 fun getLocalizedUnit(unit: String): String
 fun formatNumber(value: Double): String
 fun getAppLocale(): Locale
}
