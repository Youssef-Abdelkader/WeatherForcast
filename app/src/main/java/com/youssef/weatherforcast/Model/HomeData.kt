package com.youssef.weatherforcast.Model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Home_Data")
data class HomeData(
    @PrimaryKey
    val id: Int = 0,
    val weather: WeatherResponse?,
    val forecast: ForecastResponse?,
//    val language: String,
//    val units: String,
//    val location: String,
)
