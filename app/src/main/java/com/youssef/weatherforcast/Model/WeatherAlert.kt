// File name: WeatherAlert.kt
package com.youssef.weatherforcast.Model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weather_alerts")
data class WeatherAlert(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val type: AlertType,
    val message: String,
    val timestamp: Long,
    val startTime: String,
    val endTime: String
)

enum class AlertType {
    ALARM, NOTIFICATION, ALARM_SOUND
}