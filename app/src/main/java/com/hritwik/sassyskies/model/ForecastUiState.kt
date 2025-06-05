package com.hritwik.sassyskies.model

data class ForecastUiState(
    val isLoading: Boolean = false,
    val dailyForecasts: List<DailyForecast> = emptyList(),
    val error: String? = null,
    val selectedMemeVersion: MemeVersion = MemeVersion.GLOBAL
)