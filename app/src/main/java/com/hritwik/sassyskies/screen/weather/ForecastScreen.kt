package com.hritwik.sassyskies.screen.weather

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hritwik.sassyskies.model.utils.MemeVersion
import com.hritwik.sassyskies.model.weather.core.WeatherResponse
import com.hritwik.sassyskies.model.weather.data.DailyForecast
import com.hritwik.sassyskies.model.weather.detail.Clouds
import com.hritwik.sassyskies.model.weather.detail.Coord
import com.hritwik.sassyskies.model.weather.detail.Main
import com.hritwik.sassyskies.model.weather.detail.Sys
import com.hritwik.sassyskies.model.weather.detail.Weather
import com.hritwik.sassyskies.model.weather.detail.Wind
import com.hritwik.sassyskies.screen.components.AnimatedIcon
import com.hritwik.sassyskies.ui.theme.JosefinSans
import com.hritwik.sassyskies.viewmodel.ForecastViewModel
import com.hritwik.sassyskies.viewmodel.WeatherViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForecastScreen(
    forecastViewModel: ForecastViewModel = hiltViewModel(),
    weatherViewModel: WeatherViewModel,
    latitude: Double,
    longitude: Double,
    onBackClick: () -> Unit
) {
    val forecastUiState by forecastViewModel.uiState.collectAsStateWithLifecycle()
    val selectedMemeVersion by weatherViewModel.memeVersion.collectAsStateWithLifecycle()

    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // Load forecast data when screen opens
    LaunchedEffect(latitude, longitude, selectedMemeVersion) {
        forecastViewModel.getForecast(latitude, longitude, selectedMemeVersion)
    }

    // Update meme version when it changes in weather view model
    LaunchedEffect(selectedMemeVersion) {
        forecastViewModel.updateMemeVersion(selectedMemeVersion)
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = false,
        drawerContent = {
            ForecastMemeVersionDrawer(
                selectedVersion = selectedMemeVersion,
                onVersionSelected = { version ->
                    weatherViewModel.setMemeVersion(version)
                    scope.launch { drawerState.close() }
                },
                onCloseDrawer = {
                    scope.launch { drawerState.close() }
                }
            )
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "7-Day Forecast",
                            fontFamily = JosefinSans,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = {
                            scope.launch { drawerState.open() }
                        }) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "Options",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )
            },
            containerColor = MaterialTheme.colorScheme.background
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                when {
                    forecastUiState.isLoading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                CircularProgressIndicator()
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "Loading sarcastic forecasts...",
                                    fontFamily = JosefinSans,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }

                    forecastUiState.error != null -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Text(
                                    text = "Forecast Error",
                                    style = MaterialTheme.typography.headlineSmall,
                                    textAlign = TextAlign.Center,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = forecastUiState.error!!,
                                    style = MaterialTheme.typography.bodyMedium,
                                    textAlign = TextAlign.Center,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(24.dp))
                                Button(
                                    onClick = {
                                        forecastViewModel.retryForecast(latitude, longitude)
                                    }
                                ) {
                                    Text("Try Again")
                                }
                            }
                        }
                    }

                    forecastUiState.dailyForecasts.isNotEmpty() -> {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            contentPadding = PaddingValues(vertical = 16.dp)
                        ) {
                            items(forecastUiState.dailyForecasts) { forecast ->
                                ForecastCard(forecast = forecast)
                            }
                        }
                    }

                    else -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No forecast data available",
                                fontFamily = JosefinSans,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ForecastCard(forecast: DailyForecast) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* Could expand for more details */ },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.surface,
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f)
                        )
                    )
                )
                .padding(16.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Date and Weather Info Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = forecast.dayOfWeek,
                            fontFamily = JosefinSans,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = forecast.date,
                            fontFamily = JosefinSans,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Normal,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Weather Icon
                        AnimatedIcon(
                            weather = WeatherResponse(
                                coord = Coord(0.0, 0.0),
                                weather = listOf(
                                    Weather(
                                        id = 0,
                                        main = forecast.weatherMain,
                                        description = forecast.weatherDescription,
                                        icon = forecast.iconCode
                                    )
                                ),
                                base = "",
                                main = Main(
                                    temp = forecast.maxTemp.toDouble(),
                                    feelsLike = forecast.maxTemp.toDouble(),
                                    tempMin = forecast.minTemp.toDouble(),
                                    tempMax = forecast.maxTemp.toDouble(),
                                    pressure = 1013,
                                    humidity = forecast.humidity
                                ),
                                visibility = 10000,
                                wind = Wind(
                                    speed = forecast.windSpeed,
                                    deg = 0
                                ),
                                clouds = Clouds(0),
                                dt = System.currentTimeMillis() / 1000,
                                sys = Sys(
                                    country = "",
                                    sunrise = 0L,
                                    sunset = 0L
                                ),
                                timezone = 0,
                                id = 0,
                                name = "",
                                cod = 200
                            ),
                            modifier = Modifier.size(50.dp)
                        )

                        // Temperature
                        Column(
                            horizontalAlignment = Alignment.End
                        ) {
                            Text(
                                text = "${forecast.maxTemp}°",
                                fontFamily = JosefinSans,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "${forecast.minTemp}°",
                                fontFamily = JosefinSans,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Normal,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // Weather Description
                Text(
                    text = forecast.weatherDescription.replaceFirstChar { it.uppercase() },
                    fontFamily = JosefinSans,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Sarcastic Message
                if (forecast.sarcasticMessage.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f))
                            .padding(12.dp)
                    ) {
                        Text(
                            text = forecast.sarcasticMessage,
                            fontFamily = JosefinSans,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface,
                            lineHeight = 18.sp
                        )
                    }
                }

                // Additional Weather Details
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    WeatherDetailChip(
                        label = "Humidity",
                        value = "${forecast.humidity}%"
                    )
                    WeatherDetailChip(
                        label = "Wind",
                        value = "${forecast.windSpeed.toInt()} km/h"
                    )
                    WeatherDetailChip(
                        label = "Rain",
                        value = "${forecast.precipitationProbability}%"
                    )
                }
            }
        }
    }
}

@Composable
private fun WeatherDetailChip(
    label: String,
    value: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            fontFamily = JosefinSans,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = label,
            fontFamily = JosefinSans,
            fontSize = 11.sp,
            fontWeight = FontWeight.Normal,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun ForecastMemeVersionDrawer(
    selectedVersion: MemeVersion,
    onVersionSelected: (MemeVersion) -> Unit,
    onCloseDrawer: () -> Unit
) {
    ModalDrawerSheet(
        modifier = Modifier.width(300.dp),
        drawerContainerColor = MaterialTheme.colorScheme.surface,
        drawerContentColor = MaterialTheme.colorScheme.onSurface
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Forecast Style",
                    fontFamily = JosefinSans,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                IconButton(onClick = onCloseDrawer) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Global Version Option
            ForecastMemeVersionOption(
                title = "Global Sass",
                description = "International sarcasm for your weekly weather doom.",
                isSelected = selectedVersion == MemeVersion.GLOBAL,
                onClick = { onVersionSelected(MemeVersion.GLOBAL) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Indian Version Option
            ForecastMemeVersionOption(
                title = "Desi Tadka",
                description = "Indian style weather predictions with local flavor!",
                isSelected = selectedVersion == MemeVersion.INDIAN,
                onClick = { onVersionSelected(MemeVersion.INDIAN) }
            )

            Spacer(modifier = Modifier.weight(1f))

            // Footer
            Text(
                text = "Choose your preferred style for 7-day weather roasts!",
                fontFamily = JosefinSans,
                fontSize = 12.sp,
                fontWeight = FontWeight.Normal,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun ForecastMemeVersionOption(
    title: String,
    description: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 8.dp else 2.dp
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = title,
                    fontFamily = JosefinSans,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) {
                        MaterialTheme.colorScheme.onPrimaryContainer
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    }
                )
                RadioButton(
                    selected = isSelected,
                    onClick = onClick,
                    colors = RadioButtonDefaults.colors(
                        selectedColor = MaterialTheme.colorScheme.primary,
                        unselectedColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = description,
                fontFamily = JosefinSans,
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                color = if (isSelected) {
                    MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                }
            )
        }
    }
}