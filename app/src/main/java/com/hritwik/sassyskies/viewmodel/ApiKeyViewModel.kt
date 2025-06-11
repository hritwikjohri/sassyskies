package com.hritwik.sassyskies.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hritwik.sassyskies.model.uistate.ApiKeyUiState
import com.hritwik.sassyskies.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ApiKeyViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ApiKeyUiState())
    val uiState: StateFlow<ApiKeyUiState> = _uiState.asStateFlow()

    init {
        loadCurrentApiKeys()
    }

    /**
     * Load current API keys from the repository
     */
    private fun loadCurrentApiKeys() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            authRepository.getApiKeys()
                .onSuccess { (weatherKey, geminiKey) ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        weatherApiKey = weatherKey,
                        geminiApiKey = geminiKey,
                        error = null
                    )
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = error.message
                    )
                }
        }
    }

    /**
     * Update API keys
     */
    fun updateApiKeys(weatherApiKey: String, geminiApiKey: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            authRepository.updateApiKeys(weatherApiKey, geminiApiKey)
                .onSuccess {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        weatherApiKey = weatherApiKey,
                        geminiApiKey = geminiApiKey,
                        isSuccess = true,
                        error = null
                    )
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = error.message,
                        isSuccess = false
                    )
                }
        }
    }

    /**
     * Clear success state
     */
    fun clearSuccess() {
        _uiState.value = _uiState.value.copy(isSuccess = false)
    }

    /**
     * Clear error state
     */
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}