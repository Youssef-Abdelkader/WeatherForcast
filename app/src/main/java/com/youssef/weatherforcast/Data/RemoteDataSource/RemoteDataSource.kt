package com.youssef.weatherforcast.Data.RemoteDataSource

import com.youssef.weatherforcast.Model.ForecastResponse
import com.youssef.weatherforcast.Model.WeatherResponse

interface RemoteDataSource {
    suspend fun getWeatherOverNetwork(
        lat: Double,
        lon: Double,
        units:String,
        language:String
    ): WeatherResponse
    suspend fun getForecastOverNetwork(
        lat: Double,
        lon: Double,
        units:String,
        language:String):ForecastResponse
}