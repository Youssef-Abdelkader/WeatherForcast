package com.youssef.weatherforcast.Setting

import android.annotation.SuppressLint
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.youssef.weatherforcast.MainActivity

import java.util.Locale
class LanguageChangeReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_LOCALE_CHANGED) {
            val settingsPrefs = SettingsPreferences(context)
            if (settingsPrefs.getSetting("language", "Default") == "Default") {
                context.restartActivity()
            }
        }
    }

    private fun Context.restartActivity() {
        val intent = Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            putExtra("RESTARTED_DUE_TO_LANG_CHANGE", true)
        }
        startActivity(intent)
        if (this is Activity) finish()
    }
}