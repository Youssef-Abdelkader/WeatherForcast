package com.youssef.weatherforcast.Model

import com.youssef.weatherforcast.R

data class WeatherResponse(
    val base: String,
    val clouds: Clouds,
    val cod: Int,
    val coord: Coord,
    val dt: Int,
    val id: Int,
    val main: Main,
    val name: String,
    val sys: Sys,
    val timezone: Int,
    val visibility: Int,
    val weather: List<Weather>,
    val wind: Wind
) {


    data class Clouds(
        val all: Int
    )

    data class Coord(
        val lat: Double,
        val lon: Double
    )

    data class Main(
        val feels_like: Double,
        val grnd_level: Int,
        val humidity: Int,
        val pressure: Int,
        val sea_level: Int,
        val temp: Double,
        val temp_max: Double,
        val temp_min: Double
    )

    data class Sys(
        val country: String,
        val sunrise: Int,
        val sunset: Int
    )

    data class Weather(
        val description: String,
        val icon: String,
        val id: Int,
        val main: String
    )

    data class Wind(
        val deg: Int,
        val gust: Double,
        val speed: Double
    )


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


