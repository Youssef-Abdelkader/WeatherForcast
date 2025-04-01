package com.youssef.weatherforcast.Model

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class HomeDataConverters {

    private val gson = Gson()

    // Convert WeatherResponse to JSON String
    @TypeConverter
    fun fromWeatherResponse(weatherResponse: WeatherResponse?): String? {
        return weatherResponse?.let { gson.toJson(it) }
    }

    // Convert JSON String back to WeatherResponse
    @TypeConverter
    fun toWeatherResponse(weatherResponseString: String?): WeatherResponse? {
        return weatherResponseString?.let {
            gson.fromJson(it, WeatherResponse::class.java)
        }
    }

    // Convert ForecastResponse to JSON String
    @TypeConverter
    fun fromForecastResponse(forecastResponse: ForecastResponse?): String? {
        return forecastResponse?.let { gson.toJson(it) }
    }

    // Convert JSON String back to ForecastResponse
    @TypeConverter
    fun toForecastResponse(forecastResponseString: String?): ForecastResponse? {
        return forecastResponseString?.let {
            gson.fromJson(it, ForecastResponse::class.java)
        }
    }
}
