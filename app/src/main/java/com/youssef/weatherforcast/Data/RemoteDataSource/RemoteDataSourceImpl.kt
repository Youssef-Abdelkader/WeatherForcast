package com.youssef.weatherforcast.Data.RemoteDataSource

import com.youssef.weatherforcast.Model.ForecastResponse
import com.youssef.weatherforcast.Model.WeatherResponse


class RemoteDataSourceImpl private constructor(private val service: ApiService):RemoteDataSource {


    companion object{
        private var remoteDataSource:RemoteDataSource? =null
        fun getInstance(service: ApiService):RemoteDataSource
        {
            return remoteDataSource?: synchronized(this){
                val instance = RemoteDataSourceImpl(service)
                remoteDataSource=instance
                instance
            }
        }
    }

    override suspend fun getWeatherOverNetwork(
        lat: Double,
        lon: Double,
        units: String,
        language: String
    ): WeatherResponse {
        return service.getCurrentWeather(lat,lon,units,language)
    }

    override suspend fun getForecastOverNetwork(
        lat: Double,
        lon: Double,
        units: String,
        language: String
    ): ForecastResponse {
        return service.getCurrentForcast(lat,lon,units,language)
    }
}