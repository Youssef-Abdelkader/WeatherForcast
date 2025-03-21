package com.youssef.weatherforcast.Model


import com.youssef.weatherforcast.Data.RemoteDataSource.RemoteDataSource


class RepoImpl(
    private val remoteDataSource: RemoteDataSource,

    ) : Repo {
    override suspend fun getWeather(
        lat: Double,
        lon: Double,
        units: String,
        language: String
    ): WeatherResponse {
        return remoteDataSource.getWeatherOverNetwork(lat, lon, units, language)
    }

    override suspend fun getForecast(
        lat: Double,
        lon: Double,
        units: String,
        language: String
    ): ForecastResponse {
        return remoteDataSource.getForecastOverNetwork(lat, lon, units, language)
    }

    override suspend fun updateSettings(
        language: String,
        units: String,
        locationMethod: String,
        windSpeedUnit: String
    ) {
        // هنا ممكن تخزن الإعدادات باستخدام DataStore أو SharedPreferences
        println("Settings Updated: Language=$language, Units=$units, Location=$locationMethod, Wind Speed=$windSpeedUnit")
    }

}
