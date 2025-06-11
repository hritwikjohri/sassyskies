package com.hritwik.sassyskies.core

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.hritwik.sassyskies.screen.DetailedWeatherScreen
import com.hritwik.sassyskies.screen.DeveloperInfoScreen
import com.hritwik.sassyskies.screen.ForecastScreen
import com.hritwik.sassyskies.screen.Home
import com.hritwik.sassyskies.screen.Splash
import com.hritwik.sassyskies.screen.auth.ApiKeySetupScreen
import com.hritwik.sassyskies.screen.auth.ForgotPasswordScreen
import com.hritwik.sassyskies.screen.auth.LoginScreen
import com.hritwik.sassyskies.screen.auth.SignUpScreen
import com.hritwik.sassyskies.viewmodel.AuthViewModel
import com.hritwik.sassyskies.viewmodel.WeatherViewModel
import com.hritwik.sassyskies.viewmodel.ForecastViewModel

@Composable
fun AppRoute() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = hiltViewModel()
    val authUiState by authViewModel.uiState.collectAsStateWithLifecycle()

    // Handle navigation based on auth state changes
    LaunchedEffect(authUiState.isAuthenticated, authUiState.currentUser, authUiState.isLoading) {
        // Wait for auth loading to complete
        if (!authUiState.isLoading) {
            when {
                authUiState.isAuthenticated && authUiState.currentUser != null -> {
                    val user = authUiState.currentUser!!

                    if (!user.hasAllApiKeys()) {
                        // User is authenticated but missing API keys - go to setup
                        navController.navigate("ApiKeySetup") {
                            popUpTo(0) { inclusive = true }
                        }
                    } else {
                        // User is authenticated and has API keys - go to home
                        navController.navigate("Home") {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                }
                !authUiState.isAuthenticated -> {
                    // User is not authenticated - go to login
                    navController.navigate("Login") {
                        popUpTo(0) { inclusive = true }
                    }
                }
                // If still loading, stay on splash
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = "Splash"
    ) {
        // Splash Screen - Remove the navigation callback
        composable("Splash") {
            Splash {
                // Remove this navigation - let LaunchedEffect handle it
                // The splash will automatically navigate based on auth state
            }
        }

        // Authentication Screens
        composable("Login") {
            LoginScreen(
                onNavigateToSignUp = {
                    navController.navigate("SignUp")
                },
                onNavigateToForgotPassword = {
                    navController.navigate("ForgotPassword")
                },
                authViewModel = authViewModel
            )
        }

        composable("SignUp") {
            SignUpScreen(
                onNavigateToLogin = {
                    navController.navigate("Login") {
                        popUpTo("SignUp") { inclusive = true }
                    }
                },
                authViewModel = authViewModel
            )
        }

        composable("ForgotPassword") {
            ForgotPasswordScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                authViewModel = authViewModel
            )
        }

        // API Key Setup Screen
        composable("ApiKeySetup") {
            ApiKeySetupScreen(
                onSetupComplete = {
                    navController.navigate("Home") {
                        popUpTo("ApiKeySetup") { inclusive = true }
                    }
                }
            )
        }

        // Main App Screens (Protected - require authentication and API keys)
        composable("Home") { backStackEntry ->
            val weatherViewModel: WeatherViewModel = hiltViewModel(backStackEntry)

            Home(
                weatherViewModel = weatherViewModel,
                onNavigateToDetailedWeather = {
                    navController.navigate("DetailedWeather")
                },
                onNavigateToDeveloperInfo = {
                    navController.navigate("DeveloperInfo")
                },
                onNavigateToForecast = { latitude, longitude ->
                    navController.navigate("Forecast/$latitude/$longitude")
                }
            )
        }

        composable("DetailedWeather") { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry("Home")
            }
            val weatherViewModel: WeatherViewModel = hiltViewModel(parentEntry)

            DetailedWeatherScreen(
                weatherViewModel = weatherViewModel,
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            "Forecast/{latitude}/{longitude}",
            arguments = listOf(
                navArgument("latitude") { type = NavType.FloatType },
                navArgument("longitude") { type = NavType.FloatType }
            )
        ) { backStackEntry ->
            val latitude = backStackEntry.arguments?.getFloat("latitude")?.toDouble() ?: 0.0
            val longitude = backStackEntry.arguments?.getFloat("longitude")?.toDouble() ?: 0.0

            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry("Home")
            }
            val weatherViewModel: WeatherViewModel = hiltViewModel(parentEntry)
            val forecastViewModel: ForecastViewModel = hiltViewModel()

            ForecastScreen(
                forecastViewModel = forecastViewModel,
                weatherViewModel = weatherViewModel,
                latitude = latitude,
                longitude = longitude,
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        composable("ManageApiKeys") {
            ApiKeySetupScreen(
                onSetupComplete = {
                    navController.popBackStack()
                }
            )
        }

        composable("DeveloperInfo") {
            DeveloperInfoScreen(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}