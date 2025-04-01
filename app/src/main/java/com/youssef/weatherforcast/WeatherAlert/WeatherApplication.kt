package com.youssef.weatherforcast.WeatherAlert

import android.app.Application
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.media.RingtoneManager
import android.os.Build
import com.youssef.weatherforcast.Data.LocalDataSource.AppDatabase
import com.youssef.weatherforcast.Data.LocalDataSource.LocalDataSourceImpl
import com.youssef.weatherforcast.Data.RemoteDataSource.RemoteDataSourceImpl
import com.youssef.weatherforcast.Data.RemoteDataSource.RetrofitHelper
import com.youssef.weatherforcast.Model.Repo
import com.youssef.weatherforcast.Model.RepoImpl
import com.youssef.weatherforcast.Setting.SettingsPreferences

class WeatherApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "alarm_channel",
                "Weather Alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Weather alert notifications"
                enableLights(true)
                enableVibration(true)
                vibrationPattern = longArrayOf(500, 500, 500, 500)
                setSound(
                    RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM),
                    Notification.AUDIO_ATTRIBUTES_DEFAULT
                )
            }

            getSystemService(NotificationManager::class.java)?.createNotificationChannel(channel)
        }
    }

    val repo: Repo by lazy {
        val database = AppDatabase.getInstance(applicationContext)
        val favoriteDao = database.favoriteDao()

        RepoImpl(
            remoteDataSource = RemoteDataSourceImpl.getInstance(service = RetrofitHelper.service),
            settingsPreferences = SettingsPreferences(context = applicationContext),
            localDataSource = favoriteDao // ✅ Fixed
        )
    }
}
