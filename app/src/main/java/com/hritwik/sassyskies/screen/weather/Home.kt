package com.hritwik.sassyskies.screen.weather

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hritwik.sassyskies.model.utils.ErrorStates
import com.hritwik.sassyskies.viewmodel.WeatherViewModel
import com.hritwik.sassyskies.viewmodel.LocationViewModel
import com.hritwik.sassyskies.viewmodel.LocationViewModelFactory
import com.hritwik.sassyskies.repositoryImpl.LocationRepositoryImpl
import com.hritwik.sassyskies.screen.components.LocationErrorContent
import com.hritwik.sassyskies.screen.components.PermissionDeniedContent
import com.hritwik.sassyskies.screen.components.WeatherErrorContent

@Composable
fun Home(
    weatherViewModel: WeatherViewModel = hiltViewModel(),
    onNavigateToDetailedWeather: (() -> Unit)? = null,
    onNavigateToDeveloperInfo: (() -> Unit)? = null,
    onNavigateToForecast: ((Double, Double, ErrorStates) -> Unit)? = null
) {
    val context = LocalContext.current

    val locationRepository = remember { LocationRepositoryImpl(context) }
    val locationViewModel: LocationViewModel = viewModel(
        factory = LocationViewModelFactory(locationRepository)
    )

    val weatherUiState by weatherViewModel.uiState.collectAsStateWithLifecycle()
    val locationUiState by locationViewModel.uiState.collectAsStateWithLifecycle()
    val selectedMemeVersion by weatherViewModel.memeVersion.collectAsStateWithLifecycle()

    var hasLocationPermission by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        hasLocationPermission = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true

        if (hasLocationPermission) {
            locationViewModel.getCurrentLocation()
        }
    }

    LaunchedEffect(Unit) {
        permissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    // Listen for location updates and fetch weather
    LaunchedEffect(locationUiState.location) {
        locationUiState.location?.let { location ->
            weatherViewModel.getCurrentWeather(
                latitude = location.latitude,
                longitude = location.longitude
            )
        }
    }

    // Create error states to pass to forecast
    val errorStates = ErrorStates(
        hasLocationPermission = hasLocationPermission,
        locationError = locationUiState.errorMessage,
        weatherError = weatherUiState.error
    )

    // Use full screen for weather content, centered layout for loading/error states
    when {
        weatherUiState.weatherData != null -> {
            // Full screen weather content
            WeatherContent(
                weather = weatherUiState.weatherData!!,
                sarcasticMessage = weatherUiState.sarcasticMessage,
                selectedMemeVersion = selectedMemeVersion,
                onRefreshClick = {
                    locationUiState.location?.let { location ->
                        weatherViewModel.getCurrentWeather(
                            latitude = location.latitude,
                            longitude = location.longitude
                        )
                    }
                },
                onLocationClick = {
                    locationViewModel.getCurrentLocation()
                },
                onDetailedWeatherClick = {
                    onNavigateToDetailedWeather?.invoke()
                },
                onMemeVersionChanged = { version ->
                    weatherViewModel.setMemeVersion(version)
                },
                onDeveloperInfoClick = {
                    onNavigateToDeveloperInfo?.invoke()
                },
                onForecastClick = {
                    locationUiState.location?.let { location ->
                        onNavigateToForecast?.invoke(
                            location.latitude,
                            location.longitude,
                            errorStates
                        )
                    }
                }
            )
        }

        else -> {
            // Centered content for loading/error states with non-blocking overlays
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                // Main loading content
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    when {
                        locationUiState.isLoading -> {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                CircularProgressIndicator()
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "Getting your location...",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                        }

                        weatherUiState.isLoading -> {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                CircularProgressIndicator()
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "Fetching weather data...",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                        }

                        else -> {
                            // Fallback state
                            Text(
                                text = "Initializing...",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }

                // Non-blocking error overlays
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopCenter)
                        .padding(top = 50.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Permission Denied Overlay
                    if (!hasLocationPermission) {
                        PermissionDeniedContent(
                            onRetryPermission = {
                                permissionLauncher.launch(
                                    arrayOf(
                                        Manifest.permission.ACCESS_FINE_LOCATION,
                                        Manifest.permission.ACCESS_COARSE_LOCATION
                                    )
                                )
                            }
                        )
                    }

                    // Location Error Overlay
                    locationUiState.errorMessage?.let { error ->
                        LocationErrorContent(
                            errorMessage = error,
                            onRetryLocation = {
                                locationViewModel.getCurrentLocation()
                            },
                            onUseDefaultLocation = {
                                // Fallback to New York coordinates
                                weatherViewModel.getCurrentWeather(40.78, -73.97)
                            }
                        )
                    }

                    // Weather Error Overlay
                    weatherUiState.error?.let { error ->
                        WeatherErrorContent(
                            sarcasticMessage = weatherUiState.sarcasticMessage.ifEmpty {
                                "Weather API decided to take a coffee break. Typical."
                            },
                            onRetryWeather = {
                                locationUiState.location?.let { location ->
                                    weatherViewModel.getCurrentWeather(
                                        latitude = location.latitude,
                                        longitude = location.longitude
                                    )
                                } ?: run {
                                    // Fallback if no location
                                    weatherViewModel.getCurrentWeather(40.78, -73.97)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}