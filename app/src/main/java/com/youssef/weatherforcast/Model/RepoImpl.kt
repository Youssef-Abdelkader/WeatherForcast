// RepoImpl.kt
package com.youssef.weatherforcast.Model

import com.youssef.weatherforcast.Data.LocalDataSource.FavoriteDao
import com.youssef.weatherforcast.Data.RemoteDataSource.RemoteDataSource
import com.youssef.weatherforcast.Setting.SettingsPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import java.text.NumberFormat
import java.util.Locale

class RepoImpl(
    private val remoteDataSource: RemoteDataSource,
    private val settingsPreferences: SettingsPreferences,
    private val localDataSource: FavoriteDao
) : Repo {

    override suspend fun getWeather(lat: Double, lon: Double, units: String, language: String): Flow<WeatherResponse> {
        val apiLangCode = getLanguageCode(language)
      return flowOf(remoteDataSource.getWeatherOverNetwork(lat, lon, units, apiLangCode))
    }

    override suspend fun getForecast(lat: Double, lon: Double, units: String, language: String): Flow<ForecastResponse >{
        val apiLangCode = getLanguageCode(language)
return flowOf(remoteDataSource.getForecastOverNetwork(lat, lon, units, apiLangCode))
    }

    override fun saveSetting(key: String, value: String) {
        settingsPreferences.saveSetting(key, value)
    }

    override fun getSetting(key: String, defaultValue: String): String {
        return settingsPreferences.getSetting(key, defaultValue)
    }

    override suspend fun insertFavorite(favoriteLocation: FavoriteLocation) {
        localDataSource.insertFavorite(favoriteLocation)
    }

    override suspend fun deleteFavorite(favoriteLocation: FavoriteLocation) {
        localDataSource.deleteFavorite(favoriteLocation)
    }

    override fun getAllFavorites(): Flow<List<FavoriteLocation>> {
        return localDataSource.getAllFavorites()
    }

    override suspend fun insertAlert(weatherAlert: WeatherAlert) {
        localDataSource.insertAlert(weatherAlert)
    }

    override suspend fun deleteAlert(weatherAlert: WeatherAlert) {
        localDataSource.deleteAlert(weatherAlert)
    }

    override fun getAllAlerts(): Flow<List<WeatherAlert>> {
        return localDataSource.getAllAlerts()
    }

    override suspend fun insertHomeData(homeData: HomeData) {
        return localDataSource.insertHomeData(homeData)
    }

    override fun getHomeDate(): Flow<HomeData?> {
    return localDataSource.getHomeData()
    }
    // endregion

    fun getLanguageCode(language: String): String {
        return when (language) {
            "Arabic" -> "ar"
            "English" -> "en"
            else -> "en"
        }
    }

    override fun getAppLocale(): Locale {
        return when (getSetting("language", "English")) {
            "Arabic" -> Locale("ar")
            else -> Locale.ENGLISH
        }
    }

    override fun formatNumber(value: Double): String {
        val locale = getAppLocale()
        return NumberFormat.getInstance(locale).format(value)
    }


    override fun getLocalizedUnit(unit: String): String {
        val language = getSetting("language", "English")
        return when (unit) {
            "C" -> getUnitCode(language, "C")
            "F" -> getUnitCode(language, "F")
            "K" -> getUnitCode(language, "K")
            "Meter/sec" -> if (language == "Arabic") "م/ث" else "m/s"
            "Mile/hour" -> if (language == "Arabic") "ميل/س" else "mph"
            else -> unit
        }
    }
}

    fun getUnitCode(language: String, unit: String): String {
        val unitMap = mapOf(
            "C" to mapOf("Arabic" to "س", "English" to "C"),
            "F" to mapOf("Arabic" to "ف", "English" to "F"),
            "K" to mapOf("Arabic" to "ك", "English" to "K")
        )
        return unitMap[unit]?.get(language) ?: unit
    }
