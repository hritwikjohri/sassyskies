package com.hritwik.sassyskies.repositoryImpl

import com.hritwik.sassyskies.service.WeatherApiService
import com.hritwik.sassyskies.model.weather.forecast.ForecastResponse
import com.hritwik.sassyskies.repository.ForecastRepository
import com.hritwik.sassyskies.repository.ApiKeyRepository
import com.hritwik.sassyskies.model.utils.Result
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ForecastRepositoryImpl @Inject constructor(
    private val apiService: WeatherApiService,
    private val apiKeyRepository: ApiKeyRepository,
    private val fallbackApiKey: String
) : ForecastRepository {

    override suspend fun getForecast(
        latitude: Double,
        longitude: Double
    ): Result<ForecastResponse> {
        return try {
            // Get API key from user data or use fallback
            val apiKey = apiKeyRepository.getWeatherApiKey().takeIf { it.isNotBlank() }
                ?: fallbackApiKey

            val response = apiService.getForecast(
                latitude = latitude,
                longitude = longitude,
                apiKey = apiKey,
                units = "metric"
            )

            if (response.isSuccessful) {
                response.body()?.let { forecastResponse ->
                    Result.Success(forecastResponse)
                } ?: Result.Error("Empty response body")
            } else {
                Result.Error("API Error: ${response.code()} - ${response.message()}")
            }
        } catch (e: Exception) {
            Result.Error("Network error: ${e.localizedMessage ?: e.message}")
        }
    }
}