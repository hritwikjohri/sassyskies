package com.hritwik.sassyskies.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hritwik.sassyskies.repository.WeatherRepository
import com.hritwik.sassyskies.model.uistate.WeatherUiState
import com.hritwik.sassyskies.model.utils.Result
import com.hritwik.sassyskies.model.weather.core.WeatherResponse
import com.hritwik.sassyskies.model.utils.MemeVersion
import com.hritwik.sassyskies.service.GeminiService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.roundToInt

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val weatherRepository: WeatherRepository,
    private val geminiService: GeminiService
) : ViewModel() {

    private val _uiState = MutableStateFlow(WeatherUiState())
    val uiState: StateFlow<WeatherUiState> = _uiState.asStateFlow()

    private val _memeVersion = MutableStateFlow(MemeVersion.GLOBAL)
    val memeVersion: StateFlow<MemeVersion> = _memeVersion.asStateFlow()

    fun getCurrentWeather(latitude: Double, longitude: Double) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            when (val result = weatherRepository.getCurrentWeather(latitude, longitude)) {
                is Result.Success -> {
                    // First show weather data with loading message for sarcastic comment
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        weatherData = result.data,
                        sarcasticMessage = when (_memeVersion.value) {
                            MemeVersion.GLOBAL -> "Hold your horses..."
                            MemeVersion.INDIAN -> "Ruk jaa, bata raha hu yaar..."
                        },
                        error = null
                    )

                    // Then generate sarcastic message based on selected version
                    generateSarcasticMessage(result.data, _memeVersion.value)
                }
                is Result.Error -> {
                    val errorMsg = when (_memeVersion.value) {
                        MemeVersion.GLOBAL -> "Weather API is having a moment. Typical."
                        MemeVersion.INDIAN -> "API ki halat kharab hai yaar. Kya karein ab?"
                    }
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = result.message,
                        sarcasticMessage = "API down hai yaar. Typical Indian server problems."
                    )
                }
                is Result.Loading -> {
                    _uiState.value = _uiState.value.copy(isLoading = true)
                }
            }
        }
    }

    fun setMemeVersion(version: MemeVersion) {
        _memeVersion.value = version
        // Regenerate sarcastic message with new version if we have weather data
        _uiState.value.weatherData?.let { weather ->
            generateSarcasticMessage(weather, version)
        }
    }

    private fun generateSarcasticMessage(weather: WeatherResponse, memeVersion: MemeVersion) {
        viewModelScope.launch {
            val description = weather.weather.firstOrNull()?.description ?: ""
            val temp = weather.main.temp.roundToInt()
            val humidity = weather.main.humidity
            val cityName = weather.name
            val feelsLike = weather.main.feelsLike.roundToInt()

            try {
                val result = when (memeVersion) {
                    MemeVersion.GLOBAL -> geminiService.generateGlobalSarcasticMessage(
                        weatherDescription = description,
                        temperature = temp,
                        humidity = humidity,
                        cityName = cityName,
                        feelsLike = feelsLike
                    )
                    MemeVersion.INDIAN -> geminiService.generateIndianSarcasticMessage(
                        weatherDescription = description,
                        temperature = temp,
                        humidity = humidity,
                        cityName = cityName,
                        feelsLike = feelsLike
                    )
                }

                result.onSuccess { message ->
                    _uiState.value = _uiState.value.copy(sarcasticMessage = message)
                }.onFailure {
                    // Fall back to local generation if Gemini fails
                    val fallbackMessage = generateLocalSarcasticMessage(weather, memeVersion)
                    _uiState.value = _uiState.value.copy(sarcasticMessage = fallbackMessage)
                }
            } catch (_: Exception) {
                // Fall back to local generation
                val fallbackMessage = generateLocalSarcasticMessage(weather, memeVersion)
                _uiState.value = _uiState.value.copy(sarcasticMessage = fallbackMessage)
            }
        }
    }

    private fun generateLocalSarcasticMessage(weather: WeatherResponse, memeVersion: MemeVersion): String {
        val description = weather.weather.firstOrNull()?.description?.lowercase() ?: ""
        val temp = weather.main.temp.roundToInt()
        val humidity = weather.main.humidity

        return when (memeVersion) {
            MemeVersion.GLOBAL -> generateGlobalFallback(description, temp, humidity)
            MemeVersion.INDIAN -> generateIndianFallback(description, temp, humidity)
        }
    }

    private fun generateGlobalFallback(description: String, temp: Int, humidity: Int): String {
        return when {
            description.contains("fog") || description.contains("mist") ->
                "Great visibility! Perfect for playing hide and seek with buildings."
            description.contains("rain") || description.contains("drizzle") ->
                "It's fucking raining. Now you know."
            description.contains("snow") ->
                "It's snowing. Hope you like being cold and wet."
            description.contains("clear") || description.contains("sunny") ->
                if (temp > 25) "It's fucking hot out there. Enjoy melting."
                else "Fuck, it's lovely in the sun. For once."
            description.contains("cloud") ->
                "Cloudy with a chance of disappointment."
            description.contains("storm") || description.contains("thunder") ->
                "Storm's coming. Mother Nature's having a tantrum."
            temp < 0 ->
                "It's freezing. Congrats, you live in a freezer."
            temp > 35 ->
                "Satan's armpit is cooler than this."
            humidity > 80 ->
                "It's so humid, you could swim through the air."
            else ->
                "Weather exists. That's... something."
        }
    }

    private fun generateIndianFallback(description: String, temp: Int, humidity: Int): String {
        return when {
            description.contains("fog") || description.contains("mist") ->
                "Yaar itna fog hai, Delhi pollution se competition kar raha hai!"
            description.contains("rain") || description.contains("drizzle") ->
                "Barish ho rahi hai bhai. Mumbai local ki tarah packed clouds se."
            description.contains("snow") ->
                "Snow dekh ke Kashmir ka yaad aa gaya. Thand mein bindass chill kar."
            description.contains("clear") || description.contains("sunny") ->
                if (temp > 25) "Garmi itni hai ki AC bhi ghar jaana chahta hai!"
                else "Dhoop mein bhi thanda lag raha hai. Matlab climate change real hai yaar."
            description.contains("cloud") ->
                "Badal dekh kar lagta hai aaj office se chutti le lein."
            description.contains("storm") || description.contains("thunder") ->
                "Aandhi-toofan aa raha hai! Drama queen weather ka mood off hai."
            temp < 0 ->
                "Itni thand hai ki Rajasthani bhi sweater pehen raha hoga."
            temp > 35 ->
                "Garmi itni hai ki tandoor bhi sharma jaye. AC chalao yaar!"
            humidity > 80 ->
                "Humidity level: Mumbai local train during monsoon."
            else ->
                "Weather ka mood kya hai pata nahi. Jugaad kar ke dekho."
        }
    }

    fun retryWeatherFetch(latitude: Double, longitude: Double) {
        getCurrentWeather(latitude, longitude)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}