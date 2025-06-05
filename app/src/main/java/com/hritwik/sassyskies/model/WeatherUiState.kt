package com.hritwik.sassyskies.model

data class WeatherUiState(
    val isLoading: Boolean = false,
    val weatherData: WeatherResponse? = null,
    val error: String? = null,
    val sarcasticMessage: String = "",
    val selectedMemeVersion: MemeVersion = MemeVersion.GLOBAL
)