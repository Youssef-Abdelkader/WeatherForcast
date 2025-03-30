package com.youssef.weatherforcast.WeatherAlert

import android.app.*
import android.content.*
import android.os.Build
import android.os.IBinder
import android.provider.Settings
import androidx.core.app.NotificationCompat
import com.youssef.weatherforcast.MainActivity
import com.youssef.weatherforcast.R

class NotificationService : Service() {
    private val notificationManager by lazy {
        getSystemService(NotificationManager::class.java)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val alertId = intent?.getIntExtra("alert_id", 0) ?: 0
        val alertType = AlertType.valueOf(
            intent?.getStringExtra("alert_type") ?: AlertType.NOTIFICATION.name
        )

        createNotificationChannel()
        val notification = buildNotification(alertId, alertType)
        startForeground(alertId, notification)

        return START_NOT_STICKY
    }

    private fun buildNotification(alertId: Int, alertType: AlertType): Notification {
        val stopIntent = Intent(this, StopReceiver::class.java).apply {
            putExtra("alert_id", alertId)
        }
        val stopPendingIntent = PendingIntent.getBroadcast(
            this,
            alertId,
            stopIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Intent عند الضغط على الإشعار لفتح MainActivity
        val contentIntent = PendingIntent.getActivity(
            this,
            alertId,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, "weather_alerts")
            .setContentTitle("Weather Alert")
            .setContentText("Active weather alert!")
            .setSmallIcon(R.drawable.alert)
            .setContentIntent(contentIntent) // لجعل الضغط على الإشعار يفتح MainActivity
            .setSound(when(alertType) {
                AlertType.ALARM_SOUND -> Settings.System.DEFAULT_ALARM_ALERT_URI
                AlertType.NOTIFICATION -> null
                AlertType.ALARM -> Settings.System.DEFAULT_NOTIFICATION_URI
            })
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setAutoCancel(true) // لجعل الإشعار يختفي عند الضغط عليه
            .addAction(R.drawable.stop, "Stop", stopPendingIntent)
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "weather_alerts",
                "Weather Alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Weather alerts channel"
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
class StopReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val alertId = intent.getIntExtra("alert_id", 0)
        val serviceIntent = Intent(context, NotificationService::class.java)
        context.stopService(serviceIntent)

        // Use application context to avoid memory leaks
        WeatherAlertsViewModel(context.applicationContext)
            .cancelAlert(alertId)
    }
}