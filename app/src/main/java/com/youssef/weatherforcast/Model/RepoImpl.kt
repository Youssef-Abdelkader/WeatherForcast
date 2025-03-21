package com.youssef.weatherforcast.Model

import com.youssef.weatherforcast.Data.RemoteDataSource.RemoteDataSource
import com.youssef.weatherforcast.Setting.SettingsPreferences

class RepoImpl(
    private val remoteDataSource: RemoteDataSource,
    private val settingsPreferences: SettingsPreferences
) : Repo {

    private fun getLanguageCode(language: String): String {
        return when (language) {
            "Arabic" -> "ar"
            "English" -> "en"
            else -> "en" // Default to English
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
}
