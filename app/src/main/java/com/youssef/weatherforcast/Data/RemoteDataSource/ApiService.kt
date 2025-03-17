package com.youssef.weatherforcast.Data.RemoteDataSource

import com.youssef.weatherforcast.Model.ForecastResponse
import com.youssef.weatherforcast.Model.WeatherResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("weather")
    suspend fun getCurrentWeather(@Query("lat") lat: Double,
                                  @Query("lon") lon: Double,
                                  @Query("units") units: String,
                                  @Query("lang") language: String,
                                  @Query("appid") apiKey: String=Constants.API_KEY
                                  )
            : WeatherResponse

    @GET("forecast")
    suspend fun getCurrentForcast(@Query("lat") lat: Double,
                                  @Query("lon") lon: Double,
                                  @Query("units") units: String,
                                  @Query("lang") language: String,
                                  @Query("appid") apiKey: String=Constants.API_KEY
    )
            : ForecastResponse
}
