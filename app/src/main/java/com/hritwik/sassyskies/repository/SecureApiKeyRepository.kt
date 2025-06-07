package com.hritwik.sassyskies.repository

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SecureApiKeyRepository @Inject constructor(
    private val context: Context
) {

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val encryptedPrefs = EncryptedSharedPreferences.create(
        context,
        "secure_api_keys",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun saveWeatherApiKey(apiKey: String) {
        encryptedPrefs.edit().putString("weather_api_key", apiKey).apply()
    }

    fun getWeatherApiKey(): String? {
        return encryptedPrefs.getString("weather_api_key", null)
    }

    fun saveGeminiApiKey(apiKey: String) {
        encryptedPrefs.edit().putString("gemini_api_key", apiKey).apply()
    }

    fun getGeminiApiKey(): String? {
        return encryptedPrefs.getString("gemini_api_key", null)
    }

    fun clearApiKeys() {
        encryptedPrefs.edit().clear().apply()
    }
}
