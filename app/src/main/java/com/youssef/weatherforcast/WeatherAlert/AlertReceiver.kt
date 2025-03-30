package com.youssef.weatherforcast.WeatherAlert

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat

// 4. Broadcast Receiver
class AlertReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val alertId = intent.getIntExtra("alert_id", 0)
        val alertType = AlertType.valueOf(
            intent.getStringExtra("alert_type") ?: AlertType.NOTIFICATION.name
        )

        val serviceIntent = Intent(context, NotificationService::class.java).apply {
            putExtra("alert_id", alertId)
            putExtra("alert_type", alertType.name)
        }
        ContextCompat.startForegroundService(context, serviceIntent)
    }
}