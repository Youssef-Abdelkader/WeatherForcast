package com.youssef.weatherforcast.WeatherAlert

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.youssef.weatherforcast.R

class MyAlarm : BroadcastReceiver() {


        override fun onReceive(context: Context, intent: Intent) {
            val temperature = intent.getStringExtra("TEMP_VALUE") ?: "N/A"
            val message = "Current temperature: $temperature"
            showNotification(context, "Weather Alert", message)
        }
    }

    private fun showNotification(context: Context, title: String, desc: String) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "alarm_channel"
        val channelName = "Alarm Notifications"

        // Set the default alarm sound
        val soundUri: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                setSound(soundUri, null)
                enableVibration(true)
            }
            manager.createNotificationChannel(channel)
        }

        val builder = NotificationCompat.Builder(context, channelId)
            .setContentTitle(title)
            .setContentText(desc)  // Shows temperature message
            .setSmallIcon(R.drawable.alert)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setSound(soundUri)

        manager.notify(1, builder.build())
    }

