package com.hritwik.sassyskies.widget

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.hritwik.sassyskies.model.WidgetWeatherData
import androidx.core.content.edit

object WidgetDataManager {
    private const val PREFS_NAME = "widget_weather_prefs"
    private const val KEY_WEATHER_DATA = "weather_data"
    private const val CACHE_DURATION = 1 * 60 * 1000L // 30 minutes in milliseconds

    /**
     * Get SharedPreferences instance
     */
    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    /**
     * Save weather data to cache
     */
    fun saveWeatherData(context: Context, weatherData: WidgetWeatherData) {
        try {
            val prefs = getPrefs(context)
            val json = Gson().toJson(weatherData)
            prefs.edit { putString(KEY_WEATHER_DATA, json) }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Get cached weather data if still valid
     */
    fun getCachedWeatherData(context: Context): WidgetWeatherData? {
        return try {
            val prefs = getPrefs(context)
            val json = prefs.getString(KEY_WEATHER_DATA, null) ?: return null

            val type = object : TypeToken<WidgetWeatherData>() {}.type
            val weatherData = Gson().fromJson<WidgetWeatherData>(json, type)

            // Check if cache is still valid (within 30 minutes)
            if (System.currentTimeMillis() - weatherData.lastUpdated < CACHE_DURATION) {
                weatherData
            } else {
                null // Cache expired
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Clear all cached data
     */
    fun clearCache(context: Context) {
        try {
            val prefs = getPrefs(context)
            prefs.edit { remove(KEY_WEATHER_DATA) }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Check if cached data is still valid
     */
    fun isCacheValid(context: Context): Boolean {
        return getCachedWeatherData(context) != null
    }

    /**
     * Get cache age in minutes
     */
    fun getCacheAgeMinutes(context: Context): Long {
        return try {
            val prefs = getPrefs(context)
            val json = prefs.getString(KEY_WEATHER_DATA, null) ?: return -1

            val type = object : TypeToken<WidgetWeatherData>() {}.type
            val weatherData = Gson().fromJson<WidgetWeatherData>(json, type)

            (System.currentTimeMillis() - weatherData.lastUpdated) / (1000 * 60)
        } catch (e: Exception) {
            e.printStackTrace()
            -1
        }
    }
}