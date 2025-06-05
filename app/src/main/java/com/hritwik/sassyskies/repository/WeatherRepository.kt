package com.hritwik.sassyskies.repository

import com.hritwik.sassyskies.model.WeatherResponse
import com.hritwik.sassyskies.model.Result

interface WeatherRepository {
    suspend fun getCurrentWeather(latitude: Double, longitude: Double): Result<WeatherResponse>
}