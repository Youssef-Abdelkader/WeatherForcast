package com.youssef.weatherforcast.Model

interface Repo {
    suspend fun getWeather( lat: Double,
                            lon: Double,
                            units:String,
                            language:String
    ) : WeatherResponse
    suspend fun getForecast( lat: Double,
                             lon: Double,
                             units:String,
                             language:String
    ) : ForecastResponse
}