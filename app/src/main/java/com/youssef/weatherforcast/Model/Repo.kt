package com.youssef.weatherforcast.Model

import com.youssef.weatherforcast.Model.FavoriteLocation
import kotlinx.coroutines.flow.Flow

interface Repo {
    suspend fun getWeather(lat: Double, lon: Double, units: String, language: String): WeatherResponse
    suspend fun getForecast(lat: Double, lon: Double, units: String, language: String): ForecastResponse

    fun saveSetting(key: String, value: String)
    fun getSetting(key: String, defaultValue: String): String

    // التعامل مع قاعدة البيانات المحلية
    suspend fun insertFavorite(favoriteLocation: FavoriteLocation)
    suspend fun deleteFavorite(favoriteLocation: FavoriteLocation)
    fun getAllFavorites(): Flow<List<FavoriteLocation>>
}
