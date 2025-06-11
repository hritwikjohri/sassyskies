package com.hritwik.sassyskies.model.uistate

import com.hritwik.sassyskies.model.location.LocationData

data class LocationUiState(
    val isLoading: Boolean = false,
    val location: LocationData? = null,
    val errorMessage: String? = null,
    val isLocationUpdating: Boolean = false
)