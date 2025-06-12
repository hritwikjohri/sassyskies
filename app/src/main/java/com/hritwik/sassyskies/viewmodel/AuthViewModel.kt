package com.hritwik.sassyskies.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hritwik.sassyskies.model.uistate.AuthUiState
import com.hritwik.sassyskies.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    // Initialize with loading state
    private val _uiState = MutableStateFlow(AuthUiState(isLoading = true))
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    init {
        // Start observing authentication state changes as soon as ViewModel is created
        observeAuthState()
    }

    /**
     * Observes Firebase authentication state changes and updates UI state accordingly
     */
    private fun observeAuthState() {
        viewModelScope.launch {
            authRepository.getCurrentUser().collect { user ->
                _uiState.value = _uiState.value.copy(
                    isAuthenticated = user != null,
                    currentUser = user,
                    isLoading = false // Clear loading state once we have auth data
                )
            }
        }
    }

    /**
     * Handles user registration (sign-up) process
     */
    fun signUp(email: String, password: String, displayName: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            authRepository.signUp(email, password, displayName)
                .onSuccess { user ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isEmailSent = true,
                        error = null
                    )
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = error.message,
                        isEmailSent = false
                    )
                }
        }
    }

    /**
     * Handles user sign-in process
     */
    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            authRepository.signIn(email, password)
                .onSuccess { user ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = null
                    )
                    // observeAuthState() will handle user state updates automatically
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
     * Handles user sign-out process
     */
    fun signOut() {
        viewModelScope.launch {
            authRepository.signOut()
            // Reset UI state to initial empty state
            _uiState.value = AuthUiState(isLoading = false)
        }
    }

    /**
     * Handles password reset functionality
     */
    fun resetPassword(email: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            authRepository.resetPassword(email)
                .onSuccess {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isEmailSent = true,
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
     * Updates user's display name
     */
    fun updateProfile(displayName: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            authRepository.updateUserProfile(displayName)
                .onSuccess {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
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
     * Permanently deletes the user's account
     */
    fun deleteAccount() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            authRepository.deleteAccount()
                .onSuccess {
                    _uiState.value = AuthUiState(isLoading = false)
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
     * Gets current user profile from Firebase Auth
     */
    fun getUserProfile() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            authRepository.getUserProfile()
                .onSuccess { user ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        currentUser = user,
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
     * Clears any error messages from the UI state
     */
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    /**
     * Clears email sent confirmation state
     */
    fun clearEmailSent() {
        _uiState.value = _uiState.value.copy(isEmailSent = false)
    }
}