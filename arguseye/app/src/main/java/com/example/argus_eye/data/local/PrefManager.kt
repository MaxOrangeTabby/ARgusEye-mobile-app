// File: com/example/argus_eye/data/local/PrefManager.kt
package com.example.argus_eye.data.local

import android.content.Context
import android.content.SharedPreferences

class PrefManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("argus_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_BASE_URL = "base_url"
        // Use 10.0.2.2 to connect to 'localhost' of the host machine from the Android Emulator
        // Use your computer's local IP (e.g. 192.168.x.x) if using a physical device
        private const val DEFAULT_URL = "http://10.0.2.2:5000/"
    }

    fun getBaseUrl(): String = prefs.getString(KEY_BASE_URL, DEFAULT_URL) ?: DEFAULT_URL

    fun setBaseUrl(url: String) {
        // Ensure URL ends with a slash as Retrofit requires it for base URLs
        val formattedUrl = if (url.endsWith("/")) url else "$url/"
        prefs.edit().putString(KEY_BASE_URL, formattedUrl).apply()
    }
}
