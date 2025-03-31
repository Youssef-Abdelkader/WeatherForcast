package com.youssef.weatherforcast.WeatherAlert

import android.app.Notification
import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.youssef.weatherforcast.R

class AlertService : Service() {
    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(1, createNotification())
        return START_NOT_STICKY
    }

    private fun createNotification(): Notification {
        return NotificationCompat.Builder(this, "alarm_channel")
            .setContentTitle("Weather Alert Service")
            .setContentText("Monitoring weather alerts")
            .setSmallIcon(R.drawable.alert)
            .build()
    }
}