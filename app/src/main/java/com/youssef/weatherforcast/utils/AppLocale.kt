package com.youssef.weatherforcast.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import org.intellij.lang.annotations.Language
import java.util.Locale

fun restartActivity(context: Context) {
    val intent = (context as? Activity)?.intent
    intent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
    context.startActivity(intent)
    (context as? Activity)?.finish()
}
fun String.toArabicNumbers(): String {
    val arabicDigits = arrayOf('٠', '١', '٢', '٣', '٤', '٥', '٦', '٧', '٨', '٩')
    return this.map { if (it.isDigit()) arabicDigits[it.digitToInt()] else it }.joinToString("")
}

fun String.formatBasedOnLanguage(): String {
    return if (Locale.getDefault().language == "ar") this.toArabicNumbers() else this
}