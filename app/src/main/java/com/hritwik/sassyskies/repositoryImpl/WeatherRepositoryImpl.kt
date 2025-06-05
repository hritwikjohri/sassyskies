package com.hritwik.sassyskies.repositoryImpl

import com.hritwik.sassyskies.service.WeatherApiService
import com.hritwik.sassyskies.model.WeatherResponse
import com.hritwik.sassyskies.repository.WeatherRepository
import com.hritwik.sassyskies.model.Result
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class WeatherRepositoryImpl @Inject constructor(
    private val apiService: WeatherApiService,
    @Named("weather_api_key") private val apiKey: String
) : WeatherRepository {

    override suspend fun getCurrentWeather(
        latitude: Double,
        longitude: Double
    ): Result<WeatherResponse> {
        return try {
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