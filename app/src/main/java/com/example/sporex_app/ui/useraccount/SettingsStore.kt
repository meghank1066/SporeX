package com.example.sporex_app.ui.useraccount

import android.content.Context

object SettingsStore {
    private const val PREFS = "sporex_prefs"
    private const val KEY_DARK = "dark_mode"

    fun getDarkMode(context: Context): Boolean {
        return context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getBoolean(KEY_DARK, false)
    }

    fun setDarkMode(context: Context, enabled: Boolean) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(KEY_DARK, enabled)
            .apply()
    }
}
