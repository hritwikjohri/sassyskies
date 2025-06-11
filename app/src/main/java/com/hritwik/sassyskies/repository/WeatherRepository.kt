package com.hritwik.sassyskies.repository

import com.hritwik.sassyskies.model.weather.core.WeatherResponse
import com.hritwik.sassyskies.model.utils.Result

interface WeatherRepository {
    suspend fun getCurrentWeather(latitude: Double, longitude: Double): Result<WeatherResponse>
}