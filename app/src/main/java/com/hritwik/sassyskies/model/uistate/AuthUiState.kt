package com.hritwik.sassyskies.model.uistate

import com.hritwik.sassyskies.model.auth.User

data class AuthUiState(
    val isLoading: Boolean = false,
    val isAuthenticated: Boolean = false,
    val currentUser: User? = null,
    val error: String? = null,
    val isEmailSent: Boolean = false
)