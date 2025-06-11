package com.hritwik.sassyskies.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hritwik.sassyskies.model.weather.data.DailyForecast
import com.hritwik.sassyskies.model.weather.forecast.ForecastResponse
import com.hritwik.sassyskies.model.uistate.ForecastUiState
import com.hritwik.sassyskies.model.utils.MemeVersion
import com.hritwik.sassyskies.model.utils.Result
import com.hritwik.sassyskies.repository.ForecastRepository
import com.hritwik.sassyskies.service.GeminiService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import kotlin.math.roundToInt

@HiltViewModel
class ForecastViewModel @Inject constructor(
    private val forecastRepository: ForecastRepository,
    private val geminiService: GeminiService
) : ViewModel() {

    private val _uiState = MutableStateFlow(ForecastUiState())
    val uiState: StateFlow<ForecastUiState> = _uiState.asStateFlow()

    fun getForecast(latitude: Double, longitude: Double, memeVersion: MemeVersion = MemeVersion.GLOBAL) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = null,
                selectedMemeVersion = memeVersion
            )

            when (val result = forecastRepository.getForecast(latitude, longitude)) {
                is Result.Success -> {
                    val dailyForecasts = processForecastData(result.data)
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        dailyForecasts = dailyForecasts,
                        error = null
                    )

                    // Generate sarcastic messages for each day
                    generateSarcasticMessages(dailyForecasts, memeVersion)
                }
                is Result.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = result.message
                    )
                }
                is Result.Loading -> {
                    _uiState.value = _uiState.value.copy(isLoading = true)
                }
            }
        }
    }

    fun updateMemeVersion(memeVersion: MemeVersion) {
        _uiState.value = _uiState.value.copy(selectedMemeVersion = memeVersion)
        val currentForecasts = _uiState.value.dailyForecasts
        if (currentForecasts.isNotEmpty()) {
            generateSarcasticMessages(currentForecasts, memeVersion)
        }
    }

    private fun processForecastData(forecastResponse: ForecastResponse): List<DailyForecast> {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val dayFormat = SimpleDateFormat("EEEE", Locale.getDefault())
        val displayDateFormat = SimpleDateFormat("MMM dd", Locale.getDefault())

        // Group forecast items by date and take the next 7 days
        val groupedByDate = forecastResponse.list
            .groupBy { item ->
                dateFormat.format(Date(item.dt * 1000))
            }
            .toList()
            .take(7)

        return groupedByDate.map { (dateString, items) ->
            // Find midday forecast for most accurate daily weather
            val middayForecast = items.minByOrNull { item ->
                val hour = SimpleDateFormat("HH", Locale.getDefault()).format(Date(item.dt * 1000)).toInt()
                kotlin.math.abs(hour - 12) // Find closest to noon
            } ?: items.first()

            val date = Date(middayForecast.dt * 1000)
            val maxTemp = items.maxOfOrNull { it.main.tempMax }?.roundToInt() ?: middayForecast.main.tempMax.roundToInt()
            val minTemp = items.minOfOrNull { it.main.tempMin }?.roundToInt() ?: middayForecast.main.tempMin.roundToInt()
            val avgHumidity = items.map { it.main.humidity }.average().roundToInt()
            val avgWindSpeed = items.map { it.wind.speed }.average()
            val maxPrecipitation = items.maxOfOrNull { it.pop }?.times(100)?.roundToInt() ?: 0

            DailyForecast(
                date = displayDateFormat.format(date),
                dayOfWeek = dayFormat.format(date),
                weatherDescription = middayForecast.weather.firstOrNull()?.description ?: "Unknown",
                weatherMain = middayForecast.weather.firstOrNull()?.main ?: "Unknown",
                iconCode = middayForecast.weather.firstOrNull()?.icon ?: "01d",
                maxTemp = maxTemp,
                minTemp = minTemp,
                humidity = avgHumidity,
                windSpeed = avgWindSpeed,
                precipitationProbability = maxPrecipitation
            )
        }
    }

    private fun generateSarcasticMessages(forecasts: List<DailyForecast>, memeVersion: MemeVersion) {
        viewModelScope.launch {
            val updatedForecasts = forecasts.map { forecast ->
                try {
                    val result = when (memeVersion) {
                        MemeVersion.GLOBAL -> geminiService.generateGlobalSarcasticMessage(
                            weatherDescription = forecast.weatherDescription,
                            temperature = forecast.maxTemp,
                            humidity = forecast.humidity,
                            cityName = "", // Not needed for forecast
                            feelsLike = forecast.maxTemp
                        )
                        MemeVersion.INDIAN -> geminiService.generateIndianSarcasticMessage(
                            weatherDescription = forecast.weatherDescription,
                            temperature = forecast.maxTemp,
                            humidity = forecast.humidity,
                            cityName = "", // Not needed for forecast
                            feelsLike = forecast.maxTemp
                        )
                    }

                    val sarcasticMessage = result.getOrNull()
                        ?: generateLocalSarcasticMessage(forecast, memeVersion)

                    forecast.copy(sarcasticMessage = sarcasticMessage)
                } catch (e: Exception) {
                    forecast.copy(sarcasticMessage = generateLocalSarcasticMessage(forecast, memeVersion))
                }
            }

            _uiState.value = _uiState.value.copy(dailyForecasts = updatedForecasts)
        }
    }

    private fun generateLocalSarcasticMessage(forecast: DailyForecast, memeVersion: MemeVersion): String {
        val description = forecast.weatherDescription.lowercase()
        val temp = forecast.maxTemp
        val humidity = forecast.humidity
        val precipitation = forecast.precipitationProbability

        return when (memeVersion) {
            MemeVersion.GLOBAL -> when {
                description.contains("rain") || precipitation > 60 ->
                    "Rain incoming. Pack an umbrella or just accept being wet."
                description.contains("snow") ->
                    "Snowpocalypse alert! Time to hibernate."
                description.contains("clear") && temp > 25 ->
                    "Sunny and hot. Sunscreen or become a lobster."
                description.contains("cloud") ->
                    "Cloudy skies ahead. Nature's mood lighting."
                temp > 30 ->
                    "Heat wave warning. Hell's having a yard sale."
                temp < 5 ->
                    "Freezing temps. Penguins would complain."
                humidity > 80 ->
                    "Humidity level: Sauna without the relaxation."
                else ->
                    "Weather doing weather things. Revolutionary."
            }
            MemeVersion.INDIAN -> when {
                description.contains("rain") || precipitation > 60 ->
                    "Barish ka plan hai. Umbrella leke nikalna yaar!"
                description.contains("snow") ->
                    "Snow dekh kar Kashmir ka mood aa gaya!"
                description.contains("clear") && temp > 25 ->
                    "Dhoop mein nikalne ka plan cancel kar do."
                description.contains("cloud") ->
                    "Badal dekh kar chai peene ka mann kar raha hai."
                temp > 30 ->
                    "Garmi ka tandav! AC full power pe chalao."
                temp < 5 ->
                    "Thand itni hai ki blanket bhi kam pad jaaye."
                humidity > 80 ->
                    "Humidity level: Mumbai local train wali."
                else ->
                    "Mausam ka kya bharosa hai yaar. Dekh lenge."
            }
        }
    }

    fun retryForecast(latitude: Double, longitude: Double) {
        getForecast(latitude, longitude, _uiState.value.selectedMemeVersion)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}