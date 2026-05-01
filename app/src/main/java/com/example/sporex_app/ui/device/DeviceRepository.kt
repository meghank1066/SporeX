package com.example.sporex_app.ui.device


import android.content.Context

class DeviceRepository(context: Context) {

    private val prefs = context.getSharedPreferences("device_prefs", Context.MODE_PRIVATE)

    fun getDeviceName(): String =
        prefs.getString("device_name", "AIREX 400") ?: "AIREX 400"

    fun setDeviceName(name: String) {
        prefs.edit().putString("device_name", name).apply()
    }

    fun setDeviceId(id: String) {
        prefs.edit().putString("device_id", id).apply()
    }

    fun getDeviceId(): String? =
        prefs.getString("device_id", null)

    fun isDevicePaired(): Boolean =
        !getDeviceId().isNullOrBlank()

    fun clearDevice() {
        prefs.edit().remove("device_id").apply()
    }

    fun touchDevice() {
        prefs.edit().putLong("device_refresh", System.currentTimeMillis()).apply()
    }
}
