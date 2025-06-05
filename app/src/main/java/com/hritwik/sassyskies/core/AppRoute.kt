package com.hritwik.sassyskies.core

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
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
import com.hritwik.sassyskies.viewmodel.WeatherViewModel
import com.hritwik.sassyskies.viewmodel.ForecastViewModel

@Composable
fun AppRoute(){
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "Splash"
    ) {
        composable("Splash") {
            Splash {
                navController.navigate("Home") {
                    popUpTo("Splash") { inclusive = true }
                }
            }
        }

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

        composable("DeveloperInfo") {
            DeveloperInfoScreen(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}