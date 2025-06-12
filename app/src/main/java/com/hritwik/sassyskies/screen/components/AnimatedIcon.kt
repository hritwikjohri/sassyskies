package com.hritwik.sassyskies.screen.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.hritwik.sassyskies.model.weather.core.WeatherResponse

@Composable
fun AnimatedIcon(
    weather: WeatherResponse,
    modifier: Modifier = Modifier
) {
    val iconCode = weather.weather.firstOrNull()?.icon ?: "01d"
    val weatherMain = weather.weather.firstOrNull()?.main?.lowercase() ?: ""
    val isDay = iconCode.endsWith("d")

    val iconFileName = when {
        weatherMain.contains("clear") -> if (isDay) "clear-day.svg" else "clear-night.svg"
        weatherMain.contains("clouds") -> when {
            iconCode.startsWith("02") -> if (isDay) "partly-cloudy-day.svg" else "partly-cloudy-night.svg"
            iconCode.startsWith("03") -> "cloudy.svg"
            iconCode.startsWith("04") -> "overcast.svg"
            else -> "cloudy.svg"
        }
        weatherMain.contains("rain") -> when {
            iconCode.startsWith("09") -> "rain.svg"
            iconCode.startsWith("10") -> if (isDay) "partly-cloudy-day-rain.svg" else "partly-cloudy-night-rain.svg"
            else -> "rain.svg"
        }
        weatherMain.contains("drizzle") -> "drizzle.svg"
        weatherMain.contains("thunderstorm") -> "thunderstorms.svg"
        weatherMain.contains("snow") -> "snow.svg"
        weatherMain.contains("mist") || weatherMain.contains("fog") -> "fog.svg"
        weatherMain.contains("smoke") -> "smoke.svg"
        weatherMain.contains("haze") -> "haze.svg"
        weatherMain.contains("dust") || weatherMain.contains("sand") -> "dust.svg"
        weatherMain.contains("tornado") -> "tornado.svg"
        else -> if (isDay) "clear-day.svg" else "clear-night.svg"
    }

    val svgPath = "file:///android_asset/weather_icons/$iconFileName"

    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(svgPath)
            .crossfade(true)
            .build(),
        contentDescription = "Weather icon",
        modifier = modifier
    )
}