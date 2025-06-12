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
import com.hritwik.sassyskies.screen.weather.DetailedWeatherScreen
import com.hritwik.sassyskies.screen.info.DeveloperInfoScreen
import com.hritwik.sassyskies.screen.weather.ForecastScreen
import com.hritwik.sassyskies.screen.weather.Home
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

    LaunchedEffect(authUiState.isAuthenticated, authUiState.currentUser) {
        when {
            !authUiState.isAuthenticated -> {
                // User is not authenticated, navigate to login
                navController.navigate("Login") {
                    popUpTo("Splash") { inclusive = true }
                    popUpTo("Home") { inclusive = true }
                    popUpTo("ApiKeySetup") { inclusive = true }
                }
            }
            authUiState.isAuthenticated && authUiState.currentUser?.hasAllApiKeys() != true -> {
                // User is authenticated but doesn't have API keys
                navController.navigate("ApiKeySetup") {
                    popUpTo("Splash") { inclusive = true }
                    popUpTo("Login") { inclusive = true }
                    popUpTo("SignUp") { inclusive = true }
                    popUpTo("ForgotPassword") { inclusive = true }
                }
            }
            authUiState.isAuthenticated && authUiState.currentUser?.hasAllApiKeys() == true -> {
                // User is authenticated and has API keys
                navController.navigate("Home") {
                    popUpTo("Splash") { inclusive = true }
                    popUpTo("Login") { inclusive = true }
                    popUpTo("SignUp") { inclusive = true }
                    popUpTo("ForgotPassword") { inclusive = true }
                    popUpTo("ApiKeySetup") { inclusive = true }
                }
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = "Splash" // Always start with splash
    ) {
        // Splash Screen
        composable("Splash") {
            Splash()
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
                onNavigateToForecast = { latitude, longitude, errorStates ->
                    // Pass error states as navigation arguments
                    navController.navigate(
                        "Forecast/$latitude/$longitude/${errorStates.hasLocationPermission}/${errorStates.locationError ?: "null"}/${errorStates.weatherError ?: "null"}"
                    )
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
            "Forecast/{latitude}/{longitude}/{hasPermission}/{locationError}/{weatherError}",
            arguments = listOf(
                navArgument("latitude") { type = NavType.FloatType },
                navArgument("longitude") { type = NavType.FloatType },
                navArgument("hasPermission") { type = NavType.BoolType },
                navArgument("locationError") { type = NavType.StringType },
                navArgument("weatherError") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val latitude = backStackEntry.arguments?.getFloat("latitude")?.toDouble() ?: 0.0
            val longitude = backStackEntry.arguments?.getFloat("longitude")?.toDouble() ?: 0.0
            val hasPermission = backStackEntry.arguments?.getBoolean("hasPermission") != false
            val locationError = backStackEntry.arguments?.getString("locationError")?.takeIf { it != "null" }
            val weatherError = backStackEntry.arguments?.getString("weatherError")?.takeIf { it != "null" }

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
                hasLocationPermission = hasPermission,
                locationError = locationError,
                weatherError = weatherError,
                onBackClick = {
                    navController.popBackStack()
                },
                onRetryPermission = {
                    // Navigate back to home to retry permission
                    navController.popBackStack()
                },
                onRetryLocation = {
                    // Navigate back to home to retry location
                    navController.popBackStack()
                },
                onUseDefaultLocation = {
                    // Navigate back to home with default location
                    navController.popBackStack()
                },
                onRetryWeather = {
                    // Navigate back to home to retry weather
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