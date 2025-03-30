package com.youssef.weatherforcast.WeatherAlert

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import java.text.SimpleDateFormat
import java.util.*

class AlarmScheduler(private val context: Context) {
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    fun schedule(alert: WeatherAlert) {
        val intent = Intent(context, AlertReceiver::class.java).apply {
            putExtra("alert_id", alert.id)
            putExtra("alert_type", alert.type.name)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alert.id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // تحويل startTime إلى توقيت Long
        val triggerTime = convertTimeToMillis(alert.startTime)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (alarmManager.canScheduleExactAlarms()) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
            } else {
                Log.e("AlarmManager", "Exact alarms are not allowed!")
            }
        } else {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
        }
    }

    fun cancel(alertId: Int) {
        val intent = Intent(context, AlertReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alertId,
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        pendingIntent?.let {
            alarmManager.cancel(it)
        }
    }

    private fun convertTimeToMillis(time: String): Long {
        return try {
            val format = SimpleDateFormat("HH:mm", Locale.getDefault())
            val date = format.parse(time) // تحويل النص إلى كائن Date

            val calendar = Calendar.getInstance().apply {
                timeInMillis = System.currentTimeMillis() // ضبط الوقت الحالي
                date?.let {
                    val tempCalendar = Calendar.getInstance().apply { this.time = it }
                    set(Calendar.HOUR_OF_DAY, tempCalendar.get(Calendar.HOUR_OF_DAY))
                    set(Calendar.MINUTE, tempCalendar.get(Calendar.MINUTE))
                    set(Calendar.SECOND, 0) // لجعل الثواني صفرًا
                    set(Calendar.MILLISECOND, 0) // لجعل الميلي ثانية صفرًا
                }
            }

            calendar.timeInMillis // إرجاع الوقت بالميلي ثانية
        } catch (e: Exception) {
            System.currentTimeMillis() // في حالة حدوث خطأ، يتم إرجاع الوقت الحالي
        }
    }


}
