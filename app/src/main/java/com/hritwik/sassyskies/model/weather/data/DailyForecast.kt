package com.hritwik.sassyskies.model.weather.data

data class DailyForecast(
    val date: String,
    val dayOfWeek: String,
    val weatherDescription: String,
    val weatherMain: String,
    val iconCode: String,
    val maxTemp: Int,
    val minTemp: Int,
    val humidity: Int,
    val windSpeed: Double,
    val precipitationProbability: Int,
    val sarcasticMessage: String = ""
)