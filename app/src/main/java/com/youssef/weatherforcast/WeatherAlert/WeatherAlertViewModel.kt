package com.youssef.weatherforcast.WeatherAlert

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import java.util.Calendar

class WeatherAlertsViewModel(private val context: Context) : ViewModel() {

    private val alarmManager by lazy {
        context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    }
    fun scheduleAlert(alert: WeatherAlert) {
        val startTimeMillis = parseTimeToMillis(alert.startTime) ?: return
        val endTimeMillis = alert.endTime?.let { parseTimeToMillis(it) }

        val intent = Intent(context, AlertReceiver::class.java).apply {
            putExtra("alert_id", alert.id)
            putExtra("alert_type", alert.type.name)
            putExtra("alert_message", alert.message)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alert.id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        startTimeMillis,
                        pendingIntent
                    )
                } else {
                    Log.e("WeatherAlertsViewModel", "App is not allowed to schedule exact alarms.")
                }
            } else {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    startTimeMillis,
                    pendingIntent
                )
            }
        } catch (e: SecurityException) {
            Log.e("WeatherAlertsViewModel", "SecurityException: ${e.message}")
        }

        endTimeMillis?.let { endTime ->
            val cancelIntent = Intent(context, StopReceiver::class.java).apply {
                putExtra("alert_id", alert.id)
            }
            val cancelPendingIntent = PendingIntent.getBroadcast(
                context,
                alert.id,
                cancelIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                endTime,
                cancelPendingIntent
            )
        }
    }

    private fun parseTimeToMillis(time: String): Long? {
        val parts = time.split(":")
        if (parts.size != 2) return null
        val hour = parts[0].toIntOrNull() ?: return null
        val minute = parts[1].toIntOrNull() ?: return null

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            if (time < String.format("%02d:%02d", get(Calendar.HOUR_OF_DAY), get(Calendar.MINUTE))) {
                add(Calendar.DAY_OF_MONTH, 1) // تأجيل التنبيه لليوم التالي إذا كان الوقت قد مضى اليوم
            }
        }
        return calendar.timeInMillis
    }


    fun cancelAlert(alertId: Int) {
        val intent = Intent(context, AlertReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alertId,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
        context.stopService(Intent(context, NotificationService::class.java))
    }
}

class WeatherAlertsViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WeatherAlertsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return WeatherAlertsViewModel(context.applicationContext) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
