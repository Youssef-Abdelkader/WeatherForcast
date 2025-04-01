package com.youssef.weatherforcast.WeatherAlert

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import androidx.core.content.ContextCompat
import com.youssef.weatherforcast.Model.AlertType

class AlertReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Log.d("AlertReceiver", "Alarm triggered! ${intent.extras}")
        val alertId = intent.getIntExtra("alert_id", 0)
        val alertType = AlertType.valueOf(
            intent.getStringExtra("alert_type") ?: AlertType.NOTIFICATION.name
        )
        val message = intent.getStringExtra("alert_message") ?: "Weather alert!"

        // Start foreground service
        val serviceIntent = Intent(context, AlertService::class.java).apply {
            putExtra("alert_id", alertId)
            putExtra("alert_type", alertType.name)
            putExtra("alert_message", message)
        }
        ContextCompat.startForegroundService(context, serviceIntent)

        if (alertType == AlertType.ALARM_SOUND) {
            Log.d("AlertReceiver", "Starting vibration")

            val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createWaveform(longArrayOf(500, 1000), 0))
            } else {
                vibrator.vibrate(500)
            }
        }
    }
}