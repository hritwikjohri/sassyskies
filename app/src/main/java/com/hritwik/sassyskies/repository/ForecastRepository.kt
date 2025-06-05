package com.hritwik.sassyskies.repository

import com.hritwik.sassyskies.model.ForecastResponse
import com.hritwik.sassyskies.model.Result

interface ForecastRepository {
    suspend fun getForecast(latitude: Double, longitude: Double): Result<ForecastResponse>
}