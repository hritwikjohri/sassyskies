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
import com.hritwik.sassyskies.screen.*
import com.hritwik.sassyskies.viewmodel.AuthViewModel
import com.hritwik.sassyskies.viewmodel.WeatherViewModel
import com.hritwik.sassyskies.viewmodel.ForecastViewModel
import com.hritwik.sassyskies.viewmodel.ApiKeyViewModel

@Composable
fun AppRoute() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = hiltViewModel()
    val authUiState by authViewModel.uiState.collectAsStateWithLifecycle()

    // Debug logging - remove in production
    LaunchedEffect(authUiState) {
        println("🔍 Auth State Debug:")
        println("  - isAuthenticated: ${authUiState.isAuthenticated}")
        println("  - currentUser: ${authUiState.currentUser?.email}")
        println("  - weatherApiKey: ${authUiState.currentUser?.weatherApiKey?.isNotEmpty()}")
        println("  - geminiApiKey: ${authUiState.currentUser?.geminiApiKey?.isNotEmpty()}")
    }

    // Determine start destination based on auth state
    val startDestination = when {
        !authUiState.isAuthenticated -> "Login"
        authUiState.currentUser == null -> "Login"
        else -> "Splash"
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Authentication Screens
        composable("Login") {
            LoginScreen(
                onNavigateToSignUp = {
                    navController.navigate("SignUp")
                },
                onNavigateToForgotPassword = {
                    navController.navigate("ForgotPassword")
                },
                onLoginSuccess = {
                    navController.navigate("Splash") {
                        popUpTo("Login") { inclusive = true }
                    }
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

        // Splash Screen with proper navigation logic
        composable("Splash") {
            SplashScreenWithNavigation(
                authUiState = authUiState,
                onNavigateToHome = {
                    navController.navigate("Home") {
                        popUpTo("Splash") { inclusive = true }
                    }
                },
                onNavigateToApiSetup = {
                    navController.navigate("ApiKeySetup") {
                        popUpTo("Splash") { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.navigate("Login") {
                        popUpTo("Splash") { inclusive = true }
                    }
                }
            )
        }

        // API Key Setup Screen
        composable("ApiKeySetup") {
            ApiKeySetupScreen(
                onSetupComplete = {
                    navController.navigate("Home") {
                        popUpTo("ApiKeySetup") { inclusive = true }
                    }
                },
                onSkipForNow = {
                    navController.navigate("Home") {
                        popUpTo("ApiKeySetup") { inclusive = true }
                    }
                }
            )
        }

        // Main App Screens (Protected)
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
                },
                onNavigateToProfile = {
                    navController.navigate("Profile")
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

        composable("Profile") {
            ProfileScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToApiKeys = {
                    navController.navigate("ManageApiKeys")
                },
                onSignOut = {
                    navController.navigate("Login") {
                        popUpTo(0) { inclusive = true }
                    }
                },
                authViewModel = authViewModel
            )
        }

        composable("ManageApiKeys") {
            ApiKeySetupScreen(
                onSetupComplete = {
                    navController.popBackStack()
                },
                onSkipForNow = {
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