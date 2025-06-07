package com.hritwik.sassyskies.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hritwik.sassyskies.model.ApiKeyUiState
import com.hritwik.sassyskies.repository.AuthRepository
import com.hritwik.sassyskies.repository.SecureApiKeyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ApiKeyViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val secureApiKeyRepository: SecureApiKeyRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ApiKeyUiState())
    val uiState: StateFlow<ApiKeyUiState> = _uiState.asStateFlow()

    init {
        loadCurrentApiKeys()
    }

    private fun loadCurrentApiKeys() {
        val weatherKey = secureApiKeyRepository.getWeatherApiKey() ?: ""
        val geminiKey = secureApiKeyRepository.getGeminiApiKey() ?: ""

        _uiState.value = _uiState.value.copy(
            weatherApiKey = weatherKey,
            geminiApiKey = geminiKey
        )
    }

    fun updateApiKeys(weatherApiKey: String, geminiApiKey: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            authRepository.updateApiKeys(weatherApiKey, geminiApiKey)
                .onSuccess {
                    secureApiKeyRepository.saveWeatherApiKey(weatherApiKey)
                    secureApiKeyRepository.saveGeminiApiKey(geminiApiKey)

                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        weatherApiKey = weatherApiKey,
                        geminiApiKey = geminiApiKey,
                        isSuccess = true
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

    fun clearSuccess() {
        _uiState.value = _uiState.value.copy(isSuccess = false)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}