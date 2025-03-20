package com.youssef.weatherforcast.Model

import com.youssef.weatherforcast.R

object WeatherUtils {
    fun weatherIconResourceId(iconCode: String): Int {
        return when (iconCode) {
            "01d" -> R.drawable.day_clear
            "01n" -> R.drawable.night_clear
            "02d" -> R.drawable.day_partial_cloud
            "02n" -> R.drawable.night_partial_cloud
            "03d" -> R.drawable.cloudy
            "03n" -> R.drawable.night_partial_cloud
            "04d", "04n" -> R.drawable.overcast
            "09d", "09n" -> R.drawable.rain
            "10d" -> R.drawable.day_rain
            "10n" -> R.drawable.night_rain
            "11d", "11n" -> R.drawable.rain_thunder
            "13d", "13n" -> R.drawable.snow
            "50d", "50n" -> R.drawable.fog
            else -> R.drawable.day_clear
        }
    }
}