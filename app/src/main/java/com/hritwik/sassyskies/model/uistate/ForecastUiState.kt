package com.hritwik.sassyskies.model.uistate

import com.hritwik.sassyskies.model.utils.MemeVersion
import com.hritwik.sassyskies.model.weather.data.DailyForecast

data class ForecastUiState(
    val isLoading: Boolean = false,
    val dailyForecasts: List<DailyForecast> = emptyList(),
    val error: String? = null,
    val selectedMemeVersion: MemeVersion = MemeVersion.GLOBAL
)