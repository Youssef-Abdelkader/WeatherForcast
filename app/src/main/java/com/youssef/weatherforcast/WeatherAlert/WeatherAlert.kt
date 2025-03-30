package com.youssef.weatherforcast.WeatherAlert

data class WeatherAlert(
    val id: Int = 0,
    val type: AlertType,
    val message: String,
    val startTime: String,
    val endTime: String
)

enum class AlertType {
    ALARM, NOTIFICATION, ALARM_SOUND
}
