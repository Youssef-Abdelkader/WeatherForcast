package com.youssef.weatherforcast.WeatherAlert

data class WeatherAlert(
    val id: Int = 0,
    val durationMillis: Long,
    val alertType: AlertType,
    val triggerTime: Long = System.currentTimeMillis() + durationMillis
)

enum class AlertType { NOTIFICATION, ALARM_SOUND }