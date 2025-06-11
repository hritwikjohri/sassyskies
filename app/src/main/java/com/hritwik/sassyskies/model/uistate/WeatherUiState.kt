package com.hritwik.sassyskies.model.uistate

import com.hritwik.sassyskies.model.utils.MemeVersion
import com.hritwik.sassyskies.model.weather.core.WeatherResponse

data class WeatherUiState(
    val isLoading: Boolean = false,
    val weatherData: WeatherResponse? = null,
    val error: String? = null,
    val sarcasticMessage: String = "",
    val selectedMemeVersion: MemeVersion = MemeVersion.GLOBAL
)