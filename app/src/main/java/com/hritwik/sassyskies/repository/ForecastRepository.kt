package com.hritwik.sassyskies.repository

import com.hritwik.sassyskies.model.weather.forecast.ForecastResponse
import com.hritwik.sassyskies.model.utils.Result

interface ForecastRepository {
    suspend fun getForecast(latitude: Double, longitude: Double): Result<ForecastResponse>
}