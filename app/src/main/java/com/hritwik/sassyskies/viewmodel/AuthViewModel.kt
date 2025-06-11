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

/**
 * AuthViewModel handles all authentication-related operations and state management
 * It manages user sign-in, sign-up, password reset for Firebase Auth only
 */
@HiltViewModel
class AuthViewModel @Inject constructor(
    // Repository for Firebase authentication operations
    private val authRepository: AuthRepository
) : ViewModel() {

    // Private mutable state that can only be modified within this ViewModel
    private val _uiState = MutableStateFlow(AuthUiState())

    // Public read-only state exposed to UI components
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    init {
        // Start observing authentication state changes as soon as ViewModel is created
        observeAuthState()
    }

    /**
     * Observes Firebase authentication state changes and updates UI state accordingly
     * This function runs continuously and reacts to any changes in user authentication
     */
    private fun observeAuthState() {
        // Launch a coroutine in the ViewModel's scope (tied to ViewModel lifecycle)
        viewModelScope.launch {
            // Collect user state changes from the repository
            authRepository.getCurrentUser().collect { user ->
                // Update UI state with the latest user information
                _uiState.value = _uiState.value.copy(
                    isAuthenticated = user != null,  // User is authenticated if user object exists
                    currentUser = user,               // Store current user data
                    isLoading = false                // Authentication check is complete
                )
            }
        }
    }

    /**
     * Handles user registration (sign-up) process
     * Creates a new Firebase account and sends email verification
     */
    fun signUp(email: String, password: String, displayName: String) {
        // Launch coroutine for async operation
        viewModelScope.launch {
            // Set loading state and clear any previous errors
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            // Attempt to create new user account through repository
            authRepository.signUp(email, password, displayName)
                .onSuccess { user ->
                    // Sign-up successful - update state to show email verification screen
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isEmailSent = true,  // Show email verification confirmation
                        error = null
                    )
                }
                .onFailure { error ->
                    // Sign-up failed - update state with error message
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
     * Authenticates user with Firebase and loads their data
     */
    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            // Set loading state and clear any previous errors
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            // Attempt to sign in user through repository
            authRepository.signIn(email, password)
                .onSuccess { user ->
                    // Sign-in successful - clear loading and error states
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = null
                    )
                    // Note: Don't manually set currentUser here
                    // Let observeAuthState() handle user state updates automatically
                }
                .onFailure { error ->
                    // Sign-in failed - update state with error message
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = error.message
                    )
                }
        }
    }

    /**
     * Handles user sign-out process
     * Clears Firebase session
     */
    fun signOut() {
        viewModelScope.launch {
            // Call repository to handle Firebase sign-out
            authRepository.signOut()

            // Reset UI state to initial empty state
            _uiState.value = AuthUiState()
        }
    }

    /**
     * Handles password reset functionality
     * Sends password reset email to user's email address
     */
    fun resetPassword(email: String) {
        viewModelScope.launch {
            // Set loading state and clear any previous errors
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            // Attempt to send password reset email through repository
            authRepository.resetPassword(email)
                .onSuccess {
                    // Password reset email sent successfully
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isEmailSent = true,  // Show email sent confirmation
                        error = null
                    )
                }
                .onFailure { error ->
                    // Password reset failed - update state with error message
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
            // Set loading state and clear any previous errors
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            // Attempt to update user profile through repository
            authRepository.updateUserProfile(displayName)
                .onSuccess {
                    // Profile update successful
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = null
                    )
                    // observeAuthState() will automatically update the user data
                }
                .onFailure { error ->
                    // Profile update failed - update state with error message
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
            // Set loading state and clear any previous errors
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            // Attempt to delete user account through repository
            authRepository.deleteAccount()
                .onSuccess {
                    // Account deletion successful - reset state
                    _uiState.value = AuthUiState()
                }
                .onFailure { error ->
                    // Account deletion failed - update state with error message
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
            // Set loading state
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            // Attempt to get user profile through repository
            authRepository.getUserProfile()
                .onSuccess { user ->
                    // Profile retrieval successful
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        currentUser = user,
                        error = null
                    )
                }
                .onFailure { error ->
                    // Profile retrieval failed - update state with error message
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = error.message
                    )
                }
        }
    }

    /**
     * Clears any error messages from the UI state
     * Called when user starts typing or dismisses error
     */
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    /**
     * Clears email sent confirmation state
     * Used to navigate away from email confirmation screens
     */
    fun clearEmailSent() {
        _uiState.value = _uiState.value.copy(isEmailSent = false)
    }
}