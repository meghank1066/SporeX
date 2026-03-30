package com.example.sporex_app.useraccount

import android.content.Context

object UserSession {
    private const val PREFS = "sporex_user_session"
    private const val KEY_USERNAME = "username"
    private const val KEY_EMAIL = "email"

    fun saveUser(context: Context, username: String?, email: String?) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_USERNAME, username ?: "")
            .putString(KEY_EMAIL, email ?: "")
            .apply()
    }

    fun getUsername(context: Context): String {
        return context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getString(KEY_USERNAME, "You") ?: "You"
    }

    fun getEmail(context: Context): String {
        return context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getString(KEY_EMAIL, "") ?: ""
    }

    fun clear(context: Context) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit()
            .clear()
            .apply()
    }
}