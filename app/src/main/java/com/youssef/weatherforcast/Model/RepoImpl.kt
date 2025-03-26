package com.youssef.weatherforcast.Model

import com.youssef.weatherforcast.Data.LocalDataSource.FavoriteDao
import com.youssef.weatherforcast.Data.RemoteDataSource.RemoteDataSource
import com.youssef.weatherforcast.Model.FavoriteLocation
import com.youssef.weatherforcast.Setting.SettingsPreferences
import kotlinx.coroutines.flow.Flow

class RepoImpl(
    private val remoteDataSource: RemoteDataSource,
    private val settingsPreferences: SettingsPreferences,
    private val favoriteDao: FavoriteDao // إضافة DAO للتعامل مع قاعدة البيانات
) : Repo {

    private fun getLanguageCode(language: String): String {
        return when (language) {
            "Arabic" -> "ar"
            "English" -> "en"
            else -> "en"
        }
    }

    override suspend fun getWeather(lat: Double, lon: Double, units: String, language: String): WeatherResponse {
        val apiLangCode = getLanguageCode(language)
        return remoteDataSource.getWeatherOverNetwork(lat, lon, units, apiLangCode)
    }

    override suspend fun getForecast(lat: Double, lon: Double, units: String, language: String): ForecastResponse {
        val apiLangCode = getLanguageCode(language)
        return remoteDataSource.getForecastOverNetwork(lat, lon, units, apiLangCode)
    }

    override fun saveSetting(key: String, value: String) {
        settingsPreferences.saveSetting(key, value)
    }

    override fun getSetting(key: String, defaultValue: String): String {
        return settingsPreferences.getSetting(key, defaultValue)
    }

    override suspend fun insertFavorite(favoriteLocation: FavoriteLocation) {
        favoriteDao.insertFavorite(favoriteLocation)
    }

    override suspend fun deleteFavorite(favoriteLocation: FavoriteLocation) {
        favoriteDao.deleteFavorite(favoriteLocation)
    }

    override fun getAllFavorites(): Flow<List<FavoriteLocation>> {
        return favoriteDao.getAllFavorites()
    }
}
