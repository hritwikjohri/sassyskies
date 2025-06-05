package com.hritwik.sassyskies.model

data class LocationUiState(
    val isLoading: Boolean = false,
    val location: LocationData? = null,
    val errorMessage: String? = null,
    val isLocationUpdating: Boolean = false
)