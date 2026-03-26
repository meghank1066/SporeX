package com.example.sporex_app.utils

import android.content.Context

fun setDarkMode(context: Context, enabled: Boolean) {
    val prefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
    prefs.edit().putBoolean("dark_mode", enabled).apply()
}

fun isDarkMode(context: Context): Boolean {
    val prefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
    return prefs.getBoolean("dark_mode", false)
}