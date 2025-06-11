package com.hritwik.sassyskies.model.auth

import com.google.firebase.database.PropertyName

data class User(
    val uid: String = "",
    val email: String = "",

    @get:PropertyName("display_name")
    @set:PropertyName("display_name")
    var displayName: String = "",

    @get:PropertyName("is_verified")
    @set:PropertyName("is_verified")
    var isVerified: Boolean = false,

    @get:PropertyName("created_at")
    @set:PropertyName("created_at")
    var createdAt: Long = System.currentTimeMillis(),

    @get:PropertyName("updated_at")
    @set:PropertyName("updated_at")
    var updatedAt: Long = System.currentTimeMillis(),

    @get:PropertyName("profile_image_url")
    @set:PropertyName("profile_image_url")
    var profileImageUrl: String = "",

    @get:PropertyName("last_login")
    @set:PropertyName("last_login")
    var lastLogin: Long = System.currentTimeMillis(),

    // API Keys - stored as plain text
    @get:PropertyName("weather_api_key")
    @set:PropertyName("weather_api_key")
    var weatherApiKey: String = "",

    @get:PropertyName("gemini_api_key")
    @set:PropertyName("gemini_api_key")
    var geminiApiKey: String = ""
) {
    // No-argument constructor required by Firebase Realtime Database
    constructor() : this(
        uid = "",
        email = "",
        displayName = "",
        isVerified = false,
        createdAt = System.currentTimeMillis(),
        updatedAt = System.currentTimeMillis(),
        profileImageUrl = "",
        lastLogin = System.currentTimeMillis(),
        weatherApiKey = "",
        geminiApiKey = ""
    )

    // Helper function to check if user has all API keys
    fun hasAllApiKeys(): Boolean {
        return weatherApiKey.isNotBlank() && geminiApiKey.isNotBlank()
    }

    // Helper function to get masked API keys for display
    fun getMaskedWeatherApiKey(): String {
        return if (weatherApiKey.isBlank()) "Not set"
        else "${weatherApiKey.take(8)}${"*".repeat(weatherApiKey.length - 12)}${weatherApiKey.takeLast(4)}"
    }

    fun getMaskedGeminiApiKey(): String {
        return if (geminiApiKey.isBlank()) "Not set"
        else "${geminiApiKey.take(8)}${"*".repeat(geminiApiKey.length - 12)}${geminiApiKey.takeLast(4)}"
    }
}