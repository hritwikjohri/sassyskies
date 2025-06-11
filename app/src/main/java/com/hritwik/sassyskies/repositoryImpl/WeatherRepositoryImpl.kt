package com.hritwik.sassyskies.repositoryImpl

import com.hritwik.sassyskies.service.WeatherApiService
import com.hritwik.sassyskies.repository.WeatherRepository
import com.hritwik.sassyskies.repository.ApiKeyRepository
import com.hritwik.sassyskies.model.weather.core.WeatherResponse
import com.hritwik.sassyskies.model.utils.Result
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeatherRepositoryImpl @Inject constructor(
    private val apiService: WeatherApiService,
    private val apiKeyRepository: ApiKeyRepository,
    private val fallbackApiKey: String
) : WeatherRepository {

    override suspend fun getCurrentWeather(
        latitude: Double,
        longitude: Double
    ): Result<WeatherResponse> {
        return try {
            // Get API key from user data or use fallback
            val apiKey = apiKeyRepository.getWeatherApiKey().takeIf { it.isNotBlank() }
                ?: fallbackApiKey

            val response = apiService.getCurrentWeather(
                latitude = latitude,
                longitude = longitude,
                apiKey = apiKey,
                units = "metric"
            )

            if (response.isSuccessful) {
                response.body()?.let { weatherResponse ->
                    Result.Success(weatherResponse)
                } ?: Result.Error("Empty response body")
            } else {
                Result.Error("API Error: ${response.code()} - ${response.message()}")
            }
        } catch (e: Exception) {
            Result.Error("Network error: ${e.localizedMessage ?: e.message}")
        }
    }
}