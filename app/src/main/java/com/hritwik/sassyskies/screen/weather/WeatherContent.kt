package com.hritwik.sassyskies.screen.weather

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hritwik.sassyskies.model.utils.MemeVersion
import com.hritwik.sassyskies.model.weather.core.WeatherResponse
import com.hritwik.sassyskies.screen.components.AnimatedIcon
import com.hritwik.sassyskies.ui.theme.JosefinSans
import com.hritwik.sassyskies.ui.theme.WeatherTypography
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherContent(
    weather: WeatherResponse,
    sarcasticMessage: String,
    selectedMemeVersion: MemeVersion = MemeVersion.GLOBAL,
    onRefreshClick: () -> Unit = {},
    onLocationClick: () -> Unit = {},
    onMemeVersionChanged: (MemeVersion) -> Unit = {},
    onDetailedWeatherClick: () -> Unit = {},
    onDeveloperInfoClick: () -> Unit = {},
    onForecastClick: () -> Unit = {}
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // Calculate dynamic font size based on message length
    val dynamicFontSize = remember(sarcasticMessage) {
        calculateDynamicFontSize(sarcasticMessage)
    }

    // Calculate dynamic line height based on font size
    val dynamicLineHeight = remember(dynamicFontSize) {
        (dynamicFontSize.value * 1.1f).sp
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = false,
        drawerContent = {
            MemeVersionDrawer(
                selectedVersion = selectedMemeVersion,
                onVersionSelected = { version ->
                    onMemeVersionChanged(version)
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
                WeatherTopAppBar(
                    location = weather.name,
                    lastUpdated = Date(weather.dt * 1000L),
                    onRefreshClick = onRefreshClick,
                    onLocationClick = onLocationClick,
                    onOptionsClick = {
                        scope.launch { drawerState.open() }
                    },
                    onDeveloperInfoClick = onDeveloperInfoClick,
                    onForecastClick = onForecastClick
                )
            },
            bottomBar = {
                WeatherBottomBar(
                    weather = weather,
                    onDetailedWeatherClick = onDetailedWeatherClick
                )
            },
            containerColor = MaterialTheme.colorScheme.background
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 32.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Spacer(modifier = Modifier.height(20.dp))

                // Weather Icon
                AnimatedIcon(
                    weather = weather,
                    modifier = Modifier.size(120.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Create annotated string with highlighted keywords
                val annotatedMessage = buildAnnotatedString {
                    val words = sarcasticMessage.split(" ")
                    words.forEachIndexed { index, word ->
                        val shouldHighlight = word.lowercase().contains(
                            Regex("rain|snow|hot|cold|fuck|shit|damn|hell|storm|sun|cloud|wind|fog|mist|freezing|melting|humid|dry|windy|sunny|cloudy|clear|thunder|lightning|yaar|bhai|arre|saala|bc|bhencho|matlab|kya|hai|ye|toh|abey|oye|bindass|jugaad")
                        )

                        if (shouldHighlight) {
                            withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.primary)) {
                                append(word)
                            }
                        } else {
                            withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.onBackground)) {
                                append(word)
                            }
                        }

                        if (index < words.size - 1) {
                            append(" ")
                        }
                    }
                }

                Spacer(modifier = Modifier.weight(0.5f))

                // Temperature display
                Text(
                    text = "${weather.main.temp.roundToInt()}°",
                    style = WeatherTypography.MainTemperature,
                    fontWeight = FontWeight.Light,
                    color = MaterialTheme.colorScheme.onBackground
                )

                // Dynamic sarcastic message with calculated font size
                Text(
                    text = annotatedMessage,
                    fontFamily = JosefinSans,
                    fontSize = dynamicFontSize,
                    fontWeight = FontWeight.Bold,
                    lineHeight = dynamicLineHeight,
                    textAlign = TextAlign.Start,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WeatherTopAppBar(
    location: String,
    lastUpdated: Date,
    onRefreshClick: () -> Unit,
    onLocationClick: () -> Unit,
    onOptionsClick: () -> Unit,
    onDeveloperInfoClick: () -> Unit = {},
    onForecastClick: () -> Unit
) {
    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

    TopAppBar(
        title = {
            Column(
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Location",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = location,
                        fontFamily = JosefinSans,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Text(
                    text = "Updated ${timeFormat.format(lastUpdated)}",
                    fontFamily = JosefinSans,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        actions = {
            // Add a details button
            IconButton(onClick = onDeveloperInfoClick) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "Dev Details",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(onClick = onForecastClick) {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = "7-Day Forecast",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(onClick = onLocationClick) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "Change Location",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(onClick = onRefreshClick) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Refresh Weather",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(onClick = onOptionsClick) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "Options",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent,
            titleContentColor = MaterialTheme.colorScheme.onSurface
        )
    )
}

@Composable
private fun MemeVersionDrawer(
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
                    text = "Sarcasm Style",
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
            MemeVersionOption(
                title = "Global Sass",
                description = "International sarcasm with no boundaries. Brutal and universal.",
                examples = listOf(
                    "\"It's fucking raining. Now you know.\"",
                    "\"Satan's armpit is cooler than this.\"",
                    "\"Weather so shitty even clouds are embarrassed.\""
                ),
                isSelected = selectedVersion == MemeVersion.GLOBAL,
                onClick = { onVersionSelected(MemeVersion.GLOBAL) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Indian Version Option
            MemeVersionOption(
                title = "Desi Tadka",
                description = "Indian memes and slang. Relatable, funny, and totally bindass!",
                examples = listOf(
                    "\"Yaar ye kya barish hai, Mumbai local jaisi crowded!\"",
                    "\"Garmi itni hai ki AC bhi ghar jaana chahta hai.\"",
                    "\"Humidity level: Rajasthani summer wedding level.\""
                ),
                isSelected = selectedVersion == MemeVersion.INDIAN,
                onClick = { onVersionSelected(MemeVersion.INDIAN) }
            )

            Spacer(modifier = Modifier.weight(1f))

            // Footer
            Text(
                text = "Choose your preferred style of weather roasts!",
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
private fun MemeVersionOption(
    title: String,
    description: String,
    examples: List<String>,
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

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Examples:",
                fontFamily = JosefinSans,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (isSelected) {
                    MaterialTheme.colorScheme.onPrimaryContainer
                } else {
                    MaterialTheme.colorScheme.onSurface
                }
            )

            examples.forEach { example ->
                Text(
                    text = "• $example",
                    fontFamily = JosefinSans,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Normal,
                    color = if (isSelected) {
                        MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                    },
                    modifier = Modifier.padding(top = 2.dp, start = 8.dp)
                )
            }
        }
    }
}

@Composable
private fun WeatherBottomBar(
    weather: WeatherResponse,
    onDetailedWeatherClick: () -> Unit = {}
) {
    BottomAppBar(
        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
        tonalElevation = 8.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            WeatherDetailButton(
                label = "Feels like",
                value = "${weather.main.feelsLike.roundToInt()}°",
                onClick = onDetailedWeatherClick
            )

            WeatherDetailButton(
                label = "Humidity",
                value = "${weather.main.humidity}%",
                onClick = onDetailedWeatherClick
            )

            WeatherDetailButton(
                label = "Wind",
                value = "${weather.wind.speed.roundToInt()} km/h",
                onClick = onDetailedWeatherClick
            )
        }
    }
}

@Composable
private fun WeatherDetailButton(
    label: String,
    value: String,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        modifier = Modifier
            .padding(horizontal = 4.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = value,
                fontFamily = JosefinSans,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
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
}

private fun calculateDynamicFontSize(text: String): TextUnit {
    val length = text.length
    val wordCount = text.split(" ").size

    return when {
        length <= 30 -> 52.sp
        length <= 50 -> 46.sp
        length <= 80 -> 42.sp
        length <= 120 -> 36.sp
        length <= 160 -> 32.sp
        length <= 200 -> 28.sp
        else -> 24.sp
    }.let { baseFontSize ->
        when {
            wordCount <= 5 -> baseFontSize
            wordCount <= 10 -> (baseFontSize.value * 0.95f).sp
            wordCount <= 15 -> (baseFontSize.value * 0.9f).sp
            else -> (baseFontSize.value * 0.85f).sp
        }
    }
}