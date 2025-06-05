package com.hritwik.sassyskies.widget

import android.content.Context
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.hritwik.sassyskies.repository.WeatherRepository
import com.hritwik.sassyskies.repositoryImpl.LocationRepositoryImpl
import com.hritwik.sassyskies.service.GeminiService
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.roundToInt
import com.hritwik.sassyskies.model.WidgetWeatherData

@HiltWorker
class ComposeWidgetUpdateWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val weatherRepository: WeatherRepository,
    private val geminiService: GeminiService
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return withContext(Dispatchers.IO) {
            try {
                // Get current location
                val locationRepository = LocationRepositoryImpl(context)
                val locationResult = locationRepository.getCurrentLocation()

                locationResult.fold(
                    onSuccess = { locationData ->
                        // Get weather data
                        when (val weatherResult = weatherRepository.getCurrentWeather(
                            locationData.latitude,
                            locationData.longitude
                        )) {
                            is com.hritwik.sassyskies.model.Result.Success -> {
                                val weather = weatherResult.data

                                // Generate sarcastic message
                                val sarcasticResult = try {
                                    geminiService.generateGlobalSarcasticMessage(
                                        weatherDescription = weather.weather.firstOrNull()?.description ?: "",
                                        temperature = weather.main.temp.roundToInt(),
                                        humidity = weather.main.humidity,
                                        cityName = weather.name,
                                        feelsLike = weather.main.feelsLike.roundToInt()
                                    )
                                } catch (e: Exception) {
                                    kotlin.Result.failure(e)
                                }

                                val sarcasticMessage = sarcasticResult.getOrElse {
                                    generateFallbackMessage(weather.weather.firstOrNull()?.description ?: "")
                                }

                                // Save weather data for widget
                                val widgetData = WidgetWeatherData(
                                    temperature = weather.main.temp.roundToInt(),
                                    location = weather.name,
                                    weatherCondition = weather.weather.firstOrNull()?.main ?: "Clear",
                                    sarcasticMessage = sarcasticMessage
                                )

                                WidgetDataManager.saveWeatherData(context, widgetData)

                                // Update all Compose widgets
                                updateAllComposeWidgets()

                                Result.success()
                            }
                            is com.hritwik.sassyskies.model.Result.Error -> {
                                Result.failure()
                            }
                            is com.hritwik.sassyskies.model.Result.Loading -> {
                                Result.retry()
                            }
                        }
                    },
                    onFailure = {
                        // Use default location if GPS fails
                        getWeatherForDefaultLocation()
                    }
                )
            } catch (_: Exception) {
                Result.failure()
            }
        }
    }

    private suspend fun getWeatherForDefaultLocation(): Result {
        return try {
            when (val weatherResult = weatherRepository.getCurrentWeather(28.4595, 77.0266)) {
                is com.hritwik.sassyskies.model.Result.Success -> {
                    val weather = weatherResult.data

                    val sarcasticMessage = try {
                        geminiService.generateGlobalSarcasticMessage(
                            weatherDescription = weather.weather.firstOrNull()?.description ?: "",
                            temperature = weather.main.temp.roundToInt(),
                            humidity = weather.main.humidity,
                            cityName = weather.name,
                            feelsLike = weather.main.feelsLike.roundToInt()
                        ).getOrElse {
                            generateFallbackMessage(weather.weather.firstOrNull()?.description ?: "")
                        }
                    } catch (_: Exception) {
                        generateFallbackMessage(weather.weather.firstOrNull()?.description ?: "")
                    }

                    val widgetData = WidgetWeatherData(
                        temperature = weather.main.temp.roundToInt(),
                        location = weather.name,
                        weatherCondition = weather.weather.firstOrNull()?.main ?: "Clear",
                        sarcasticMessage = sarcasticMessage
                    )

                    WidgetDataManager.saveWeatherData(context, widgetData)
                    updateAllComposeWidgets()

                    Result.success()
                }
                else -> Result.failure()
            }
        } catch (_: Exception) {
            Result.failure()
        }
    }

    private suspend fun updateAllComposeWidgets() {
        val glanceAppWidgetManager = GlanceAppWidgetManager(context)
        val widget = WeatherWidget()
        val glanceIds = glanceAppWidgetManager.getGlanceIds(widget.javaClass)

        glanceIds.forEach { glanceId ->
            widget.update(context, glanceId)
        }
    }

    private fun generateFallbackMessage(weatherDescription: String): String {
        return when {
            weatherDescription.contains("rain", ignoreCase = true) ->
                "It's raining. Shocking revelation."
            weatherDescription.contains("snow", ignoreCase = true) ->
                "Snow. Because walking was easy."
            weatherDescription.contains("clear", ignoreCase = true) ->
                "Clear skies. Revolutionary."
            weatherDescription.contains("cloud", ignoreCase = true) ->
                "Cloudy with a chance of meh."
            else -> "Weather happened. Congrats."
        }
    }
}