package com.hritwik.sassyskies.model.weather.data

data class WidgetWeatherData(
    val temperature: Int,
    val location: String,
    val weatherCondition: String,
    val sarcasticMessage: String,
    val lastUpdated: Long = System.currentTimeMillis()
)