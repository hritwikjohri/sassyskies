package com.hritwik.sassyskies.core

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.hritwik.sassyskies.screen.auth.ApiKeySetupScreen
import com.hritwik.sassyskies.screen.auth.ForgotPasswordScreen
import com.hritwik.sassyskies.screen.auth.LoginScreen
import com.hritwik.sassyskies.screen.auth.SignUpScreen
import com.hritwik.sassyskies.screen.components.Profile
import com.hritwik.sassyskies.screen.info.DeveloperInfoScreen
import com.hritwik.sassyskies.screen.weather.DetailedWeatherScreen
import com.hritwik.sassyskies.screen.weather.ForecastScreen
import com.hritwik.sassyskies.screen.weather.Home
import com.hritwik.sassyskies.viewmodel.ApiKeyViewModel
import com.hritwik.sassyskies.viewmodel.AuthViewModel
import com.hritwik.sassyskies.viewmodel.ForecastViewModel
import com.hritwik.sassyskies.viewmodel.WeatherViewModel

@Composable
fun AppRoute(
    startDestination: String,
    authViewModel: AuthViewModel,
    apiKeyViewModel: ApiKeyViewModel
) {
    val navController = rememberNavController()

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
                        popUpTo(0) { inclusive = true }
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
                onNavigateToProfile = {
                    navController.navigate("Profile")
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

        composable("Profile") {
            Profile(
                onNavigateBack = {
                    navController.popBackStack()
                },
                authViewModel = authViewModel,
                apiKeyViewModel = apiKeyViewModel,
                onNavigateToApiKeys = {
                    navController.navigate("ApiKeySetup")
                },
                onSignOut = {
                    authViewModel.signOut()
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