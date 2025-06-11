package com.hritwik.sassyskies.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ApiKeyRepository @Inject constructor(
    private val authRepository: AuthRepository
) {

    /**
     * Gets the current user's weather API key
     */
    suspend fun getWeatherApiKey(): String {
        return authRepository.getApiKeys().fold(
            onSuccess = { (weatherKey, _) -> weatherKey },
            onFailure = { "" }
        )
    }

    /**
     * Gets the current user's Gemini API key
     */
    suspend fun getGeminiApiKey(): String {
        return authRepository.getApiKeys().fold(
            onSuccess = { (_, geminiKey) -> geminiKey },
            onFailure = { "" }
        )
    }

    /**
     * Observes weather API key changes
     */
    fun observeWeatherApiKey(): Flow<String> {
        return authRepository.getCurrentUser().map { user ->
            user?.weatherApiKey ?: ""
        }
    }

    /**
     * Observes Gemini API key changes
     */
    fun observeGeminiApiKey(): Flow<String> {
        return authRepository.getCurrentUser().map { user ->
            user?.geminiApiKey ?: ""
        }
    }

    /**
     * Checks if user has valid API keys
     */
    suspend fun hasValidApiKeys(): Boolean {
        return authRepository.hasValidApiKeys().fold(
            onSuccess = { it },
            onFailure = { false }
        )
    }
}