// LocalDataSource.kt
package com.youssef.weatherforcast.Data.LocalDataSource

import com.youssef.weatherforcast.Model.FavoriteLocation
import com.youssef.weatherforcast.WeatherAlert.WeatherAlert
import kotlinx.coroutines.flow.Flow

interface LocalDataSource {
    suspend fun insertFavorite(favoriteLocation: FavoriteLocation)
    suspend fun deleteFavorite(favoriteLocation: FavoriteLocation)
    fun getAllFavorites(): Flow<List<FavoriteLocation>>

    suspend fun insertAlert(weatherAlert: WeatherAlert)
    suspend fun deleteAlert(weatherAlert: WeatherAlert)
    fun getAllAlerts(): Flow<List<WeatherAlert>>
}