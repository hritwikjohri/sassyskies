package com.hritwik.sassyskies.model

data class ApiKeyUiState(
    val isLoading: Boolean = false,
    val weatherApiKey: String = "",
    val geminiApiKey: String = "",
    val error: String? = null,
    val isSuccess: Boolean = false,
    val usageStats: UsageStats? = null
)
