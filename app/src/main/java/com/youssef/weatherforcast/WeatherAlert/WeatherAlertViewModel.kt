package com.youssef.weatherforcast.WeatherAlert

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

// 2. ViewModel
class WeatherAlertsViewModel(
    private val context: Context
) : ViewModel() {
    private val alarmScheduler = AlarmScheduler(context)
    var currentAlertId = 0

    fun scheduleAlert(durationMillis: Long, alertType: AlertType) {
        val alert = WeatherAlert(
            id = ++currentAlertId,
            durationMillis = durationMillis,
            alertType = alertType
        )
        alarmScheduler.schedule(alert)
    }

    fun cancelAlert(alertId: Int) {
        alarmScheduler.cancel(alertId)
    }
}

class WeatherAlertsViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WeatherAlertsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return WeatherAlertsViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}