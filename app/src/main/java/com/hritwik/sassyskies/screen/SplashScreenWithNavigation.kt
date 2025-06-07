package com.hritwik.sassyskies.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect

@Composable
fun SplashScreenWithNavigation(
    authUiState: com.hritwik.sassyskies.model.AuthUiState,
    onNavigateToHome: () -> Unit,
    onNavigateToApiSetup: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(2000)

        // Debug current state
        println("🚀 Splash Navigation Logic:")
        println("  - isAuthenticated: ${authUiState.isAuthenticated}")
        println("  - currentUser: ${authUiState.currentUser}")
        println("  - hasWeatherKey: ${authUiState.currentUser?.weatherApiKey?.isNotEmpty()}")
        println("  - hasGeminiKey: ${authUiState.currentUser?.geminiApiKey?.isNotEmpty()}")

        when {
            // Not authenticated - go to login
            !authUiState.isAuthenticated -> {
                println("  → Navigating to Login (not authenticated)")
                onNavigateToLogin()
            }

            // Authenticated but no user data - go to login
            authUiState.currentUser == null -> {
                println("  → Navigating to Login (no user data)")
                onNavigateToLogin()
            }

            // Authenticated but missing API keys - go to setup
            authUiState.currentUser.weatherApiKey.isEmpty() ||
                    authUiState.currentUser.geminiApiKey.isEmpty() -> {
                println("  → Navigating to API Setup (missing keys)")
                onNavigateToApiSetup()
            }

            // Everything is ready - go to home
            else -> {
                println("  → Navigating to Home (all ready)")
                onNavigateToHome()
            }
        }
    }
}