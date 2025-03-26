package com.youssef.weatherforcast.Data.RemoteDataSource

import com.youssef.weatherforcast.Model.ForecastResponse
import com.youssef.weatherforcast.Model.GeoCoderResponse
import com.youssef.weatherforcast.Model.WeatherResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {


    @GET("data/2.5/weather")
    suspend fun getCurrentWeather(@Query("lat") lat: Double,
                                  @Query("lon") lon: Double,
                                  @Query("units") units: String,
                                  @Query("lang") language: String,
                                  @Query("appid") apiKey: String=Constants.API_KEY
                                  )
            : WeatherResponse

    @GET("data/2.5/forecast")
    suspend fun getCurrentForecast(@Query("lat") lat: Double,
                                  @Query("lon") lon: Double,
                                  @Query("units") units: String,
                                  @Query("lang") language: String,
                                  @Query("appid") apiKey: String=Constants.API_KEY
    )
            : ForecastResponse

    @GET("geo/1.0/direct?")
    suspend fun getCityName(
        @Query("q") cityName: String,
        @Query("limit") limit: Int = 1,
        @Query("appid") apiKey: String = Constants.API_KEY
    ): List<GeoCoderResponse>
}
