package com.hritwik.sassyskies.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hritwik.sassyskies.repository.LocationRepository
import com.hritwik.sassyskies.model.uistate.LocationUiState
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LocationViewModel(
    private val locationRepository: LocationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LocationUiState())
    val uiState: StateFlow<LocationUiState> = _uiState.asStateFlow()

    private var locationUpdatesJob: Job? = null

    fun getCurrentLocation() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            locationRepository.getCurrentLocation()
                .onSuccess { locationData ->
                    _uiState.value = _uiState.value.copy(
                        location = locationData,
                        isLoading = false
                    )
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = exception.message ?: "Unknown error occurred"
                    )
                }
        }
    }

    fun startLocationUpdates(intervalMs: Long = 60000) {
        locationUpdatesJob?.cancel()

        locationUpdatesJob = viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLocationUpdating = true, errorMessage = null)

            locationRepository.startLocationUpdates(intervalMs).collect { result ->
                result.onSuccess { locationData ->
                    _uiState.value = _uiState.value.copy(
                        location = locationData,
                        isLoading = false
                    )
                }.onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        errorMessage = exception.message ?: "Location update failed"
                    )
                }
            }
        }
    }

    fun stopLocationUpdates() {
        locationUpdatesJob?.cancel()
        locationRepository.stopLocationUpdates()
        _uiState.value = _uiState.value.copy(isLocationUpdating = false)
    }

    override fun onCleared() {
        super.onCleared()
        stopLocationUpdates()
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}