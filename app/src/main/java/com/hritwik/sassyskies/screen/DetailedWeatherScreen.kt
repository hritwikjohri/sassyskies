package com.hritwik.sassyskies.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Compress
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Nightlight
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.Update
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hritwik.sassyskies.model.weather.core.WeatherResponse
import com.hritwik.sassyskies.ui.theme.JosefinSans
import com.hritwik.sassyskies.viewmodel.WeatherViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailedWeatherScreen(
    weatherViewModel: WeatherViewModel = hiltViewModel(),
    onBackClick: () -> Unit
) {
    val weatherUiState by weatherViewModel.uiState.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()

    // Show loading or error states if needed
    when {
        weatherUiState.weatherData == null -> {
            // Handle no data case
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
                        text = "Loading weather details...",
                        fontFamily = JosefinSans,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
            return
        }
    }

    val weather = weatherUiState.weatherData!!

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Weather Details",
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
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Main Weather Card
            MainWeatherCard(weather = weather)

            // Temperature Details
            TemperatureDetailsCard(weather = weather)

            // Atmospheric Conditions
            AtmosphericConditionsCard(weather = weather)

            // Wind Information
            WindInformationCard(weather = weather)

            // Sun Information
            SunInformationCard(weather = weather)

            // Additional Information
            AdditionalInfoCard(weather = weather)

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun MainWeatherCard(weather: WeatherResponse) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primaryContainer,
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                        )
                    )
                )
                .padding(24.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                AnimatedIcon(
                    weather = weather,
                    modifier = Modifier.size(80.dp)
                )

                Text(
                    text = "${weather.main.temp.roundToInt()}°C",
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Light,
                    fontFamily = JosefinSans,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )

                Text(
                    text = weather.weather.firstOrNull()?.description?.replaceFirstChar {
                        it.uppercase()
                    } ?: "Unknown",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = JosefinSans,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
                    textAlign = TextAlign.Center
                )

                Text(
                    text = weather.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                    fontFamily = JosefinSans,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
private fun TemperatureDetailsCard(weather: WeatherResponse) {
    DetailCard(
        title = "Temperature Details",
        icon = Icons.Default.Thermostat
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            TemperatureItem(
                label = "Feels Like",
                value = "${weather.main.feelsLike.roundToInt()}°C",
                icon = Icons.Default.Thermostat,
                modifier = Modifier.weight(1f)
            )
            TemperatureItem(
                label = "Min Temp",
                value = "${weather.main.tempMin.roundToInt()}°C",
                icon = Icons.Default.Thermostat,
                modifier = Modifier.weight(1f)
            )
            TemperatureItem(
                label = "Max Temp",
                value = "${weather.main.tempMax.roundToInt()}°C",
                icon = Icons.Default.Thermostat,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun AtmosphericConditionsCard(weather: WeatherResponse) {
    DetailCard(
        title = "Atmospheric Conditions",
        icon = Icons.Default.Cloud
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                AtmosphericItem(
                    icon = Icons.Default.WaterDrop,
                    label = "Humidity",
                    value = "${weather.main.humidity}%",
                    modifier = Modifier.weight(1f)
                )
                AtmosphericItem(
                    icon = Icons.Default.Speed,
                    label = "Pressure",
                    value = "${weather.main.pressure} hPa",
                    modifier = Modifier.weight(1f)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                AtmosphericItem(
                    icon = Icons.Default.Visibility,
                    label = "Visibility",
                    value = "${weather.visibility / 1000} km",
                    modifier = Modifier.weight(1f)
                )
                AtmosphericItem(
                    icon = Icons.Default.Cloud,
                    label = "Cloudiness",
                    value = "${weather.clouds.all}%",
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun WindInformationCard(weather: WeatherResponse) {
    DetailCard(
        title = "Wind Information",
        icon = Icons.Default.Air
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            WindItem(
                label = "Speed",
                value = "${weather.wind.speed} m/s",
                icon = Icons.Default.Air,
                modifier = Modifier.weight(1f)
            )
            WindItem(
                label = "Direction",
                value = "${weather.wind.deg}°",
                icon = Icons.Default.Navigation,
                modifier = Modifier.weight(1f)
            )
            WindItem(
                label = "Gust",
                value = weather.wind.gust?.let { "$it m/s" } ?: "N/A",
                icon = Icons.Default.Air,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun SunInformationCard(weather: WeatherResponse) {
    val sunriseTime = SimpleDateFormat("HH:mm", Locale.getDefault())
        .format(Date(weather.sys.sunrise * 1000L))
    val sunsetTime = SimpleDateFormat("HH:mm", Locale.getDefault())
        .format(Date(weather.sys.sunset * 1000L))

    DetailCard(
        title = "Sun Information",
        icon = Icons.Default.WbSunny
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            SunItem(
                label = "Sunrise",
                value = sunriseTime,
                icon = Icons.Default.LightMode,
                modifier = Modifier.weight(1f)
            )
            SunItem(
                label = "Sunset",
                value = sunsetTime,
                icon = Icons.Default.Nightlight,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun AdditionalInfoCard(weather: WeatherResponse) {
    val lastUpdated = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
        .format(Date(weather.dt * 1000L))

    DetailCard(
        title = "Additional Information",
        icon = Icons.Default.Info
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            InfoRow(
                icon = Icons.Default.Public,
                label = "Country",
                value = weather.sys.country
            )
            InfoRow(
                icon = Icons.Default.LocationOn,
                label = "Coordinates",
                value = "${String.format("%.2f", weather.coord.lat)}, ${String.format("%.2f", weather.coord.lon)}"
            )
            InfoRow(
                icon = Icons.Default.Schedule,
                label = "Timezone",
                value = "UTC${if (weather.timezone >= 0) "+" else ""}${weather.timezone / 3600}"
            )
            InfoRow(
                icon = Icons.Default.Update,
                label = "Last Updated",
                value = lastUpdated
            )
            weather.main.seaLevel?.let {
                InfoRow(
                    icon = Icons.Default.Compress,
                    label = "Sea Level Pressure",
                    value = "$it hPa"
                )
            }
            weather.main.grndLevel?.let {
                InfoRow(
                    icon = Icons.Default.Compress,
                    label = "Ground Level Pressure",
                    value = "$it hPa"
                )
            }
        }
    }
}

@Composable
private fun DetailCard(
    title: String,
    icon: ImageVector,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = JosefinSans,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            content()
        }
    }
}

@Composable
private fun TemperatureItem(
    label: String,
    value: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = value,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = JosefinSans,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.Normal,
            fontFamily = JosefinSans,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun AtmosphericItem(
    icon: ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = value,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            fontFamily = JosefinSans,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.Normal,
            fontFamily = JosefinSans,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun WindItem(
    label: String,
    value: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = value,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            fontFamily = JosefinSans,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.Normal,
            fontFamily = JosefinSans,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun SunItem(
    label: String,
    value: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(28.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = value,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = JosefinSans,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.Normal,
            fontFamily = JosefinSans,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun InfoRow(
    icon: ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp)
        )
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal,
            fontFamily = JosefinSans,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            fontFamily = JosefinSans,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}